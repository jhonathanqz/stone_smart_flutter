import 'package:flutter/material.dart';
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
    debugPrint('***onAuthProgress: $message');
  }

  @override
  void onError(String message) {
    debugPrint('***onError: $message');
  }

  @override
  void onMessage(String message) {
    debugPrint('***********onMessage: $message');
  }

  @override
  void onFinishedResponse(String message) {
    debugPrint('***onFinishedResponse: $message');
  }

  @override
  void onTransactionSuccess() {
    debugPrint('***onTransactionSuccess');
  }

  @override
  void onLoading(bool show) {
    debugPrint('***onLoading: $show');
  }

  @override
  void onChanged(String message) {
    debugPrint('***onChanged: $message');
  }
}
