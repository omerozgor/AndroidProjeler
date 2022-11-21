package com.ozgortech.retrofit;

import com.google.gson.annotations.SerializedName;
// Bu import ettiğimiz Retrofitin gson kütüphanesi, JSON verileri ile çalışmamızı
// kolaylaştırıyor. Hani bir nevi Room'um SQL ile çalışmayı kolaylaştırması gibi.
// Json dosyalarını okuma kodları ile çekme kodları ile uğraşmadan anotasyonlar ile
// neyin ne olduğunu belirtiyoruz.

public class CryptoModel {

    //JSON dosyasından gelecek currency değerini bizim currency değerimize ata demek bu
    @SerializedName("currency")
    private String currency;

    @SerializedName("price")
    private String price;

    @SerializedName("logo_url")
    private String logoUrl;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
