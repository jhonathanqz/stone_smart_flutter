package com.qztech.stone_smart_flutter.core;

public class StoneTransaction {
    private int idFromBase;
    private String amount;
    private String transactionStatus;
    public int getIdFromBase() {
        return idFromBase;
    }

    public String getAmount() {
        return amount;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setIdFromBase(int value) {
        this.idFromBase  = value;
    }

    public void setAmount(String value) {
        this.amount = value;
    }

    public void setTransactionStatus(String value) {
        this.transactionStatus = value;
    }



}
