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

  private String method;
  private String transactionStatus;
  private String messageFromAuthorize;

  private String errorMessage;
  private String authorizationCode;

  public void setAuthorizationCode(String value){this.authorizationCode = value;}

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMsg) {
    this.errorMessage = errorMsg;
  }

  public String getMethod(){return this.method;}
  public void setMethod(String value) {this.method = value;}
  public String getTransactionStatus(){return this.transactionStatus;}
  public void setTransactionStatus(String value){this.transactionStatus = value;}
  public String getMessageFromAuthorize(){return this.messageFromAuthorize;}
  public void setMessageFromAuthorize(String value){this.messageFromAuthorize = value;}
  public String getActionCode(){return this.actionCode;}
  public void setActionCode(String value){this.actionCode = value;}

  public String getSerialNumber() {return this.serialNumber;}
  public void setSerialNumber(String value) {this.serialNumber = value;}
  public String getManufacture(){return this.manufacture;}
  public void setManufacture(String value){this.manufacture = value;}

  public int getIdFromBase(){return this.idFromBase;}
  public void setIdFromBase(int value) {this.idFromBase = value;}

  public String getAmount() {
    return this.amount;
  }
  public void setAmount(String value) {this.amount = value;}

  public String getCardHolderNumber() {
    return this.cardHolderNumber;
  }
  public void setCardHolderNumber(String value) {this.cardHolderNumber = value;}

  public String getCardBrand() {
    return this.cardBrand;
  }
  public void setCardBrand(String value) { this.cardBrand = value;}

  public String getDate() {
    return this.date;
  }
  public void setDate(String value) {this.date = value;}

  public String getTime() {
    return this.time;
  }
  public void setTime(String value) {this.time = value;}

  public String getAid(){return this.aid;}
  public void setAid(String value) {this.aid = value;}
  public String getArcq() {return this.arcq;}
  public void setArcq(String value) {this.arcq = value;}
  public String getTransactionReference(){return this.transactionReference;}
  public void setTransactionReference(String value) {this.transactionReference = value;}
  public String getSaleAffiliationKey(){return this.saleAffiliationKey;}
  public void setSaleAffiliationKey(String value) {this.saleAffiliationKey = value;}
  public String getEntryMode(){return this.entryMode;}
  public void setEntryMode(String value) {this.entryMode = value;}
  public String getTypeOfTransactionEnum(){return this.typeOfTransactionEnum;}
  public void setTypeOfTransactionEnum(String value) {this.typeOfTransactionEnum = value;}



  private int result = 0;

  public void setResult(int value) {this.result = value;}

  int isBuildResponse = 0;

  public int getBuildResponse() {
    if (isBuildResponse != 0) {
      int response = this.isBuildResponse;
      this.isBuildResponse = 0;
      return response;
    }
    return 0;
  }

  private boolean isPrinterRequest;

  public void setPrinterRequest(boolean value) {this.isPrinterRequest = value;}



  public void buildResponseStoneTransaction(List<TransactionObject> transactionObjects) {
    for (TransactionObject list : transactionObjects) {
      setIdFromBase(list.getIdFromBase());
      setActionCode(String.valueOf(list.getActionCode()));
      setAmount(list.getAmount());
      setCardHolderNumber(String.valueOf(list.getCardHolderNumber()));
      setDate(String.valueOf(list.getDate()));
      setTime(String.valueOf(list.getTime()));
      setAid(String.valueOf(list.getAid()));
      setArcq(String.valueOf(list.getArcq()));
      setSaleAffiliationKey(String.valueOf(list.getSaleAffiliationKey()));
      setEntryMode(String.valueOf(list.getEntryMode()));//
      setTypeOfTransactionEnum(String.valueOf(list.getTypeOfTransactionEnum()));//
      setSerialNumber(Stone.getPosAndroidDevice().getPosAndroidSerialNumber());
      setManufacture(Stone.getPosAndroidDevice().getPosAndroidManufacturer());
      setTransactionReference(String.valueOf(list.getTransactionReference()));
      setCardBrand(String.valueOf(list.getCardBrand()));//
      setPrinterRequest(true);
      
    }
    this.isBuildResponse = 1;
  }
}
