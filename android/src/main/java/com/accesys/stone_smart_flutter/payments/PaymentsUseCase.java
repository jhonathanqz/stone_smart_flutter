package com.accesys.stone_smart_flutter.payments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.accesys.stone_smart_flutter.core.ActionResult;
import com.accesys.stone_smart_flutter.printer.StonePrinter;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

import br.com.stone.posandroid.providers.PosTransactionProvider;
import stone.application.StoneStart;
import stone.application.enums.Action;
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

public class PaymentsUseCase {

  private PaymentsFragment mFragment;
  private StoneHelper mStoneHelper;
  private StonePrinter mStonePrinter;
  private TransactionObject currentTransactionObject;
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

  public void transaction(
          Context context,
          String amount,
          int typeTransaction,
          int parc,
          boolean withInterest
  ) {
    try {
      mFragment.onMessage("Iniciando transação");
      final TransactionObject transaction = mStoneHelper.getTransactionObject(amount, typeTransaction, parc, withInterest);
      currentTransactionObject = transaction;

      UserModel currentUser = Stone.getUserModel(0);
      final PosTransactionProvider provider = new PosTransactionProvider(context, transaction, currentUser);
      ActionResult actionResult = new ActionResult();

      mFragment.onMessage("Comunicando com o servidor Stone. Aguarde.");

      provider.setConnectionCallback(new StoneActionCallback() {
        @Override
        public void onSuccess() {
          TransactionDAO transactionDAO = new TransactionDAO(context);
          List<TransactionObject> transactionObjects = transactionDAO.getAllTransactionsOrderByIdDesc();
          Log.d("print", "*** STONE -> transaction onSuccess: " + transactionObjects.get(0));
          actionResult.buildResponseStoneTransaction(transactionObjects);
          String jsonStoneResult = getGson().toJson(actionResult);
          finishTransaction(jsonStoneResult);
          mStonePrinter.printerFromTransaction(context, transaction);
          //alertPrinter(context, jsonStoneResult, transaction);

        }
        @Override
        public void onStatusChanged(Action action) {
          Log.d("print", "*** STONE -> onStatusChanged: " + action.toString());
          String actionMessage = mStoneHelper.getMessageFromTransactionAction(action);
          Log.d("print", "*** STONE -> textoOnStatusChanged: " + actionMessage);
          mFragment.onMessage(actionMessage);
          if (action == Action.TRANSACTION_WAITING_QRCODE_SCAN) {
            mFragment.onAuthProgress("transaction", mStoneHelper.convertBitmapToString(transaction.getQRCode()));
          }
          currentTransactionObject = null;

        }
        @Override
        public void onError() {
          Log.d("print", "*** STONE -> transaction onError2: " + provider.getListOfErrors().toString());
          mFragment.onMessage("Erro ao realizar transação");
          mFragment.onError("transaction", provider.getListOfErrors().toString());
          currentTransactionObject = null;
        }

      });
      provider.execute();
    } catch (Exception e) {
      Log.d("print", "*** STONE -> Error_catch1: " + e.toString());
      Log.d("print", "*** STONE -> Error_catch2: " + e.getMessage());
      mFragment.onError("transaction", e.getMessage());
      currentTransactionObject = null;
    }
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

    mFragment.onMessage("Transação aprovada");
    mFragment.onTransactionSuccess();
    mFragment.onFinishedResponse("transaction", result);
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
    List<UserModel> userList = StoneStart.init(context);
      ActionResult actionResult = new ActionResult();
      Log.d("print", "*** STONE -> ativação 2");
      if (userList == null) {
        Log.d("print", "*** STONE -> ativação 3");
        ActiveApplicationProvider activeApplicationProvider = getActiveApplicationProvider(context);
        activeApplicationProvider.activate(stoneCode);

      } else {
        Log.d("print", "*** STONE -> ativação 5");
        mFragment.onMessage("Terminal ativado");
        mFragment.onAuthProgress("active", "Terminal ativado");
      }
  }

