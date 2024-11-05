import 'package:interface_stone_smart_flutter/utils/types/payment_type_handler.dart';

extension PaymentTypeHandlerExt on PaymentTypeHandler {
  String get method {
    Map<PaymentTypeHandler, String> paymentTypeToStringMap = {
      PaymentTypeHandler.onTransactionSuccess: "onTransactionSuccess",
      PaymentTypeHandler.onError: "onError",
      PaymentTypeHandler.onMessage: "onMessage",
      PaymentTypeHandler.onLoading: "onLoading",
      PaymentTypeHandler.writeToFile: "writeToFile",
      PaymentTypeHandler.onAbortedSuccessfully: "onAbortedSuccessfully",
      PaymentTypeHandler.disposeDialog: "disposeDialog",
      PaymentTypeHandler.activeDialog: "activeDialog",
      PaymentTypeHandler.onAuthProgress: "onAuthProgress",
      PaymentTypeHandler.onTransactionInfo: "onTransactionInfo",
      PaymentTypeHandler.onFinishedResponse: "onFinishedResponse",
      PaymentTypeHandler.onChanged: "onChanged",
    };

    return paymentTypeToStringMap[this] ?? (throw "Not Implemented");
  }
}
