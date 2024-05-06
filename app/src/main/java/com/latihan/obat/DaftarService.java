package com.latihan.obat;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DaftarService {
    @POST("register")
    Call<ResponseDaftar> userDaftar(@Body RequestDaftar requestDaftar);
}
