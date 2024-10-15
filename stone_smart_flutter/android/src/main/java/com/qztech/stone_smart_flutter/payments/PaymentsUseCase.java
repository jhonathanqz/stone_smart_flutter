package com.qztech.stone_smart_flutter.payments;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.qztech.stone_smart_flutter.core.ActionResult;
import com.qztech.stone_smart_flutter.core.BasicResult;
import com.qztech.stone_smart_flutter.printer.StonePrinter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.stone.posandroid.providers.PosPrintReceiptProvider;
import br.com.stone.posandroid.providers.PosTransactionProvider;
import stone.application.StoneStart;
import stone.application.enums.Action;
import stone.application.enums.ReceiptType;
import stone.application.enums.TransactionStatusEnum;
import stone.application.interfaces.StoneActionCallback;
import stone.application.interfaces.StoneCallbackInterface;
import stone.application.xml.enums.ResponseCodeEnum;
import stone.database.transaction.TransactionDAO;
import stone.database.transaction.TransactionObject;
import stone.providers.ActiveApplicationProvider;
import stone.providers.CancellationProvider;
import stone.providers.ReversalProvider;
import stone.user.UserModel;
import stone.utils.Stone;
import io.flutter.plugin.common.MethodChannel;
import stone.utils.keys.StoneKeyType;

public class PaymentsUseCase {

  private PaymentsFragment mFragment;
  private StoneHelper mStoneHelper;
  private StonePrinter mStonePrinter;
  private TransactionObject currentTransactionObject;
  private PosTransactionProvider posTransactionProvider;
  private List<UserModel> userModel;
  private List<String> optionList;
  private boolean isInitialized = false;

  List<TransactionObject> transactionObjects;

  private boolean isActivePinpad = false;
  Gson gson = null;
  public PaymentsUseCase(MethodChannel channel) {
    this.mFragment = new PaymentsFragment(channel);
    this.mStonePrinter = new StonePrinter(channel);
    this.mStoneHelper = new StoneHelper();
  }

  private Gson getGson() {
    if (gson == null) {
      gson = new Gson();
    }
    return gson;
  }

  public void initTransaction(Context context,
                              String amount,
                              int typeTransaction,
                              String initiatorTransactionKey,
                              int parc,
                              boolean withInterest,
                              Map<StoneKeyType, String> stoneKeys,
                              boolean isPrinter
                              ){
    if(stoneKeys == null) {
      checkUserModel(context);
      transaction(context, amount, typeTransaction, initiatorTransactionKey, parc, withInterest, isPrinter);
      return;
    }
    if(userModel == null) {
      userModel = StoneStart.init(context, stoneKeys);
    }
    
    transaction(context, amount, typeTransaction, initiatorTransactionKey, parc, withInterest, isPrinter);
  }

  private void checkUserModel(Context context) {
      isInitialized = StoneStart.INSTANCE.getSDKInitialized();

      if (isInitialized) {
        return;
      }

      if(userModel == null) {
        userModel = StoneStart.init(context);
      }
  }

  public void handleTransactionError(Context context, String message, ActionResult actionResult, BasicResult basicResult) {
    mFragment.onMessage(message);

    checkStatusWithErrorTransaction(posTransactionProvider.getTransactionStatus(), context);

    actionResult.setTransactionStatus(posTransactionProvider.getTransactionStatus().toString());
    actionResult.setMessageFromAuthorize(posTransactionProvider.getMessageFromAuthorize());
    actionResult.setAuthorizationCode(posTransactionProvider.getAuthorizationCode());
    actionResult.setResult(999999);
    actionResult.setErrorMessage(mStoneHelper.getErrorFromErrorList(posTransactionProvider.getListOfErrors()));

    mFragment.onMessage(posTransactionProvider.getMessageFromAuthorize());

    basicResult.setResult(999999);
    basicResult.setErrorMessage(mStoneHelper.getErrorFromErrorList(posTransactionProvider.getListOfErrors()));

    mFragment.onError(convertBasicResultToJson(basicResult));
    String jsonError = convertActionToJson(actionResult);
    mFragment.onFinishedResponse(jsonError);
    // posTransactionProvider.abortPayment();
    currentTransactionObject = null;
    posTransactionProvider= null;

    mFragment.onMessage("Transação concluída");
  }

