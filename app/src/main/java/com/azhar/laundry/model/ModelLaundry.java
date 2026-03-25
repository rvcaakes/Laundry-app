package com.azhar.laundry.model;

import com.google.gson.annotations.SerializedName;

public class ModelLaundry {

    @SerializedName("id")
    private int id;

    @SerializedName("nama_user")
    private String nama_user;

    @SerializedName("alamat")
    private String alamat;

    @SerializedName("category")
    private String category;

    @SerializedName("item_name")
    private String item_name;

    @SerializedName("berat")
    private String berat;

    @SerializedName("total_price")
    private int total_price;

    @SerializedName("status")
    private String status;

    @SerializedName("created_at")
    private String created_at;

    // ===== GETTER =====
    public int getId() { return id; }
    public String getNama_user() { return nama_user; }
    public String getAlamat() { return alamat; }
    public String getCategory() { return category; }
    public String getItem_name() { return item_name; }
    public String getBerat() { return berat; }
    public int getTotal_price() { return total_price; }
    public String getStatus() { return status; }
    public String getCreated_at() { return created_at; }

    // ===== UNTUK UI =====
    public String getNamaJasa() {
        return category;
    }

    public int getHarga() {
        return total_price;
    }

    public int getItems() {
        if (item_name == null || item_name.isEmpty()) return 0;
        return item_name.split(";").length;
    }

    public String getItemDisplay() {
        if (item_name == null || berat == null) return "";

        String[] items = item_name.split(";");
        String[] beratArr = berat.split(";");

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            String nama = items[i].trim();
            String kg = (i < beratArr.length) ? beratArr[i].trim() : "0";
            result.append(nama).append(" ").append(kg).append("kg");
            if (i < items.length - 1) result.append(", ");
        }
        return result.toString();
    }
}
