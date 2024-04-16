package com.qztech.stone_smart_flutter.core;

import android.content.Context;
import android.util.Log;


import com.qztech.stone_smart_flutter.payments.PaymentsPresenter;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class StoneSmart {
    final MethodChannel mChannel;

    // FUNCTIONS
    PaymentsPresenter payment;

    // METHODS
    private static final String PAYMENT_DEBIT = "paymentDebit";
    private static final String PAYMENT_CREDIT = "paymentCredit";
    private static final String PAYMENT_VOUCHER = "paymentVoucher";
    private static final String PAYMENT_PIX = "paymentPix";
    private static final String ACTIVE_PINPAD = "paymentActivePinpad";

    private static final String ACTIVE_PINPAD_CREDENTIALS = "paymentActivePinpadCredentials";
    private static final String PAYMENT_ABORT = "paymentAbort";
    private static final String PAYMENT_ABORT_PIX = "paymentAbortPix";
    private static final String PAYMENT_CANCEL_TRANSACTION = "paymentCancelTransaction";

    private static final String PAYMENT_REVERSAL = "paymentReversal";

    private static final String PAYMENT_PRINTER_TRANSACTION = "paymentPrinterTransaction";

    final Context currentContext;

    public StoneSmart(Context context, MethodChannel channel) {
        this.currentContext = context;
        this.mChannel = channel;
    }

    public void initPayment(MethodCall call, MethodChannel.Result result) {
        if (this.payment == null) {
            this.payment = new PaymentsPresenter(this.mChannel);
        }

        if(call.method.equals(PAYMENT_REVERSAL)) {
            this.payment.onReversal(currentContext);
            return;
        }

        if(call.method.equals(PAYMENT_PRINTER_TRANSACTION)) {
            boolean isPrinter = call.argument("isPrinter");
            this.payment.printerCurrentTransaction(currentContext, isPrinter);
            return;
        }

        if(call.method.equals(ACTIVE_PINPAD)) {
            String appName = call.argument("appName");
            String stoneCode = call.argument("stoneCode");
            this.payment.activate(appName, stoneCode, currentContext);
            return;
        }

        if(call.method.equals(ACTIVE_PINPAD_CREDENTIALS)) {
            String appName = call.argument("appName");
            String stoneCode = call.argument("stoneCode");
            String qrCodeAuthotization = call.argument("qrCodeAuthorization");
            String qrCodeProviderid = call.argument("qrCodeProviderid");
            this.payment.activateWithCredentials(appName, stoneCode,qrCodeAuthotization, qrCodeProviderid, currentContext);
            return;
        }

        if(call.method.equals(PAYMENT_ABORT_PIX)){
            this.payment.abortPIXtransaction(currentContext);
            return;
        }

        if(call.method.equals(PAYMENT_ABORT)){
            this.payment.abortCurrentPosTransaction();
            return;
        }

        String amount = call.argument("amount");

        if (call.method.equals(PAYMENT_CANCEL_TRANSACTION)) {
            int typeTransaction = call.argument("typeTransaction");
            this.payment.cancelTransaction(currentContext, amount, typeTransaction);
            return;
        }

        int parc = call.argument("installment");
        boolean withInterest = call.argument("withInterest");

        if (call.method.equals(PAYMENT_DEBIT)) {
            this.payment.doTransaction(currentContext,amount, 2, parc, withInterest, null,null);
        } else if (call.method.equals(PAYMENT_PIX)) {
            String qrCodeAuthotization = call.argument("qrCodeAuthorization");
            String qrCodeProviderid = call.argument("qrCodeProviderid");
            this.payment.doTransaction(currentContext,amount, 3, parc, withInterest, qrCodeAuthotization, qrCodeProviderid);
        } else if (call.method.equals(PAYMENT_CREDIT)) {
            this.payment.doTransaction(currentContext,amount, 1, parc, withInterest,null,null);
        }  else if (call.method.equals(PAYMENT_VOUCHER)) {
            this.payment.doTransaction(currentContext,amount, 4, parc, withInterest, null,null);
        } else {
            result.notImplemented();
        }
    }

    public void dispose() {
        if (this.payment != null) {
            this.payment.dispose();
        }
    }
}
