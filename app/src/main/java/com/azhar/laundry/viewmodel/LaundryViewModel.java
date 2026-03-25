package com.azhar.laundry.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.azhar.laundry.model.HistoryResponse;
import com.azhar.laundry.model.ModelLaundry;
import com.azhar.laundry.networking.ApiClientBackend;
import com.azhar.laundry.networking.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaundryViewModel extends AndroidViewModel {

    private MutableLiveData<List<ModelLaundry>> dataLaundry = new MutableLiveData<>();

    public LaundryViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<ModelLaundry>> getDataLaundryByUser(String namaUser) {

        ApiService apiService =
                ApiClientBackend.getClient().create(ApiService.class);

        apiService.getHistoryByUser(namaUser)
                .enqueue(new Callback<HistoryResponse>() {
                    @Override
                    public void onResponse(
                            Call<HistoryResponse> call,
                            Response<HistoryResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            dataLaundry.setValue(response.body().getData());

                        } else {
                            dataLaundry.setValue(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onFailure(Call<HistoryResponse> call, Throwable t) {
                        dataLaundry.setValue(new ArrayList<>());
                    }
                });

        return dataLaundry;
    }
}

