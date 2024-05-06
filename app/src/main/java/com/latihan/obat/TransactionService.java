package com.latihan.obat;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TransactionService {
    @POST("add-cart")
    Call<ResponseTransaction> createTransaction(@Body RequestTransaction requestTransaction);
}
