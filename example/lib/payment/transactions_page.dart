import 'package:flutter/material.dart';
import 'package:stone_smart_flutter_example/payment/payment_smart_controller.dart';

import 'payment_controller.dart';

class TransactionsPage extends StatefulWidget {
  final PaymentController controller;
  final PaymentSmartController smartController;
  const TransactionsPage({
    Key? key,
    required this.controller,
    required this.smartController,
  }) : super(key: key);

  @override
  _TransactionsPageState createState() => _TransactionsPageState();
}

class _TransactionsPageState extends State<TransactionsPage> {
  double? saleValue;
  TextEditingController moneyController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder(
      valueListenable: widget.controller.message,
      builder: (context, value, child) {
        return ListView.builder(
            itemCount: widget.smartController.transactions.value.length,
            itemBuilder: (context, index) {
              final transaction =
                  widget.smartController.transactions.value[index];
              return ListTile(
                title: Text(transaction['transactionCode']),
                subtitle: Text(transaction['transactionId']),
                trailing: Text(transaction['value']),
              );
            });
      },
    );
  }
}
