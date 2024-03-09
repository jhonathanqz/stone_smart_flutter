// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'dart:convert';

class StoneResponse {
  final String? operation;
  final String? response;

  StoneResponse({
    this.operation,
    this.response,
  });

  StoneResponse copyWith({
    String? operation,
    String? response,
  }) {
    return StoneResponse(
      operation: operation ?? this.operation,
      response: response ?? this.response,
    );
  }

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'operation': operation,
      'response': response,
    };
  }

  factory StoneResponse.fromMap(Map<String, dynamic> map) {
    return StoneResponse(
      operation: map['operation'] != null ? map['operation'] as String : null,
      response: map['response'] != null ? map['response'] as String : null,
    );
  }

  String toJson() => json.encode(toMap());

  factory StoneResponse.fromJson(String source) => StoneResponse.fromMap(json.decode(source) as Map<String, dynamic>);

  @override
  String toString() => 'StoneResponse(operation: $operation, response: $response)';

  @override
  bool operator ==(covariant StoneResponse other) {
    if (identical(this, other)) return true;

    return other.operation == operation && other.response == response;
  }

  @override
  int get hashCode => operation.hashCode ^ response.hashCode;
}
