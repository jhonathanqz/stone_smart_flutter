<h1 align="center">Stone Smart Flutter</h1>

<div align="center" id="top"> 
  <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/c9/Stone_pagamentos.png/800px-Stone_pagamentos.png" alt="Stone" height=100 />
</div>
<br>

<p align="center">
  <a href="#dart-sobre">Sobre</a> &#xa0; | &#xa0; 
  <a href="#rocket-tecnologias">Tecnologias</a> &#xa0; | &#xa0;
  <a href="#checkered_flag-configuração">Configuração</a> &#xa0; | &#xa0;
  <a href="#memo-autores">Autores</a> &#xa0; | &#xa0;
</p>

<br>

<a href="https://buymeacoffee.com/jhonathanqr" target="_blank">
  <img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Book" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;">
</a>

[![Github Badge](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white&link=https://github.com/jhonathanqz)](https://github.com/jhonathanqz)

## Plugin não oficial!!!

## :dart: Sobre

Projeto destinado a facilitar a integração com o SDK da Stone Smart no Flutter.
Funciona somente com máquinas smarts.

Máquinas compatíveis:

- Positivo L300
- Positivo L400
- Ingenico APOS A8
- Sunmi P2
- Gertec GPOS700X

## :rocket: Tecnologias

As seguintes ferramentas foram usadas na construção do projeto:

- [Flutter](https://flutter.dev/)
- SDK Stone version: 4.9.5

## :checkered_flag: Configuração

### # Pubspec.yaml

Para usar este plugin, adicione `stone_smart_flutter` como [dependência](https://flutter.io/using-packages/) ao seu arquivo `pubspec.yaml`.

```yaml
dependencies:
  stone_smart_flutter: any
```

This will get you the latest version.

### # Build.gradle

Em seu build.gradle a nivel do app, a propriedade `minSdkVersion` precisa ser level 23. Recurso este exigido pela versão 4.9.5 do SDK Stone.

```xml
...
defaultConfig {
        applicationId "com.example.stone_example"
        minSdkVersion 22
        targetSdkVersion flutter.targetSdkVersion
        versionCode flutterVersionCode.toInteger()
        versionName flutterVersionName
    }
...
```

### # Implementação

Para começar é necessário criar uma classe que implemente ´StoneHandler´, sendo que essa é a responsável por monitorar e retornar os dados da Stone.

### Criando classe StoneController

```dart
import 'dart:convert';

import 'package:stone_smart_flutter/stone_smart_flutter.dart';

import '../../../sqflite/data/helper/debug_log.dart';
import '../entities/stone_transaction.dart';

class StoneHandler extends IStoneHandler {
  final Function(StoneTransaction?)? onTransaction;
  final Function(String)? onMessageMonitor;

  StoneHandler({
    this.onTransaction,
    this.onMessageMonitor,
  });

  @override
  Future<void> onError(String message) async {
    DebugLog.payment('onError_STONE: $message');
    final stoneTransaction = _getStoneTransaction(message);
    if (onTransaction != null) {
      onTransaction!(stoneTransaction);
    }
  }

  @override
  Future<void> onMessage(String message) async {
    DebugLog.payment('onMessage_STONE: $message');
    if (onMessageMonitor != null) {
      onMessageMonitor!(message);
    }
  }

  @override
  Future<void> onFinishedResponse(String message) async {
    DebugLog.payment('onFinishedResponse_STONE: $message');
    final stoneTransaction = _getStoneTransaction(message);
    if (onTransaction != null) {
      onTransaction!(stoneTransaction);
    }
  }

  @override
  Future<void> onChanged(String message) async {
    DebugLog.payment('onChanged_STONE: $message');
    final stoneTransaction = _getStoneTransaction(message);
    if (onTransaction != null) {
      onTransaction!(stoneTransaction);
    }
  }

  @override
  Future<void> onAuthProgress(String message) async {
    DebugLog.payment('onAuthProgress_STONE: $message');
    final stoneTransaction = _getStoneTransaction(message);
    if (stoneTransaction?.method != 'active') {
      if (onTransaction != null) {
        onTransaction!(stoneTransaction);
      }
    }
  }

  @override
  Future<void> onTransactionSuccess() async {
    DebugLog.payment('onTransactionSuccess_STONE');
  }

  @override
  Future<void> onLoading(bool show) async {}

  StoneTransaction? _getStoneTransaction(String message) {
    try {
      if (message.contains('{')) {
        final map = json.decode(message);
        final stoneTransaction = StoneTransaction.fromMap(map);
        return stoneTransaction;
      }
    } catch (e) {
      DebugLog.payment('***Erro_getStoneTransaction: $e');
      return null;
    }
    return null;
  }
}
```

##### Classe StoneTransaction

Essa classe é responsável por decodificar o retorno da STONE.

```dart
// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'dart:convert';

import 'package:acc_checkout/app/core/domain/entities/i_transaction_response.dart';

class StoneTransaction extends ITransactionResponse {
  final String? actionCode;
  final String? aid;
  final String? amount;
  final String? arcq;
  final String? cardBrand;
  final int? cardBrandId;
  final String? cardHolderNumber;
  final String? cardSequenceNumber;
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
  final String? externalId;
  final String? transactionKey;
  final String? initiatorTransactionKey;

  final String? actionResultMessage;
  final bool isPrinterRequest;
  final List<String>? options;

  List<String> get getOptions => options ?? [];

  bool get isApproved =>
      transactionStatus?.toUpperCase() == 'APPROVED' &&
      method?.toLowerCase() == 'transaction';

  StoneTransaction({
    required this.actionCode,
    required this.aid,
    required this.amount,
    required this.arcq,
    required this.cardBrand,
    required this.cardBrandId,
    required this.cardHolderNumber,
    required this.cardSequenceNumber,
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
    required this.actionResultMessage,
    required this.authorizationCode,
    required this.transactionKey,
    required this.externalId,
    required this.initiatorTransactionKey,
    this.isPrinterRequest = false,
    this.options,
  });

  StoneTransaction copyWith({
    String? actionCode,
    String? aid,
    String? amount,
    String? arcq,
    String? cardBrand,
    int? cardBrandId,
    String? cardHolderNumber,
    String? cardSequenceNumber,
    String? date,
    String? entryMode,
    int? idFromBase,
    int? isBuildResponse,
    String? manufacture,
    int? result,
    String? saleAffiliationKey,
    String? serialNumber,
    String? time,
    String? transactionReference,
    String? typeOfTransactionEnum,
    String? errorMessage,
    String? method,
    String? transactionStatus,
    String? messageFromAuthorize,
    String? message,
    String? actionResultMessage,
    String? authorizationCode,
    String? transactionKey,
    String? externalId,
    String? initiatorTransactionKey,
    bool? isPrinterRequest,
    List<String>? options,
  }) {
    return StoneTransaction(
      actionCode: actionCode ?? this.actionCode,
      aid: aid ?? this.aid,
      amount: amount ?? this.amount,
      arcq: arcq ?? this.arcq,
      cardBrand: cardBrand ?? this.cardBrand,
      cardBrandId: cardBrandId ?? this.cardBrandId,
      cardHolderNumber: cardHolderNumber ?? this.cardHolderNumber,
      cardSequenceNumber: cardSequenceNumber ?? this.cardSequenceNumber,
      date: date ?? this.date,
      entryMode: entryMode ?? this.entryMode,
      idFromBase: idFromBase ?? this.idFromBase,
      isBuildResponse: isBuildResponse ?? this.isBuildResponse,
      manufacture: manufacture ?? this.manufacture,
      result: result ?? this.result,
      saleAffiliationKey: saleAffiliationKey ?? this.saleAffiliationKey,
      serialNumber: serialNumber ?? this.serialNumber,
      time: time ?? this.time,
      transactionReference: transactionReference ?? this.transactionReference,
      typeOfTransactionEnum:
          typeOfTransactionEnum ?? this.typeOfTransactionEnum,
      errorMessage: errorMessage ?? this.errorMessage,
      method: method ?? this.method,
      transactionStatus: transactionStatus ?? this.transactionStatus,
      messageFromAuthorize: messageFromAuthorize ?? this.messageFromAuthorize,
      message: message ?? this.message,
      actionResultMessage: actionResultMessage ?? this.actionResultMessage,
      authorizationCode: authorizationCode ?? this.authorizationCode,
      transactionKey: transactionKey ?? this.transactionKey,
      externalId: externalId ?? this.externalId,
      initiatorTransactionKey:
          initiatorTransactionKey ?? this.initiatorTransactionKey,
      isPrinterRequest: isPrinterRequest ?? this.isPrinterRequest,
      options: options ?? this.options,
    );
  }

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'actionCode': actionCode,
      'aid': aid,
      'amount': amount,
      'arcq': arcq,
      'cardBrand': cardBrand,
      'cardBrandId': cardBrandId,
      'cardHolderNumber': cardHolderNumber,
      'cardSequenceNumber': cardSequenceNumber,
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
      'actionResultMessage': actionResultMessage ?? '',
      'authorizationCode': authorizationCode ?? '',
      'externalId': externalId,
      'transactionKey': transactionKey,
      'initiatorTransactionKey': initiatorTransactionKey,
      'isPrinterRequest': isPrinterRequest,
      'options': options,
    };
  }

  factory StoneTransaction.fromMap(Map<String, dynamic> map) {
    return StoneTransaction(
      actionCode: map['actionCode']?.toString(),
      aid: map['aid']?.toString(),
      amount: map['amount']?.toString(),
      arcq: map['arcq']?.toString(),
      cardBrand: map['cardBrand']?.toString(),
      cardBrandId: map['cardBrandId'] != null
          ? int.tryParse(map['cardBrandId'].toString())
          : null,
      cardHolderNumber: map['cardHolderNumber']?.toString(),
      cardSequenceNumber: map['cardSequenceNumber']?.toString(),
      date: map['date']?.toString(),
      entryMode: map['entryMode']?.toString(),
      idFromBase: map['idFromBase'] != null
          ? int.tryParse(map['idFromBase'].toString())
          : null,
      isBuildResponse: map['isBuildResponse'] != null
          ? int.tryParse(map['isBuildResponse'].toString())
          : null,
      manufacture: map['manufacture']?.toString(),
      result:
          map['result'] != null ? int.tryParse(map['result'].toString()) : null,
      saleAffiliationKey: map['saleAffiliationKey']?.toString(),
      serialNumber: map['serialNumber']?.toString(),
      time: map['time']?.toString(),
      transactionReference: map['transactionReference']?.toString(),
      typeOfTransactionEnum: map['typeOfTransactionEnum']?.toString(),
      errorMessage: map['errorMessage']?.toString(),
      method: map['method']?.toString(),
      transactionStatus: map['transactionStatus']?.toString(),
      messageFromAuthorize: map['messageFromAuthorize']?.toString(),
      message: map['message']?.toString(),
      actionResultMessage: map['actionResultMessage'] != null
          ? map['actionResultMessage']!.toString()
          : '',
      authorizationCode: map['authorizationCode'] != null
          ? map['authorizationCode']!.toString()
          : '',
      externalId: map['externalId']?.toString(),
      transactionKey: map['transactionKey']?.toString(),
      initiatorTransactionKey: map['initiatorTransactionKey']?.toString(),
      isPrinterRequest: map['isPrinterRequest'] != null
          ? map['isPrinterRequest'] as bool
          : false,
      options:
          map['options'] != null ? List<String>.from(map['options']) : null,
    );
  }

  factory StoneTransaction.buildErrorResponse({
    required int result,
    required String errorMessage,
    required String method,
  }) {
    return StoneTransaction(
      actionCode: '',
      aid: '',
      amount: '',
      arcq: '',
      cardBrand: '',
      cardBrandId: 0,
      cardHolderNumber: '',
      cardSequenceNumber: '',
      date: '',
      entryMode: '',
      idFromBase: 0,
      isBuildResponse: 0,
      manufacture: '',
      result: result,
      saleAffiliationKey: '',
      serialNumber: '',
      time: '',
      transactionReference: '',
      typeOfTransactionEnum: '',
      errorMessage: errorMessage,
      method: method,
      transactionStatus: '',
      messageFromAuthorize: '',
      message: errorMessage,
      actionResultMessage: null,
      authorizationCode: '',
      transactionKey: '',
      externalId: '',
      initiatorTransactionKey: '',
      isPrinterRequest: false,
      options: null,
    );
  }

  String toJson() => json.encode(toMap());

  factory StoneTransaction.fromJson(String source) =>
      StoneTransaction.fromMap(json.decode(source) as Map<String, dynamic>);

  @override
  // TODO: implement props
  List<Object?> get props => throw UnimplementedError();
}
```

#### Métodos da ´StoneHandler´

##### onAbortedSuccessfully

Acionado quando uma transação de abort é concluída com sucesso.

##### onAuthProgress

Acionado quando uma transação está em progresso.
Retorno do status do Pinpad também é mapeado aqui.

##### onError

Acionado quando uma transação retorna um estado de ´Erro´, devolvendo como parâmetro um objeto em formato ´String´ com a mensagem e o método.

##### onMessage

Método responsável por devolver para o usuário uma mensagem retornada da Stone.

##### onFinishedResponse

Método responsável por devolver uma response da transação.

##### onTransactionSuccess

Método acionado quando a transação foi concluída com sucesso.

#### Iniciar transação

Para iniciar a transação é necessário primeiro chamar a função de ativação do PinPad, passando como parâmetro o código de ativação daquele POS (código este informado na sua conta PagBank).

`StoneSmart.instance().payment.activePinpad(stoneCode: '12345');`

Logo após ativação, o SDK da Stone fornece algumas opções de transação como:

- Crédito = `StoneSmart.instance().payment.creditPayment(12.50)`

- Crédito Parcelado = `StoneSmart.instance().payment.creditPaymentParc(value: controller.amount, installment: 2)`

- Débito = `StoneSmart.instance().payment.debitPayment(12.50)`

- PIX = `StoneSmart.instance().payment.pixPayment.(amount: 1250, qrCodeAuthorization: '', qrCodeProviderid: '')`

- Voucher (alimentação) = `StoneSmart.instance().payment.voucherPayment(12.50)`

- Estorno = `StoneSmart.instance().payment.cancelTransaction(amount: controller.saleValue, transactionType: PaymentTypeTransaction.CREDIT)`

- Abortar transação = `StoneSmart.instance().payment.abortTransaction()`

\*\*Obs: Por padrão o SDK da Stone SEMPRE imprime a via do consumidor.

### Modelo de resposta

#### Método onAuthProgress, onChanged e onError

Estes métodos iram retornar um objeto em formato de string com a seguinte estrutura:

```
{
  "method": "transaction",
  "message": "Transação aprovada",
  "errorMessage": "",
  "result": 0,
}
```

O campo `errorMessage` só é preenchido caso venha algum erro;
Métodos mapeados para o campo `method`: abort, transaction, active, printer, reversal;

#### Método onFinishedResponse

Este método irá retornar um objeto em formato de String com a seguinte estrutura:

```
{
"method": "transaction",
"idFromBase": 0,
"amount": 1250,
"cardHolderNumber": "",
"cardBrand": "",
"date": "",
"time": "",
"aid": "",
"arcq": "",
"transactionReference": "",
"saleAffiliationKey": "",
"entryMode": "",
"typeOfTransactionEnum": "",
"serialNumber": "",
"manufacture": "",
"actionCode": "",
"transactionStatus": "",
"messageFromAuthorize": "",
"errorMessage": "",
"result": 0,
}
```

#### Observações:

Para a transação com `PIX` é necessário fornecer nos parâmetros o `qrCodeAuthroization` e o `qrCodeProviderid` fornecidos pela Stone.

No método onChanged, pode vir um retorno com o campo "method" preenchido como "QRCode" e no campo message virá a imagem em formato Bitmap, convertida em String, ficando a cargo do desenvolvedor mostrar o QRCode gerado para o usuário final.

##### Cancelamento

Para cancelar uma transação é necessário chamar o método `cancelTransaction` passando o ID da transação.
Caso você não tenha a informação do ID da transação, é necessário chamar o método `getAllTransactions` para trazer todas transações em forma de lista, e você conseguir pegar as informações de valor, id e status.

## DICA

Mapeem o retorno sempre pela função `onFinishedresponse`, todos os retornos do SDK estão concentrados nela.
Para você saber do que trata o retorno, verifique o campo `method` quem junto na response. Esse campo é responsável por lhe indicar do que se trata, se é `transaction`, `printer`, etc.

Os possíveis retornos para o campo `method` são:

```dart
void onFinishedResponseMonitor(StoneTransaction? stoneTransaction) {
    switch (stoneTransaction?.method) {
      case 'active':
        //Retorno se o terminal foi ativado ou não
        break;
      case 'transaction':
        //Retorno completo de uma transação
        break;
      case 'abort':
        break;
      case 'abortPix':
        break;
      case 'cancel':
        break;
      case 'printer':
        break;
      case 'QRCode':
        //O QRCode vem aqui e deve ser exibido ao cliente
        break;
      case 'PaymentOptions':
        //Abrir modal para escolher forma de pagamento (alimentacao ou refeicao)
        //Chamar método  _stoneSmart.payment.setPaymentOption(option: <opcao_escolhida>);
        break;
      case 'reversal':
        break;
      default:
    }
  }
```

## :memo: Autores

Este projeto foi desenvolvido por:
<a href="https://github.com/jhonathanqz" target="_blank">Jhonathan Queiroz</a>

</br>

<div> 
<a href="https://github.com/jhonathanqz">
  <img src="https://avatars.githubusercontent.com/u/74057391?s=96&v=4" height=90 />
</a>
<a href="https://github.com/Qz-Developer">
  <img src="https://avatars.githubusercontent.com/u/149726256?s=200&v=4" height=90 />
</a>
<br>
<a href="https://github.com/jhonathanqz" target="_blank">Jhonathan Queiroz</a> e
<a href="https://github.com/Qz-Developer" target="_blank">QZ Tech</a>
</div>

&#xa0;

<a href="#top">Voltar para o topo</a>
