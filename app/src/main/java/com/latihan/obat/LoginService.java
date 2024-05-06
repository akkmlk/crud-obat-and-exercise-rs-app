package com.latihan.obat;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {
    @POST("login")
    Call<LoginResponse> userLogin(@Body LoginRequest loginRequest);
}
