package com.azhar.laundry.view.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.azhar.laundry.R;
import com.azhar.laundry.model.ModelUser;
import com.azhar.laundry.networking.ApiClientBackend;
import com.azhar.laundry.networking.ApiService;
import com.azhar.laundry.view.main.MainActivity;
import com.azhar.laundry.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etIdentifier, etPassword;
    Button btnLogin;
    TextView tvGoRegister;
    ProgressDialog progressDialog;
    ApiService apiService;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_login);

        etIdentifier = findViewById(R.id.etIdentifier);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);
        ImageView btnShowPass = findViewById(R.id.btnShowPass);
        EditText etPassword = findViewById(R.id.etPassword);

        btnShowPass.setOnClickListener(v -> {
            if (etPassword.getInputType() == (android.text.InputType.TYPE_CLASS_TEXT |
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        apiService = ApiClientBackend.getClient().create(ApiService.class);

        sessionManager = new SessionManager(this);

        btnLogin.setOnClickListener(v -> doLogin());

        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void doLogin() {
        String identifier = etIdentifier.getText().toString();
        String password = etPassword.getText().toString();

        if (identifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        Map<String, String> body = new HashMap<>();
        body.put("identifier", identifier);
        body.put("password", password);

        apiService.loginUser(body).enqueue(new Callback<ModelUser>() {
            @Override
            public void onResponse(Call<ModelUser> call, Response<ModelUser> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {

                    String token = response.body().getToken();
                    String name  = response.body().getUser().name;
                    String alamat = response.body().getUser().alamat;

                    sessionManager.saveToken(token);
                    sessionManager.saveUserName(name);
                    sessionManager.saveUserAddress(alamat);

                    Toast.makeText(LoginActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login gagal!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelUser> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Koneksi error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
