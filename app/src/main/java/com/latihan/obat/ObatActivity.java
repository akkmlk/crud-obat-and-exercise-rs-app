package com.latihan.obat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ObatActivity extends AppCompatActivity implements AdapterObat.OnEditClickListener {

    ImageButton btnAdd;
    LocalStorage localStorage;
    RecyclerView recyclerView;
    AdapterObat adapterObat;
    Button btnLogout, btnContainer;
    EditText etObatList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obat);

        localStorage = new LocalStorage(ObatActivity.this);
        etObatList = findViewById(R.id.etObatList);
        etObatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ObatActivity.this, "Halo", Toast.LENGTH_SHORT).show();
            }
        });

        String token = localStorage.getToken();
        Toast.makeText(ObatActivity.this, "token : " + token, Toast.LENGTH_SHORT).show();
        FailedMsg(token);

        Intent messageIntent = getIntent();
        if (messageIntent != null && messageIntent.getBooleanExtra("dataSaved", false)) {
            Toast.makeText(this, "Obat Berhasil disimpan", Toast.LENGTH_SHORT).show();
        } else if (messageIntent != null && messageIntent.getBooleanExtra("dataUpdated", false)) {
            Toast.makeText(this, "Obat berhasil di update", Toast.LENGTH_SHORT).show();
        }

        btnContainer = findViewById(R.id.btnContainer);
        btnContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ObatActivity.this, ContainerActivity.class));
            }
        });

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertLogout("Mau keluar?");
            }
        });

        btnAdd = findViewById(R.id.btnAddObat);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ObatActivity.this, CreateObatActivity.class));
            }
        });

        OkHttpClient client =  new OkHttpClient.Builder().addInterceptor(new Interceptor() {
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

        ObatService obatService = retrofit.create(ObatService.class);
        Call<ResponseObat> call = obatService.getObatList();
        call.enqueue(new Callback<ResponseObat>() {
            @Override
            public void onResponse(Call<ResponseObat> call, retrofit2.Response<ResponseObat> response) {
                if (response.isSuccessful()) {
//                    List<ModelObat> obatList= response.body();
                    ResponseObat responseObat = response.body();
                    if (responseObat != null && responseObat.isSuccess()) {
                        List<ModelObat> obatList = responseObat.getDataObat();

                        recyclerView = findViewById(R.id.rv_obat);
                        adapterObat = new AdapterObat(ObatActivity.this, obatList);
                        adapterObat.setOnEditClickListener(ObatActivity.this);
//                        adapterObat.setSelectedObatListListener(ObatActivity.this);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ObatActivity.this);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapterObat);
                    } else {
                        try {
                            System.out.println("Error get data nya : " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        System.out.println("Error response nya : " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObat> call, Throwable t) {
//                Toast.makeText(ObatActivity.this, "Error nya : " + t, Toast.LENGTH_LONG).show();
                FailedMsg("Error nya : " + t);
            }
        });
    }

    private void alertLogout(String msg) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(ObatActivity.this)
                .setTitle("Peringatan!")
                .setMessage(msg)
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        proccesslogout();
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = alertBuild.create();
        alert.show();
    }

//    public void onSelectedObatListChanged(List<ModelObat> selectedObatList) {
//        StringBuilder selectedObatNames = new StringBuilder();
//        for (ModelObat obat : selectedObatList) {
//            selectedObatNames.append(obat.getName()).append(", ");
//        }
//        etObatList.setText(selectedObatNames);
//    }

    private void proccesslogout() {
        localStorage = new LocalStorage(ObatActivity.this);
        String token = localStorage.getToken();

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

        LogoutService logoutService = retrofit.create(LogoutService.class);
        Call<ResponseLogout> call = logoutService.logoutUser();
        call.enqueue(new Callback<ResponseLogout>() {
            @Override
            public void onResponse(Call<ResponseLogout> call, retrofit2.Response<ResponseLogout> response) {
                if (response.isSuccessful()) {
                    localStorage.clearToken();
                    Intent intent = new Intent(ObatActivity.this, MainActivity.class);
                    intent.putExtra("logouted", true);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseLogout> call, Throwable t) {

            }
        });
    }

    private void FailedMsg(String msg) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(ObatActivity.this)
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

    @Override
    public void onEditClick(int obatId) {
        Intent intent = new Intent(ObatActivity.this, EditObatActivity.class);
        intent.putExtra("obatId", obatId);
        startActivity(intent);
    }
}