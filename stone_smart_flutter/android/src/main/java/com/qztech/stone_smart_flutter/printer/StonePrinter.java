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

    private String convertBasicResultToJson(BasicResult basicResult) {
        return getGson().toJson(basicResult);
    }

    public void printerFromTransaction(Context context, TransactionObject transactionObject) {
        // BasicResult basicResult = new BasicResult();
        // basicResult.setMethod("printer");
        try {
            PosPrintReceiptProvider posPrintReceiptProvider = new PosPrintReceiptProvider(context, transactionObject, ReceiptType.CLIENT);
            posPrintReceiptProvider.setConnectionCallback(new StoneCallbackInterface() {
                @Override
                public void onSuccess() {
                    // mFragment.onMessage("Impressão realizada com sucesso");
                    // basicResult.setResult(0);
                    // basicResult.setMessage("Impressão finalizada com sucesso");
                    // mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));
                }

                @Override
                public void onError() {
                    // basicResult.setResult(999999);
                    // basicResult.setErrorMessage(posPrintReceiptProvider.getListOfErrors().toString());
                    // mFragment.onMessage("Erro ao realizar impressão");
                    // mFragment.onError(convertBasicResultToJson(basicResult));
                }
            });
            posPrintReceiptProvider.execute();
        } catch (Exception error) {
            // basicResult.setResult(999999);
            // basicResult.setErrorMessage(error.getMessage());
            // mFragment.onMessage("Erro ao realizar impressão");
            // mFragment.onError(convertBasicResultToJson(basicResult));
        }
    }

    public void printerFromBase64(String base64, Context context) {
        BasicResult basicResult = new BasicResult();
        basicResult.setMethod("printerFromBase64");
        try {
            PosPrintProvider posPrintProvider = new PosPrintProvider(context);
            posPrintProvider.addBase64Image(base64);

            posPrintProvider.setConnectionCallback(new StoneCallbackInterface() {
                @Override
                public void onSuccess() {
                    basicResult.setResult(0);
                    mFragment.onMessage("Impressão realizada com sucesso");
                    mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));

                }

                @Override
                public void onError() {

                    basicResult.setResult(999999);

                    List<ErrorsEnum> errors = posPrintProvider.getListOfErrors();
                    String error = "Erro ao realizar impressão";
                    if(!errors.isEmpty()) {
                        error = mStoneHelper.getErrorMessageFromErrorEnum(errors.get(0));
                        basicResult.setErrorMessage(error);
                    }else {
                        basicResult.setErrorMessage("Erro ao imprimir: " + posPrintProvider.getListOfErrors());
                    }

                    mFragment.onMessage(error);
                    mFragment.onError(convertBasicResultToJson(basicResult));
                }
            });

            posPrintProvider.execute();
        } catch (Exception error) {
            basicResult.setResult(999999);
            basicResult.setErrorMessage(error.getMessage());
            mFragment.onMessage("Erro ao realizar impressão");
            mFragment.onError(convertBasicResultToJson(basicResult));
        }
    }

    public void customPrinter(String params, Context context) {
        BasicResult basicResult = new BasicResult();
        basicResult.setMethod("customPrinter");
        try {
            Map<String, Object> data = new Gson().fromJson(params, new TypeToken<Map<String, Object>>(){}.getType());
            List<Map<String, Object>> printers = (List<Map<String, Object>>) data.get("printers");


            PosPrintProvider customPosPrintProvider = new PosPrintProvider(context);

            for (Map<String, Object> printer : printers) {
                String key = printer.get("key").toString();
                String value = printer.get("value").toString();
                String type = printer.get("type").toString();

                if(type.equals("text")) {
                    customPosPrintProvider.addLine(key + value);
                }

                if(type.equals("base64")) {
                    String imageBase64 = printer.get("fileBase64").toString();
                    customPosPrintProvider.addBase64Image(imageBase64);
                }
            }

            customPosPrintProvider.setConnectionCallback(new StoneCallbackInterface() {
                @Override
                public void onSuccess() {
                    basicResult.setResult(0);
                    mFragment.onMessage("Impressão realizada com sucesso");
                    mFragment.onFinishedResponse(convertBasicResultToJson(basicResult));
                    
                }
            
                @Override
                public void onError() {
                    basicResult.setResult(999999);

                    List<ErrorsEnum> errors = customPosPrintProvider.getListOfErrors();
                    String error = "Erro ao realizar impressão";
                    if(!errors.isEmpty()) {
                        error = mStoneHelper.getErrorMessageFromErrorEnum(errors.get(0));
                        basicResult.setErrorMessage(error);
                    }else {
                        basicResult.setErrorMessage("Erro ao imprimir: " + customPosPrintProvider.getListOfErrors());
                    }

                    mFragment.onMessage(error);
                    mFragment.onError(convertBasicResultToJson(basicResult));
                }
            });

            customPosPrintProvider.execute();
        } catch (Exception error) {
            basicResult.setResult(999999);
            basicResult.setErrorMessage(error.getMessage());
            mFragment.onMessage("Erro ao realizar impressão");
            mFragment.onError(convertBasicResultToJson(basicResult));
        }
    }
}
