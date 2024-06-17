import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:stone_smart_flutter/payments/handler/istone_handler.dart';
import 'package:stone_smart_flutter/payments/handler/istone_smart_handler.dart';
import 'package:stone_smart_flutter/payments/model/stone_response.dart';
import 'package:stone_smart_flutter/payments/model/stone_transaction_model.dart';

import '../utils/payment_types.dart';

abstract class IStoneHelper {
  static StoneResponse convertToStoneResponse(String message) {
    if (message.contains('{')) {
      final map = json.decode(message);
      return StoneResponse.fromMap(map);
    }
    return StoneResponse(message: message);
  }

  static StoneTransactionModel convertToStoneTransactionModel(String message) {
    if (message.contains('{')) {
      final map = json.decode(message);
      return StoneTransactionModel.fromMap(map);
    }
    return StoneTransactionModel.toError(message);
  }

  static Future<dynamic> callHandler({
    required MethodCall call,
    required IStoneHandler iStoneHandler,
    IStoneSmartHanlder? stoneSmartHanlder,
  }) async {
    switch (call.method.handler) {
      case PaymentTypeHandler.ON_TRANSACTION_SUCCESS:
        {
          iStoneHandler.onTransactionSuccess();
          _stSmartHandler(message: call.arguments, iStoneSmartHanlder: stoneSmartHanlder);
        }
        break;
      case PaymentTypeHandler.ON_ERROR:
        {
          iStoneHandler.onError(call.arguments);
          _stSmartHandler(message: call.arguments, iStoneSmartHanlder: stoneSmartHanlder);
        }
        break;
      case PaymentTypeHandler.ON_MESSAGE:
        {
          iStoneHandler.onMessage(call.arguments);
          _stSmartHandler(message: call.arguments, iStoneSmartHanlder: stoneSmartHanlder);
        }
        break;
      case PaymentTypeHandler.ON_FINISHED_RESPONSE:
        {
          iStoneHandler.onFinishedResponse(call.arguments);
          _stSmartHandler(message: call.arguments, iStoneSmartHanlder: stoneSmartHanlder);
        }
        break;
      case PaymentTypeHandler.ON_CHANGED:
        {
          iStoneHandler.onChanged(call.arguments);
          _stSmartHandler(message: call.arguments, iStoneSmartHanlder: stoneSmartHanlder);
        }
        break;
      case PaymentTypeHandler.ON_LOADING:
        {
          iStoneHandler.onLoading(call.arguments);
          _stSmartHandler(message: call.arguments, iStoneSmartHanlder: stoneSmartHanlder);
        }
        break;
      case PaymentTypeHandler.ON_AUTH_PROGRESS:
        {
          iStoneHandler.onAuthProgress(call.arguments);
          _stSmartHandler(message: call.arguments, iStoneSmartHanlder: stoneSmartHanlder);
        }
        break;
      default:
        throw "METHOD NOT IMPLEMENTED";
    }
    return true;
  }

  static void _stSmartHandler({
    required dynamic message,
    IStoneSmartHanlder? iStoneSmartHanlder,
  }) {
    if (iStoneSmartHanlder == null) return;
    switch (message.handler) {
      case PaymentTypeHandler.ON_TRANSACTION_SUCCESS:
        {
          iStoneSmartHanlder.onTransactionSuccess();
        }
        break;
      case PaymentTypeHandler.ON_ERROR:
        {
          iStoneSmartHanlder.onError(IStoneHelper.convertToStoneResponse(message));
        }
        break;
      case PaymentTypeHandler.ON_MESSAGE:
        {
          iStoneSmartHanlder.onMessage(message);
        }
        break;
      case PaymentTypeHandler.ON_FINISHED_RESPONSE:
        {
          iStoneSmartHanlder.onFinishedResponse(IStoneHelper.convertToStoneTransactionModel(message));
        }
        break;
      case PaymentTypeHandler.ON_CHANGED:
        {
          iStoneSmartHanlder.onChanged(IStoneHelper.convertToStoneResponse(message));
        }
        break;
      case PaymentTypeHandler.ON_LOADING:
        {
          iStoneSmartHanlder.onLoading(message);
        }
        break;
      case PaymentTypeHandler.WRITE_TO_FILE:
        {
          iStoneSmartHanlder.writeToFile(
            transactionCode: message['transactionCode'],
            transactionId: message['transactionId'],
            response: message['response'],
          );
        }
        break;
      case PaymentTypeHandler.ON_ABORTED_SUCCESSFULLY:
        {
          iStoneSmartHanlder.onAbortedSuccessfully();
        }
        break;
      case PaymentTypeHandler.ON_AUTH_PROGRESS:
        {
          iStoneSmartHanlder.onAuthProgress(IStoneHelper.convertToStoneResponse(message));
        }
        break;
      case PaymentTypeHandler.ON_TRANSACTION_INFO:
        {
          iStoneSmartHanlder.onTransactionInfo(message);
        }
        break;
      default:
        throw "METHOD NOT IMPLEMENTED";
    }
  }
}
