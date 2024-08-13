import 'package:flutter/material.dart';
import 'package:stone_smart_flutter/stone_smart_flutter.dart';
import 'package:stone_smart_flutter_example/payment/payment_controller.dart';
import 'package:stone_smart_flutter_example/payment/payment_smart_controller.dart';
import 'package:stone_smart_flutter_example/payment/transactions_page.dart';

import 'payment/payment_page.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final PaymentController controller = PaymentController();
  final PaymentSmartController smartController = PaymentSmartController();

  @override
  void initState() {
    StoneSmart.instance()
        .initPayment(handler: controller, iStoneSmartHandler: smartController);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: DefaultTabController(
        length: 2,
        child: Scaffold(
          appBar: AppBar(
            title: const Text('Stone Smart Flutter'),
            bottom: const TabBar(tabs: [
              Tab(
                child: Text("Vender"),
              ),
              Tab(
                child: Text("Transações"),
              )
            ]),
          ),
          body: TabBarView(
            children: [
              PaymentPage(
                controller: controller,
                smartController: smartController,
              ),
              TransactionsPage(
                controller: controller,
                smartController: smartController,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
