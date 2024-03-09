package com.accesys.stone_smart_flutter.payments;

//Contract class
interface PaymentsContract {
  void onTransactionSuccess();

  void onError(String operation, String message);

  void onMessage(String message);

  void onFinishedResponse(String operation, String message);

  void onLoading(boolean show);

  void writeToFile(
    String transactionCode,
    String transactionId,
    String response
  );

  void onAbortedSuccessfully();

  void disposeDialog();

  void onActivationDialog();

  void onAuthProgress(String operation, String message);

  void onTransactionInfo(
    String transactionCode,
    String transactionId,
    String response
  );
}
