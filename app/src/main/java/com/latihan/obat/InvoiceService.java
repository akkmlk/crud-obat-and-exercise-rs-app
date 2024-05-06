package com.latihan.obat;

import retrofit2.Call;
import retrofit2.http.GET;

public interface InvoiceService {
    @GET("invoice")
    Call<ResponseInvoice> getInvoice();
}
