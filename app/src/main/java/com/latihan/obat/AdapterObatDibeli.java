package com.latihan.obat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterObatDibeli extends RecyclerView.Adapter<AdapterObatDibeli.HolderObatDibeli> {

    Context context;
    List<ModelTransactionDetail> modelTransactionDetailList;

    public AdapterObatDibeli(Context context, List<ModelTransactionDetail> modelTransactionDetailList) {
        this.context = context;
        this.modelTransactionDetailList = modelTransactionDetailList;
    }

    @NonNull
    @Override
    public AdapterObatDibeli.HolderObatDibeli onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.obat_dibeli, parent, false);
        return new HolderObatDibeli(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterObatDibeli.HolderObatDibeli holder, int position) {
        holder.etObat.setText(String.valueOf(modelTransactionDetailList.get(position).getObat_dibeli()));
    }

    @Override
    public int getItemCount() {
        return modelTransactionDetailList != null ? modelTransactionDetailList.size() : 0;
    }

    public class HolderObatDibeli extends RecyclerView.ViewHolder {

        EditText etObat;

        public HolderObatDibeli(@NonNull View itemView) {
            super(itemView);
            etObat = itemView.findViewById(R.id.etObat);
        }
    }
}
