package com.example.moneyconverter;

public class HistoryRecord {
    private String result;
    private String date;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public HistoryRecord(String result, String date) {
        this.result = result;
        this.date = date;
    }
}
