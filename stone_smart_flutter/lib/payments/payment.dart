import 'package:flutter/services.dart';
import 'package:interface_stone_smart_flutter/interface_stone_smart_flutter.dart';

import 'helper/istone_helper.dart';

//Fixed channel name
// ignore: constant_identifier_names
const CHANNEL_NAME = "stone_smart_flutter";

class Payment {
  final MethodChannel channel;
  final IStoneHandler paymentHandler;
  final IStoneSmartHandler? iStoneSmartHandler;

  Payment({
    required this.channel,
    required this.paymentHandler,
    this.iStoneSmartHandler,
  }) {
    channel.setMethodCallHandler((e) => IStoneHelper.callHandler(
          call: e,
          iStoneHandler: paymentHandler,
          stoneSmartHandler: iStoneSmartHandler,
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
    bool printClientVia = true,
  }) async {
    try {
      await channel
          .invokeMethod(PaymentTypeCall.ACTIVEPINPAD_CREDENTIALS.method, {
        "appName": appName,
        "stoneCode": stoneCode,
        "qrCodeAuthorization": qrCodeAuthroization,
        "qrCodeProviderid": qrCodeProviderid,
        "printClientVia": printClientVia,
      });
      return true;
    } catch (e) {
      return false;
    }
  }

  //Function to invoke method from credit payment with sdk the Stone
  Future<bool> creditPayment(
    int value, {
    bool printClientVia = true,
    String initiatorTransactionKey = "",
  }) async {
    return await channel.invokeMethod(
      PaymentTypeCall.CREDIT.method,
      {
        "amount": value.toString(),
        "installment": 1,
        "withInterest": false,
        "printClientVia": printClientVia,
        "initiatorTransactionKey": initiatorTransactionKey,
      },
    );
  }

  //Function to invoke method from credit installment payment  with sdk the Stone
  Future<bool> creditPaymentParc({
    required int value,
    int installment = 1,
    bool withInterest = false,
    bool printClientVia = true,
    String initiatorTransactionKey = "",
  }) async {
    return await channel.invokeMethod(
      PaymentTypeCall.CREDIT_PARC.method,
      {
        "amount": value.toString(),
        "installment": installment,
        "withInterest": withInterest,
        "printClientVia": printClientVia,
        "initiatorTransactionKey": initiatorTransactionKey,
      },
    );
  }

  //Function to invoke method from debit payment with sdk the Stone
  Future<bool> debitPayment(
    int value, {
    bool printClientVia = true,
    String initiatorTransactionKey = "",
  }) async {
    return await channel.invokeMethod(
      PaymentTypeCall.DEBIT.method,
      {
        "amount": value.toString(),
        "installment": 1,
        "withInterest": false,
        "printClientVia": printClientVia,
        "initiatorTransactionKey": initiatorTransactionKey,
      },
    );
  }

  //Function to invoke method from debit payment with sdk the Stone
  Future<bool> pixPayment({
    required int amount,
    required String qrCodeAuthroization,
    required String qrCodeProviderid,
    bool printClientVia = true,
    String initiatorTransactionKey = "",
  }) async {
    return await channel.invokeMethod(
      PaymentTypeCall.PIX.method,
      {
        "amount": amount.toString(),
        "installment": 1,
        "withInterest": false,
        "qrCodeAuthorization": qrCodeAuthroization,
        "qrCodeProviderid": qrCodeProviderid,
        "printClientVia": printClientVia,
        "initiatorTransactionKey": initiatorTransactionKey,
      },
    );
  }

  //Function to invoke method from voucher payment with sdk the Stone
  Future<bool> voucherPayment(
    int value, {
    bool printClientVia = true,
    String initiatorTransactionKey = "",
  }) async {
    return await channel.invokeMethod(
      PaymentTypeCall.VOUCHER.method,
      {
        "amount": value.toString(),
        "installment": 1,
        "withInterest": false,
        "printClientVia": printClientVia,
        "initiatorTransactionKey": initiatorTransactionKey,
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
    required bool printClientVia,
  }) async {
    return await channel
        .invokeMethod(PaymentTypeCall.PRINTER_TRANSACTION.method, {
      "printClientVia": printClientVia,
    });
  }

  //Function to invoke method from cancel transaction with sdk the Stone
  Future<bool> cancelTransaction({
    required int idFromBase,
  }) async {
    return await channel
        .invokeMethod(PaymentTypeCall.CANCEL_TRANSACTION.method, {
      "idFromBase": idFromBase,
    });
  }

  Future<bool> getAllTransactions() async {
    return await channel
        .invokeMethod(PaymentTypeCall.GET_ALL_TRANSACTIONS.method);
  }

  Future<bool> setPaymentOption({
    required String option,
  }) async {
    return await channel.invokeMethod(PaymentTypeCall.PAYMENT_OPTION.method, {
      "option": option,
    });
  }

  Future<bool> getTransactionByInitiatorTransactionKey({
    required String initiatorTransactionKey,
  }) async {
    return await channel.invokeMethod(
        PaymentTypeCall.GET_TRANSACTION_BY_INITIATOR_TRANSACTION_KEY.method, {
      "initiatorTransactionKey": initiatorTransactionKey,
    });
  }

  Future<bool> customPrinter(StonePrinterParams stonePrinterParams) async {
    try {
      await channel.invokeMethod(PaymentTypeCall.CUSTOM_PRINTER.method, {
        "printerParams": stonePrinterParams.toJson(),
      });
      return true;
    } catch (e) {
      return false;
    }
  }

  Future<bool> printerFromBase64(String base64) async {
    try {
      await channel.invokeMethod(PaymentTypeCall.PRINTER_FROM_BASE64.method, {
        "printerParams": base64,
      });
      return true;
    } catch (e) {
      return false;
    }
  }
}
