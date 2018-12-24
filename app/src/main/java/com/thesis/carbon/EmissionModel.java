package com.thesis.carbon;

public class EmissionModel {

    private long timestamp;
    private int emi_val;
    private String emi_val_unit;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getEmi_val() {
        return emi_val;
    }

    public void setEmi_val(int emi_val) {
        this.emi_val = emi_val;
    }

    public String getEmi_val_unit() {
        return emi_val_unit;
    }

    public void setEmi_val_unit(String emi_val_unit) {
        this.emi_val_unit = emi_val_unit;
    }
}
