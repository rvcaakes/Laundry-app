package com.azhar.laundry.networking;

import com.azhar.laundry.model.ModelUser;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Query;
import com.azhar.laundry.model.ModelPrice;
import com.azhar.laundry.model.HistoryResponse;

import java.util.List;

import retrofit2.http.Path;
import java.util.List;

public interface ApiService {

    @POST("auth/login")
    Call<ModelUser> loginUser(@Body Map<String, String> body);

    @POST("auth/register")
    Call<Map<String, String>> registerUser(@Body Map<String, String> body);

    @GET("profile")
    Call<Map<String, String>> getProfile(@Header("Authorization") String token);

    @PUT("profile")
    Call<Map<String, String>> updateProfile(
            @Header("Authorization") String token,
            @Body Map<String, String> body
    );

    @GET("master-price/price")
    Call<ModelPrice> getPriceByCategoryAndItem(
            @Query("category") String category,
            @Query("item") String item
    );

    @POST("transactions/create")
    Call<Map<String, Object>> createTransaction(
            @Body Map<String, Object> body
    );

    @GET("transactions/user/{nama_user}")
        Call<HistoryResponse> getHistoryByUser(
                @Path("nama_user") String namaUser
        );

}
