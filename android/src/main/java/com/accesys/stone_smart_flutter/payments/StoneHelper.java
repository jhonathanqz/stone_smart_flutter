package com.accesys.stone_smart_flutter.payments;

import android.graphics.Bitmap;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;

import stone.application.enums.Action;
import stone.application.enums.InstalmentTransactionEnum;
import stone.application.enums.TypeOfTransactionEnum;
import stone.application.xml.enums.ResponseCodeEnum;
import stone.database.transaction.TransactionObject;

public class StoneHelper {

    @NonNull
    public TransactionObject getTransactionObject(String amount, int typeTransaction, int parc, boolean withInterest) {
        final TransactionObject transaction = new TransactionObject();
        transaction.setAmount(amount);
        transaction.setInstalmentTransaction(getInstallment(parc, withInterest));
        TypeOfTransactionEnum typeSelected = getTypeTransaction(typeTransaction);
        transaction.setTypeOfTransaction(typeSelected);
        transaction.setCapture(true);
        return transaction;
    }

    TypeOfTransactionEnum getTypeTransaction(int type) {
        switch (type) {
            case 1:
                return TypeOfTransactionEnum.CREDIT;
            case 2:
                return TypeOfTransactionEnum.DEBIT;
            case 3:
                return TypeOfTransactionEnum.PIX;
            case 4:
                return TypeOfTransactionEnum.VOUCHER;
            default:
                return TypeOfTransactionEnum.INSTANT_PAYMENT;
        }
    }


    public String getMessageFromTransactionAction(Action action) {
        switch (action) {
            case  TRANSACTION_WAITING_CARD:
                return "Insira, passe ou aproxime o cartão.";
            case  TRANSACTION_WAITING_PASSWORD:
                return "Digite a sua senha.";
            case  TRANSACTION_SENDING:
                return "Enviando transação. Aguarde.";
            case  TRANSACTION_REMOVE_CARD:
                return "Remova o cartão.";
            case  TRANSACTION_CARD_REMOVED:
                return "Cartão removido.";
            case  REVERSING_TRANSACTION_WITH_ERROR:
                return "Abortando transação.";
            case  TRANSACTION_TYPE_SELECTION:
                return "Selecione Alimentação ou Refeição.";
            case  TRANSACTION_WAITING_QRCODE_SCAN:
                return "Leia o QRCode para prosseguir com o pagamento.";
            default:
                return "Em processamento. Aguarde.";
        }
    }

    public String getMessageFromResponseCodeEnum(ResponseCodeEnum response) {
        switch (response) {
            case Declined:
                return "Recusado";
            case Approved:
                return "Aprovado";
            case TechnicalError:
                return "Erro técnico";
            case Default:
                return "Erro na operação";
            default:
                return "Erro na operação";
        }
    }

    private InstalmentTransactionEnum getInstallment(int parc, boolean withInterest) {
    if(parc == 1){
        return InstalmentTransactionEnum.ONE_INSTALMENT;
    }
    if (withInterest) {
        switch (parc) {
            case 2:
                return InstalmentTransactionEnum.TWO_INSTALMENT_WITH_INTEREST;
            case 3:
                return InstalmentTransactionEnum.THREE_INSTALMENT_WITH_INTEREST;
            case 4:
                return InstalmentTransactionEnum.FOUR_INSTALMENT_WITH_INTEREST;
            case 5:
                return InstalmentTransactionEnum.FIVE_INSTALMENT_WITH_INTEREST;
            case 6:
                return InstalmentTransactionEnum.SIX_INSTALMENT_WITH_INTEREST;
            case 7:
                return InstalmentTransactionEnum.SEVEN_INSTALMENT_WITH_INTEREST;
            case 8:
                return InstalmentTransactionEnum.EIGHT_INSTALMENT_WITH_INTEREST;
            case 9:
                return InstalmentTransactionEnum.NINE_INSTALMENT_WITH_INTEREST;
            case 10:
                return InstalmentTransactionEnum.TEN_INSTALMENT_WITH_INTEREST;
            case 11:
                return InstalmentTransactionEnum.ELEVEN_INSTALMENT_WITH_INTEREST;
            case 12:
                return InstalmentTransactionEnum.TWELVE_INSTALMENT_WITH_INTEREST;
            case 13:
                return InstalmentTransactionEnum.THIRTEEN_INSTALMENT_WITH_INTEREST;
            default:
                return InstalmentTransactionEnum.ONE_INSTALMENT;
        }
    }
        switch (parc) {
            case 2:
                return InstalmentTransactionEnum.TWO_INSTALMENT_NO_INTEREST;
            case 3:
                return InstalmentTransactionEnum.THREE_INSTALMENT_NO_INTEREST;
            case 4:
                return InstalmentTransactionEnum.FOUR_INSTALMENT_NO_INTEREST;
            case 5:
                return InstalmentTransactionEnum.FIVE_INSTALMENT_NO_INTEREST;
            case 6:
                return InstalmentTransactionEnum.SIX_INSTALMENT_NO_INTEREST;
            case 7:
                return InstalmentTransactionEnum.SEVEN_INSTALMENT_NO_INTEREST;
            case 8:
                return InstalmentTransactionEnum.EIGHT_INSTALMENT_NO_INTEREST;
            case 9:
                return InstalmentTransactionEnum.NINE_INSTALMENT_NO_INTEREST;
            case 10:
                return InstalmentTransactionEnum.TEN_INSTALMENT_NO_INTEREST;
            case 11:
                return InstalmentTransactionEnum.ELEVEN_INSTALMENT_NO_INTEREST;
            case 12:
                return InstalmentTransactionEnum.TWELVE_INSTALMENT_NO_INTEREST;
            case 13:
                return InstalmentTransactionEnum.THIRTEEN_INSTALMENT_NO_INTEREST;
            default:
                return InstalmentTransactionEnum.ONE_INSTALMENT;
        }
    }

    public String convertBitmapToString(Bitmap value) {
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        value.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}
