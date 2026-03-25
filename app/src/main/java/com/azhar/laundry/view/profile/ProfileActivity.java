package com.azhar.laundry.view.profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.azhar.laundry.R;
import com.azhar.laundry.networking.ApiClientBackend;
import com.azhar.laundry.networking.ApiService;
import com.azhar.laundry.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    EditText etEmail, etName, etPhone, etAddress;
    Button btnUpdate;
    ProgressDialog progressDialog;
    ApiService apiService;
    SessionManager sessionManager;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        token = sessionManager.getToken();

        if (token == null) {
            Toast.makeText(this, "Session habis, silakan login ulang", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        token = "Bearer " + token;
        setContentView(R.layout.activity_profile);

        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnUpdate = findViewById(R.id.btnUpdateProfile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        apiService = ApiClientBackend.getClient().create(ApiService.class);

        loadProfile();

        btnUpdate.setOnClickListener(v -> updateProfile());

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadProfile() {
        progressDialog.show();

        apiService.getProfile(token).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {

                    etEmail.setText(response.body().get("email"));
                    etName.setText(response.body().get("name"));
                    etPhone.setText(response.body().get("no_hp"));
                    etAddress.setText(response.body().get("alamat"));

                } else {
                    Toast.makeText(ProfileActivity.this,
                            "Gagal memuat profile (Session mungkin expired)",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this,
                        "Koneksi error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void updateProfile() {
        String name = etName.getText().toString();
        String phone = etPhone.getText().toString();
        String address = etAddress.getText().toString();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("no_hp", phone);
        body.put("alamat", address);

        apiService.updateProfile(token, body).enqueue(new Callback<Map<String, String>>() {
            @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    progressDialog.dismiss();

                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ProfileActivity.this, "Profile berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ProfileActivity.this,
                                "Gagal update profile (Token tidak valid)",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Koneksi error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
