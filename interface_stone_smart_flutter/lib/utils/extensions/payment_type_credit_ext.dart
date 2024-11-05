import 'package:interface_stone_smart_flutter/utils/types/payment_type_credit.dart';

extension PaymentTypecreditExt on PaymentTypecredit {
  int get value {
    const Map<PaymentTypecredit, int> paymentTypecreditMap = {
      PaymentTypecredit.salesman: 2,
      PaymentTypecredit.client: 3,
    };
    return paymentTypecreditMap[this] ?? (throw 'Not Implemented');
  }
}
