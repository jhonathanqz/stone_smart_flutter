import 'dart:async';

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
  ValueNotifier<String> message = ValueNotifier<String>('');

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
  Future<void> onAuthProgress(String message) async {
    debugPrint('***onAuthProgress: $message');
  }

  @override
  Future<void> onChanged(String message) async {
    debugPrint('***onChanged: $message');
  }

  @override
  Future<void> onError(String message) async {
    debugPrint('***onError: $message');
  }

  @override
  Future<void> onFinishedResponse(String message) async {
    debugPrint('***onFinishedResponse: $message');
  }

  @override
  Future<void> onLoading(bool show) async {
    debugPrint('***onLoading: $show');
  }

  @override
  Future<void> onMessage(String message) async {
    this.message.value = message;
  }

  @override
  Future<void> onTransactionSuccess() async {
    debugPrint('***onTransactionSuccess');
  }
}
