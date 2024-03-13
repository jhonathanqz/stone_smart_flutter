// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'dart:convert';

class StoneResponse {
  final String? method;
  final String? errorMessage;
  final String? message;
  final int? result;

  StoneResponse({
    this.method,
    this.errorMessage,
    this.message,
    this.result,
  });

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'method': method,
      'errorMessage': errorMessage,
      'message': message,
      'result': result,
    };
  }

  factory StoneResponse.fromMap(Map<String, dynamic> map) {
    return StoneResponse(
      method: map['method'] != null ? map['method'] as String : null,
      errorMessage: map['errorMessage'] != null ? map['errorMessage'] as String : null,
      message: map['message'] != null ? map['message'] as String : null,
      result: map['result'] != null ? map['result'] as int : null,
    );
  }

  String toJson() => json.encode(toMap());

  factory StoneResponse.fromJson(String source) => StoneResponse.fromMap(json.decode(source) as Map<String, dynamic>);

  @override
  String toString() {
    return 'StoneResponse(method: $method, errorMessage: $errorMessage, message: $message, result: $result)';
  }

  @override
  bool operator ==(covariant StoneResponse other) {
    if (identical(this, other)) return true;

    return other.method == method && other.errorMessage == errorMessage && other.message == message && other.result == result;
  }

  @override
  int get hashCode {
    return method.hashCode ^ errorMessage.hashCode ^ message.hashCode ^ result.hashCode;
  }
}
