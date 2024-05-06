package com.latihan.obat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText etUsername,  etPassword;
    Button btnLogin;
    TextView tvDaftar;
    LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localStorage = new LocalStorage(MainActivity.this);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btnMasuk);
        tvDaftar = findViewById(R.id.tvDaftar);

        String token = localStorage.getToken();
        if (token != null) {
            startActivity(new Intent(MainActivity.this, ObatActivity.class));
        }
        Intent messageIntent = getIntent();
        if (messageIntent != null &&  messageIntent.getBooleanExtra("logouted", false))  {
            Toast.makeText(MainActivity.this, "Berhasil Keluar", Toast.LENGTH_SHORT).show();
        }

        tvDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DaftarActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    FailedMsg("Username atau password tidak boleh kosong");
                } else {
                    loginSend();
                }
            }
        });
    }

    private Retrofit loginSend() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(etUsername.getText().toString().trim());
        loginRequest.setPassword(etPassword.getText().toString().trim());

        HttpLoggingInterceptor httpLoggingInterceptor  = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://192.168.0.49:8000/api/")
                .client(okHttpClient)
                .build();

        LoginService loginService = retrofit.create(LoginService.class);
        Call<LoginResponse> loginResponseCall = loginService.userLogin(loginRequest);
        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                String status = response.body().getSuccess();
                if (status == "true") {
                    String token = response.body().getToken();
                    localStorage.setToken(token);
                    etUsername.setText("");
                    etPassword.setText("");
//                    Toast.makeText(MainActivity.this, "token : " + localStorage.getToken(), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, ObatActivity.class));
                } else {
                    FailedMsg("Username atau password salah");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return retrofit;
    }

    private void FailedMsg(String msg) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(MainActivity.this);
        alertBuild.setTitle("Gagal Login")
        .setMessage(msg)
        .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = alertBuild.create();
        alert.show();
    }
}