import 'package:flutter/material.dart';
import 'package:stone_smart_flutter/payments/handler/istone_smart_handler.dart';
import 'package:stone_smart_flutter/payments/model/stone_response.dart';
import 'package:stone_smart_flutter/payments/model/stone_transaction_model.dart';

class PaymentSmartController extends IStoneSmartHandler {
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
  void onAbortedSuccessfully() {
    debugPrint('***onAbortedSuccessfully');
  }

  @override
  void onAuthProgress(StoneResponse response) {
    debugPrint('***onAuthProgress: ${response.toJson()}');
    debugPrint('***onAuthProgress: ${response.message}');
  }

  @override
  void onChanged(StoneResponse response) {
    debugPrint('***onChanged: ${response.toJson()}');
    debugPrint('***onChanged: ${response.message}');
  }

  @override
  void onError(StoneResponse response) {
    debugPrint('***onError: ${response.toJson()}');
    debugPrint('***onError: ${response.message}');
  }

  @override
  void onFinishedResponse(StoneTransactionModel response) {
    debugPrint('***onFinishedResponse: ${response.toJson()}');
    debugPrint('***onFinishedResponse: ${response.message}');
  }

  @override
  void onLoading(bool show) {
    debugPrint('***onLoading: $show');
  }

  @override
  void onMessage(String message) {
    debugPrint('***********onMessage: $message');
  }

  @override
  void onTransactionInfo(String response) {
    debugPrint('***onTransactionInfo: $response');
  }

  @override
  void onTransactionSuccess() {
    debugPrint('***onTransactionSuccess');
  }

  @override
  void writeToFile({
    String? transactionCode,
    String? transactionId,
    String? response,
  }) {
    debugPrint('***writeToFile: $transactionCode, $transactionId, $response');
  }
}
