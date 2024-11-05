import 'package:interface_stone_smart_flutter/interface_stone_smart_flutter.dart';

extension PrinterTypeExt on PrinterType {
  String get type {
    const Map<PrinterType, String> printerTypeMap = {
      PrinterType.text: "text",
      PrinterType.image: "base64",
    };
    return printerTypeMap[this] ?? "text";
  }
}