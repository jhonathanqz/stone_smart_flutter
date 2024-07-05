import 'package:stone_smart_flutter/payments/handler/istone_handler.dart';

class PaymentController extends IStoneHandler {
  int saleValue = 0;
  bool enable = false;
  bool clickPayment = false;
  bool enableRefund = false;
  String? transactionCode;
  String? transactionId;
  String? response;

  void setSaleValue(double value) {
    if (value > 0.0) {
      saleValue = (value * 100).toInt();
      clickPayment = false;
      enable = true;
    } else {
      clickPayment = false;
      enable = false;
    }
  }

  @override
  void onAuthProgress(String message) {
    print('***onAuthProgress: $message');
  }

  @override
  void onError(String message) {
    print('***onError: $message');
  }

  @override
  void onMessage(String message) {
    print('***********onMessage: $message');
  }

  @override
  void onFinishedResponse(String message) {
    print('***onFinishedResponse: $message');
  }

  @override
  void onTransactionSuccess() {}

  @override
  void onLoading(bool show) {}

  @override
  void onChanged(String message) {
    // TODO: implement onChanged
  }
}
