package com.latihan.obat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditObatActivity extends AppCompatActivity {

    ImageButton btnBack;
    EditText etName, etPrice, etCategory;
    Button btnSave;
    LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_obat);

        btnBack = findViewById(R.id.btnBack);
        etName = findViewById(R.id.etNameObat);
        etPrice = findViewById(R.id.etPriceObat);
        etCategory = findViewById(R.id.etCategoryObat);
        btnSave = findViewById(R.id.btnUpdateObat);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditObatActivity.this, ObatActivity.class));
            }
        });

        localStorage = new LocalStorage(EditObatActivity.this);
        String token = localStorage.getToken();
        int obatId = getIntent().getIntExtra("obatId", -1);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String price = etPrice.getText().toString();
                String category = etCategory.getText().toString();

                if (name.isEmpty() || price.isEmpty() || category.isEmpty()) {
                    FailedMsg("Harus diisi semua");
                } else {
                    saveObat();
                }
            }
        });

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

        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl("http://192.168.0.49:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ObatEditService obatEditService = retrofit.create(ObatEditService.class);
        Call<ResponseEditObat> call = obatEditService.editObat(obatId);
        call.enqueue(new Callback<ResponseEditObat>() {
            @Override
            public void onResponse(Call<ResponseEditObat> call, retrofit2.Response<ResponseEditObat> response) {
                if (response.isSuccessful()) {
                    ModelObat obat = response.body().data();

                    etName.setText(obat.getName());
//                    etPrice.setText(obat.getPrice());
                    String price = String.valueOf(obat.getPrice());
                    etPrice.setText(price);
                    etCategory.setText(obat.getCategory());
                } else {
                    try {
                        Toast.makeText(EditObatActivity.this, "Error nya : " + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseEditObat> call, Throwable t) {
                Toast.makeText(EditObatActivity.this, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveObat() {
        localStorage = new LocalStorage(EditObatActivity.this);
        String token = localStorage.getToken();
        int obatId = getIntent().getIntExtra("obatId", -1);

        Double price = Double.parseDouble(etPrice.getText().toString().trim());
        RequestUpdateObat requestUpdateObat = new RequestUpdateObat();
        requestUpdateObat.setName(etName.getText().toString().trim());
        requestUpdateObat.setPrice(price);
        requestUpdateObat.setKategori(etCategory.getText().toString().trim());

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

        ObatUpdateService obatUpdateService = retrofit.create(ObatUpdateService.class);
        Call<ResponseUpdateObat> call = obatUpdateService.updateObat(obatId, requestUpdateObat);
        call.enqueue(new Callback<ResponseUpdateObat>() {
            @Override
            public void onResponse(Call<ResponseUpdateObat> call, retrofit2.Response<ResponseUpdateObat> response) {
                if (response.isSuccessful()) {
                    ModelObat obat = response.body().data();
                    Intent intent = new Intent(EditObatActivity.this, ObatActivity.class);
                    intent.putExtra("dataUpdated", true);
                    startActivity(intent);
                } else {
                    try {
                        Toast.makeText(EditObatActivity.this, "Error nya  : " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseUpdateObat> call, Throwable t) {
                Toast.makeText(EditObatActivity.this, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void FailedMsg(String msg) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(EditObatActivity.this)
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