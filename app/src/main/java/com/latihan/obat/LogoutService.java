package com.latihan.obat;

import retrofit2.Call;
import retrofit2.http.POST;

public interface LogoutService {
    @POST("logout")
    Call<ResponseLogout> logoutUser();
}
