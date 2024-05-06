package com.latihan.obat;

import java.util.List;

public class RequestTransaction {
    private String pasien;
    private String type;
    private Double priceTotal;
    private List<String> listObat;

    public void setPasien(String pasien) {
        this.pasien = pasien;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPriceTotal(Double priceTotal) {
        this.priceTotal = priceTotal;
    }

    public void setListObat(List<String> listObat) {
        this.listObat = listObat;
    }
}
