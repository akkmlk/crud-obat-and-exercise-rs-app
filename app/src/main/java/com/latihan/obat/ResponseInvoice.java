package com.latihan.obat;

import java.util.List;

public class ResponseInvoice {
    private boolean success;
    private ModelTransaction data;
    private List<ModelTransactionDetail> detail;

    public boolean isSuccess() {
        return success;
    }

    public ModelTransaction getData() {
        return data;
    }

    public List<ModelTransactionDetail> getDetail() {
        return detail;
    }
}
