package com.qztech.stone_smart_flutter.core;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

import stone.database.transaction.TransactionObject;
import stone.utils.Stone;

public class ActionResult {

  private int idFromBase;
  private String amount;
  private String cardHolderNumber;
  private String cardBrand;
  private String date;
  private String time;
  private String aid;
  private String arcq;
  private String transactionReference;
  private String saleAffiliationKey;
  private String entryMode;
  private String typeOfTransactionEnum;
  private String serialNumber;
  private String manufacture;
  private String actionCode;
  private int cardBrandId;

  private String method;
  private String transactionStatus;
  private String messageFromAuthorize;

  private String errorMessage;
  private String authorizationCode;
  private String transactionObject;

  private String userModel;

  private List<StoneTransaction> stoneTransactions;

  public void setUserModel(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.userModel = value;
  }

  public void setCardBrandId(int value) {
    this.cardBrandId = value;
  }

  public void setTransactionObject(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.transactionObject = value;
  }

  public void setAuthorizationCode(String value){
    if(value == null || value.isEmpty()) {
      return;
    }
    this.authorizationCode = value;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.errorMessage = value;
  }

  public String getMethod(){return this.method;}
  public void setMethod(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.method = value;}
  public String getTransactionStatus(){return this.transactionStatus;}
  public void setTransactionStatus(String value){
    if(value == null || value.isEmpty()) {
      return;
    }
    this.transactionStatus = value;}
  public String getMessageFromAuthorize(){return this.messageFromAuthorize;}
  public void setMessageFromAuthorize(String value){
    if(value == null || value.isEmpty()) {
      return;
    }
    this.messageFromAuthorize = value;}
  public String getActionCode(){return this.actionCode;}
  public void setActionCode(String value){
    if(value == null || value.isEmpty()) {
      return;
    }
    this.actionCode = value;}

  public String getSerialNumber() {return this.serialNumber;}
  public void setSerialNumber(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.serialNumber = value;}
  public String getManufacture(){return this.manufacture;}
  public void setManufacture(String value){
    if(value == null || value.isEmpty()) {
      return;
    }
    this.manufacture = value;}

  public int getIdFromBase(){return this.idFromBase;}
  public void setIdFromBase(int value) {

    this.idFromBase = value;}

  public String getAmount() {
    return this.amount;
  }
  public void setAmount(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.amount = value;}

  public String getCardHolderNumber() {
    return this.cardHolderNumber;
  }
  public void setCardHolderNumber(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.cardHolderNumber = value;}

  public String getCardBrand() {
    return this.cardBrand;
  }
  public void setCardBrand(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.cardBrand = value;}

  public String getDate() {
    return this.date;
  }
  public void setDate(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.date = value;}

  public String getTime() {
    return this.time;
  }
  public void setTime(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.time = value;}

  public String getAid(){return this.aid;}
  public void setAid(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.aid = value;}
  public String getArcq() {return this.arcq;}
  public void setArcq(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.arcq = value;}
  public String getTransactionReference(){return this.transactionReference;}
  public void setTransactionReference(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.transactionReference = value;}
  public String getSaleAffiliationKey(){return this.saleAffiliationKey;}
  public void setSaleAffiliationKey(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.saleAffiliationKey = value;}
  public String getEntryMode(){return this.entryMode;}
  public void setEntryMode(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.entryMode = value;}
  public String getTypeOfTransactionEnum(){return this.typeOfTransactionEnum;}
  public void setTypeOfTransactionEnum(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.typeOfTransactionEnum = value;}



  private int result = 0;

  public void setResult(int value) {

    this.result = value;}

  int isBuildResponse = 0;

  public int getBuildResponse() {
    if (isBuildResponse != 0) {
      int response = this.isBuildResponse;
      this.isBuildResponse = 0;
      return response;
    }
    return 0;
  }

  private boolean printCustomerSlipRequest;

  public void setPrinterRequest(boolean value) {this.printCustomerSlipRequest = value;}

  public String cardSequenceNumber;
  public void setCardSequenceNumber(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.cardSequenceNumber = value;
  }

  public String transactionKey;

  public void setTransactionKey(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.transactionKey = value;
  }

  public String externalId;
  public void setExternalID(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.externalId = value;
  }

  public String initiatorTransactionKey;
  public void setInitiatorTransactionKey(String value) {
    if(value == null || value.isEmpty()) {
      return;
    }
    this.initiatorTransactionKey = value;
  }



  public void buildResponseStoneTransaction(TransactionObject transactionObjects, boolean isPaymentApproved) {
    if (!isPaymentApproved) {
      return;
    }
    
    
    Gson gson = new Gson();
    String transactionObjectsJson = gson.toJson(transactionObjects);

    setIdFromBase(transactionObjects.getIdFromBase());
    setActionCode(String.valueOf(transactionObjects.getActionCode()));
    setAmount(transactionObjects.getAmount());
    setCardHolderNumber(String.valueOf(transactionObjects.getCardHolderNumber()));
    setDate(String.valueOf(transactionObjects.getDate()));
    setTime(String.valueOf(transactionObjects.getTime()));
    setAid(String.valueOf(transactionObjects.getAid()));
    setArcq(String.valueOf(transactionObjects.getArcq()));
    setSaleAffiliationKey(String.valueOf(transactionObjects.getSaleAffiliationKey()));
    setEntryMode(String.valueOf(transactionObjects.getEntryMode()));//
    setTypeOfTransactionEnum(String.valueOf(transactionObjects.getTypeOfTransactionEnum()));//
    setSerialNumber(Stone.getPosAndroidDevice().getPosAndroidSerialNumber());
    setManufacture(Stone.getPosAndroidDevice().getPosAndroidManufacturer());
    setTransactionReference(String.valueOf(transactionObjects.getTransactionReference()));
    setCardBrand(String.valueOf(transactionObjects.getCardBrandName()));//
    setCardBrandId(transactionObjects.getCardBrandId());
    setCardSequenceNumber(transactionObjects.getCardSequenceNumber());
    setTransactionObject(transactionObjectsJson);
    setAuthorizationCode(transactionObjects.getAuthorizationCode());
    setTransactionKey(transactionObjects.getAcquirerTransactionKey());
    setExternalID(transactionObjects.getExternalId());
    setInitiatorTransactionKey(transactionObjects.getInitiatorTransactionKey());
    setTransactionStatus(transactionObjects.getTransactionStatus().toString());
    setPrinterRequest(true);

    this.isBuildResponse = 1;
  }


  public void buildAllTransactions(List<TransactionObject> transactionObjects) {
    stoneTransactions.clear();
    StoneTransaction stoneTransaction = new StoneTransaction();
    for (int i = 0; i < transactionObjects.size(); i++) {
      stoneTransaction.setAmount(transactionObjects.get(i).getAmount());
      stoneTransaction.setIdFromBase(transactionObjects.get(i).getIdFromBase());
      stoneTransaction.setTransactionStatus(transactionObjects.get(i).getTransactionStatus().toString());
      stoneTransactions.add(stoneTransaction);
    }
  }
}
