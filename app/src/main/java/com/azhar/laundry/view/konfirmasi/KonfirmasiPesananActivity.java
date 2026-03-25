package com.azhar.laundry.view.konfirmasi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.azhar.laundry.R;
import com.azhar.laundry.utils.SessionManager;
import com.azhar.laundry.networking.ApiClientBackend;
import com.azhar.laundry.networking.ApiService;
import com.azhar.laundry.view.payment.MidtransWebViewActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

import java.util.Map;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KonfirmasiPesananActivity extends AppCompatActivity {

    EditText etAlamat;
    TextView tvTotal;
    Button btnBayar;
    LinearLayout layoutItems;
    CheckBox cbPewangi, cbExpress;
    ImageView btnBack;
    String category;

    int baseTotal = 0;
    int addonTotal = 0;

    String finalItemName = "";
    String finalBerat = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_pesanan);

        etAlamat = findViewById(R.id.etAlamat);
        tvTotal = findViewById(R.id.tvTotal);
        btnBayar = findViewById(R.id.btnBayar);
        layoutItems = findViewById(R.id.layoutItems);
        cbPewangi = findViewById(R.id.cbPewangi);
        cbExpress = findViewById(R.id.cbExpress);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        SessionManager sessionManager = new SessionManager(this);
        String alamatUser = sessionManager.getUserAddress();
        String items = getIntent().getStringExtra("items");
        baseTotal = getIntent().getIntExtra("total", 0);
        category = getIntent().getStringExtra("category");

        etAlamat.setText(alamatUser);
        tvTotal.setText("Rp " + baseTotal);

        tampilkanItem(items);
        setupAddon();

        btnBayar.setOnClickListener(v -> {
            btnBayar.setEnabled(false);
            kirimTransaksiKeServer();
        });
    }

    private void tampilkanItem(String items) {
        String[] lines = items.split("\n");

        StringBuilder itemBuilder = new StringBuilder();
        StringBuilder beratBuilder = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i]; // contoh: Baju:2kg

            String[] parts = line.split(":");
            String namaItem = parts[0].trim();
            String beratItem = parts[1].replace("kg", "").trim();

            // ✅ SIMPAN UNTUK DIKIRIM KE API
            itemBuilder.append(namaItem);
            beratBuilder.append(beratItem);

            if (i < lines.length - 1) {
                itemBuilder.append("; ");
                beratBuilder.append("; ");
            }

            // ✅ TAMPILKAN KE UI (TETAP SEPERTI SEBELUMNYA)
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView kiri = new TextView(this);
            kiri.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            kiri.setText(namaItem);

            TextView kanan = new TextView(this);
            kanan.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            kanan.setText(beratItem + "kg");
            kanan.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_END);

            row.addView(kiri);
            row.addView(kanan);
            layoutItems.addView(row);
        }

        // ✅ HASIL AKHIR UNTUK API
        finalItemName = itemBuilder.toString();
        finalBerat = beratBuilder.toString();
    }

    private void setupAddon() {
        cbPewangi.setOnCheckedChangeListener((buttonView, isChecked) -> {
            addonTotal += isChecked ? 5000 : -5000;
            updateTotal();
        });

        cbExpress.setOnCheckedChangeListener((buttonView, isChecked) -> {
            addonTotal += isChecked ? 10000 : -10000;
            updateTotal();
        });
    }

    private void updateTotal() {
        int finalTotal = baseTotal + addonTotal;
        tvTotal.setText("Rp " + finalTotal);
    }

    // =========================
    // ✅ KIRIM TRANSAKSI KE API
    // =========================
    private void kirimTransaksiKeServer() {

        SessionManager sessionManager = new SessionManager(this);
        ApiService apiService = ApiClientBackend.getClient().create(ApiService.class);

        Map<String, Object> body = new HashMap<>();
        body.put("nama_user", sessionManager.getUserName());
        body.put("alamat", etAlamat.getText().toString());
        body.put("category", category);
        body.put("item_name", finalItemName);
        body.put("berat", finalBerat);
        body.put("total_price", baseTotal + addonTotal);

        Call<Map<String, Object>> call = apiService.createTransaction(body);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {

                btnBayar.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> res = response.body();

                    Boolean success = (Boolean) res.get("success");
                    if (success != null && success) {

                        String redirectUrl = (String) res.get("redirect_url");

                        Intent intent = new Intent(
                                KonfirmasiPesananActivity.this,
                                MidtransWebViewActivity.class
                        );
                        intent.putExtra("url", redirectUrl);
                        startActivity(intent);

                    } else {
                        Toast.makeText(
                                KonfirmasiPesananActivity.this,
                                String.valueOf(res.get("message")),
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                } else {
                    Toast.makeText(
                            KonfirmasiPesananActivity.this,
                            "Gagal memproses transaksi",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btnBayar.setEnabled(true);
                Toast.makeText(
                        KonfirmasiPesananActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
