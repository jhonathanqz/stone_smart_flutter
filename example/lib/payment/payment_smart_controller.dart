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
  ValueNotifier<List<Map>> transactions = ValueNotifier<List<Map>>([]);

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
    debugPrint('***onAuthProgress: $response');
  }

  @override
  void onChanged(StoneResponse response) {
    debugPrint('***onAuthProgress: $response');
  }

  @override
  void onError(StoneResponse response) {
    debugPrint('***onAuthProgress: $response');
  }

  @override
  void onFinishedResponse(StoneTransactionModel response) {
    debugPrint('***onAuthProgress: $response');
  }

  @override
  void onLoading(bool show) {}

  @override
  void onMessage(String message) {
    debugPrint('***onAuthProgress: $message');
  }

  @override
  void onTransactionInfo(String response) {
    debugPrint('***onAuthProgress: $response');
  }

  @override
  void onTransactionSuccess() {}

  @override
  void writeToFile({
    String? transactionCode,
    String? transactionId,
    String? response,
  }) {
    transactions.value.add({
      'transactionId': transactionId,
      'transactionCode': transactionCode,
      'value': saleValue.toString(),
    });
  }
}
