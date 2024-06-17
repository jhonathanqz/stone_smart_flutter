//Contract payment handler
abstract class IStoneHandler {
  void onTransactionSuccess();

  void onError(String message);

  void onMessage(String message);

  void onChanged(String message);

  void onFinishedResponse(String message);

  void onLoading(bool show);

  void onAuthProgress(String message);
}
