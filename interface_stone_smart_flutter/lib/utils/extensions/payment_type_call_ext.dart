import 'package:interface_stone_smart_flutter/utils/types/payment_type_call.dart';

extension PaymentTypeCallExt on PaymentTypeCall {
  String get method {
    const Map<PaymentTypeCall, String> paymentTypeCallMap = {
      PaymentTypeCall.credit: "paymentcredit",
      PaymentTypeCall.creditParc: "paymentcreditParc",
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
      PaymentTypeCall.getTransactionByInitiatorKey:
          "paymentGetTransactionByInitiatorTransactionKey",
      PaymentTypeCall.getAllTransactions: "paymentGetAllTransactions",
      PaymentTypeCall.customPrinter: "paymentCustomPrinter",
      PaymentTypeCall.printFromBase64: "paymentPrinterBase64",
      PaymentTypeCall.printWrapPaper: "printWrapPaper",
    };
    return paymentTypeCallMap[this] ?? (throw 'Not Implemented');
  }
}
