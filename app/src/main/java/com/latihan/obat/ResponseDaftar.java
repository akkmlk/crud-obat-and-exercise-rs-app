package com.latihan.obat;

public class ResponseDaftar {
    private boolean success;
    private String message;
    private ModelUser data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ModelUser data() {
        return data;
    }
}
