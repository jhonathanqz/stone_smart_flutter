package com.qztech.stone_smart_flutter.payments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.qztech.stone_smart_flutter.core.ActionResult;
import com.qztech.stone_smart_flutter.core.BasicResult;
import com.qztech.stone_smart_flutter.printer.StonePrinter;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.stone.posandroid.providers.PosPrintReceiptProvider;
import br.com.stone.posandroid.providers.PosTransactionProvider;
import stone.application.StoneStart;
import stone.application.enums.Action;
import stone.application.enums.ReceiptType;
import stone.application.enums.TransactionStatusEnum;
import stone.application.enums.TypeOfTransactionEnum;
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
                              int parc,
                              boolean withInterest,
                              Map<StoneKeyType, String> stoneKeys,
                              boolean isPrinter
                              ){
    if(stoneKeys == null) {
      checkUserModel(context);
      transaction(context, amount, typeTransaction, parc, withInterest, isPrinter);
      return;
    }
    userModel = StoneStart.init(context, stoneKeys);
    transaction(context, amount, typeTransaction, parc, withInterest, isPrinter);
  }

  private void checkUserModel(Context context) {
      if(userModel == null) {
        userModel = StoneStart.init(context);
      }

      boolean isInitialized = StoneStart.INSTANCE.getSDKInitialized();
      if(!isInitialized){
        userModel = StoneStart.init(context);
      }
  }

  public void transaction(
          Context context,
          String amount,
          int typeTransaction,
          int parc,
          boolean withInterest,
          boolean isPrinter
  ) {
    BasicResult basicResult = new BasicResult();
    basicResult.setMethod("transaction");
    try {
      mFragment.onMessage("Iniciando transação");
      currentTransactionObject = null;
      posTransactionProvider = null;

      final TransactionObject transaction = mStoneHelper.getTransactionObject(amount, typeTransaction, parc, withInterest);
      currentTransactionObject = transaction;

      posTransactionProvider = new PosTransactionProvider(context, transaction, userModel.get(0));
      //Essa variavel posTransactionProvider está armazenando o provider para que possamos cancelar a transação

      ActionResult actionResult = new ActionResult();
      actionResult.setMethod("transaction");

      mFragment.onMessage("Comunicando com o servidor Stone. Aguarde.");

      posTransactionProvider.setConnectionCallback(new StoneActionCallback() {
        @Override
        public void onSuccess() {
          TransactionDAO transactionDAO = new TransactionDAO(context);
          List<TransactionObject> transactionObjects = transactionDAO.getAllTransactionsOrderByIdDesc();
          checkStatusWithErrorTransaction(posTransactionProvider.getTransactionStatus(), context);
          actionResult.setTransactionStatus(posTransactionProvider.getTransactionStatus().toString());
          actionResult.setMessageFromAuthorize(posTransactionProvider.getMessageFromAuthorize());
          actionResult.setAuthorizationCode(posTransactionProvider.getAuthorizationCode());
          actionResult.buildResponseStoneTransaction(transactionObjects);
          String jsonStoneResult = convertActionToJson(actionResult);
          finishTransaction(jsonStoneResult);
          if(isPrinter){
            printerReceiptTransaction(context, currentTransactionObject);
          }

          posTransactionProvider = null;
        }
        @Override
        public void onStatusChanged(Action action) {
          String actionMessage = mStoneHelper.getMessageFromTransactionAction(action);
          mFragment.onMessage(actionMessage);
          if (action == Action.TRANSACTION_WAITING_QRCODE_SCAN) {
            basicResult.setMethod("QRCode");
            basicResult.setMessage(mStoneHelper.convertBitmapToString(transaction.getQRCode()));
            mFragment.onChanged(convertBasicResultToJson(basicResult));
            mFragment.onAuthProgress(convertBasicResultToJson(basicResult));
          }
        }
        @Override
        public void onError() {
          mFragment.onMessage("Erro ao realizar transação");

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
          currentTransactionObject = null;
          posTransactionProvider= null;
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

  public void printerReceiptTransaction(Context context, TransactionObject transactionObject) {
    try {
      PosPrintReceiptProvider printer = new PosPrintReceiptProvider(context, transactionObject, ReceiptType.MERCHANT);
      printer.execute();
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

  public void abortPIXtransaction(Context context) {
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

  private void alertPrinter(final Context context, final String result, final TransactionObject transactionObject) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Impressão comprovante");
        builder.setMessage("Deseja imprimir sua via?");
        builder.setPositiveButton(
                "Sim",
                new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                    mStonePrinter.printerFromTransaction(context, transactionObject);
                    finishTransaction(result);
                  }
                }
        );
        builder.setNegativeButton(
                "Não",
                new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                    finishTransaction(result);
                  }
                }
        );
        AlertDialog alerta = builder.create();
        alerta.show();
      }
    });
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
        mFragment.onMessage("Terminal ativado");

        basicResult.setMethod("active");
        basicResult.setResult(0);
        basicResult.setMessage("Terminal ativado");
        String resultJson = convertBasicResultToJson(basicResult);
        mFragment.onFinishedResponse(resultJson);
        mFragment.onAuthProgress(resultJson);
      }
    } catch (Exception error) {}
  }

  public void initializeAndActivatePinpadWithCredentials(
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
        String resultJson = convertBasicResultToJson(basicResult);
        mFragment.onFinishedResponse(resultJson);
        mFragment.onAuthProgress(resultJson);
      }
    }catch (Exception error){}
  }

  public void cancelTransaction(Context context, String amount, int typeTransaction) {
    BasicResult basicResult = new BasicResult();
    basicResult.setMethod("cancel");
    try {
      final TransactionObject transaction = mStoneHelper.getTransactionObject(amount, typeTransaction, 1, false);
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
    try {
      final CancellationProvider cancellationProvider = new CancellationProvider(context, currentTransactionObject);
      cancellationProvider.execute();
    } catch (Exception error) {
      mFragment.onMessage("Transação cancelada");
      basicResult.setResult(999999);
      basicResult.setMessage(error.getMessage());
      String jsonError = convertBasicResultToJson(basicResult);
      mFragment.onFinishedResponse(jsonError);
    }

    try {
      final TransactionObject transaction = mStoneHelper.getTransactionObject("1", 3, 1, false);
      final PosTransactionProvider provider = new PosTransactionProvider(context, transaction, userModel.get(0));
      provider.execute();
    } catch(Exception e) {}
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
       basicResult.setMessage(mStoneHelper.getMessageFromResponseCodeEnum(provider.getResponseCodeEnum()));
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

  public void checkStatusWithErrorTransaction(TransactionStatusEnum status, Context context){
    if(status == null) return;
    if(status == TransactionStatusEnum.WITH_ERROR){
      onReversalTransaction(context);
    }

  }

  public void onReversalTransaction(Context context) {
    BasicResult basicResult = new BasicResult();
    basicResult.setMethod("reversal");

    try {
      ReversalProvider reversalProvider = new ReversalProvider(context);
      mFragment.onMessage("Cancelando transação com erro");
      reversalProvider.setDialogMessage("Cancelando transação com erro");
      reversalProvider.isDefaultUI();
      reversalProvider.setConnectionCallback(new StoneCallbackInterface() {
        @Override
        public void onSuccess() {
          ResponseCodeEnum responseCodeEnum = reversalProvider.getResponseCodeEnum();
          basicResult.setResult(0);
          basicResult.setMessage(mStoneHelper.getMessageFromResponseCodeEnum(responseCodeEnum));

          if(responseCodeEnum == ResponseCodeEnum.Approved){
            mFragment.onMessage("Transação aprovada");
            mFragment.onTransactionSuccess();

            mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));
            return;
          }
          mFragment.onMessage("Transação concluída");
          mFragment.onTransactionSuccess();
          mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));
        }

        @Override
        public void onError() {
          ResponseCodeEnum responseCodeEnum = reversalProvider.getResponseCodeEnum();
          mFragment.onMessage("Erro processar transação " + mStoneHelper.getMessageFromResponseCodeEnum(responseCodeEnum));
          basicResult.setResult(999999);
          basicResult.setErrorMessage(mStoneHelper.getMessageFromResponseCodeEnum(responseCodeEnum));

          mFragment.onError(convertBasicResultToJson(basicResult));
        }
      });
      reversalProvider.execute();
    } catch (Exception error) {
      basicResult.setErrorMessage(error.getMessage());
      mFragment.onError(convertBasicResultToJson(basicResult));
      mFragment.onMessage("Erro ao tentar reverter transação");
    }
  }
}
