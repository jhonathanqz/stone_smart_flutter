import 'package:flutter/services.dart';
import 'package:interface_stone_smart_flutter/interface_stone_smart_flutter.dart';

import 'payments/payment.dart';
export 'package:interface_stone_smart_flutter/interface_stone_smart_flutter.dart';

class StoneSmart {
  static const channelName = "stone_smart_flutter";
  final MethodChannel _channel;
  Payment? _payment;

  static StoneSmart? _instance;

  StoneSmart(this._channel);

  static StoneSmart instance() {
    _instance ??= StoneSmart(const MethodChannel(channelName));
    return _instance!;
  }

  /// Function to initialize payment and register the notification handler
  void initPayment({
    required IStoneHandler handler,
    IStoneSmartHandler? iStoneSmartHandler,
  }) {
    _payment = Payment(
      channel: _channel,
      paymentHandler: handler,
      iStoneSmartHandler: iStoneSmartHandler,
    );
  }

  /// Get the Payment object. Note: It needs to be initialized.
  Payment get payment {
    if (_payment == null) {
      throw "PAYMENT NEEDS TO BE INITIALIZED! \n TRY: StoneSmart._instance.initPayment(handler)";
    }
    return _payment!;
  }
}
