import 'package:flutter/material.dart';
import 'package:stone_smart_flutter/stone_smart_flutter.dart';

import 'payment_controller.dart';

class PaymentPage extends StatefulWidget {
  const PaymentPage({Key? key}) : super(key: key);

  @override
  _PaymentPageState createState() => _PaymentPageState();
}

class _PaymentPageState extends State<PaymentPage> {
  final PaymentController controller = PaymentController();

  double? saleValue;
  TextEditingController moneyController = TextEditingController();

  @override
  void initState() {
    StoneSmart.instance().initPayment(handler: controller);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Column(
        children: <Widget>[
          const SizedBox(
            height: 20,
          ),
          TextField(
            onChanged: (value) => setState(() {
              controller.setSaleValue(moneyController.text.isNotEmpty ? double.parse(moneyController.text) : 0.0);
            }),
            keyboardType: TextInputType.number,
            decoration: const InputDecoration(hintText: "Digite o valor"),
            controller: moneyController,
          ),
          const SizedBox(
            height: 20,
          ),
          const Text(
            "Selecione o método de pagamento",
            style: TextStyle(fontSize: 16),
          ),
          const SizedBox(
            height: 20,
          ),
          Wrap(
            spacing: 10.0,
            children: <Widget>[
              ElevatedButton(
                child: const Text("Débito"),
                onPressed: () {
                  FocusScope.of(context).unfocus();
                  setState(() {
                    controller.clickPayment = true;
                  });
                  StoneSmart.instance().payment.debitPayment(6);
                },
              ),
              ElevatedButton(
                child: const Text("Crédito"),
                onPressed: controller.enable
                    ? () {
                        FocusScope.of(context).unfocus();
                        setState(() {
                          controller.clickPayment = true;
                        });
                        StoneSmart.instance().payment.creditPayment(controller.saleValue);
                      }
                    : null,
              ),
              ElevatedButton(
                child: const Text("Crédito Parc- 2"),
                onPressed: controller.enable
                    ? () {
                        FocusScope.of(context).unfocus();
                        setState(() {
                          controller.clickPayment = true;
                        });
                        StoneSmart.instance().payment.creditPaymentParc(
                              value: controller.saleValue,
                              installment: 2,
                              withInterest: false,
                            );
                      }
                    : null,
              ),
              ElevatedButton(
                child: const Text("Voucher"),
                onPressed: controller.enable
                    ? () {
                        FocusScope.of(context).unfocus();
                        setState(() {
                          controller.clickPayment = true;
                        });
                        StoneSmart.instance().payment.voucherPayment(controller.saleValue);
                      }
                    : null,
              ),
              ElevatedButton(
                child: const Text("ATIVAR PINPAD"),
                onPressed: () async {
                  FocusScope.of(context).unfocus();
                  setState(() {
                    controller.clickPayment = true;
                  });
                  StoneSmart.instance().payment.activePinpad(appName: 'AppDemo', stoneCode: '206192723');
                },
              ),
            ],
          ),
          const SizedBox(
            height: 20,
          ),
          ElevatedButton(
            onPressed: controller.clickPayment
                ? () {
                    controller.setSaleValue(0.0);
                    StoneSmart.instance().payment.abortTransaction();
                  }
                : null,
            child: const Text("Cancelar Operação"),
          ),
          const SizedBox(
            height: 20,
          ),
          ElevatedButton(
            onPressed: () {
              StoneSmart.instance().payment.abortTransaction();
            },
            child: const Text("Abortar transação"),
          ),
          const SizedBox(
            height: 20,
          ),
          ElevatedButton(
            onPressed: () {
              StoneSmart.instance().payment.cancelTransaction(
                    idFromBase: 0, //Precisa pegar o id da transação.
                  );

              /**
               * Caso não tenha o Id da Transação, pode ser utilizado o método abaixo para obter a lista de transações
               * StoneSmart.instance().payment.getAllTransactions();
               * a resposta vem no método onFinishedResponse, no campo "stoneTransactions"
               */
            },
            child: const Text("Cancelar transação"),
          ),
        ],
      ),
    );
  }
}
