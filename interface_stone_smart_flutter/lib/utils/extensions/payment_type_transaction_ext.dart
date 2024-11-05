import 'package:interface_stone_smart_flutter/utils/types/payment_type_transaction.dart';

extension PaymentTypeTransactionExt on PaymentTypeTransaction {
  int get type {
    Map<PaymentTypeTransaction, int> paymentTypeTransactionMap = {
      PaymentTypeTransaction.credit: 1,
      PaymentTypeTransaction.debit: 2,
      PaymentTypeTransaction.pix: 3,
      PaymentTypeTransaction.voucher: 4,
    };
    return paymentTypeTransactionMap[this] ?? (throw 'Not Implemented');
  }
}
