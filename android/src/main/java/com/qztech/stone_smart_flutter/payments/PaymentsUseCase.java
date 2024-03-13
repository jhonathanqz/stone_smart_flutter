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

import br.com.stone.posandroid.providers.PosTransactionProvider;
import stone.application.StoneStart;
import stone.application.enums.Action;
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
                              Map<StoneKeyType, String> stoneKeys
                              ){
    if(stoneKeys == null){
      checkUserModel(context);
      transaction(context, amount, typeTransaction, parc, withInterest);
      return;
    }
    userModel = StoneStart.init(context, stoneKeys);
    transaction(context, amount, typeTransaction, parc, withInterest);
  }

  private void checkUserModel(Context context) {
    if(userModel == null) {
      userModel = StoneStart.init(context);
    }

    Log.d("print", "*** STONE -> userModel: " + (userModel != null ? userModel.get(0).toString() : null));
  }

  public void transaction(
          Context context,
          String amount,
          int typeTransaction,
          int parc,
          boolean withInterest
  ) {
    BasicResult basicResult = new BasicResult();
    basicResult.setMethod("transaction");
    try {
      mFragment.onMessage("Iniciando transação");
      checkUserModel(context);
      final TransactionObject transaction = mStoneHelper.getTransactionObject(amount, typeTransaction, parc, withInterest);
      currentTransactionObject = transaction;

      final PosTransactionProvider provider = new PosTransactionProvider(context, transaction, userModel.get(0));
      posTransactionProvider = provider;

      ActionResult actionResult = new ActionResult();
      actionResult.setMethod("transaction");

      mFragment.onMessage("Comunicando com o servidor Stone. Aguarde.");

      provider.setConnectionCallback(new StoneActionCallback() {
        @Override
        public void onSuccess() {
          TransactionDAO transactionDAO = new TransactionDAO(context);
          List<TransactionObject> transactionObjects = transactionDAO.getAllTransactionsOrderByIdDesc();
          checkStatusWithErrorTransaction(provider.getTransactionStatus(), context);
          Log.d("print", "*** STONE -> transaction onSuccess: " + transactionObjects.get(0));
          actionResult.setTransactionStatus(provider.getTransactionStatus().toString());
          actionResult.setMessageFromAuthorize(provider.getMessageFromAuthorize());
          actionResult.setAuthorizationCode(provider.getAuthorizationCode());

          //mFragment.onMessage(provider.getMessageFromAuthorize());

          actionResult.buildResponseStoneTransaction(transactionObjects);
          String jsonStoneResult = convertActionToJson(actionResult);
          finishTransaction(jsonStoneResult);
          mStonePrinter.printerFromTransaction(context, transaction);
          currentTransactionObject = null;
          posTransactionProvider = null;
          //alertPrinter(context, jsonStoneResult, transaction);
        }
        @Override
        public void onStatusChanged(Action action) {
          Log.d("print", "*** STONE -> onStatusChanged: " + action.toString());
          String actionMessage = mStoneHelper.getMessageFromTransactionAction(action);
          Log.d("print", "*** STONE -> textoOnStatusChanged: " + actionMessage);
          mFragment.onMessage(actionMessage);
          if (action == Action.TRANSACTION_WAITING_QRCODE_SCAN) {
            basicResult.setMethod("QRCode");
            //basicResult.setQrCodeBitmap(transaction.getQRCode());
            basicResult.setMessage(mStoneHelper.convertBitmapToString(transaction.getQRCode()));
            mFragment.onChanged(convertBasicResultToJson(basicResult));
            mFragment.onAuthProgress(convertBasicResultToJson(basicResult));
          }
        }
        @Override
        public void onError() {
          Log.d("print", "*** STONE -> transaction onError2: " + mStoneHelper.getErrorFromErrorList(provider.getListOfErrors()));
          mFragment.onMessage("Erro ao realizar transação");

          checkStatusWithErrorTransaction(provider.getTransactionStatus(), context);

          actionResult.setTransactionStatus(provider.getTransactionStatus().toString());
          actionResult.setMessageFromAuthorize(provider.getMessageFromAuthorize());
          actionResult.setAuthorizationCode(provider.getAuthorizationCode());
          actionResult.setResult(999999);
          actionResult.setErrorMessage(mStoneHelper.getErrorFromErrorList(provider.getListOfErrors()));

          mFragment.onMessage(provider.getMessageFromAuthorize());

          basicResult.setResult(999999);
          basicResult.setErrorMessage(mStoneHelper.getErrorFromErrorList(provider.getListOfErrors()));

          mFragment.onError(convertBasicResultToJson(basicResult));
          String jsonError = convertActionToJson(actionResult);
          mFragment.onFinishedResponse(jsonError);
          currentTransactionObject = null;
          posTransactionProvider= null;
        }

      });
      provider.execute();
    } catch (Exception e) {
      Log.d("print", "*** STONE -> Error_catch1: " + e.toString());
      Log.d("print", "*** STONE -> Error_catch2: " + e.getMessage());
      basicResult.setResult(999999);
      basicResult.setErrorMessage(e.getMessage());

      mFragment.onError(convertBasicResultToJson(basicResult));
      currentTransactionObject = null;
      posTransactionProvider = null;
    }
  }

  public void abortCurrentPosTransaction() {
    Log.d("print", "*** STONE -> Entrei no abortPosTransaction");
    if(posTransactionProvider == null){
      Log.d("print", "*** STONE -> abortPosTransaction é NULL");
      return;
    }
    try {
      posTransactionProvider.abortPayment();
      Log.d("print", "*** STONE -> EXECUTEI abortPosTransaction");
    } catch (Exception error){
      Log.d("print", "*** STONE -> ERRO CATCH no abortPosTransaction: " + error.getMessage());
  }
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
    currentTransactionObject = null;
  }


  public void initializeAndActivatePinpad(
          String appName,
          String stoneCode,
          Context context
  ) {
    Log.d("print", "*** STONE -> iniciando ativação PINPAD");
    Log.d("print", "*** STONE -> appName: " + appName);
    Log.d("print", "*** STONE -> stoneCode: " + stoneCode);
    mFragment.onMessage("Iniciando ativação");
    Stone.setAppName(appName);
    checkUserModel(context);
      BasicResult basicResult = new BasicResult();
      Log.d("print", "*** STONE -> ativação 2");
      if (userModel == null) {
        Log.d("print", "*** STONE -> ativação 3");
        ActiveApplicationProvider activeApplicationProvider = getActiveApplicationProvider(context);
        activeApplicationProvider.activate(stoneCode);

      } else {
        Log.d("print", "*** STONE -> ativação 5");
        mFragment.onMessage("Terminal ativado");

        basicResult.setMethod("active");
        basicResult.setResult(0);
        basicResult.setMessage("Terminal ativado");
        String resultJson = convertBasicResultToJson(basicResult);
        mFragment.onFinishedResponse(resultJson);
        mFragment.onAuthProgress(resultJson);
      }
  }

  public void initializeAndActivatePinpadWithCredentials(
          String appName,
          String stoneCode,
          Map<StoneKeyType, String> stoneKeys,
          Context context
  ) {
    Log.d("print", "*** STONE -> iniciando ativação PINPAD");
    Log.d("print", "*** STONE -> appName: " + appName);
    Log.d("print", "*** STONE -> stoneCode: " + stoneCode);
    mFragment.onMessage("Iniciando ativação");
    Stone.setAppName(appName);
    List<UserModel> userList = StoneStart.init(context, stoneKeys);
    userModel = userList;
    BasicResult basicResult = new BasicResult();
    Log.d("print", "*** STONE -> ativação 2");
    if (userList == null) {
      Log.d("print", "*** STONE -> ativação 3");
      ActiveApplicationProvider activeApplicationProvider = getActiveApplicationProvider(context);
      activeApplicationProvider.activate(stoneCode);

    } else {
      Log.d("print", "*** STONE -> ativação 5");
      mFragment.onMessage("Terminal ativado");

      basicResult.setMethod("active");
      basicResult.setResult(0);
      basicResult.setMessage("Terminal ativado");
      String resultJson = convertBasicResultToJson(basicResult);
      mFragment.onFinishedResponse(resultJson);
      mFragment.onAuthProgress(resultJson);
    }
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
        Log.d("print", "*** STONE -> ABORT_onError: " + mStoneHelper.getErrorFromErrorList(provider.getListOfErrors()));
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
    Log.d("print", "*** STONE -> ENTREI ATIVAÇÃO STONE");
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
        Log.d("print", "*** STONE -> ativado com sucesso");
        mFragment.onMessage("Terminal ativado");
        mFragment.onTransactionSuccess();
        basicResult.setResult(0);
        basicResult.setMessage("Terminal ativado");

        mFragment.onAuthProgress(convertBasicResultToJson(basicResult));
        mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));
      }

      @Override
      public void onError() {
        Log.d("print", "*** STONE -> ativação onError");
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
    Log.d("print", "*** STONE -> Entrei no onReversal");

    try {
      ReversalProvider reversalProvider = new ReversalProvider(context);
      mFragment.onMessage("Cancelando transação com erro");
      reversalProvider.setDialogMessage("Cancelando transação com erro");
      reversalProvider.isDefaultUI();
      Log.d("print", "*** STONE -> Executando o onReversal");
      reversalProvider.setConnectionCallback(new StoneCallbackInterface() {
        @Override
        public void onSuccess() {
          ResponseCodeEnum responseCodeEnum = reversalProvider.getResponseCodeEnum();
          Log.d("print", "*** STONE -> onSuccess onReversal: " + mStoneHelper.getMessageFromResponseCodeEnum(responseCodeEnum));
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
          Log.d("print", "*** STONE -> onError no onReversal: " + mStoneHelper.getMessageFromResponseCodeEnum(responseCodeEnum));
          mFragment.onMessage("Erro processar transação " + mStoneHelper.getMessageFromResponseCodeEnum(responseCodeEnum));
          basicResult.setResult(999999);
          basicResult.setErrorMessage(mStoneHelper.getMessageFromResponseCodeEnum(responseCodeEnum));

          mFragment.onError(convertBasicResultToJson(basicResult));
        }
      });
      reversalProvider.execute();
    } catch (Exception error) {
      Log.d("print", "*** STONE -> Catch no onReversal: " + error.getMessage());
      basicResult.setErrorMessage(error.getMessage());
      mFragment.onError(convertBasicResultToJson(basicResult));
      mFragment.onMessage("Erro ao tentar reverter transação");
    }
  }


}
