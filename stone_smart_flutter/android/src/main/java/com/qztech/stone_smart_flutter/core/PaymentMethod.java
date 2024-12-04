package com.qztech.stone_smart_flutter.core;

enum PaymentMethod {
    PAYMENT_DEBIT("paymentDebit"),
    PAYMENT_CREDIT("paymentCredit"),
    PAYMENT_CREDIT_PARC("paymentCreditParc"),
    PAYMENT_VOUCHER("paymentVoucher"),
    PAYMENT_PIX("paymentPix"),
    ACTIVE_PINPAD("paymentActivePinpad"),
    ACTIVE_PINPAD_CREDENTIALS("paymentActivePinpadCredentials"),
    PAYMENT_ABORT("paymentAbort"),
    PAYMENT_ABORT_PIX("paymentAbortPix"),
    PAYMENT_CANCEL_TRANSACTION("paymentCancelTransaction"),
    PAYMENT_REVERSAL("paymentReversal"),
    PAYMENT_OPTIONS("paymentOptions"),
    PAYMENT_PRINTER_TRANSACTION("paymentPrinterTransaction"),
    PAYMENT_GET_TRANSACTION_BY_INITIATOR_TRANSACTION_KEY("paymentGetTransactionByInitiatorTransactionKey"),
    PAYMENT_GET_ALL_TRANSACTIONS("paymentGetAllTransactions"),
    PAYMENT_CUSTOM_PRINTER("paymentCustomPrinter"),
    PAYMENT_PRINT_BASE64("paymentPrintBase64"),
    PAYMENT_PRINT_WRAP_PAPER("paymentPrintWrapPaper"),
    PAYMENT_ACTIVE_DEBUG_LOG("paymentActiveDebugLog"),
    PAYMENT_GET_SERIAL_NUMBER("paymentGetSerialNumber"),
    PAYMENT_GET_MANUFACTURE("paymentGetManufacture");


    private final String method;

    PaymentMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public static PaymentMethod fromString(String method) {
        for (PaymentMethod pm : PaymentMethod.values()) {
            if (pm.getMethod().equals(method)) {
                return pm;
            }
        }
        return null;
    }
}