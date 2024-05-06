package com.latihan.obat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterTypeObat extends RecyclerView.Adapter<AdapterTypeObat.HolderTypeObat> {
    Context context;
    List<ModelObat> modelObatList;
    SelectedTypeObatListener selectedTypeObatListener;

    public interface SelectedTypeObatListener {
        void onTypeObatSelected(String selectedTypeObat);
    }
    public void setSelectedTypeObatListener(SelectedTypeObatListener listener) {
        this.selectedTypeObatListener = listener;
    }
    public void setFilteredList(List<ModelObat> filteredList) {
        this.modelObatList = filteredList;
        notifyDataSetChanged();
    }

    public AdapterTypeObat(Context context, List<ModelObat> modelObatList) {
        this.context = context;
        this.modelObatList = modelObatList;
    }

    @NonNull
    @Override
    public AdapterTypeObat.HolderTypeObat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.type_obat, parent, false);
        return new HolderTypeObat(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTypeObat.HolderTypeObat holder, @SuppressLint("RecyclerView") int position) {
        holder.tvTypeObat.setText((CharSequence) modelObatList.get(position).getCategory());

        holder.tvTypeObat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedTypeObat = modelObatList.get(position).getCategory();
                if (selectedTypeObatListener != null) {
                    selectedTypeObatListener.onTypeObatSelected(selectedTypeObat);
                }
                Toast.makeText(context, "Klik " + modelObatList.get(position).getCategory(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelObatList != null ? modelObatList.size() : 0;
    }

    public class HolderTypeObat extends RecyclerView.ViewHolder {
        TextView tvTypeObat;
        public HolderTypeObat(@NonNull View itemView) {
            super(itemView);

            tvTypeObat = itemView.findViewById(R.id.tvTypeObat);
        }
    }
}
