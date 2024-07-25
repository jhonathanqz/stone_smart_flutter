import 'package:flutter/services.dart';

import 'payments/handler/istone_handler.dart';
import 'payments/handler/istone_smart_handler.dart';
import 'payments/payment.dart';

class StoneSmart {
  final MethodChannel _channel;
  Payment? _payment;

  static StoneSmart? _instance;

  StoneSmart(this._channel);

//GET instance from StoneSmart
  static StoneSmart instance() {
    _instance ??= StoneSmart(const MethodChannel(CHANNEL_NAME));
    return _instance!;
  }

//Function to init payment and register handler from notify
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

  Payment get payment {
    if (_payment == null) {
      throw "PAYMENT NEED INITIALIZE! \n TRY: StoneSmart._instance.initPayment(handler)";
    }
    return _payment!;
  }
}
