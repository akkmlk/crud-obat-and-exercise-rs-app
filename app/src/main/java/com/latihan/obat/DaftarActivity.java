package com.latihan.obat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DaftarActivity extends AppCompatActivity {

    TextView tvLogin;
    Button btnDaftar;
    EditText etName, etUsername, etAddress, etPassword, etPasswordConfirm;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        tvLogin = findViewById(R.id.tvLogin);
        btnDaftar = findViewById(R.id.btnDaftar);
        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DaftarActivity.this, MainActivity.class));
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String passwordConfirm = etPasswordConfirm.getText().toString().trim();

                if (name.isEmpty() || username.isEmpty() ||  address.isEmpty() || password.isEmpty()) {
                    failedMsg("Harus diisi semua");
                } else  if (!password.equals(passwordConfirm)) {
                    failedMsg("Password dan Konfirmasi password tidak cocok");
                } else {
                    proccessRegister();
                }
            }
        });

    }

    private Retrofit proccessRegister() {
        RequestDaftar requestDaftar =  new RequestDaftar();
        requestDaftar.setName(etName.getText().toString().trim());
        requestDaftar.setUsername(etUsername.getText().toString().trim());
        requestDaftar.setAlamat(etAddress.getText().toString().trim());
        requestDaftar.setPassword(etPassword.getText().toString().trim());
        requestDaftar.setPassword_confirmation(etPasswordConfirm.getText().toString().trim());

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.49:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        DaftarService daftarService = retrofit.create(DaftarService.class);
        Call<ResponseDaftar> call = daftarService.userDaftar(requestDaftar);
        call.enqueue(new Callback<ResponseDaftar>() {
            @Override
            public void onResponse(Call<ResponseDaftar> call, Response<ResponseDaftar> response) {
                if (response.isSuccessful()) {
                    ModelUser data = response.body().data();
                    etName.setText("");
                    etUsername.setText("");
                    etAddress.setText("");
                    etPassword.setText("");
                    etPasswordConfirm.setText("");
                    Toast.makeText(DaftarActivity.this, data.getName() + " Berhasil terdaftar", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(DaftarActivity.this, MainActivity.class);
//                    intent.putExtra("registered", true);
//                    startActivity(intent);
                } else  {
                    try {
                        Toast.makeText(DaftarActivity.this, "Error nya : " + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseDaftar> call, Throwable t) {
                Toast.makeText(DaftarActivity.this, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return retrofit;
    }

    private void failedMsg(String msg) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(DaftarActivity.this)
                .setTitle("Oops!")
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