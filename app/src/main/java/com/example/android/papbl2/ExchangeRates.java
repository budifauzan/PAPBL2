package com.example.android.papbl2;

public class ExchangeRates {
    private String currency, date, base;
    private double rate;

    public ExchangeRates(String currency, String date, String base, double rate) {
        this.currency = currency;
        this.date = date;
        this.base = base;
        this.rate = rate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
