package com.latihan.obat;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ObatCreateService {
    @POST("obat")
    Call<ResponseCreateObat> getCreateObat(@Body RequestCreateObat requestCreateObat);
}
