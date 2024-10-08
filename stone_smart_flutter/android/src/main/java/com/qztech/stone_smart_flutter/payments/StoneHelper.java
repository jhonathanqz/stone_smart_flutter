package com.qztech.stone_smart_flutter.payments;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.List;

import stone.application.enums.Action;
import stone.application.enums.ErrorsEnum;
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
                return "Processando pagamento. Aguarde.";
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
        if(response == null) {
            return "Erro na operação";
        }

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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        value.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    public String getErrorFromErrorList(List<ErrorsEnum> list) {
        if(list.isEmpty()) {
            return "";
        }
        final ErrorsEnum error = list.get(0);
        return getErrorMessageFromErrorEnum(error);
    }

    public String getErrorMessageFromErrorEnum(ErrorsEnum errorsEnum) {
        String aux = "";
        switch (errorsEnum){
            case CARD_BLOCKED:
                aux =  "Cartão bloqueado";
                break;
            case CONNECTION_NOT_FOUND:
                aux= "Sem conexão com a internet, ligue a internet.";
                break;
            case DEVICE_NOT_COMPATIBLE:
                aux= "Dispositivo bluetooth não possui a biblioteca compartilhada.";
                break;
            case UNEXPECTED_STATUS_COMMAND:
                aux= "Status de um comando inexperado, tente novamente.";
                break;
            case GENERIC_ERROR:
                aux= "Erro genérico";
                break;
            case IO_ERROR_WITH_PINPAD:
                aux= "Erro de leitura e escrita com o Pinpad.";
                break;
            case PINPAD_CONNECTION_NOT_FOUND:
                aux= "Sem conexão com um Pinpad, conecte-se com um dispositivo.";
                break;
            case TABLES_NOT_FOUND:
                aux= "";
                break;
            case NEED_LOAD_TABLES:
                aux= "";
                break;
            case TIME_OUT:
                aux= "Tempo expirado, tente novamente.";
                break;
            case OPERATION_CANCELLED_BY_USER:
                aux= "Operação cancelada pelo usuário.";
                break;
            case CARD_REMOVED_BY_USER:
                aux= "Cartão removido pelo usuário indevidamente.";
                break;
            case CANT_READ_CARD_HOLDER_INFORMATION:
                aux= "Erro na leitura das informações do cartão, tente novamente.";
                break;
            case INVALID_TRANSACTION:
                aux= "Transação inválida, talvez um cartão de crédito esteja passando uma transação de débito, ou viceversa.";
                break;
            case PASS_TARGE_WITH_CHIP:
                aux= "Cartão de chip passou tarja.";
                break;
            case NULL_RESPONSE:
                aux= "Não houve resposta do Pinpad.";
                break;
            case ERROR_RESPONSE_COMMAND:
                aux= "Erro na resposta do comando.";
                break;
            case ACCEPTOR_REJECTION:
                aux= "Transação rejeitada pelo autorizador.";
                break;
            case EMAIL_MESSAGE_ERROR:
                aux= "Erro no envio do email.";
                break;
            case INVALID_EMAIL_CLIENT:
                aux= "Email do cliente é inválido.";
                break;
            case EMAIL_EMPTY:
                aux= "Email do cliente está vazio.";
                break;
            case EMAIL_RECIPIENT_EMPTY:
                aux= "Email do destinatário vazio";
                break;
            case INVALID_STONE_CODE_OR_UNKNOWN:
                aux= "StoneCode foi digitado de forma errada ou não existe.";
                break;
            case TRANSACTION_NOT_FOUND:
                aux= "Não se pode cancelar uma transação que não foi aprovada.";
                break;
            case INVALID_TRANSACTION_STATUS:
                aux= "Status da transação inválido";
                break;
            case INVALID_STONECODE:
                aux= "Seu stonecode tem mais do que 9 caracteres.";
                break;
            case USERMODEL_NOT_FOUND:
                aux= "User Model não encontrado.";
                break;
            case NO_PRINT_SUPPORT:
                aux= "Seu Pinpad não tem suporte para impressão.";
                break;
            case CANT_READ_CHIP_CARD:
                aux= "O Pinpad não consegue ler o chip do cartão.";
                break;
            case PINPAD_WITHOUT_KEY:
                aux= "O Pinpad não tem chave de criptografia";
                break;
            case PINPAD_WITHOUT_STONE_KEY:
                aux= "O Pinpad não tem a chave de criptografia da Stone.";
                break;
            case PINPAD_ALREADY_CONNECTED:
                aux= "Pinpad já conectado.";
                break;
            case CONNECTIVITY_ERROR:
                aux= "Erro de conectividade.";
                break;
            case SWIPE_INCORRECT:
                aux= "";
                break;
            case TOO_MANY_CARDS:
                aux= "";
                break;
            case UNKNOWN_ERROR:
                aux= "";
                break;
            case INTERNAL_ERROR:
                aux= "Erro interno";
                break;
            case EMV_GENERIC_ERROR:
                aux= "Erro genérico de Smartcard (Contato/Contactless)";
                break;
            case SWITCH_INTERFACE:
                aux= "";
                break;
            case EMV_FAILED_CARD_CONN_ERROR:
                aux= "Falha ao comunicar com o cartão";
                break;
            case EMV_NO_APP_ERROR:
                aux= "Nenhuma aplicação compatível (Crédito/Débito) disponível no cartão";
                break;
            case EMV_INITIALIZATION_ERROR:
                aux= "Erro ao inicializar fluxo de pagamento";
                break;
            case EMV_CAPK_ERROR:
                aux= "Erro ao configurar chave pública da bandeira";
                break;
            case EMV_TLV_ERROR:
                aux= "Erro no formato (TLV) dos dados de configuração";
                break;
            case EMV_NO_CAPK_ERROR:
                aux= "Chave pública da bandeira não está presente";
                break;
            case EMV_AID_ERROR:
                aux= "Erro ao configurar aplicação (Crédito/Débito)";
                break;
            case PRINTER_GENERIC_ERROR:
                aux= "Erro genérico de impressão";
                break;
            case PRINTER_BUSY_ERROR:
                aux= "Impressora ocupada.";
                break;
            case PRINTER_INIT_ERROR:
                aux= "Erro ao inicializar impressora";
                break;
            case PRINTER_LOW_ENERGY_ERROR:
                aux= "Erro de baixa energia da impressora";
                break;
            case PRINTER_OUT_OF_PAPER_ERROR:
                aux= "Impressora sem papel ou com a tampa de bobina aberta";
                break;
            case PRINTER_UNSUPPORTED_FORMAT_ERROR:
                aux= "Algum formato enviado não corresponde ao padrão de texto, imagem ou texto customizado";
                break;
            case PRINTER_INVALID_DATA_ERROR:
                aux= "Limite máximo do buffer foi ultrapassado";
                break;
            case PRINTER_OVERHEATING_ERROR:
                aux= "Erro de superaquecimento na impressora";
                break;
            case PED_PASS_GENERIC_ERROR:
                aux= "Erro genérico na criptografia do PIN";
                break;
            case PED_PASS_KEY_ERROR:
                aux= "";
                break;
            case PED_PASS_USER_CANCELED_ERROR:
                aux= "";
                break;
            case PED_PASS_NO_PIN_INPUT_ERROR:
                aux= "";
                break;
            case PED_PASS_TIMEOUT_ERROR:
                aux= "";
                break;
            case PED_PASS_INIT_ERROR:
                aux= "Erro na inicialização do processo de criptografia do PIN";
                break;
            case PED_PASS_CRYPT_ERROR:
                aux= "Erro de criptografia do PIN";
                break;
            case PED_PASS_NO_KEY_FOUND_ERROR:
                aux= "";
                break;
            case TRANS_GENERIC_ERROR:
                aux= "Erro genérico de transação";
                break;
            case TRANS_APP_BLOCKED_ERROR:
                aux= "";
                break;
            case TRANS_SELECT_TYPE_USER_CANCELED_ERROR:
                aux= "";
                break;
            case TRANS_INVALID_AMOUNT_ERROR:
                aux= "Valor inválido";
                break;
            case TRANS_PASS_MAG_BUT_IS_ICC_ERROR:
                aux= "";
                break;
            case TRANS_NO_TRANS_TYPE_ERROR:
                aux= "";
                break;
            case TRANS_WRONG_TRANS_TYPE_ERROR:
                aux= "";
                break;
            case TRANS_APP_INVALID_ERROR:
                aux= "Aplicação selecionada (Crédito/Débito) é inválida para esse fluxo";
                break;
            case TRANS_CVV_NOT_PROVIDED_ERROR:
                aux= "CVV é obrigatório para esta transação e não foi provido";
                break;
            case TRANS_CVV_INVALID_ERROR:
                aux= "CVV é inválido";
                break;
            case TRANS_APP_INVALID_INDEX_ERROR:
                aux= "Erro de índice inválido";
                break;
            case TRANS_ONLINE_PROCESS_ERROR_ERROR:
                aux= "Erro ao gerar os dados para processar a transação junto ao autorizador";
                break;
            case CARD_GENERIC_ERROR:
                aux= "Erro genérico do Cartão";
                break;
            case CARD_READ_ERROR:
                aux= "Erro ao ler cartão";
                break;
            case CARD_READ_TIMEOUT_ERROR:
                aux= "Tempo excedido na tentativa de ler cartão";
                break;
            case CARD_READ_CANCELED_ERROR:
                aux= "Erro de leitura do cartão";
                break;
            case CARD_UNSUPPORTED_ERROR:
                aux= "cartão não suportado";
                break;
            case CARD_READ_MULTI_ERROR:
                aux= "Multiplos cartões detectados na area de Aproximação";
                break;
            case TRANSACTION_FALLBACK_STARTED:
                aux= "";
                break;
            case TRANSACTION_FALLBACK_TIMEOUT:
                aux= "";
                break;
            case TRANSACTION_FALLBACK_INVALID_CARD_MODE:
                aux= "";
                break;
            case NO_ACTIVE_APPLICATION:
                aux= "";
                break;
            case MULTI_INSTANCES_OF_PROVIDER_RUNNING:
                aux= "";
                break;
            case UNKNOWN_TYPE_OF_USER:
                aux= "";
                break;
            case TRANSACTION_OBJECT_NULL_ERROR:
                aux= "";
                break;
            case INVALID_AMOUNT:
                aux= "Valor inválido";
                break;
            case APPNAME_NOT_SET:
                aux= "";
                break;
            case SDK_VERSION_OUTDATED:
                aux= "";
                break;
            case PINPAD_CLOSED_CONNECTION:
                aux= "";
                break;
            case NOT_STONE_POS_OR_POS_MISCONFIGURED:
                aux= "";
                break;
            case COULD_NOT_ACTIVATE_ALL_STONE_CODES:
                aux= "";
                break;
            case COULD_NOT_ACTIVATE_WITH_ACCEPTOR_CONFIGURATION_UPDATE_DATA_NULL:
                aux= "";
                break;
            case NO_MIFARE_SUPPORT:
                aux= "";
                break;
            case MIFARE_ABORTED:
                aux= "";
                break;
            case MIFARE_DETECT_TIMEOUT:
                aux= "";
                break;
            case MIFARE_WRONG_CARD_TYPE:
                aux= "";
                break;
            case MIFARE_INVALID_KEY:
                aux= "";
                break;
            case MIFARE_NOT_AUTHENTICATED:
                aux= "";
                break;
            case MIFARE_INVALID_SECTOR_NUMBER:
                aux= "";
                break;
            case MIFARE_INVALID_BLOCK_NUMBER:
                aux= "";
                break;
            case MIFARE_INVALID_BLOCK_FORMAT:
                aux= "";
                break;
            case MIFARE_MULTI_CARD_DETECTED:
                aux= "";
                break;
            case DATA_CONTAINER_CONSTRAINT_ERROR:
                aux= "";
                break;
            case DATA_CONTAINER_INTEGRATION_ERROR:
                aux= "";
                break;
            case QRCODE_NOT_GENERATED:
                aux= "";
                break;
            case QRCODE_EXPIRED:
                aux= "QRCode expirado";
                break;

            default:
                aux = "";
                break;
        }
        return errorsEnum + ": " + aux;
    }
}
