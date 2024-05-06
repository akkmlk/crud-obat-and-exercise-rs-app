package com.latihan.obat;

public class ModelTransactionDetail {
    private int id;
    private int transaction_id;
    private String obat_dibeli;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getObat_dibeli() {
        return obat_dibeli;
    }

    public void setObat_dibeli(String obat_dibeli) {
        this.obat_dibeli = obat_dibeli;
    }
}
