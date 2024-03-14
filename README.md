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
- SDK Stone version: 4.8.7

## :checkered_flag: Configuração

### # Pubspec.yaml

Para usar este plugin, adicione `stone_smart_flutter` como [dependência](https://flutter.io/using-packages/) ao seu arquivo `pubspec.yaml`.

```yaml
dependencies:
  stone_smart_flutter: any
```

This will get you the latest version.

### # Build.gradle

Em seu build.gradle a nivel do app, a propriedade `minSdkVersion` precisa ser level 23. Recurso este exigido pela versão 4.8.7 do SDK Stone.

```xml
...
defaultConfig {
        applicationId "com.example.stone_example"
        minSdkVersion 23
        targetSdkVersion flutter.targetSdkVersion
        versionCode flutterVersionCode.toInteger()
        versionName flutterVersionName
    }
...
```

### # Implementação

Para começar é necessário criar uma classe que implemente ´StoneHandler´, sendo que essa é a responsável por monitorar e retornar os dados da Stone.

### Criando classe StoneController

```
class StoneController extends StoneHandler {
  int saleValue = 0;
  bool enable = false;
  bool clickPayment = false;
  bool enableRefund = false;
  String? transactionCode;
  String? transactionId;
  String? response;

  void setSaleValue(double value) {
    if (value > 0.0) {
      saleValue = (value * 100).toInt();
      clickPayment = false;
      enable = true;
    } else {
      clickPayment = false;
      enable = false;
    }
  }

  @override
  void disposeDialog() {
    BotToast.cleanAll();
  }

  @override
  void onAbortedSuccessfully() {
    BotToast.showText(text: "Operação cancelada");
  }

  @override
  void onActivationDialog() {}

  @override
  void onAuthProgress(String message) {
    BotToast.showLoading();
  }

  @override
  void onError(String message) {
    BotToast.showText(text: message);
  }

  @override
  void onMessage(String message) {
    BotToast.showText(text: message);
  }

  @override
  void onFinishedResponse(String message) {
    BotToast.showText(text: message);
  }

  @override
  void onTransactionSuccess() {
    BotToast.showText(text: "Transacao com successo!");
  }

  @override
  void writeToFile({
    String? transactionCode,
    String? transactionId,
    String? response,
  }) {}

  @override
  void onLoading(bool show) {
    if (show) {
      BotToast.showLoading();
      return;
    }
    BotToast.closeAllLoading();
  }

  @override
  void onTransactionInfo({
    String? transactionCode,
    String? transactionId,
    String? response,
  }) {
    this.transactionCode = transactionCode;
    this.transactionId = transactionId;
    this.response = response;
    BotToast.showText(
        text:
            "{transactionCode: $transactionCode \n transactionId: $transactionId}");
    enableRefund = true;
  }

  @override
  void onChanged(String message) {
  }
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

##### onTransactionInfo

Método resposável por devolver uma response completa da transação, sendo possível mapear vários campos retornados.

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
