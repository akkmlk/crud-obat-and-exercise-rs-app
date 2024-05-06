package com.latihan.obat;

import static java.util.Objects.*;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ObatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ObatFragment extends Fragment implements AdapterObat.SelectedObatListListener, AdapterTypeObat.SelectedTypeObatListener {

    EditText etObatList, etPasienName, etTypeObat;
    Button btnAdd;
    BottomSheetDialog bottomSheetDialogObat;
    BottomSheetDialog bottomSheetDialogTypeObat;
    LocalStorage localStorage;
    RecyclerView recyclerView;
    TextView tvSubtotal;
    SearchView svSearch;
    List<ModelObat> obatList;
    AdapterObat adapterObat;
    AdapterTypeObat adapterTypeObat;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ObatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ObatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ObatFragment newInstance(String param1, String param2) {
        ObatFragment fragment = new ObatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_obat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getActivity(), "list obat " + obatList, Toast.LENGTH_SHORT).show();

        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        etPasienName = view.findViewById(R.id.etPasienName);
        svSearch = view.findViewById(R.id.svSearch);

        etObatList = view.findViewById(R.id.etObatList);
        bottomSheetDialogObat = new BottomSheetDialog(getActivity());

        createDialog();
        etObatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogObat.show();
            }
        });
        bottomSheetDialogObat.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        etTypeObat = view.findViewById(R.id.etTypeObat);
        bottomSheetDialogTypeObat = new BottomSheetDialog(getActivity());
        bottomSheetDialogTypeObat.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        etTypeObat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogTypeObat.show();
            }
        });
        createDialogTypeObat();

        btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pasien = etPasienName.getText().toString().trim();
                String obat = etObatList.getText().toString().trim();
                String typeObat = etTypeObat.getText().toString().trim();
                Double priceTotal = Double.parseDouble(tvSubtotal.getText().toString().trim());

                if (pasien.isEmpty() || obat.isEmpty() || typeObat.isEmpty()) {
                    failedMsg("Semua harus diisi");
                } else {
                    localStorage = new LocalStorage(getActivity());
                    String token = localStorage.getToken();

                    // Memisahkan string obat menjadi array menggunakan koma sebagai delimiter
                    String[] listObat = obat.split(",\\s*");

                    RequestTransaction requestTransaction = new RequestTransaction();
                    requestTransaction.setPasien(pasien);
                    requestTransaction.setType(typeObat);
                    requestTransaction.setPriceTotal(priceTotal);
                    requestTransaction.setListObat(Arrays.asList(listObat));

                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                        @NonNull
                        @Override
                        public Response intercept(@NonNull Chain chain) throws IOException {
                            Request newRequest =  chain.request().newBuilder()
                                    .addHeader("Authorization", "Bearer " + token)
                                    .build();
                            return chain.proceed(newRequest);
                        }
                    }).build();

                    Retrofit retrofit  = new Retrofit.Builder()
                            .baseUrl("http://192.168.0.49:8000/api/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(client)
                            .build();

                    TransactionService transactionService = retrofit.create(TransactionService.class);
                    Call<ResponseTransaction> call = transactionService.createTransaction(requestTransaction);
                    call.enqueue(new Callback<ResponseTransaction>() {
                        @Override
                        public void onResponse(Call<ResponseTransaction> call, retrofit2.Response<ResponseTransaction> response) {
                            if (response.isSuccessful()) {
                                ResponseTransaction responseTransaction = response.body();
                                if  (responseTransaction.isSuccess())  {
                                    etPasienName.setText("");
                                    etObatList.setText("");
                                    etTypeObat.setText("");
                                    startActivity(new Intent(getActivity(), InvoiceActivity.class));
                                    Toast.makeText(getActivity(), "Bayar hei " + listObat, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                try {
                                    Toast.makeText(getActivity(), "Error nya : " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseTransaction> call, Throwable t) {
                            Toast.makeText(getActivity(), "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void failedMsg(String msg) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(getActivity())
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

    private void createDialogTypeObat() {
        View view = getLayoutInflater().inflate(R.layout.bottom_dialog, null, false);
        bottomSheetDialogTypeObat.setContentView(view);

        localStorage = new LocalStorage(getActivity());
        String token = localStorage.getToken();

        Button btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogTypeObat.dismiss();
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
        ObatFragment obatFragment = this;

        TypeObatService typeObatService =  retrofit.create(TypeObatService.class);
        Call<ResponseTypeObat> call = typeObatService.getTypeObatList();
        call.enqueue(new Callback<ResponseTypeObat>() {
            @Override
            public void onResponse(Call<ResponseTypeObat> call, retrofit2.Response<ResponseTypeObat> response) {
                if (response.isSuccessful()) {
                    ResponseTypeObat responseTypeObat = response.body();
                    if (response != null && responseTypeObat.isSuccess()) {
                        List<ModelObat> obatList = response.body().getData();

                        AdapterTypeObat adapterTypeObat = new AdapterTypeObat(getActivity(), obatList);
                        adapterTypeObat.setSelectedTypeObatListener(obatFragment);
                        recyclerView = view.findViewById(R.id.rvObatName);
                        RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapterTypeObat);
                    }
                } else {
                    try {
                        Toast.makeText(getActivity(), "Error nya : " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseTypeObat> call, Throwable t) {
                Toast.makeText(getActivity(), "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        svSearch = view.findViewById(R.id.svSearch);
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterListType(newText);
                return true;
            }
        });
    }

    private void filterListType(String newText) {
        List<ModelObat> filteredList = new ArrayList<>();
        for (ModelObat modelObat : obatList) {
            if (modelObat.getCategory().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(modelObat);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getActivity(), "Tidak ada type tersebut ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "" + filteredList, Toast.LENGTH_SHORT).show();
            adapterTypeObat = new AdapterTypeObat(getActivity(), filteredList);
            adapterTypeObat.setFilteredList(filteredList);
        }
    }

    private void createDialog() {
        View view = getLayoutInflater().inflate(R.layout.bottom_dialog, null, false);
        bottomSheetDialogObat.setContentView(view);

        localStorage = new LocalStorage(getActivity());
        String token = localStorage.getToken();

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogObat.dismiss();
                Toast.makeText(getActivity(), "Ini Submit", Toast.LENGTH_SHORT).show();
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
        ObatFragment obatFragment = this;

        ObatService obatService = retrofit.create(ObatService.class);
        Call<ResponseObat> call = obatService.getObatList();
        call.enqueue(new Callback<ResponseObat>() {
            @Override
            public void onResponse(Call<ResponseObat> call, retrofit2.Response<ResponseObat> response) {
                if (response.isSuccessful()) {
                    ResponseObat responseObat = response.body();
                    if (response != null && responseObat.isSuccess()) {
                        obatList = response.body().getDataObat();

                        AdapterObat adapterObat = new AdapterObat(getActivity(), obatList);
                        adapterObat.setSelectedObatListListener(obatFragment);
                        recyclerView =  view.findViewById(R.id.rvObatName);
                        RecyclerView.LayoutManager layoutManager =  new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapterObat);
                    }
                } else {
                    try {
                        Toast.makeText(getActivity(), "Error nya : " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObat> call, Throwable t) {
                Toast.makeText(getActivity(), "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        svSearch = view.findViewById(R.id.svSearch);
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
    }

    private void filterList(String newText) {
        List<ModelObat> filteredList = new ArrayList<>();
        for (ModelObat modelObat : obatList) {
            if (modelObat.getName().toLowerCase().contains(newText.toLowerCase()))  {
                filteredList.add(modelObat);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getActivity(), "Oops! Obat tidak ditemukan", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "" + filteredList, Toast.LENGTH_SHORT).show();
            View view = getLayoutInflater().inflate(R.layout.bottom_dialog, null, false);
            adapterObat = new AdapterObat(getActivity(), filteredList);
            recyclerView = view.findViewById(R.id.rvObatName);
            recyclerView.setAdapter(adapterObat);
            adapterObat.setFilteredList(filteredList);
//            adapterObat.notifyDataSetChanged();
        }
    }

    @Override
    public void onSelectedObatListChanged(List<ModelObat> selectedObatList) {
        StringBuilder selectedObatNames = new StringBuilder();
        double totalPrices = 0;
        for (ModelObat obat : selectedObatList) {
            selectedObatNames.append(obat.getName()).append(", ");
            totalPrices += obat.getPrice();
        }
        etObatList.setText(selectedObatNames);
        tvSubtotal.setText(String.valueOf(totalPrices));
    }

    @Override
    public void onTypeObatSelected(String selectedTypeObat) {
        etTypeObat.setText(selectedTypeObat);
    }
}