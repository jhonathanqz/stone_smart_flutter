package com.qztech.stone_smart_flutter.core;
import android.content.Context;
import android.util.Log;
import com.qztech.stone_smart_flutter.payments.PaymentsPresenter;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;


public class StoneSmart {
    final MethodChannel mChannel;

    PaymentsPresenter payment;

    final Context currentContext;
    private String TAG = "StoneSmart";
    private boolean isDebugLogActive = false;


    public StoneSmart(Context context, MethodChannel channel) {
        this.currentContext = context;
        this.mChannel = channel;
    }

    public void initPayment(MethodCall call, MethodChannel.Result result) {
        if (this.payment == null) {
            this.payment = new PaymentsPresenter(this.mChannel, isDebugLogActive);
        }

        Log.i(TAG, "Call Method: " + call.method);
        PaymentMethod paymentMethod = PaymentMethod.fromString(call.method);
        if (paymentMethod == null) {
            result.notImplemented();
            return;
        }

        switch (paymentMethod) {
            case PAYMENT_ACTIVE_DEBUG_LOG:
                boolean isDebugLogParams = call.argument("isDebugLog");
                isDebugLogActive = isDebugLogParams;
                this.payment = new PaymentsPresenter(this.mChannel, isDebugLogActive);
                break;
            case PAYMENT_GET_SERIAL_NUMBER:
                String serialNumber = this.payment.getPosSerialNumber();
                result.success(serialNumber);
                break;
            case PAYMENT_GET_MANUFACTURE:
                String posManufacture = this.payment.getPosManufacture();
                result.success(posManufacture);
                break;
            case PAYMENT_CUSTOM_PRINTER:
                String printerParams = call.argument("printerParams");
                this.payment.customPrinter(printerParams, currentContext);
                break;

            case PAYMENT_PRINT_BASE64:
                String base64PrinterParams = call.argument("printerParams");
                this.payment.printFromBase64(base64PrinterParams, currentContext);
                break;

            case PAYMENT_PRINT_WRAP_PAPER:
                int lines = call.argument("lines");
                this.payment.printWrapPaper(lines, currentContext);
                break;

            case PAYMENT_GET_ALL_TRANSACTIONS:
                this.payment.getAllTransactions(currentContext);
                break;

            case PAYMENT_REVERSAL:
                this.payment.onReversal(currentContext);
                break;

            case PAYMENT_GET_TRANSACTION_BY_INITIATOR_TRANSACTION_KEY:
                String initiatorTransactionKey = call.argument("InitiatorTransactionKey");
                this.payment.getTransactionByInitiatorTransactionKey(currentContext, initiatorTransactionKey);
                break;

            case PAYMENT_PRINTER_TRANSACTION:
                boolean printCustomerSlip = call.argument("printCustomerSlip");
                this.payment.printerCurrentTransaction(currentContext, printCustomerSlip);
                break;

            case PAYMENT_PRINTER_TRANSACTION_KEY:
                String transactionKey = call.argument("paymentPrinterTransactionKey");
                this.payment.printerFromTransactionKey(currentContext, transactionKey);
                break;

            case ACTIVE_PINPAD:
                String appName = call.argument("appName");
                String stoneCode = call.argument("stoneCode");
                this.payment.activate(appName, stoneCode, currentContext);
                break;

            case ACTIVE_PINPAD_CREDENTIALS:
                this.payment.activateWithCredentials(
                    call.argument("appName"),
                    call.argument("stoneCode"),
                    call.argument("qrCodeAuthorization"),
                    call.argument("qrCodeProviderid"),
                    currentContext
                );
                break;

            case PAYMENT_ABORT_PIX:
                this.payment.abortPIXtransaction(currentContext);
                break;

            case PAYMENT_ABORT:
                this.payment.abortCurrentPosTransaction();
                break;

            case PAYMENT_OPTIONS:
                this.payment.setPaymentOption(call.argument("option"));
                break;

            case PAYMENT_CANCEL_TRANSACTION:
                int idFromBase = call.argument("idFromBase");
                this.payment.cancelTransaction(currentContext, idFromBase);
                break;

            default:
                String amount = call.argument("amount");
                int parc = call.argument("installment");
                boolean withInterest = call.argument("withInterest");
                boolean withCustomerSlip = call.argument("printCustomerSlip");
                boolean withEstablishmentSlip = call.argument("printEstablishmentSlip");
                String initiatorKey = call.argument("initiatorTransactionKey");

                switch (paymentMethod) {
                    case PAYMENT_DEBIT:
                        this.payment.doTransaction(currentContext, amount, 2, initiatorKey, parc, withInterest, null, null, withCustomerSlip, withEstablishmentSlip);
                        break;

                    case PAYMENT_PIX:
                        String withQrCodeAuthorization = call.argument("qrCodeAuthorization");
                        String withQrCodeProviderid = call.argument("qrCodeProviderid");
                        this.payment.doTransaction(currentContext, amount, 3, initiatorKey, parc, withInterest,
                                withQrCodeAuthorization, withQrCodeProviderid, withCustomerSlip, withEstablishmentSlip);
                        break;

                    case PAYMENT_CREDIT:
                    case PAYMENT_CREDIT_PARC:
                        this.payment.doTransaction(currentContext, amount, 1, initiatorKey, parc, withInterest, null, null, withCustomerSlip, withEstablishmentSlip);
                        break;

                    case PAYMENT_VOUCHER:
                        this.payment.doTransaction(currentContext, amount, 4, initiatorKey, parc, withInterest, null, null, withCustomerSlip, withEstablishmentSlip);
                        break;

                    default:
                        result.notImplemented();
                        break;
                }
                break;
        }
    }

    public void dispose() {
        if (this.payment != null) {
            this.payment.dispose();
        }
    }
}
