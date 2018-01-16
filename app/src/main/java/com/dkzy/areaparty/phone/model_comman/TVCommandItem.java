package com.dkzy.areaparty.phone.model_comman;

import java.util.List;

/**
 * Created by poxiaoge on 2016/12/23.
 */

public class TVCommandItem {
    private String firstcommand;
    private String secondcommand;
    private Boolean thirdcommand;
    private String fourthcommand;
    private String fifthcommand;
    private List<String> sixthcommand;
    private String sevencommand;

    public List<String> getSixthcommand() {
        return sixthcommand;
    }

    public void setSixthcommand(List<String> sixthcommand) {
        this.sixthcommand = sixthcommand;
    }

    public String getFourthCommand() {
        return fourthcommand;
    }

    public void setFourthCommand(String fourthCommand) {
        this.fourthcommand = fourthCommand;
    }

    public String getFifthCommand() {
        return fifthcommand;
    }

    public void setFifthCommand(String fifthCommand) {
        this.fifthcommand = fifthCommand;
    }

    public void setFirstcommand(String firstcommand) {
        this.firstcommand = firstcommand;
    }

    public String getFirstcommand() {
        return firstcommand;
    }

    public void setSecondcommand(String secondcommand) {
        this.secondcommand = secondcommand;
    }

    public String getSecondcommand() {
        return secondcommand;
    }

    public void setThirdcommand(Boolean thirdcommand) {
        this.thirdcommand = thirdcommand;
    }

    public Boolean getThirdcommand() {
        return thirdcommand;
    }

    public void setSevencommand(String sevencommand) {
        this.sevencommand = sevencommand;
    }

    public String getSevencommand() {
        return sevencommand;
    }

}
