package com.latihan.obat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

import kotlin.contracts.Returns;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateObatActivity extends AppCompatActivity {

    ImageButton btnBack;
    EditText etName, etPrice, etCategory;
    Button btnSave;
    LocalStorage localStorage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_obat);

        btnBack = findViewById(R.id.btnBack);
        etName = findViewById(R.id.etNameObat);
        etPrice = findViewById(R.id.etPriceObat);
        etCategory = findViewById(R.id.etCategoryObat);
        btnSave = findViewById(R.id.btnCreateObat);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateObatActivity.this, ObatActivity.class));
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String price = etPrice.getText().toString().trim();
                String kategori = etCategory.getText().toString().trim();

                if (name.isEmpty() || price.isEmpty() || kategori.isEmpty()) {
                    FailedMsg("Semua harus diisi");
                } else {
                    SaveObat();
                }
            }
        });
    }

    private Retrofit SaveObat() {
        localStorage = new LocalStorage(CreateObatActivity.this);
        String token = localStorage.getToken();

        Double price = Double.parseDouble(etPrice.getText().toString().trim());
        RequestCreateObat requestCreateObat = new RequestCreateObat();
        requestCreateObat.setName(etName.getText().toString().trim());
        requestCreateObat.setPrice(price);
        requestCreateObat.setKategori(etCategory.getText().toString().trim());

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.49:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ObatCreateService obatCreateService = retrofit.create(ObatCreateService.class);
        Call<ResponseCreateObat> call = obatCreateService.getCreateObat(requestCreateObat);
        call.enqueue(new Callback<ResponseCreateObat>() {
            @Override
            public void onResponse(Call<ResponseCreateObat> call, retrofit2.Response<ResponseCreateObat> response) {
                if (response.isSuccessful()) {
                    etName.setText("");
                    etPrice.setText("");
                    etCategory.setText("");
//                    startActivity(new Intent(CreateObatActivity.this, ObatActivity.class));
                    Intent intent = new Intent(CreateObatActivity.this, ObatActivity.class);
                    intent.putExtra("dataSaved", true);
                    startActivity(intent);
                } else {
                    try {
                        FailedMsg("Error response : " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    FailedMsg(requestCreateObat.getName() + "\n" + requestCreateObat.getPrice() +  "\n" + requestCreateObat.getKategori());
                }
            }

            @Override
            public void onFailure(Call<ResponseCreateObat> call, Throwable t) {
//                Toast.makeText(CreateObatActivity.this, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                FailedMsg("Throwable  :  " + t.getLocalizedMessage());
            }
        });
        return retrofit;
    }

    private void FailedMsg(String msg) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(CreateObatActivity.this)
                .setTitle("Gagal menyimpan")
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