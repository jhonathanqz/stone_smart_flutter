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
  Future<void> onAuthProgress(String message) {
    throw UnimplementedError();
  }

  @override
  Future<void> onChanged(String message) {
    throw UnimplementedError();
  }

  @override
  Future<void> onError(String message) {
    throw UnimplementedError();
  }

  @override
  Future<void> onFinishedResponse(String message) {
    throw UnimplementedError();
  }

  @override
  Future<void> onLoading(bool show) {
    throw UnimplementedError();
  }

  @override
  Future<void> onMessage(String message) {
    throw UnimplementedError();
  }

  @override
  Future<void> onTransactionSuccess() {
    throw UnimplementedError();
  }
}
