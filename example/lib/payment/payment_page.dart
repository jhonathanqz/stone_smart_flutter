import 'package:flutter/material.dart';
import 'package:stone_smart_flutter/payments/utils/payment_types.dart';
import 'package:stone_smart_flutter/stone_smart_flutter.dart';
import 'package:stone_smart_flutter_example/payment/payment_smart_controller.dart';

import 'payment_controller.dart';

class PaymentPage extends StatefulWidget {
  final PaymentController controller;
  final PaymentSmartController smartController;
  const PaymentPage({
    Key? key,
    required this.controller,
    required this.smartController,
  }) : super(key: key);

  @override
  _PaymentPageState createState() => _PaymentPageState();
}

class _PaymentPageState extends State<PaymentPage> {
  double? saleValue;
  TextEditingController moneyController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        child: Column(
          children: <Widget>[
            const SizedBox(
              height: 20,
            ),
            TextField(
              onChanged: (value) => setState(() {
                widget.controller.setSaleValue(moneyController.text.isNotEmpty
                    ? double.parse(moneyController.text)
                    : 0.0);
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
                      widget.controller.clickPayment = true;
                    });
                    StoneSmart.instance().payment.debitPayment(6);
                  },
                ),
                ElevatedButton(
                  child: const Text("Crédito"),
                  onPressed: widget.controller.enable
                      ? () {
                          FocusScope.of(context).unfocus();
                          setState(() {
                            widget.controller.clickPayment = true;
                          });
                          StoneSmart.instance()
                              .payment
                              .creditPayment(widget.controller.saleValue);
                        }
                      : null,
                ),
                ElevatedButton(
                  child: const Text("Crédito Parc- 2"),
                  onPressed: widget.controller.enable
                      ? () {
                          FocusScope.of(context).unfocus();
                          setState(() {
                            widget.controller.clickPayment = true;
                          });
                          StoneSmart.instance().payment.creditPaymentParc(
                                value: widget.controller.saleValue,
                                installment: 2,
                                withInterest: false,
                              );
                        }
                      : null,
                ),
                ElevatedButton(
                  child: const Text("Voucher"),
                  onPressed: widget.controller.enable
                      ? () {
                          FocusScope.of(context).unfocus();
                          setState(() {
                            widget.controller.clickPayment = true;
                          });
                          StoneSmart.instance()
                              .payment
                              .voucherPayment(widget.controller.saleValue);
                        }
                      : null,
                ),
                ElevatedButton(
                  child: const Text("ATIVAR PINPAD"),
                  onPressed: () async {
                    FocusScope.of(context).unfocus();
                    setState(() {
                      widget.controller.clickPayment = true;
                    });
                    StoneSmart.instance().payment.activePinpad(
                        appName: 'AppDemo', stoneCode: '206192723');
                  },
                ),
              ],
            ),
            const SizedBox(
              height: 10,
            ),
            ElevatedButton(
              onPressed: widget.controller.clickPayment
                  ? () {
                      widget.controller.setSaleValue(0.0);
                      StoneSmart.instance().payment.abortTransaction();
                    }
                  : null,
              child: const Text("Cancelar Operação"),
            ),
            const SizedBox(
              height: 10,
            ),
            ElevatedButton(
              onPressed: () {
                StoneSmart.instance().payment.abortTransaction();
              },
              child: const Text("Abortar transação"),
            ),
            const SizedBox(
              height: 10,
            ),
            ElevatedButton(
              onPressed: () {
                StoneSmart.instance().payment.cancelTransaction(
                      amount: widget.controller.saleValue,
                      transactionType: PaymentTypeTransaction.CREDIT,
                    );
              },
              child: const Text("Cancelar transação"),
            ),
            const SizedBox(
              height: 10,
            ),
            const Text("Status da Transação:"),
            const SizedBox(
              height: 10,
            ),
            ValueListenableBuilder(
              valueListenable: widget.controller.message,
              builder: (context, value, child) {
                return Text(value);
              },
            )
          ],
        ),
      ),
    );
  }
}
