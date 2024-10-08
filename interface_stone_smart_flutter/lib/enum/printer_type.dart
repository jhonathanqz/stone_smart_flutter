enum PrinterType {
  text,
  image;

  String get name {
    switch (this) {
      case PrinterType.text:
        return "text";
      case PrinterType.image:
        return "base64";
      default:
        return "text";
    }
  }
}
