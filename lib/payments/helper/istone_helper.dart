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
    IStoneSmartHandler? stoneSmartHandler,
  }) async {
    switch (call.method.handler) {
      case PaymentTypeHandler.ON_TRANSACTION_SUCCESS:
        {
          iStoneHandler.onTransactionSuccess();
          _stSmartHandler(
              message: call.arguments, iStoneSmartHandler: stoneSmartHandler);
        }
        break;
      case PaymentTypeHandler.ON_ERROR:
        {
          iStoneHandler.onError(call.arguments);
          _stSmartHandler(
              message: call.arguments, iStoneSmartHandler: stoneSmartHandler);
        }
        break;
      case PaymentTypeHandler.ON_MESSAGE:
        {
          iStoneHandler.onMessage(call.arguments);
          _stSmartHandler(
              message: call.arguments, iStoneSmartHandler: stoneSmartHandler);
        }
        break;
      case PaymentTypeHandler.ON_FINISHED_RESPONSE:
        {
          iStoneHandler.onFinishedResponse(call.arguments);
          _stSmartHandler(
              message: call.arguments, iStoneSmartHandler: stoneSmartHandler);
        }
        break;
      case PaymentTypeHandler.ON_CHANGED:
        {
          iStoneHandler.onChanged(call.arguments);
          _stSmartHandler(
              message: call.arguments, iStoneSmartHandler: stoneSmartHandler);
        }
        break;
      case PaymentTypeHandler.ON_LOADING:
        {
          iStoneHandler.onLoading(call.arguments);
          _stSmartHandler(
              message: call.arguments, iStoneSmartHandler: stoneSmartHandler);
        }
        break;
      case PaymentTypeHandler.ON_AUTH_PROGRESS:
        {
          iStoneHandler.onAuthProgress(call.arguments);
          _stSmartHandler(
              message: call.arguments, iStoneSmartHandler: stoneSmartHandler);
        }
        break;
      default:
        throw "METHOD NOT IMPLEMENTED";
    }
    return true;
  }

  static void _stSmartHandler({
    required dynamic message,
    IStoneSmartHandler? iStoneSmartHandler,
  }) {
    if (iStoneSmartHandler == null) return;
    switch (message.handler) {
      case PaymentTypeHandler.ON_TRANSACTION_SUCCESS:
        {
          iStoneSmartHandler.onTransactionSuccess();
        }
        break;
      case PaymentTypeHandler.ON_ERROR:
        {
          iStoneSmartHandler
              .onError(IStoneHelper.convertToStoneResponse(message));
        }
        break;
      case PaymentTypeHandler.ON_MESSAGE:
        {
          iStoneSmartHandler.onMessage(message);
        }
        break;
      case PaymentTypeHandler.ON_FINISHED_RESPONSE:
        {
          iStoneSmartHandler.onFinishedResponse(
              IStoneHelper.convertToStoneTransactionModel(message));
        }
        break;
      case PaymentTypeHandler.ON_CHANGED:
        {
          iStoneSmartHandler
              .onChanged(IStoneHelper.convertToStoneResponse(message));
        }
        break;
      case PaymentTypeHandler.ON_LOADING:
        {
          iStoneSmartHandler.onLoading(message);
        }
        break;
      case PaymentTypeHandler.WRITE_TO_FILE:
        {
          iStoneSmartHandler.writeToFile(
            transactionCode: message['transactionCode'],
            transactionId: message['transactionId'],
            response: message['response'],
          );
        }
        break;
      case PaymentTypeHandler.ON_ABORTED_SUCCESSFULLY:
        {
          iStoneSmartHandler.onAbortedSuccessfully();
        }
        break;
      case PaymentTypeHandler.ON_AUTH_PROGRESS:
        {
          iStoneSmartHandler
              .onAuthProgress(IStoneHelper.convertToStoneResponse(message));
        }
        break;
      case PaymentTypeHandler.ON_TRANSACTION_INFO:
        {
          iStoneSmartHandler.onTransactionInfo(message);
        }
        break;
      default:
        throw "METHOD NOT IMPLEMENTED";
    }
  }
}
