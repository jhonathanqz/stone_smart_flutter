// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'dart:convert';

class StoneTransactionModel {
  final String? actionCode;
  final String? aid;
  final String? amount;
  final String? arcq;
  final String? cardBrand;
  final String? cardHolderNumber;
  final String? date;
  final String? entryMode;
  final int? idFromBase;
  final int? isBuildResponse;
  final String? manufacture;
  final int? result;
  final String? saleAffiliationKey;
  final String? serialNumber;
  final String? time;
  final String? transactionReference;
  final String? typeOfTransactionEnum;
  final String? errorMessage;
  final String? method;
  final String? transactionStatus;
  final String? messageFromAuthorize;
  final String? message;
  final String? authorizationCode;
  final String? actionResultMessage;

  StoneTransactionModel({
    required this.actionCode,
    required this.aid,
    required this.amount,
    required this.arcq,
    required this.cardBrand,
    required this.cardHolderNumber,
    required this.date,
    required this.entryMode,
    required this.idFromBase,
    required this.isBuildResponse,
    required this.manufacture,
    required this.result,
    required this.saleAffiliationKey,
    required this.serialNumber,
    required this.time,
    required this.transactionReference,
    required this.typeOfTransactionEnum,
    required this.errorMessage,
    required this.method,
    required this.transactionStatus,
    required this.messageFromAuthorize,
    required this.message,
    required this.authorizationCode,
    required this.actionResultMessage,
  });

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'actionCode': actionCode,
      'aid': aid,
      'amount': amount,
      'arcq': arcq,
      'cardBrand': cardBrand,
      'cardHolderNumber': cardHolderNumber,
      'date': date,
      'entryMode': entryMode,
      'idFromBase': idFromBase,
      'isBuildResponse': isBuildResponse,
      'manufacture': manufacture,
      'result': result,
      'saleAffiliationKey': saleAffiliationKey,
      'serialNumber': serialNumber,
      'time': time,
      'transactionReference': transactionReference,
      'typeOfTransactionEnum': typeOfTransactionEnum,
      'errorMessage': errorMessage,
      'method': method,
      'transactionStatus': transactionStatus,
      'messageFromAuthorize': messageFromAuthorize,
      'message': message,
      'authorizationCode': authorizationCode,
      'actionResultMessage': actionResultMessage,
    };
  }

  factory StoneTransactionModel.fromMap(Map<String, dynamic> map) {
    return StoneTransactionModel(
      actionCode: map['actionCode'] != null ? map['actionCode'] as String : null,
      aid: map['aid'] != null ? map['aid'] as String : null,
      amount: map['amount'] != null ? map['amount'] as String : null,
      arcq: map['arcq'] != null ? map['arcq'] as String : null,
      cardBrand: map['cardBrand'] != null ? map['cardBrand'] as String : null,
      cardHolderNumber: map['cardHolderNumber'] != null ? map['cardHolderNumber'] as String : null,
      date: map['date'] != null ? map['date'] as String : null,
      entryMode: map['entryMode'] != null ? map['entryMode'] as String : null,
      idFromBase: map['idFromBase'] != null ? map['idFromBase'] as int : null,
      isBuildResponse: map['isBuildResponse'] != null ? map['isBuildResponse'] as int : null,
      manufacture: map['manufacture'] != null ? map['manufacture'] as String : null,
      result: map['result'] != null ? map['result'] as int : null,
      saleAffiliationKey: map['saleAffiliationKey'] != null ? map['saleAffiliationKey'] as String : null,
      serialNumber: map['serialNumber'] != null ? map['serialNumber'] as String : null,
      time: map['time'] != null ? map['time'] as String : null,
      transactionReference: map['transactionReference'] != null ? map['transactionReference'] as String : null,
      typeOfTransactionEnum: map['typeOfTransactionEnum'] != null ? map['typeOfTransactionEnum'] as String : null,
      errorMessage: map['errorMessage'] != null ? map['errorMessage'] as String : null,
      method: map['method'] != null ? map['method'] as String : null,
      transactionStatus: map['transactionStatus'] != null ? map['transactionStatus'] as String : null,
      messageFromAuthorize: map['messageFromAuthorize'] != null ? map['messageFromAuthorize'] as String : null,
      message: map['message'] != null ? map['message'] as String : null,
      authorizationCode: map['authorizationCode'] != null ? map['authorizationCode'] as String : null,
      actionResultMessage: map['actionResultMessage'] != null ? map['actionResultMessage'] as String : null,
    );
  }

  String toJson() => json.encode(toMap());

  factory StoneTransactionModel.fromJson(String source) => StoneTransactionModel.fromMap(json.decode(source) as Map<String, dynamic>);

  factory StoneTransactionModel.toError(String message) {
    return StoneTransactionModel(
      actionCode: '',
      aid: '',
      amount: '',
      arcq: '',
      cardBrand: '',
      cardHolderNumber: '',
      date: '',
      entryMode: '',
      idFromBase: 0,
      isBuildResponse: 0,
      manufacture: '',
      result: 999999,
      saleAffiliationKey: '',
      serialNumber: '',
      time: '',
      transactionReference: '',
      typeOfTransactionEnum: '',
      errorMessage: message,
      method: '',
      transactionStatus: '',
      messageFromAuthorize: '',
      message: '',
      authorizationCode: '',
      actionResultMessage: '',
    );
  }

  @override
  bool operator ==(covariant StoneTransactionModel other) {
    if (identical(this, other)) return true;

    return other.actionCode == actionCode && other.aid == aid && other.amount == amount && other.arcq == arcq && other.cardBrand == cardBrand && other.cardHolderNumber == cardHolderNumber && other.date == date && other.entryMode == entryMode && other.idFromBase == idFromBase && other.isBuildResponse == isBuildResponse && other.manufacture == manufacture && other.result == result && other.saleAffiliationKey == saleAffiliationKey && other.serialNumber == serialNumber && other.time == time && other.transactionReference == transactionReference && other.typeOfTransactionEnum == typeOfTransactionEnum && other.errorMessage == errorMessage && other.method == method && other.transactionStatus == transactionStatus && other.messageFromAuthorize == messageFromAuthorize && other.message == message && other.authorizationCode == authorizationCode && other.actionResultMessage == actionResultMessage;
  }

  @override
  int get hashCode {
    return actionCode.hashCode ^ aid.hashCode ^ amount.hashCode ^ arcq.hashCode ^ cardBrand.hashCode ^ cardHolderNumber.hashCode ^ date.hashCode ^ entryMode.hashCode ^ idFromBase.hashCode ^ isBuildResponse.hashCode ^ manufacture.hashCode ^ result.hashCode ^ saleAffiliationKey.hashCode ^ serialNumber.hashCode ^ time.hashCode ^ transactionReference.hashCode ^ typeOfTransactionEnum.hashCode ^ errorMessage.hashCode ^ method.hashCode ^ transactionStatus.hashCode ^ messageFromAuthorize.hashCode ^ message.hashCode ^ authorizationCode.hashCode ^ actionResultMessage.hashCode;
  }
}
