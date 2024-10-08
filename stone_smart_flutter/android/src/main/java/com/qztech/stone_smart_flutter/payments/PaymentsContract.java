package com.qztech.stone_smart_flutter.payments;

//Contract class
interface PaymentsContract {
  void onTransactionSuccess();

  void onError(String message);

  void onMessage(String message);

  void onFinishedResponse(String message);

  void onLoading(boolean show);

  void onAbortedSuccessfully();

  void onChanged(String message);

  void onAuthProgress(String message);

}
