package com.accesys.stone_smart_flutter.printer;


import android.content.Context;

import com.accesys.stone_smart_flutter.core.BasicResult;
import com.accesys.stone_smart_flutter.payments.PaymentsFragment;
import com.google.gson.Gson;

import br.com.stone.posandroid.providers.PosPrintReceiptProvider;
import io.flutter.plugin.common.MethodChannel;
import stone.application.enums.ReceiptType;
import stone.application.interfaces.StoneCallbackInterface;
import stone.database.transaction.TransactionObject;

public class StonePrinter {
    private PaymentsFragment mFragment;
    private Gson gson = null;


    public StonePrinter(MethodChannel channel) {
        this.mFragment = new PaymentsFragment(channel);
    }

    private Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    private String convertBasicResultToJson(BasicResult basicResult) {
        return getGson().toJson(basicResult);
    }

    public void printerFromTransaction(Context context, TransactionObject transactionObject) {
        BasicResult basicResult = new BasicResult();
        basicResult.setMethod("printer");
        try {
            PosPrintReceiptProvider posPrintReceiptProvider = new PosPrintReceiptProvider(context, transactionObject, ReceiptType.CLIENT);
            posPrintReceiptProvider.setConnectionCallback(new StoneCallbackInterface() {
                @Override
                public void onSuccess() {
                    mFragment.onMessage("Impressão realizada com sucesso");
                    basicResult.setResult(0);
                    basicResult.setMessage("Impressão finalizada com sucesso");
                    mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));
                }

                @Override
                public void onError() {
                    basicResult.setResult(999999);
                    basicResult.setErrorMessage(posPrintReceiptProvider.getListOfErrors().toString());
                    mFragment.onMessage("Erro ao realizar impressão");
                    mFragment.onError(convertBasicResultToJson(basicResult));
                }
            });
            posPrintReceiptProvider.execute();
        } catch (Exception error) {
            basicResult.setResult(999999);
            basicResult.setErrorMessage(error.getMessage());
            mFragment.onMessage("Erro ao realizar impressão");
            mFragment.onError(convertBasicResultToJson(basicResult));
        }
    }
}
