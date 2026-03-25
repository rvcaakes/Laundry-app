package com.azhar.laundry.view.auth;

import android.app.ProgressDialog;
import android.content.Intent;             
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.azhar.laundry.R;
import com.azhar.laundry.networking.ApiClientBackend;
import com.azhar.laundry.networking.ApiService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPhone, etPassword;
    Button btnRegister;
    TextView tvGoLogin;
    ImageView btnShowPass;
    ProgressDialog progressDialog;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoLogin = findViewById(R.id.tvGoLogin);
        btnShowPass = findViewById(R.id.btnShowPass);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        apiService = ApiClientBackend.getClient().create(ApiService.class);

        btnShowPass.setOnClickListener(v -> {
            if (etPassword.getInputType() ==
                    (android.text.InputType.TYPE_CLASS_TEXT |
                            android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {

                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                        android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                btnShowPass.setImageResource(R.drawable.ic_visibility);

            } else {

                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

                btnShowPass.setImageResource(R.drawable.ic_visibility_off);
            }

            etPassword.setSelection(etPassword.getText().length());
        });

        btnRegister.setOnClickListener(v -> doRegister());

        tvGoLogin.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }

    private void doRegister() {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String no_hp = etPhone.getText().toString();
        String password = etPassword.getText().toString();

        if (name.isEmpty() || email.isEmpty() || no_hp.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("email", email);
        body.put("no_hp", no_hp);
        body.put("password", password);

        apiService.registerUser(body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Register berhasil!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Gagal register!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Koneksi error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
