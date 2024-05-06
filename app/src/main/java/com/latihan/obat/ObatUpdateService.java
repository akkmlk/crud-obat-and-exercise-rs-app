package com.latihan.obat;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ObatUpdateService {
    @PUT("obat/{obatId}")
    Call<ResponseUpdateObat> updateObat(@Path("obatId") int obatId, @Body RequestUpdateObat requestUpdateObat);
}
