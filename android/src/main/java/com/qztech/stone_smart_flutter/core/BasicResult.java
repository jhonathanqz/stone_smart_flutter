package com.qztech.stone_smart_flutter.core;


public class BasicResult {
    private String method;
    private String errorMessage;
    private int result;
    private String message;
    private String userModel;

    public void setUserModel(String value) {
        this.userModel = value;
    }
    public void setMethod(String value) {
        this.method = value;
    }

    public void setResult(int value) {
        this.result = value;
    }

    public void setErrorMessage(String value){ this.errorMessage = value;}

    public void setMessage(String value){this.message = value;}

    public String getMessage(){return this.message;}

    public String getMethod(){return this.method;}
    public String getErrorMessage(){return this.errorMessage;}
    public int getResult(){return this.result;}
}
