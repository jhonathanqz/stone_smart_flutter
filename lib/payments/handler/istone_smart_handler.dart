import 'package:stone_smart_flutter/payments/model/stone_response.dart';
import 'package:stone_smart_flutter/payments/model/stone_transaction_model.dart';

abstract class IStoneSmartHanlder {
  void onTransactionSuccess();

  void onError(StoneResponse response);

  void onMessage(String message);

  void onFinishedResponse(StoneTransactionModel response);

  void onLoading(bool show);

  void writeToFile({
    String? transactionCode,
    String? transactionId,
    String? response,
  });

  void onAbortedSuccessfully();

  void onAuthProgress(StoneResponse response);

  void onTransactionInfo(String response);
}
