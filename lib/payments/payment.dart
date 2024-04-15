import 'package:flutter/services.dart';

import 'package:stone_smart_flutter/payments/handler/istone_handler.dart';
import 'package:stone_smart_flutter/payments/helper/istone_helper.dart';
import 'package:stone_smart_flutter/payments/utils/payment_types.dart';

import 'handler/istone_smart_handler.dart';

//Fixed channel name
const CHANNEL_NAME = "stone_smart_flutter";

class Payment {
  final MethodChannel channel;
  final IStoneHandler paymentHandler;
  final IStoneSmartHanlder? iStoneSmartHanlder;

  Payment({
    required this.channel,
    required this.paymentHandler,
    this.iStoneSmartHanlder,
  }) {
    channel.setMethodCallHandler((e) => IStoneHelper.callHandler(
          call: e,
          iStoneHandler: paymentHandler,
          stoneSmartHanlder: iStoneSmartHanlder,
        ));
  }

  //Function to reversal transaction
  Future<bool> reversal() async {
    try {
      await channel.invokeMethod(PaymentTypeCall.REVERSAL.method);
      return true;
    } catch (e) {
      return false;
    }
  }

  //Create external functions from invoke methodChannel
  //Function to active pinpad with sdk the Stone
  Future<bool> activePinpad({
    required String appName,
    required String stoneCode,
  }) async {
    try {
      await channel.invokeMethod(PaymentTypeCall.ACTIVEPINPAD.method, {
        "appName": appName,
        "stoneCode": stoneCode,
      });
      return true;
    } catch (e) {
      return false;
    }
  }

  //Create external functions from invoke methodChannel
  //Function to active pinpad with sdk the Stone with credentials
  Future<bool> activePinpadWithCredentials({
    required String appName,
    required String stoneCode,
    required String qrCodeAuthroization,
    required String qrCodeProviderid,
  }) async {
    try {
      await channel.invokeMethod(PaymentTypeCall.ACTIVEPINPAD_CREDENTIALS.method, {
        "appName": appName,
        "stoneCode": stoneCode,
        "qrCodeAuthorization": qrCodeAuthroization,
        "qrCodeProviderid": qrCodeProviderid,
      });
      return true;
    } catch (e) {
      return false;
    }
  }

//Function to invoke method from credit payment with sdk the Stone
  Future<bool> creditPayment(int value) async {
    return await channel.invokeMethod(
      PaymentTypeCall.CREDIT.method,
      {
        "amount": value.toString(),
        "installment": 1,
        "withInterest": false,
      },
    );
  }

//Function to invoke method from credit installment payment  with sdk the Stone
  Future<bool> creditPaymentParc({
    required int value,
    int installment = 1,
    bool withInterest = false,
  }) async {
    return await channel.invokeMethod(
      PaymentTypeCall.CREDIT_PARC.method,
      {
        "amount": value.toString(),
        "installment": installment,
        "withInterest": withInterest,
      },
    );
  }

//Function to invoke method from debit payment with sdk the Stone
  Future<bool> debitPayment(int value) async {
    return await channel.invokeMethod(
      PaymentTypeCall.DEBIT.method,
      {
        "amount": value.toString(),
        "installment": 1,
        "withInterest": false,
      },
    );
  }

  //Function to invoke method from debit payment with sdk the Stone
  Future<bool> pixPayment({
    required int amount,
    required String qrCodeAuthroization,
    required String qrCodeProviderid,
  }) async {
    return await channel.invokeMethod(
      PaymentTypeCall.PIX.method,
      {
        "amount": amount.toString(),
        "installment": 1,
        "withInterest": false,
        "qrCodeAuthorization": qrCodeAuthroization,
        "qrCodeProviderid": qrCodeProviderid,
      },
    );
  }

//Function to invoke method from voucher payment with sdk the Stone
  Future<bool> voucherPayment(int value) async {
    return await channel.invokeMethod(
      PaymentTypeCall.VOUCHER.method,
      {
        "amount": value.toString(),
        "installment": 1,
        "withInterest": false,
      },
    );
  }

  //OPERATIONS
  //Function to invoke method from abort current transaction with sdk the Stone
  Future<bool> abortTransaction() async {
    return await channel.invokeMethod(PaymentTypeCall.ABORT.method);
  }

  Future<bool> abortPIXTransaction() async {
    return await channel.invokeMethod(PaymentTypeCall.ABORT_PIX.method);
  }

  //Function to invoke method from printer current transaction with sdk the Stone
  Future<bool> printerCurrentTransaction({
    required bool isPrinter,
  }) async {
    return await channel.invokeMethod(PaymentTypeCall.PRINTER_TRANSACTION.method, {
      "isPrinter": isPrinter,
    });
  }

  //Function to invoke method from cancel transaction with sdk the Stone
  Future<bool> cancelTransaction({
    required int amount,
    required PaymentTypeTransaction transactionType,
  }) async {
    return await channel.invokeMethod(PaymentTypeCall.CANCEL_TRANSACTION.method, {
      "amount": amount.toString(),
      "transactionType": transactionType.type,
    });
  }
}