  public void handleTransactionSuccess(Context context, ActionResult actionResult, boolean isPrinter, TransactionObject transaction) {
    TransactionDAO transactionDAO = new TransactionDAO(context);
    TransactionObject transactionObject = transactionDAO.findTransactionWithInitiatorTransactionKey(transaction.getInitiatorTransactionKey());
    checkStatusWithErrorTransaction(posTransactionProvider.getTransactionStatus(), context);
    actionResult.setTransactionStatus(posTransactionProvider.getTransactionStatus().toString());
    actionResult.setMessageFromAuthorize(posTransactionProvider.getMessageFromAuthorize());
    actionResult.setAuthorizationCode(posTransactionProvider.getAuthorizationCode());
    if(!userModel.isEmpty()){
      String userModelString = getGson().toJson(userModel.get(0));
      actionResult.setUserModel(userModelString);
    }
    boolean isPaymentApproved = posTransactionProvider.getTransactionStatus() == TransactionStatusEnum.APPROVED || currentTransactionObject.getTransactionStatus() == TransactionStatusEnum.APPROVED;
    actionResult.buildResponseStoneTransaction(transactionObject, isPaymentApproved);
    String jsonStoneResult = convertActionToJson(actionResult);
    finishTransaction(jsonStoneResult);

    if (isPrinter) {
      printerReceiptTransaction(context, currentTransactionObject, posTransactionProvider.getTransactionStatus());
    }

    posTransactionProvider = null;

    mFragment.onMessage("Transação concluída");
    mFragment.onTransactionSuccess();
  }

  public void handleTransactionStatusChanged(Action action, TransactionObject transaction, BasicResult basicResult) {
    String actionMessage = mStoneHelper.getMessageFromTransactionAction(action);
    mFragment.onMessage(actionMessage);
    if (action == Action.TRANSACTION_WAITING_QRCODE_SCAN) {
      basicResult.setMethod("QRCode");
      basicResult.setMessage(mStoneHelper.convertBitmapToString(transaction.getQRCode()));
      mFragment.onChanged(convertBasicResultToJson(basicResult));
      mFragment.onAuthProgress(convertBasicResultToJson(basicResult));
    }

    if(action == Action.TRANSACTION_TYPE_SELECTION) {
      List<String> options = posTransactionProvider.getTransactionTypeOptions();
      optionList = options;
      basicResult.setMethod("PaymentOptions");
      basicResult.setOptions(options);
      basicResult.setMessage(actionMessage);
      mFragment.onChanged(convertBasicResultToJson(basicResult));
      mFragment.onAuthProgress(convertBasicResultToJson(basicResult));
    }
  }

  public void transaction(
          Context context,
          String amount,
          int typeTransaction,
          String initiatorTransactionKey,
          int parc,
          boolean withInterest,
          boolean isPrinter
  ) {
    BasicResult basicResult = new BasicResult();
    basicResult.setMethod("transaction");
    ActionResult actionResult = new ActionResult();
    actionResult.setMethod("transaction");



    try {
      mFragment.onMessage("Iniciando transação");
      currentTransactionObject = null;
      posTransactionProvider = null;

      final TransactionObject transaction = mStoneHelper.getTransactionObject(amount, typeTransaction, parc, withInterest);
      currentTransactionObject = transaction;

      // Essa validação é para verificar se a transação já foi realizada (recomendação da própria Stone) para evitar duplicidade
      if(initiatorTransactionKey != null && !initiatorTransactionKey.isEmpty()){
        TransactionDAO transactionDAO = new TransactionDAO(context);

        TransactionObject transactionObject = transactionDAO.findTransactionWithInitiatorTransactionKey(initiatorTransactionKey);
        if(transactionObject != null) {
          if(transactionObject.getTransactionStatus() == TransactionStatusEnum.APPROVED){

            checkStatusWithErrorTransaction(transactionObject.getTransactionStatus(), context);
            actionResult.setTransactionStatus(transactionObject.getTransactionStatus().toString());
            actionResult.setMessageFromAuthorize(transactionObject.getAuthorizationCode());
            actionResult.setAuthorizationCode(transactionObject.getAuthorizationCode());

            if(!userModel.isEmpty()){
              String userModelString = getGson().toJson(userModel.get(0));
              actionResult.setUserModel(userModelString);
            }

            boolean isPaymentApproved = transactionObject.getTransactionStatus() == TransactionStatusEnum.APPROVED;

            actionResult.buildResponseStoneTransaction(transactionObject, isPaymentApproved);
            String jsonStoneResult = convertActionToJson(actionResult);
            finishTransaction(jsonStoneResult);

            mFragment.onMessage("Transação concluída");
            mFragment.onTransactionSuccess();
          } else {
            handleTransactionError(context, "Transação já realizada mas não aprovada", actionResult, basicResult);
          }
          return;
        }

        transaction.setInitiatorTransactionKey(initiatorTransactionKey);
      }


      //Essa variavel posTransactionProvider está armazenando o provider para que possamos cancelar a transação
      posTransactionProvider = new PosTransactionProvider(context, transaction, userModel.get(0));

      mFragment.onMessage("Comunicando com o servidor Stone. Aguarde.");

      posTransactionProvider.setConnectionCallback(new StoneActionCallback() {
        @Override
        public void onSuccess() {
          handleTransactionSuccess(context, actionResult, isPrinter, transaction);
        }
        @Override
        public void onStatusChanged(Action action) {
          handleTransactionStatusChanged(action, transaction, basicResult);
        }
        @Override
        public void onError() {
          handleTransactionError(context, "Erro ao realizar transação", actionResult, basicResult);
        }

      });
      posTransactionProvider.execute();
    } catch (Exception e) {
      basicResult.setResult(999999);
      basicResult.setErrorMessage(e.getMessage());

      mFragment.onError(convertBasicResultToJson(basicResult));
      currentTransactionObject = null;
      if(posTransactionProvider != null){
        abortCurrentPosTransaction();
      }
      posTransactionProvider = null;
    }
  }

