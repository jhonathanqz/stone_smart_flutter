import 'package:flutter/services.dart';

import 'package:stone_smart_flutter/payments/handler/payment_handler.dart';
import 'package:stone_smart_flutter/payments/utils/payment_types.dart';

//Fixed channel name
const CHANNEL_NAME = "stone_smart_flutter";

class Payment {
  final MethodChannel channel;
  final PaymentHandler paymentHandler;

  Payment({
    required this.channel,
    required this.paymentHandler,
  }) {
    channel.setMethodCallHandler(_callHandler);
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
  Future<bool> pixPayment(int value) async {
    return await channel.invokeMethod(
      PaymentTypeCall.PIX.method,
      {
        "amount": value.toString(),
        "installment": 1,
        "withInterest": false,
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
  Future<bool> abortTransaction({
    required int currentAmount,
  }) async {
    return await channel.invokeMethod(PaymentTypeCall.ABORT.method, {
      "amount": currentAmount.toString(),
    });
  }

  //Function to invoke method from abort current transaction with sdk the Stone
  Future<bool> cancelTransaction({
    required int amount,
    required PaymentTypeTransaction transactionType,
  }) async {
    return await channel.invokeMethod(PaymentTypeCall.CANCEL_TRANSACTION.method, {
      "amount": amount.toString(),
      "transactionType": transactionType.type,
    });
  }

//Function to listen to stone returns in the native environment and notify Flutter
  Future<dynamic> _callHandler(MethodCall call) async {
    switch (call.method.handler) {
      case PaymentTypeHandler.ON_TRANSACTION_SUCCESS:
        {
          paymentHandler.onTransactionSuccess();
        }
        break;
      case PaymentTypeHandler.ON_ERROR:
        {
          paymentHandler.onError(call.arguments);
        }
        break;
      case PaymentTypeHandler.ON_MESSAGE:
        {
          paymentHandler.onMessage(call.arguments);
        }
        break;
      case PaymentTypeHandler.ON_FINISHED_RESPONSE:
        {
          paymentHandler.onFinishedResponse(call.arguments);
        }
        break;
      case PaymentTypeHandler.ON_LOADING:
        {
          paymentHandler.onLoading(call.arguments);
        }
        break;
      case PaymentTypeHandler.WRITE_TO_FILE:
        {
          paymentHandler.writeToFile(
            transactionCode: call.arguments['transactionCode'],
            transactionId: call.arguments['transactionId'],
            response: call.arguments['response'],
          );
        }
        break;
      case PaymentTypeHandler.ON_ABORTED_SUCCESSFULLY:
        {
          paymentHandler.onAbortedSuccessfully();
        }
        break;
      case PaymentTypeHandler.DISPOSE_DIALOG:
        {
          paymentHandler.disposeDialog();
        }
        break;
      case PaymentTypeHandler.ACTIVE_DIALOG:
        {
          paymentHandler.onActivationDialog();
        }
        break;
      case PaymentTypeHandler.ON_AUTH_PROGRESS:
        {
          paymentHandler.onAuthProgress(call.arguments);
        }
        break;

      case PaymentTypeHandler.ON_TRANSACTION_INFO:
        {
          paymentHandler.onTransactionInfo(
            transactionCode: call.arguments['transactionCode'],
            transactionId: call.arguments['transactionId'],
            response: call.arguments['response'],
          );
        }
        break;
      default:
        throw "METHOD NOT IMPLEMENTED";
    }
    return true;
  }
}
