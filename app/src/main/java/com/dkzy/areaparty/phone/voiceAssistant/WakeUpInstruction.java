package com.dkzy.areaparty.phone.voiceAssistant;

/**
 * Created by Park on 2018/5/7.
 */

public class WakeUpInstruction {
    private String errorDesc;
    private int errorCode;
    private String word;
    public String getErrorDesc(){
        return errorDesc;
    }
    public int getErrorCode(){
        return errorCode;
    }
    public String getWord(){
        return word;
    }
    public void setErrorDesc(){
        this.errorDesc = errorDesc;
    }
    public void setErrorCode(){
        this.errorCode = errorCode;
    }
    public void setWord(){
        this.word = word;
    }
}
