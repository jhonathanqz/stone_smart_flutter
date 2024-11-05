package com.qztech.stone_smart_flutter.printer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.reflect.TypeToken;
import com.qztech.stone_smart_flutter.core.BasicResult;
import com.qztech.stone_smart_flutter.payments.PaymentsFragment;
import com.google.gson.Gson;
import com.qztech.stone_smart_flutter.payments.StoneHelper;

import java.util.List;
import java.util.Map;

import br.com.stone.posandroid.providers.PosPrintProvider;
import br.com.stone.posandroid.providers.PosPrintReceiptProvider;
import io.flutter.plugin.common.MethodChannel;
import stone.application.enums.ErrorsEnum;
import stone.application.enums.ReceiptType;
import stone.application.interfaces.StoneCallbackInterface;
import stone.database.transaction.TransactionObject;
import android.util.Log;

public class StonePrinter {
    private PaymentsFragment mFragment;
    private StoneHelper mStoneHelper;
    private Gson gson = null;

    public StonePrinter(MethodChannel channel) {
        this.mFragment = new PaymentsFragment(channel);
        this.mStoneHelper = new StoneHelper();
    }

    private Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    private String TAG= "StonePrinter";

    private String convertBasicResultToJson(BasicResult basicResult) {
        return getGson().toJson(basicResult);
    }

    public void printerFromTransaction(Context context, TransactionObject transactionObject) {
            BasicResult basicResult = new BasicResult();
            PosPrintReceiptProvider posPrintReceiptProvider = new PosPrintReceiptProvider(context, transactionObject, ReceiptType.CLIENT);
        
        try {
            StoneCallbackInterface callback = getCallback(posPrintReceiptProvider, basicResult, "Impressão realizada com sucesso", "Erro ao imprimir");
            posPrintReceiptProvider.setConnectionCallback(callback);
            posPrintReceiptProvider.execute();
        } catch (Exception error) {
            onPrintError(posPrintReceiptProvider, basicResult, error.getMessage());
        }
    }

    public void printFromBase64(String base64, Context context) {
        BasicResult basicResult = new BasicResult();
        basicResult.setMethod("printFromBase64");
        PosPrintProvider posPrintProvider = new PosPrintProvider(context);

        try {
            posPrintProvider.addBase64Image(base64);
            
            StoneCallbackInterface callback = getCallback(posPrintProvider, basicResult, "Impressão realizada com sucesso", "Erro ao imprimir");
            posPrintProvider.setConnectionCallback(callback);

            posPrintProvider.execute();
        } catch (Exception error) {
            onPrintError(posPrintProvider, basicResult, error.getMessage());
        }
    }

    public void customPrinter(String params, Context context) {
        BasicResult basicResult = new BasicResult();
        PosPrintProvider posPrintProvider = new PosPrintProvider(context);
        basicResult.setMethod("customPrinter");
        try {
            Map<String, Object> data = new Gson().fromJson(params, new TypeToken<Map<String, Object>>(){}.getType());
            List<Map<String, Object>> printers = (List<Map<String, Object>>) data.get("printers");


            for (Map<String, Object> printer : printers) {
                String key = printer.get("key").toString();
                String value = printer.get("value").toString();
                String type = printer.get("type").toString();

                if(type.equals("text")) {
                    posPrintProvider.addLine(key + value);
                }

                if(type.equals("base64")) {
                    String imageBase64 = printer.get("fileBase64").toString();
                    posPrintProvider.addBase64Image(imageBase64);
                }
            }
            StoneCallbackInterface callback = getCallback(posPrintProvider, basicResult, "Impressão realizada com sucesso", "Erro ao imprimir");
            posPrintProvider.setConnectionCallback(callback);
            posPrintProvider.execute();
        } catch (Exception error) {
            onPrintError(posPrintProvider, basicResult, error.getMessage());
        }
    }

    public void printWrapPaper(int lines, Context context) {
        Log.i(TAG, "printWrapPaper");
        BasicResult basicResult = new BasicResult();
        basicResult.setMethod("printWrapPaper");
        PosPrintProvider posPrintProvider = new PosPrintProvider(context);
        
        try {
            for (int i = 0; i < lines; i++) {
                posPrintProvider.addLine("\n");
            }

            StoneCallbackInterface callback = getCallback(posPrintProvider, basicResult, "Pulou linhas com sucesso", "Erro ao pular linhas");
            posPrintProvider.setConnectionCallback(callback);

            posPrintProvider.execute();
        } catch (Exception error) {
            onPrintError(posPrintProvider, basicResult, error.getMessage());
        }
    }

    public StoneCallbackInterface getCallback(PosPrintProvider provider, BasicResult basicResult, String successMessage, String errorMessage) {
        return new StoneCallbackInterface() {
            @Override
            public void onSuccess() {
                onPrintSuccess(basicResult, successMessage);
            }

            @Override
            public void onError() {
                onPrintError(provider, basicResult, errorMessage);
            }
        };
    }

    public void onPrintSuccess(BasicResult basicResult, String message) {
        Log.i(TAG, "Success printWrapPaper");
        basicResult.setResult(0);
        mFragment.onMessage(message);
        mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));
    }

    public void onPrintError(PosPrintProvider provider, BasicResult basicResult, String message) {
        Log.i(TAG, "Error printWrapPaper");
        basicResult.setResult(999999);
        List<ErrorsEnum> errors = provider.getListOfErrors();

        if (!errors.isEmpty()) {
            basicResult.setErrorMessage(mStoneHelper.getErrorMessageFromErrorEnum(errors.get(0)));
        } else {
            basicResult.setErrorMessage("Error: " + provider.getListOfErrors());
        }

        mFragment.onMessage(message);
        mFragment.onError(convertBasicResultToJson(basicResult));
    }

}
