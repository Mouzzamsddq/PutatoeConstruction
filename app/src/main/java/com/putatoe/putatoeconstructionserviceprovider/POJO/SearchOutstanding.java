package com.putatoe.putatoeconstructionserviceprovider.POJO;

public class SearchOutstanding {


    private String mobileNumber;
    private float  outstanding;


    public SearchOutstanding() {
    }

    public SearchOutstanding(String mobileNumber, float outstanding) {
        this.mobileNumber = mobileNumber;
        this.outstanding = outstanding;
    }


    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public float getOutstanding() {
        return outstanding;
    }

    public void setOutstanding(float outstanding) {
        this.outstanding = outstanding;
    }
}
