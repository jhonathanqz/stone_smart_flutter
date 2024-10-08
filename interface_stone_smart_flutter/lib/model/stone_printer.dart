// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'dart:convert';

import 'package:flutter/foundation.dart';

import 'package:stone_smart_flutter/payments/enum/printer_type.dart';

class StonePrinterParams {
  final List<StonePrinter> printers;
  StonePrinterParams({
    required this.printers,
  });

  StonePrinterParams copyWith({
    List<StonePrinter>? printers,
  }) {
    return StonePrinterParams(
      printers: printers ?? this.printers,
    );
  }

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'printers': printers.map((x) => x.toMap()).toList(),
    };
  }

  factory StonePrinterParams.fromMap(Map<String, dynamic> map) {
    return StonePrinterParams(
      printers: List<StonePrinter>.from(
        (map['printers'] as List<int>).map<StonePrinter>(
          (x) => StonePrinter.fromMap(x as Map<String, dynamic>),
        ),
      ),
    );
  }

  String toJson() => json.encode(toMap());

  factory StonePrinterParams.fromJson(String source) => StonePrinterParams.fromMap(json.decode(source) as Map<String, dynamic>);

  @override
  String toString() => 'StonePrinterParams(printers: $printers)';

  @override
  bool operator ==(covariant StonePrinterParams other) {
    if (identical(this, other)) return true;

    return listEquals(other.printers, printers);
  }

  @override
  int get hashCode => printers.hashCode;
}

class StonePrinter {
  final String title;
  final String description;
  final PrinterType printerType;
  final String? imageBase64;
  StonePrinter({
    required this.title,
    required this.description,
    required this.printerType,
    this.imageBase64,
  });

  StonePrinter copyWith({
    String? title,
    String? description,
    PrinterType? printerType,
    String? imageBase64,
  }) {
    return StonePrinter(
      title: title ?? this.title,
      description: description ?? this.description,
      printerType: printerType ?? this.printerType,
      imageBase64: imageBase64 ?? this.imageBase64,
    );
  }

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'key': title,
      'value': description,
      'type': printerType.name,
      'fileBase64': imageBase64,
    };
  }

  factory StonePrinter.fromMap(Map<String, dynamic> map) {
    return StonePrinter(
      title: map['key'] as String,
      description: map['value'] as String,
      printerType: PrinterType.values.firstWhere((element) => element.name == map['type'] as String),
      imageBase64: map['fileBase64'] != null ? map['fileBase64'] as String : null,
    );
  }

  String toJson() => json.encode(toMap());

  factory StonePrinter.fromJson(String source) => StonePrinter.fromMap(json.decode(source) as Map<String, dynamic>);

  @override
  String toString() {
    return 'StonePrinter(title: $title, description: $description, printerType: $printerType, imageBase64: $imageBase64)';
  }

  @override
  bool operator ==(covariant StonePrinter other) {
    if (identical(this, other)) return true;

    return other.title == title && other.description == description && other.printerType == printerType && other.imageBase64 == imageBase64;
  }

  @override
  int get hashCode {
    return title.hashCode ^ description.hashCode ^ printerType.hashCode ^ imageBase64.hashCode;
  }
}
