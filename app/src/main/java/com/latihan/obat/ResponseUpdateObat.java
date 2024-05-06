package com.latihan.obat;

public class ResponseUpdateObat {
    private boolean success;
    private String message;
    private ModelObat data;

    public boolean isSuccess() {
        return success;
    }

    public String message() {
        return message;
    }

    public ModelObat data() {
        return data;
    }
}
