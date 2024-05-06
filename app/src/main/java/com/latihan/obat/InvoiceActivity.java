package com.latihan.obat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InvoiceActivity extends AppCompatActivity {

    Button btnSave, btnShare, btnFinished;
    EditText etPasienName, etTypeObat;
    TextView tvPriceTotal;
    LocalStorage localStorage;
    RecyclerView recyclerView;
    AdapterObatDibeli adapterObatDibeli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        btnSave = findViewById(R.id.btnSave);
        btnShare = findViewById(R.id.btnShare);
        btnFinished = findViewById(R.id.btnFinished);
        etPasienName = findViewById(R.id.etPasienName);
        etTypeObat = findViewById(R.id.etTypeObat);
        tvPriceTotal = findViewById(R.id.tvPriceTotal);

        localStorage = new LocalStorage(InvoiceActivity.this);
        String token = localStorage.getToken();

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rootView = getWindow().getDecorView().getRootView();
                Bitmap bitmap = getScreenShootOffLayout(rootView);
                saveBitmapToStorage(bitmap);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("Image/*");
                Uri uri = getImageUri(InvoiceActivity.this, bitmap);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(shareIntent, "Bagikan Via"));
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rootView = getWindow().getDecorView().getRootView();
                Bitmap bitmap = getScreenShootOffLayout(rootView);
                saveBitmapToStorage(bitmap);
            }
        });

        btnFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InvoiceActivity.this, ContainerActivity.class));
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.49:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        InvoiceService invoiceService =  retrofit.create(InvoiceService.class);
        Call<ResponseInvoice> call = invoiceService.getInvoice();
        call.enqueue(new Callback<ResponseInvoice>() {
            @Override
            public void onResponse(Call<ResponseInvoice> call, retrofit2.Response<ResponseInvoice> response) {
                if (response.isSuccessful()) {
                    ResponseInvoice responseInvoice = response.body();
                    if (responseInvoice != null && responseInvoice.isSuccess()) {
                        ModelTransaction invoice = responseInvoice.getData();
                        String priceTotal = String.valueOf(invoice.getPrice_total());
                        tvPriceTotal.setText(priceTotal);
                        etPasienName.setText(invoice.getPasien());
                        etTypeObat.setText(invoice.getType_obat());

                        List<ModelTransactionDetail> detailList = responseInvoice.getDetail();
                        recyclerView = findViewById(R.id.rvObatDibeli);
                        adapterObatDibeli = new AdapterObatDibeli(InvoiceActivity.this, detailList);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(InvoiceActivity.this);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapterObatDibeli);
                    } else {
                        try {
                            Toast.makeText(InvoiceActivity.this, "Error get data nya : " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    try {
                        Toast.makeText(InvoiceActivity.this, "Error response nya : " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseInvoice> call, Throwable t) {
                Toast.makeText(InvoiceActivity.this, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void saveBitmapToStorage(Bitmap bitmap) {
        File directory = InvoiceActivity.this.getFilesDir();
        File file = new File(directory, "screenshoot.jpg");

        try {
            FileOutputStream  outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            Toast.makeText(InvoiceActivity.this, "Gambar disimpan di " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(InvoiceActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getScreenShootOffLayout(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap screenshoot = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return screenshoot;
    }
}