package com.accesys.stone_smart_flutter.core;

import android.content.Context;
import android.util.Log;


import com.accesys.stone_smart_flutter.payments.PaymentsPresenter;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class StoneSmart {
    //final PlugPag plugPag;
    final MethodChannel mChannel;

    // FUNCTIONS
    PaymentsPresenter payment;

    // METHODS
    private static final String PAYMENT_DEBIT = "paymentDebit";
    private static final String PAYMENT_CREDIT = "paymentCredit";
    private static final String PAYMENT_VOUCHER = "paymentVoucher";
    private static final String PAYMENT_PIX = "paymentPix";
    private static final String ACTIVE_PINPAD = "paymentActivePinpad";
    private static final String PAYMENT_ABORT = "paymentAbort";
    private static final String PAYMENT_CANCEL_TRANSACTION = "paymentCancelTransaction";

    final Context currentContext;

    public StoneSmart(Context context, MethodChannel channel) {
        this.currentContext = context;
        this.mChannel = channel;
    }

    public void initPayment(MethodCall call, MethodChannel.Result result) {
        if (this.payment == null) {
            this.payment = new PaymentsPresenter(this.mChannel);
        }

        if(call.method.equals(ACTIVE_PINPAD)) {
            String appName = call.argument("appName");
            String stoneCode = call.argument("stoneCode");
            this.payment.activate(appName, stoneCode, currentContext);
            return;
        }

        String amount = call.argument("amount");
        int parc = call.argument("installment");
        boolean withInterest = call.argument("withInterest");


        if (call.method.equals(PAYMENT_DEBIT)) {
            this.payment.doTransaction(currentContext,amount, 2, parc, withInterest);
        } else if (call.method.equals(PAYMENT_PIX)) {
            this.payment.doTransaction(currentContext,amount, 3, parc, withInterest);
        } else if (call.method.equals(PAYMENT_CREDIT)) {
            this.payment.doTransaction(currentContext,amount, 1, parc, withInterest);
        }  else if (call.method.equals(PAYMENT_VOUCHER)) {
            this.payment.doTransaction(currentContext,amount, 4, parc, withInterest);
        } else if (call.method.equals(PAYMENT_ABORT)) {
            this.payment.cancelCurrentTransaction(currentContext, amount);
        }else if (call.method.equals(PAYMENT_CANCEL_TRANSACTION)) {
            int typeTransaction = call.argument("typeTransaction");
            this.payment.cancelTransaction(currentContext, amount, typeTransaction);
        }else {
            result.notImplemented();
        }
    }

    public void dispose() {
        if (this.payment != null) {
            this.payment.dispose();
        }
    }
}
