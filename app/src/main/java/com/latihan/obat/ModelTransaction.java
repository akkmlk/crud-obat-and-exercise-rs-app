package com.latihan.obat;

public class ModelTransaction {
    private int id;
    private String pasien;
    private String type_obat;
    private Double price_total;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPasien() {
        return pasien;
    }

    public void setPasien(String pasien) {
        this.pasien = pasien;
    }

    public String getType_obat() {
        return type_obat;
    }

    public void setType_obat(String type_obat) {
        this.type_obat = type_obat;
    }

    public Double getPrice_total() {
        return price_total;
    }

    public void setPrice_total(Double price_total) {
        this.price_total = price_total;
    }
}
