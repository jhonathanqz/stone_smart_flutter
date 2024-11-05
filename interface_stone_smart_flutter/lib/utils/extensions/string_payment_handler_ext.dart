import 'package:interface_stone_smart_flutter/utils/types/payment_type_handler.dart';

extension StringPaymentHandlerExt on String {
  PaymentTypeHandler get handler {
    Map<String, PaymentTypeHandler> paymentTypeMap = {
      "onTransactionSuccess": PaymentTypeHandler.onTransactionSuccess,
      "onError": PaymentTypeHandler.onError,
      "onMessage": PaymentTypeHandler.onMessage,
      "onFinishedResponse": PaymentTypeHandler.onFinishedResponse,
      "onLoading": PaymentTypeHandler.onLoading,
      "writeToFile": PaymentTypeHandler.writeToFile,
      "onAbortedSuccessfully": PaymentTypeHandler.onAbortedSuccessfully,
      "disposeDialog": PaymentTypeHandler.disposeDialog,
      "activeDialog": PaymentTypeHandler.activeDialog,
      "onAuthProgress": PaymentTypeHandler.onAuthProgress,
      "onTransactionInfo": PaymentTypeHandler.onTransactionInfo,
    };

    return paymentTypeMap[this] ?? (throw "Not Implemented");
  }
}
