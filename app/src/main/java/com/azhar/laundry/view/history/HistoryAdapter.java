package com.azhar.laundry.view.history;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.azhar.laundry.R;
import com.azhar.laundry.model.ModelLaundry;
import com.azhar.laundry.utils.FunctionHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
    private List<ModelLaundry> modelInputList;

    public HistoryAdapter(Context context, List<ModelLaundry> modelInputList) {
        this.context = context;
        this.modelInputList = modelInputList;
    }

    public void setDataAdapter(List<ModelLaundry> items) {
        this.modelInputList = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {

        ModelLaundry data = modelInputList.get(position);

        holder.tvTitle.setText(data.getCategory());
        holder.tvItems.setText(data.getItemDisplay());
        holder.tvDate.setText(formatTanggal(data.getCreated_at()));
        holder.tvPrice.setText(FunctionHelper.rupiahFormat(data.getTotal_price()));

        // ✅ STATUS DARI API
        holder.tvStatus.setText(data.getStatus());

        // ✅ WARNA STATUS OTOMATIS
        if (data.getStatus().equalsIgnoreCase("Dibayar")) {
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32")); // hijau
        } else if (data.getStatus().equalsIgnoreCase("Menunggu Pembayaran")) {
            holder.tvStatus.setTextColor(Color.parseColor("#F9A825")); // kuning
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#C62828")); // merah
        }
    }

    @Override
    public int getItemCount() {
        return modelInputList == null ? 0 : modelInputList.size();
    }

    // =========================
    // ✅ FORMAT TANGGAL
    // =========================
    private String formatTanggal(String tanggalApi) {
        try {
            SimpleDateFormat apiFormat =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

            SimpleDateFormat tampilFormat =
                    new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            Date date = apiFormat.parse(tanggalApi);
            return tampilFormat.format(date);

        } catch (Exception e) {
            return tanggalApi;
        }
    }

    // =========================
    // ✅ VIEW HOLDER (SINGLE)
    // =========================
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvItems, tvDate, tvPrice, tvStatus;

        ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvItems = itemView.findViewById(R.id.tvItems);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
