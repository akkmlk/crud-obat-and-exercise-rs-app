package com.latihan.obat;

import java.util.List;

public class ResponseObat {
    private boolean success;
    private List<ModelObat> data;

    public boolean isSuccess() {
        return success;
    }

    public List<ModelObat> getDataObat() {
        return data;
    }
}
