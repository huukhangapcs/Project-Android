package com.example.moneyconverter;

import java.io.Serializable;

public class Country implements Serializable {
    private int flag;
    private String name;
    private String detail;
    private double exchangeRate;
    private double exchangeResult = 0;

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public double getExchangeResult() {
        return exchangeResult;
    }

    public void setExchangeResult(double exchangeResult) {
        this.exchangeResult = exchangeResult;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getFlag() {
        return flag;
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }


    public Country(int flag, String name, String detail, double exchangeRate) {
        this.flag = flag;
        this.name = name;
        this.detail = detail;
        this.exchangeRate = exchangeRate;
    }


}
