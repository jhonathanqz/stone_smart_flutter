import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:interface_stone_smart_flutter/interface_stone_smart_flutter.dart';
import 'package:interface_stone_smart_flutter/utils/extensions/string_payment_handler_ext.dart';
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
      case PaymentTypeHandler.onTransactionSuccess:
        {
          iStoneHandler.onTransactionSuccess();
          _stSmartHandler(
              message: call.arguments, iStoneSmartHandler: stoneSmartHandler);
        }
        break;
      case PaymentTypeHandler.onError:
        {
          iStoneHandler.onError(call.arguments);
          _stSmartHandler(
              message: call.arguments, iStoneSmartHandler: stoneSmartHandler);
        }
        break;
      case PaymentTypeHandler.onMessage:
        {
          iStoneHandler.onMessage(call.arguments);
          _stSmartHandler(
              message: call.arguments, iStoneSmartHandler: stoneSmartHandler);
        }
        break;
      case PaymentTypeHandler.onFinishedResponse:
        {
          iStoneHandler.onFinishedResponse(call.arguments);
          _stSmartHandler(
              message: call.arguments, iStoneSmartHandler: stoneSmartHandler);
        }
        break;
      case PaymentTypeHandler.onChanged:
        {
          iStoneHandler.onChanged(call.arguments);
          _stSmartHandler(
              message: call.arguments, iStoneSmartHandler: stoneSmartHandler);
        }
        break;
      case PaymentTypeHandler.onLoading:
        {
          iStoneHandler.onLoading(call.arguments);
          _stSmartHandler(
              message: call.arguments, iStoneSmartHandler: stoneSmartHandler);
        }
        break;
      case PaymentTypeHandler.onAuthProgress:
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
      case PaymentTypeHandler.onTransactionSuccess:
        {
          iStoneSmartHandler.onTransactionSuccess();
        }
        break;
      case PaymentTypeHandler.onError:
        {
          iStoneSmartHandler
              .onError(IStoneHelper.convertToStoneResponse(message));
        }
        break;
      case PaymentTypeHandler.onMessage:
        {
          iStoneSmartHandler.onMessage(message);
        }
        break;
      case PaymentTypeHandler.onFinishedResponse:
        {
          iStoneSmartHandler.onFinishedResponse(
              IStoneHelper.convertToStoneTransactionModel(message));
        }
        break;
      case PaymentTypeHandler.onChanged:
        {
          iStoneSmartHandler
              .onChanged(IStoneHelper.convertToStoneResponse(message));
        }
        break;
      case PaymentTypeHandler.onLoading:
        {
          iStoneSmartHandler.onLoading(message);
        }
        break;
      case PaymentTypeHandler.writeToFile:
        {
          iStoneSmartHandler.writeToFile(
            transactionCode: message['transactionCode'],
            transactionId: message['transactionId'],
            response: message['response'],
          );
        }
        break;
      case PaymentTypeHandler.onAbortedSuccessfully:
        {
          iStoneSmartHandler.onAbortedSuccessfully();
        }
        break;
      case PaymentTypeHandler.onAuthProgress:
        {
          iStoneSmartHandler
              .onAuthProgress(IStoneHelper.convertToStoneResponse(message));
        }
        break;
      case PaymentTypeHandler.onTransactionInfo:
        {
          iStoneSmartHandler.onTransactionInfo(message);
        }
        break;
      default:
        throw "METHOD NOT IMPLEMENTED";
    }
  }
}
