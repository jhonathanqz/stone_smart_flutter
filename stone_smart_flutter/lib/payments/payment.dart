import 'package:flutter/services.dart';
import 'package:interface_stone_smart_flutter/interface_stone_smart_flutter.dart';

import 'helper/istone_helper.dart';

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

  /// Function to reversal transaction
  Future<bool> reversal() async {
    try {
      await channel.invokeMethod(PaymentTypeCall.reversal.method);
      return true;
    } catch (e) {
      return false;
    }
  }

  /// Create external functions from invoke methodChannel
  /// Function to active pinpad with SDK the Stone
  Future<bool> activePinpad({
    required String appName,
    required String stoneCode,
  }) async {
    try {
      await channel.invokeMethod(PaymentTypeCall.activePinpad.method, {
        "appName": appName,
        "stoneCode": stoneCode,
      });
      return true;
    } catch (e) {
      return false;
    }
  }

  /// Create external functions from invoke methodChannel
  /// Function to active pinpad with SDK the Stone with credentials
  Future<bool> activePinpadWithCredentials({
    required String appName,
    required String stoneCode,
    required String qrCodeAuthroization,
    required String qrCodeProviderid,
    bool printCustomerSlip = true,
  }) async {
    try {
      await channel.invokeMethod(PaymentTypeCall.activePinpadCredentials.method, {
        "appName": appName,
        "stoneCode": stoneCode,
        "qrCodeAuthorization": qrCodeAuthroization,
        "qrCodeProviderid": qrCodeProviderid,
        "printCustomerSlip": printCustomerSlip,
      });
      return true;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from credit payment with SDK the Stone
  Future<bool> creditPayment(
    int value, {
    bool printCustomerSlip = true,
    bool printEstablishmentSlip = false,
    String initiatorTransactionKey = "",
  }) async {
    return await channel.invokeMethod(
      PaymentTypeCall.credit.method,
      {
        "amount": value.toString(),
        "installment": 1,
        "withInterest": false,
        "printCustomerSlip": printCustomerSlip,
        "printEstablishmentSlip": printEstablishmentSlip,
        "initiatorTransactionKey": initiatorTransactionKey,
      },
    );
  }

  /// Function to invoke method from credit installment payment  with SDK the Stone
  Future<bool> creditPaymentParc({
    required int value,
    int installment = 1,
    bool withInterest = false,
    bool printCustomerSlip = true,
    bool printEstablishmentSlip = false,
    String initiatorTransactionKey = "",
  }) async {
    return await channel.invokeMethod(
      PaymentTypeCall.creditParc.method,
      {
        "amount": value.toString(),
        "installment": installment,
        "withInterest": withInterest,
        "printCustomerSlip": printCustomerSlip,
        "printEstablishmentSlip": printEstablishmentSlip,
        "initiatorTransactionKey": initiatorTransactionKey,
      },
    );
  }

  /// Function to invoke method from debit payment with SDK the Stone
  Future<bool> debitPayment(
    int value, {
    bool printCustomerSlip = true,
    bool printEstablishmentSlip = false,
    String initiatorTransactionKey = "",
  }) async {
    return await channel.invokeMethod(
      PaymentTypeCall.debit.method,
      {
        "amount": value.toString(),
        "installment": 1,
        "withInterest": false,
        "printCustomerSlip": printCustomerSlip,
        "printEstablishmentSlip": printEstablishmentSlip,
        "initiatorTransactionKey": initiatorTransactionKey,
      },
    );
  }

  /// Function to invoke method from debit payment with SDK the Stone
  Future<bool> pixPayment({
    required int amount,
    required String qrCodeAuthroization,
    required String qrCodeProviderid,
    bool printCustomerSlip = true,
    bool printEstablishmentSlip = false,
    String initiatorTransactionKey = "",
  }) async {
    return await channel.invokeMethod(
      PaymentTypeCall.pix.method,
      {
        "amount": amount.toString(),
        "installment": 1,
        "withInterest": false,
        "qrCodeAuthorization": qrCodeAuthroization,
        "qrCodeProviderid": qrCodeProviderid,
        "printCustomerSlip": printCustomerSlip,
        "printEstablishmentSlip": printEstablishmentSlip,
        "initiatorTransactionKey": initiatorTransactionKey,
      },
    );
  }

  /// Function to invoke method from voucher payment with SDK the Stone
  Future<bool> voucherPayment(
    int value, {
    bool printCustomerSlip = true,
    bool printEstablishmentSlip = false,
    String initiatorTransactionKey = "",
  }) async {
    return await channel.invokeMethod(
      PaymentTypeCall.voucher.method,
      {
        "amount": value.toString(),
        "installment": 1,
        "withInterest": false,
        "printCustomerSlip": printCustomerSlip,
        "printEstablishmentSlip": printEstablishmentSlip,
        "initiatorTransactionKey": initiatorTransactionKey,
      },
    );
  }

  /// Function to invoke method from refund payment with SDK the Stone
  Future<void> activeDebugLog({
    required bool isDebugLog,
  }) async {
    await channel.invokeMethod(
      PaymentTypeCall.activeDebugLog.method,
      {
        "isDebugLog": isDebugLog,
      },
    );
  }

  //Get Pos serial number
  Future<String?> getPosSerialNumber() async {
    return await channel.invokeMethod(PaymentTypeCall.getPosSerialNumber.method);
  }

  //Get Pos manufacture
  Future<String?> getPosManufacture() async {
    return await channel.invokeMethod(PaymentTypeCall.getPosManufacture.method);
  }

  /// Function to invoke method from abort current transaction with SDK the Stone
  Future<bool> abortTransaction() async {
    return await channel.invokeMethod(PaymentTypeCall.abort.method);
  }

  /// Function to invoke method from abort current PIX transaction with SDK the Stone
  Future<bool> abortPIXTransaction() async {
    return await channel.invokeMethod(PaymentTypeCall.abortPix.method);
  }

  /// Function to invoke method from printer current transaction with SDK the Stone
  Future<bool> printerCurrentTransaction({
    required bool printCustomerSlip,
  }) async {
    return await channel.invokeMethod(PaymentTypeCall.printTransaction.method, {
      "printCustomerSlip": printCustomerSlip,
    });
  }

  /// Function to invoke method from cancel transaction with SDK the Stone
  Future<bool> cancelTransaction({
    required int idFromBase,
  }) async {
    return await channel.invokeMethod(PaymentTypeCall.cancelTransaction.method, {
      "idFromBase": idFromBase,
    });
  }

  /// Function to get all transactions
  Future<bool> getAllTransactions() async {
    return await channel.invokeMethod(PaymentTypeCall.getAllTransactions.method);
  }

  /// Function to set a payment option
  Future<bool> setPaymentOption({
    required String option,
  }) async {
    return await channel.invokeMethod(PaymentTypeCall.paymentOption.method, {
      "option": option,
    });
  }

  /// Function to get a transaction by initiator key
  Future<bool> getTransactionByInitiatorTransactionKey({
    required String initiatorTransactionKey,
  }) async {
    return await channel.invokeMethod(
      PaymentTypeCall.getTransactionByInitiatorTransactionKey.method,
      {
        "InitiatorTransactionKey": initiatorTransactionKey,
      },
    );
  }

  /// Function to send custom printer parameters
  Future<bool> customPrinter(StonePrinterParams stonePrinterParams) async {
    try {
      await channel.invokeMethod(PaymentTypeCall.customPrinter.method, {
        "printerParams": stonePrinterParams.toJson(),
      });
      return true;
    } catch (e) {
      return false;
    }
  }

  /// Function to print from base64 image
  Future<bool> printFromBase64(String base64) async {
    try {
      await channel.invokeMethod(PaymentTypeCall.printFromBase64.method, {
        "printerParams": base64,
      });
      return true;
    } catch (e) {
      return false;
    }
  }

  /// Function to print wrap paper
  Future<bool> printWrapPaper(int lines) async {
    try {
      await channel.invokeMethod(PaymentTypeCall.printWrapPaper.method, {
        "lines": lines,
      });
      return true;
    } catch (e) {
      return false;
    }
  }
}
