import 'package:interface_stone_smart_flutter/utils/types/payment_type_call.dart';

extension PaymentTypeCallExt on PaymentTypeCall {
  String get method {
    const Map<PaymentTypeCall, String> paymentTypeCallMap = {
      PaymentTypeCall.credit: "paymentCredit",
      PaymentTypeCall.creditParc: "paymentCreditParc",
      PaymentTypeCall.debit: "paymentDebit",
      PaymentTypeCall.voucher: "paymentVoucher",
      PaymentTypeCall.abort: "paymentAbort",
      PaymentTypeCall.lastTransaction: "paymentLastTransaction",
      PaymentTypeCall.refund: "paymentRefund",
      PaymentTypeCall.activePinpad: "paymentActivePinpad",
      PaymentTypeCall.pinpadAuthenticated: "paymentIsAuthenticated",
      PaymentTypeCall.pix: "paymentPix",
      PaymentTypeCall.cancelTransaction: "paymentCancelTransaction",
      PaymentTypeCall.reversal: "paymentReversal",
      PaymentTypeCall.activePinpadCredentials: "paymentActivePinpadCredentials",
      PaymentTypeCall.printTransaction: "paymentPrinterTransaction",
      PaymentTypeCall.abortPix: "paymentAbortPix",
      PaymentTypeCall.paymentOption: "paymentOptions",
      PaymentTypeCall.getTransactionByInitiatorTransactionKey:
          "paymentGetTransactionByInitiatorTransactionKey",
      PaymentTypeCall.getAllTransactions: "paymentGetAllTransactions",
      PaymentTypeCall.customPrinter: "paymentCustomPrinter",
      PaymentTypeCall.printFromBase64: "paymentPrintBase64",
      PaymentTypeCall.printWrapPaper: "paymentPrintWrapPaper",
      PaymentTypeCall.activeDebugLog: "paymentActiveDebugLog",
    };
    return paymentTypeCallMap[this] ?? (throw 'Not Implemented');
  }
}
