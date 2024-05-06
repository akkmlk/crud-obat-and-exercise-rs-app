package com.latihan.obat;

public class ModelObat{

    int id;
    private String name;
    private Double price;
    private String kategori;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return kategori;
    }

    public void setCategory(String kategori) {
        this.kategori = kategori;
    }

}
