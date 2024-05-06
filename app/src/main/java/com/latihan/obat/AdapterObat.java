package com.latihan.obat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdapterObat extends RecyclerView.Adapter<AdapterObat.HolderObat> {

    Context context;
    List<ModelObat> modelObatList;
    List<ModelObat> selectedObatList;
    LocalStorage localStorage;

    public AdapterObat(Context context, List<ModelObat> modelObatList) {
        this.context = context;
        this.modelObatList = modelObatList != null ? modelObatList : new ArrayList<>();
        this.selectedObatList = new ArrayList();
    }

    public void setFilteredList(List<ModelObat> filteredList) {
        this.modelObatList = filteredList;
        notifyDataSetChanged();
    }

    public interface OnEditClickListener {
        void onEditClick(int obatId);
    }
    private OnEditClickListener onEditClickListener;
    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }

    public interface SelectedObatListListener {
        void onSelectedObatListChanged(List<ModelObat> selectedObatList);
    }
    private SelectedObatListListener selectedObatListListener;
        public void setSelectedObatListListener(SelectedObatListListener listener) {
        this.selectedObatListListener = listener;
    }
    // Method untuk memberitahu ObatActivity setiap kali terjadi perubahan pada selectedObatList
    private void notifySelectedObatListChange(List<ModelObat> selectedObatList) {
        if (selectedObatList  != null) {
            selectedObatListListener.onSelectedObatListChanged(selectedObatList);
        }
    }

    @NonNull
    @Override
    public AdapterObat.HolderObat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.obat, parent, false);
        return new HolderObat(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterObat.HolderObat holder, @SuppressLint("RecyclerView") int position) {
        holder.tvNameObat.setText((CharSequence) modelObatList.get(position).getName());
//        holder.tvPriceObat.setText((CharSequence) modelObatList.get(position).getPrice());
        holder.tvPriceObat.setText(String.valueOf(modelObatList.get(position).getPrice()));
        holder.tvCategoryObat.setText((CharSequence) modelObatList.get(position).getCategory());

        int obatId = modelObatList.get(position).getId();
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDelete("Yakin obat " + modelObatList.get(position).getName() + " akan dihapus?", obatId);
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditClickListener.onEditClick(obatId);
            }
        });

        holder.cbObat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int adapterPosition = holder.getAdapterPosition();
                if (isChecked) {
                    if (selectedObatList == null) {
                        selectedObatList = new ArrayList<>();
                    }
                    selectedObatList.add(modelObatList.get(adapterPosition));
                    spSelectedObat(context, selectedObatList);
                    notifySelectedObatListChange(selectedObatList);
//                    Intent intent = new Intent(context, ObatActivity.class);
//                    intent.putExtra("selectedObatList", (Serializable) selectedObatList);
//                    context.startActivity(intent);
                    Toast.makeText(context, "Obat " + modelObatList.get(position).getName()  + " Dipilih", Toast.LENGTH_SHORT).show();
                } else {
                    if (selectedObatList != null) {
                        selectedObatList.remove(modelObatList.get(adapterPosition));
                        notifySelectedObatListChange(selectedObatList);
                    }
                }
            }
        });
    }

    private void spSelectedObat(Context context, List<ModelObat> selectedObatList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("selectedObatList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String selectedObatListJson = gson.toJson(selectedObatList);
        editor.putString("selectedObatList", selectedObatListJson);
        editor.apply();
    }

    private void AlertDelete(String msg, int obatId) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(context)
                .setTitle("Peringatan!")
                .setMessage(msg)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        proccessDelete(obatId);
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

    private Retrofit proccessDelete(int obatId) {
        localStorage = new LocalStorage(context);
        String token = localStorage.getToken();

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
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

        ObatDeleteService obatDeleteService =  retrofit.create(ObatDeleteService.class);
        Call<ResponseDeleteObat> call = obatDeleteService.deleteObat(obatId);
        call.enqueue(new Callback<ResponseDeleteObat>() {
            @Override
            public void onResponse(Call<ResponseDeleteObat> call, retrofit2.Response<ResponseDeleteObat> response) {
                if (response.isSuccessful()) {
                    int position = -1;
                    for (int i = 0; i < modelObatList.size(); i++) {
                        if (modelObatList.get(i).getId() == obatId) {
                            position = i;
                            break;
                        }
                    }

                    if (position != -1)  {
                        modelObatList.remove(position);
                        notifyItemRemoved(position);
                    }

                    Toast.makeText(context, "Berhasil dihapus", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Toast.makeText(context, "Error nya : " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseDeleteObat> call, Throwable t) {
                Toast.makeText(context, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return retrofit;
    }

    @Override
    public int getItemCount() {
//        return modelObatList.size();
        return modelObatList != null ? modelObatList.size() : 0;
    }

    public class HolderObat extends RecyclerView.ViewHolder {

        TextView tvNameObat, tvPriceObat, tvCategoryObat;
        ImageButton btnEdit, btnDelete;
        CheckBox cbObat;
        public HolderObat(@NonNull View itemView) {
            super(itemView);

            tvNameObat = itemView.findViewById(R.id.tv_nama_obat);
            tvPriceObat = itemView.findViewById(R.id.tv_price_obat);
            tvCategoryObat = itemView.findViewById(R.id.tv_category_obat);
            btnEdit = itemView.findViewById(R.id.btnEditObat);
            btnDelete = itemView.findViewById(R.id.btnDeleteObat);
            cbObat  = itemView.findViewById(R.id.cbObat);
        }
    }
}
