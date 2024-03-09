package com.accesys.stone_smart_flutter.printer;


import android.content.Context;

import com.accesys.stone_smart_flutter.payments.PaymentsFragment;

import br.com.stone.posandroid.providers.PosPrintReceiptProvider;
import io.flutter.plugin.common.MethodChannel;
import stone.application.enums.ReceiptType;
import stone.application.interfaces.StoneCallbackInterface;
import stone.database.transaction.TransactionObject;

public class StonePrinter {
    private PaymentsFragment mFragment;

    public StonePrinter(MethodChannel channel) {
        this.mFragment = new PaymentsFragment(channel);
    }

    public void printerFromTransaction(Context context, TransactionObject transactionObject) {
        try {
            PosPrintReceiptProvider posPrintReceiptProvider = new PosPrintReceiptProvider(context, transactionObject, ReceiptType.CLIENT);
            posPrintReceiptProvider.setConnectionCallback(new StoneCallbackInterface() {
                @Override
                public void onSuccess() {
                    mFragment.onMessage("Impressão realizada com sucesso");
                    mFragment.onFinishedResponse("printer", "Impressão finalizada com sucesso");
                }

                @Override
                public void onError() {
                    mFragment.onMessage("Erro ao realizar impressão");
                    mFragment.onError("Printer", posPrintReceiptProvider.getListOfErrors().toString());
                }
            });
            posPrintReceiptProvider.execute();
        } catch (Exception error) {
            mFragment.onMessage("Erro ao realizar impressão");
            mFragment.onError("printer", error.getMessage());
        }
    }
}
