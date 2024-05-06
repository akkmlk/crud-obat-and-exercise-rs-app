package com.latihan.obat;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface ObatDeleteService {
    @DELETE("obat/{obatId}")
    Call<ResponseDeleteObat> deleteObat(@Path("obatId") int obatId);
}