  public void setPaymentOption(String value) {
    Log.d("print", "****Chegou option: " + value);
    if(posTransactionProvider == null || optionList.isEmpty()) return;
    Integer index = optionList.indexOf(value);
    posTransactionProvider.setTransactionTypeSelected(index);
  }

  private void changePrinterRequest() {
    BasicResult basicResult = new BasicResult();
    basicResult.setMethod("printerTransaction");
    basicResult.setMessage("Deseja imprimir sua via?");
    mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));
  }

  public void printerCurrentTransaction(Context context, boolean isPrinter) {
    if(currentTransactionObject == null) return;
    if(!isPrinter) {
      currentTransactionObject = null;
      return;
    }
    try {
      mStonePrinter.printerFromTransaction(context, currentTransactionObject);
    } catch (Exception error) {}
  }

  public void printerReceiptTransaction(Context context, TransactionObject transactionObject,TransactionStatusEnum status ) {
    try {
      boolean isPrinterValid = status == TransactionStatusEnum.APPROVED || transactionObject.getTransactionStatus() == TransactionStatusEnum.APPROVED;

        if(isPrinterValid) {
        PosPrintReceiptProvider printer = new PosPrintReceiptProvider(context, transactionObject, ReceiptType.MERCHANT);
        printer.execute();
      }
    } catch(Exception e) {
    }
  }

  public void abortCurrentPosTransaction() {
    if(posTransactionProvider == null){
      return;
    }
    try {
      posTransactionProvider.abortPayment();
    } catch (Exception error){
      BasicResult basicResult = new BasicResult();
      basicResult.setResult(999999);
      basicResult.setErrorMessage(error.getMessage());
      mFragment.onError(convertBasicResultToJson(basicResult));
    }
  }

  public void abortPIXTransaction(Context context) {
    Log.d("print", "****INICIANDO ABORT PIX");
    try {
      cancelTransactionPIX(context);
    }catch (Exception err) {}

    try {
      posTransactionProvider.abortPayment();
    } catch (Exception error){}


    BasicResult basicResult = new BasicResult();
    basicResult.setMethod("abortPix");
    Log.d("print", "****FIM ABORT PIX");
    mFragment.onMessage("Transação cancelada");
    basicResult.setResult(999999);
    basicResult.setMessage("Transação cancelada");
    basicResult.setErrorMessage("Transação cancelada");
    String jsonError = convertBasicResultToJson(basicResult);
    mFragment.onFinishedResponse(jsonError);
  }

  private String convertActionToJson(ActionResult actionResult) {
    return getGson().toJson(actionResult);
  }

  private String convertBasicResultToJson(BasicResult basicResult) {
    return getGson().toJson(basicResult);
  }

  private void finishTransaction(String result) {
    mFragment.onTransactionSuccess();
    mFragment.onFinishedResponse(result);
  }


  public void initializeAndActivatePinpad(
          String appName,
          String stoneCode,
          Context context
  ) {
    try {
      mFragment.onMessage("Iniciando ativação");
      Stone.setAppName(appName);
      checkUserModel(context);
      BasicResult basicResult = new BasicResult();
      if (userModel == null) {
        ActiveApplicationProvider activeApplicationProvider = getActiveApplicationProvider(context);
        activeApplicationProvider.activate(stoneCode);

      } else {
        isActivePinpad = true;
        mFragment.onMessage("Terminal ativado");

        basicResult.setMethod("active");
        basicResult.setResult(0);
        basicResult.setMessage("Terminal ativado");
        if(!userModel.isEmpty()) {
          String userModelString = getGson().toJson(userModel.get(0));
          basicResult.setUserModel(userModelString);
        }
        String resultJson = convertBasicResultToJson(basicResult);
        mFragment.onFinishedResponse(resultJson);
        mFragment.onAuthProgress(resultJson);
      }
    } catch (Exception error) {}
  }

  public void initializeAndActivatePinPadWithCredentials(
          String appName,
          String stoneCode,
          Map<StoneKeyType, String> stoneKeys,
          Context context
  ) {
    try {
      mFragment.onMessage("Iniciando ativação");
      Stone.setAppName(appName);
      List<UserModel> userList = StoneStart.init(context, stoneKeys);
      userModel = userList;
      BasicResult basicResult = new BasicResult();
      if (userList == null) {
        ActiveApplicationProvider activeApplicationProvider = getActiveApplicationProvider(context);
        activeApplicationProvider.activate(stoneCode);

      } else {
        mFragment.onMessage("Terminal ativado");

        basicResult.setMethod("active");
        basicResult.setResult(0);
        basicResult.setMessage("Terminal ativado");
        if(!userModel.isEmpty()){
          String userModelString = getGson().toJson(userModel.get(0));
          basicResult.setUserModel(userModelString);
        }
        String resultJson = convertBasicResultToJson(basicResult);
        mFragment.onFinishedResponse(resultJson);
        mFragment.onAuthProgress(resultJson);
      }
    }catch (Exception error){}
  }

  public void cancelTransaction(Context context, int idFromBase) {
    BasicResult basicResult = new BasicResult();
    basicResult.setMethod("cancel");
    try {
      TransactionDAO transactionDAO = new TransactionDAO(context);
      TransactionObject transaction = transactionDAO.findTransactionWithId(idFromBase);
      if(transaction == null) {
        mFragment.onError("Transação não encontrada");
        basicResult.setResult(999999);
        basicResult.setErrorMessage("Transação não encontrada");
        String jsonError = convertBasicResultToJson(basicResult);
        mFragment.onFinishedResponse(jsonError);
        return;
      }
      final CancellationProvider cancellationProvider = getCancellationProvider(context, transaction);
      cancellationProvider.execute();
    } catch (Exception error) {
      mFragment.onMessage("Erro ao cancelar transação");
      basicResult.setResult(999999);
      basicResult.setErrorMessage(error.getMessage());
      mFragment.onError(convertBasicResultToJson(basicResult));
    }
  }

  public void cancelTransactionPIX(Context context) {
    BasicResult basicResult = new BasicResult();
    basicResult.setMethod("abortPix");
    // Log.d("print", "****************************************************************** ABORT PIX 1 ******************************************************************");
    if(posTransactionProvider != null) {
        posTransactionProvider.abortPayment();
      }

    try {
      final CancellationProvider cancellationProvider = new CancellationProvider(context, currentTransactionObject);
      cancellationProvider.execute();
      
    } catch (Exception error) {
      Log.d("print", "****ERRO_CANCEL_PIX_1: " + error.getMessage());
      mFragment.onMessage("Transação cancelada");
      basicResult.setResult(999999);
      
    }
   
    // try {
    //   final TransactionObject transaction = mStoneHelper.getTransactionObject("1", 3, 1, false);
    //   final PosTransactionProvider provider = new PosTransactionProvider(context, transaction, userModel.get(0));
    //   provider.execute();
    // } catch(Exception e) {
    //   Log.d("print", "****ERRO_CANCEL_PIX_2: " + e.getMessage());
    // }

    // try {
    //   Log.d("print", "****ERRO_CANCEL_PIX NOVO");
    //   ReversalProvider reversalProvider = new ReversalProvider(context);
    //   reversalProvider.setConnectionCallback(new StoneCallbackInterface() {
    //         @Override
    //         public void onSuccess() {
    //           Log.d("print", "****ERRO_CANCEL_PIX_ON SUCCESS");
    //           basicResult.setMessage("OK");
    //           String jsonError = convertBasicResultToJson(basicResult);
    //           mFragment.onFinishedResponse(jsonError);
    //           Log.d("print", "****************************************************************** ABORT PIX 3 ******************************************************************");
    //         }

    //         @Override
    //         public void onError() {
    //           Log.d("print", "****ERRO_CANCEL_PIX_ON ERROR");
    //           basicResult.setMessage("ERROR");
    //           String jsonError = convertBasicResultToJson(basicResult);
    //           mFragment.onFinishedResponse(jsonError);
    //           Log.d("print", "****************************************************************** ABORT PIX 4 ******************************************************************");
    //         }
    //   });
    //   reversalProvider.execute();
    // } catch(Exception e) {
    //   Log.d("print", "****ERRO_CANCEL_PIX_3: " + e.getMessage());
    //   basicResult.setMessage("ERROR");
    //   String jsonError = convertBasicResultToJson(basicResult);
    //   mFragment.onFinishedResponse(jsonError);
    // }
    // Log.d("print", "****************************************************************** ABORT PIX 2 ******************************************************************");
  }

  @NonNull
  private CancellationProvider getCancellationProvider(Context context, TransactionObject transaction) {
    mFragment.onMessage("Obtendo dados da transação");
    final CancellationProvider provider = new CancellationProvider(context, transaction);
    mFragment.onMessage("Processando cancelamento");
    BasicResult basicResult = new BasicResult();
    basicResult.setMethod("cancel");

    provider.setConnectionCallback(new StoneCallbackInterface() {
      @Override
      public void onSuccess() {
       mFragment.onMessage("Transação cancelada");
       mFragment.onTransactionSuccess();
       basicResult.setResult(0);
       ResponseCodeEnum responseCode = provider.getResponseCodeEnum();
       basicResult.setMessage(mStoneHelper.getMessageFromResponseCodeEnum(responseCode));
       mFragment.onAuthProgress("Transação cancelada");
       mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));
      }
      @Override
      public void onError() {
        String error = currentTransactionObject.getActionCode();
        basicResult.setResult(999999);
        basicResult.setErrorMessage(mStoneHelper.getErrorFromErrorList(provider.getListOfErrors()));
        mFragment.onMessage("Erro ao cancelar transação");
        mFragment.onAuthProgress(error);
        mFragment.onError(convertBasicResultToJson(basicResult));
        mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));
      }
    });

    return provider;
  }

  @NonNull
  private ActiveApplicationProvider getActiveApplicationProvider(Context context) {
    ActiveApplicationProvider activeApplicationProvider = new ActiveApplicationProvider(context);

    activeApplicationProvider.setDialogMessage("Ativando o Stone Code");
    activeApplicationProvider.setDialogTitle("Aguarde");
    activeApplicationProvider.useDefaultUI(true);

    BasicResult basicResult = new BasicResult();
    basicResult.setMethod("active");

    mFragment.onMessage("Conectando com terminal");
    activeApplicationProvider.setConnectionCallback(new StoneCallbackInterface() {
      @Override
      public void onSuccess() {
        mFragment.onMessage("Terminal ativado");
        mFragment.onTransactionSuccess();
        basicResult.setResult(0);
        basicResult.setMessage("Terminal ativado");

        mFragment.onAuthProgress(convertBasicResultToJson(basicResult));
        mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));
      }

      @Override
      public void onError() {
        mFragment.onMessage("Erro ao ativar terminal");
        basicResult.setResult(999999);
        basicResult.setErrorMessage(mStoneHelper.getErrorFromErrorList(activeApplicationProvider.getListOfErrors()));
        mFragment.onError(convertBasicResultToJson(basicResult));
        mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));
      }
    });
    return activeApplicationProvider;
  }

  public void checkStatusWithErrorTransaction(TransactionStatusEnum status, Context context) {
    if(status == null || (status != TransactionStatusEnum.WITH_ERROR && status != TransactionStatusEnum.APPROVED) ) {
      if(posTransactionProvider != null) {
        mFragment.onMessage("Abortando a transacao");
        posTransactionProvider.abortPayment();
      }
      return;
    }

    if(status == TransactionStatusEnum.WITH_ERROR) {
      mFragment.onMessage("Revertendo a transacao");
      onReversalTransaction(context);
    }
  }

  public void onReversalTransaction(Context context) {
    BasicResult basicResult = new BasicResult();
    basicResult.setMethod("reversal");

    try {
      ReversalProvider reversalProvider = new ReversalProvider(context);
      reversalProvider.setConnectionCallback(new StoneCallbackInterface() {
        @Override
        public void onSuccess() {
          basicResult.setResult(0);
          basicResult.setMessage("OK");
          mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));
          // Log.d("print", "****************************************************************** ABORT PIX 7 ******************************************************************");
        }

        @Override
        public void onError() {
          basicResult.setResult(999999);
          basicResult.setMessage("ERROR");
          // mFragment.onError(convertBasicResultToJson(basicResult));
          mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));
          // Log.d("print", "****************************************************************** ABORT PIX 8 ******************************************************************");
        }
      });
      reversalProvider.execute();
    } catch (Exception error) {
      basicResult.setErrorMessage(error.getMessage());
      basicResult.setMessage("ERROR");
          mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));
    }
  }

    public void getTransactionByInitiatorTransactionKey(Context context, String initiatorTransactionKey) {
      BasicResult basicResult = new BasicResult();
      basicResult.setMethod("paymentGetTransactionByInitiatorTransactionKey");
      ActionResult actionResult = new ActionResult();
      boolean isPaymentApproved = false;

      try {
        TransactionDAO transactionDAO = new TransactionDAO(context);
        TransactionObject transactionObject = transactionDAO.findTransactionWithInitiatorTransactionKey(initiatorTransactionKey);

        if (transactionObject == null) {
          handleTransactionError(context, "Transação não encontrada", actionResult, basicResult);
          return;
        }

        basicResult.setResult(0);
        basicResult.setMessage("Transação encontrada");

        if(transactionObject.getTransactionStatus() == TransactionStatusEnum.APPROVED){
          isPaymentApproved = true;
        }

        actionResult.setTransactionStatus(transactionObject.getTransactionStatus().toString());
        actionResult.setMessageFromAuthorize(transactionObject.getAuthorizationCode());
        actionResult.setAuthorizationCode(transactionObject.getAuthorizationCode());

        if(!userModel.isEmpty()){
          String userModelString = getGson().toJson(userModel.get(0));
          actionResult.setUserModel(userModelString);
        }

        actionResult.buildResponseStoneTransaction(transactionObject, isPaymentApproved);

        actionResult.buildResponseStoneTransaction(transactionObject, isPaymentApproved);
        String jsonStoneResult = convertActionToJson(actionResult);
        finishTransaction(jsonStoneResult);

      } catch (Exception error) {
        handleTransactionError(context, "Erro ao buscar transação", actionResult, basicResult);
      }
    }

    public void getAllTransactions(Context context) {
      ActionResult actionResult = new ActionResult();
      try {
        mFragment.onMessage("Obtendo todas as transações");
        // acessa todas as transacoes do banco de dados
        TransactionDAO transactionDAO = new TransactionDAO(context);
        // cria uma lista com todas as transacoes
        transactionObjects = transactionDAO.getAllTransactionsOrderByIdDesc();


        actionResult.setMethod("AllTransactions");

        if(transactionObjects == null || transactionObjects.isEmpty()) {
          transactionObjects = new ArrayList<TransactionObject>();
        }

        actionResult.buildAllTransactions(transactionObjects);
        actionResult.setResult(0);
        String jsonStoneResult = convertActionToJson(actionResult);
        mFragment.onFinishedResponse(jsonStoneResult);
      } catch (Exception e) {
        mFragment.onMessage("Erro ao buscar transações");
        actionResult.buildAllTransactions(transactionObjects);
        actionResult.setResult(999999);
        actionResult.setErrorMessage(e.getMessage());
        String jsonStoneResult = convertActionToJson(actionResult);
        mFragment.onFinishedResponse(jsonStoneResult);
        mFragment.onError(e.getMessage());
      }
    }


    public void customPrinter(String params, Context context) {
      mStonePrinter.customPrinter(params, context);
    }

    public void printerFromBase64(String params, Context context) {
      mStonePrinter.printerFromBase64(params, context);
    }
}
