package com.latihan.obat;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TypeObatService {
    @GET("obat/type")
    Call<ResponseTypeObat> getTypeObatList();;
}
