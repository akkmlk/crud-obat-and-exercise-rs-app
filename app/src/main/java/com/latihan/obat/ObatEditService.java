package com.latihan.obat;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ObatEditService {
    @GET("obat/{obatId}/edit")
    Call<ResponseEditObat> editObat(@Path("obatId") int obatId);
}
