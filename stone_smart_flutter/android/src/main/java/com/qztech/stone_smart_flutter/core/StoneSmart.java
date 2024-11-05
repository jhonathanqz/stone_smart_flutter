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
    private static final String PAYMENT_CREDIT_PARC = "paymentCreditParc";
    private static final String PAYMENT_VOUCHER = "paymentVoucher";
    private static final String PAYMENT_PIX = "paymentPix";
    private static final String ACTIVE_PINPAD = "paymentActivePinpad";

    private static final String ACTIVE_PINPAD_CREDENTIALS = "paymentActivePinpadCredentials";
    private static final String PAYMENT_ABORT = "paymentAbort";
    private static final String PAYMENT_ABORT_PIX = "paymentAbortPix";
    private static final String PAYMENT_CANCEL_TRANSACTION = "paymentCancelTransaction";

    private static final String PAYMENT_REVERSAL = "paymentReversal";
    private static final String PAYMENT_OPTIONS = "paymentOptions";
    private static final String PAYMENT_PRINT_TRANSACTION = "paymentPrinterTransaction";
    private static final String PAYMENT_GET_TRANSACTION_BY_INITIATOR_TRANSACTION_KEY = "paymentGetTransactionByInitiatorTransactionKey";

    private static final String PAYMENT_GET_ALL_TRANSACTIONS = "paymentGetAllTransactions";

    private static final String PAYMENT_CUSTOM_PRINTER = "paymentCustomPrinter";

    private static final String PAYMENT_PRINTER_BASE64 = "paymentPrinterBase64";
    final Context currentContext;

    public StoneSmart(Context context, MethodChannel channel) {
        this.currentContext = context;
        this.mChannel = channel;
    }

    public void initPayment(MethodCall call, MethodChannel.Result result) {
        if (this.payment == null) {
            this.payment = new PaymentsPresenter(this.mChannel);
        }

        if (call.method.equals(PAYMENT_CUSTOM_PRINTER)) {
            String printerParams = call.argument("printerParams");
            this.payment.customPrinter(printerParams, currentContext);
            return;
        }

        if (call.method.equals(PAYMENT_PRINTER_BASE64)) {
            String printerParams = call.argument("printerParams");
            this.payment.printFromBase64(printerParams, currentContext);
            return;
        }

        if (call.method.equals(PAYMENT_GET_ALL_TRANSACTIONS)) {
            this.payment.getAllTransactions(currentContext);
            return;
        }

        if (call.method.equals(PAYMENT_REVERSAL)) {
            this.payment.onReversal(currentContext);
            return;
        }
        
        if(call.method.equals(PAYMENT_GET_TRANSACTION_BY_INITIATOR_TRANSACTION_KEY)) {
            String initiatorKey = call.argument("initiatorKey");
            this.payment.getTransactionByInitiatorTransactionKey(currentContext, initiatorKey);
            return;
        }

        if(call.method.equals(PAYMENT_PRINT_TRANSACTION)) {
            boolean printCustomerSlip = call.argument("printCustomerSlip");
            this.payment.printerCurrentTransaction(currentContext, printCustomerSlip);
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

        if(call.method.equals(PAYMENT_OPTIONS)) {
            String optionSelected = call.argument("option");
            this.payment.setPaymentOption(optionSelected);
            return;
        }

        if (call.method.equals(PAYMENT_CANCEL_TRANSACTION)) {
            int idFromBase = call.argument("idFromBase");
            this.payment.cancelTransaction(currentContext, idFromBase);
            return;
        }

        String amount = call.argument("amount");

        int parc = call.argument("installment");
        boolean withInterest = call.argument("withInterest");
        boolean printCustomerSlip = call.argument("printCustomerSlip");
        String initiatorKey = call.argument("initiatorKey");

        if (call.method.equals(PAYMENT_DEBIT)) {
            this.payment.doTransaction(currentContext,amount, 2, initiatorKey, parc, withInterest, null,null, printCustomerSlip);
        } else if (call.method.equals(PAYMENT_PIX)) {
            String qrCodeAuthotization = call.argument("qrCodeAuthorization");
            String qrCodeProviderid = call.argument("qrCodeProviderid");
            this.payment.doTransaction(currentContext,amount, 3, initiatorKey, parc, withInterest, qrCodeAuthotization, qrCodeProviderid, printCustomerSlip);
        } else if (call.method.equals(PAYMENT_CREDIT)) {
            this.payment.doTransaction(currentContext,amount, 1, initiatorKey, parc, withInterest,null,null, printCustomerSlip);
        }  else if (call.method.equals(PAYMENT_CREDIT_PARC)) {
            this.payment.doTransaction(currentContext,amount, 1, initiatorKey, parc, withInterest,null,null, printCustomerSlip);
        } else if (call.method.equals(PAYMENT_VOUCHER)) {
            this.payment.doTransaction(currentContext,amount, 4,initiatorKey, parc, withInterest, null,null, printCustomerSlip);
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