  public void cancelCurrentTransaction(Context context, String amount) {
    try {
      if(currentTransactionObject == null) {
        mFragment.onError("cancel", "Nenhuma transação em andamento");
        return;
      }
      String currentAmount = currentTransactionObject.getAmount();
      if(!Objects.equals(currentAmount, amount)) {
        mFragment.onError("cancel", "Valor da transação difere da transação atual");
        return;
      }

      final CancellationProvider provider = getCancellationProvider(context, currentTransactionObject);
      provider.execute();
    } catch (Exception error) {
      mFragment.onMessage("Erro ao cancelar transação");
      mFragment.onError("cancel", error.getMessage());
    }

  }

  public void cancelTransaction(Context context, String amount, int typeTransaction) {
    try {
      final TransactionObject transaction = mStoneHelper.getTransactionObject(amount, typeTransaction, 1, false);
      final CancellationProvider cancellationProvider = getCancellationProvider(context, transaction);
      cancellationProvider.execute();
    } catch (Exception error) {
      mFragment.onMessage("Erro ao cancelar transação");
      mFragment.onError("cancel", error.getMessage());
    }
  }

  @NonNull
  private CancellationProvider getCancellationProvider(Context context, TransactionObject transaction) {
    mFragment.onMessage("Obtendo dados da transação");
    final CancellationProvider provider = new CancellationProvider(context, transaction);
    mFragment.onMessage("Processando cancelamento");

    provider.setConnectionCallback(new StoneCallbackInterface() {
      @Override
      public void onSuccess() {
       mFragment.onMessage("Transação cancelada");
       mFragment.onTransactionSuccess();
       mFragment.onAuthProgress("cancel", "Transação cancelada");
       mFragment.onFinishedResponse("cancel", mStoneHelper.getMessageFromResponseCodeEnum(provider.getResponseCodeEnum()));
      }
      @Override
      public void onError() {
        String error = currentTransactionObject.getActionCode();
        mFragment.onMessage("Erro ao cancelar transação");
        mFragment.onAuthProgress("cancel", error);
        mFragment.onError("cancel", provider.getListOfErrors().toString());
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
    mFragment.onMessage("Conectando com terminal");
    activeApplicationProvider.setConnectionCallback(new StoneCallbackInterface() {
      @Override
      public void onSuccess() {
        Log.d("print", "*** STONE -> ativado com sucesso");
        mFragment.onMessage("Terminal ativado");
        mFragment.onTransactionSuccess();
        mFragment.onAuthProgress("active", "Terminal ativado");
        mFragment.onFinishedResponse("active", "Terminal ativado");
      }

      @Override
      public void onError() {
        Log.d("print", "*** STONE -> ativação onError");
        mFragment.onMessage("Erro ao ativar terminal");
        mFragment.onError("active", activeApplicationProvider.getListOfErrors().toString());
      }
    });
    return activeApplicationProvider;
  }

  public void onReversalTransaction(Context context) {
    try {
      ReversalProvider reversalProvider = new ReversalProvider(context);
      mFragment.onMessage("Cancelando transação com erro");
      reversalProvider.setDialogMessage("Cancelando transação com erro");
      reversalProvider.isDefaultUI();
      reversalProvider.setConnectionCallback(new StoneCallbackInterface() {
        @Override
        public void onSuccess() {
          ResponseCodeEnum responseCodeEnum = reversalProvider.getResponseCodeEnum();
          if(responseCodeEnum == ResponseCodeEnum.Approved){
            mFragment.onMessage("Transação aprovada");
            mFragment.onTransactionSuccess();
            mFragment.onFinishedResponse("reversal", mStoneHelper.getMessageFromResponseCodeEnum(responseCodeEnum));
            return;
          }
          mFragment.onMessage("Transação concluída");
          mFragment.onTransactionSuccess();
          mFragment.onFinishedResponse("reversal", mStoneHelper.getMessageFromResponseCodeEnum(responseCodeEnum));
        }

        @Override
        public void onError() {
          ResponseCodeEnum responseCodeEnum = reversalProvider.getResponseCodeEnum();
          mFragment.onMessage("Erro processar transação " + mStoneHelper.getMessageFromResponseCodeEnum(responseCodeEnum));
          mFragment.onError("reversal", reversalProvider.getListOfErrors().toString());
        }
      });
      reversalProvider.execute();
    } catch (Exception error) {
      mFragment.onError("reversal", error.getMessage());
      mFragment.onMessage("Erro ao tentar reverter transação");
    }
  }


}
