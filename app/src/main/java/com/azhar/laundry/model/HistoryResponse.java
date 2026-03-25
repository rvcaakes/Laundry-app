package com.azhar.laundry.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HistoryResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<ModelLaundry> data;

    public boolean isSuccess() {
        return success;
    }

    public List<ModelLaundry> getData() {
        return data;
    }
}
