package com.azhar.laundry.view.ironing;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.azhar.laundry.R;
import com.azhar.laundry.utils.FunctionHelper;
import com.azhar.laundry.viewmodel.AddDataViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.azhar.laundry.networking.ApiClientBackend;
import com.azhar.laundry.networking.ApiService;
import com.azhar.laundry.model.ModelPrice;
import com.azhar.laundry.view.konfirmasi.KonfirmasiPesananActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IroningActivity extends AppCompatActivity {

    public static final String DATA_TITLE = "TITLE";
    private TextView tvTitle, tvInfo, tvJumlahBarang, tvTotalPrice;
    private TextView tvKaos, tvCelana, tvJaket, tvSprei, tvKarpet;
    private Button btnPilihKaos, btnPilihCelana, btnPilihJaket, btnPilihSprei, btnPilihKarpet;
    private TextView tvKgKaos, tvKgCelana, tvKgJaket, tvKgSprei, tvKgKarpet;
    private int hargaKaos = 0;
    private int hargaCelana = 0;
    private int hargaJaket = 0;
    private int hargaSprei = 0;
    private int hargaKarpet = 0;
    double subtotalKaos = 0;
    double subtotalCelana = 0;
    double subtotalJaket = 0;
    double subtotalSprei = 0;
    double subtotalKarpet = 0;

    int totalItems = 0;
    double totalPrice = 0;
    ApiService apiService;
    double beratKaos = 0, beratCelana = 0, beratJaket = 0, beratSprei = 0, beratKarpet = 0;
    String strTitle, strCurrentLocation, strCurrentLatLong;
    AddDataViewModel addDataViewModel;
    Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry);
        apiService = ApiClientBackend.getClient().create(ApiService.class);

        setStatusbar();
        setInitLayout();
        setButtonAction(); 
        loadAllPricesFromApi();
        setInputData();
    }

    private void setStatusbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setInitLayout() {
        tvTitle = findViewById(R.id.tvTitle);
        tvInfo = findViewById(R.id.tvInfo);

        tvJumlahBarang = findViewById(R.id.tvJumlahBarang);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        tvKaos = findViewById(R.id.tvKaos);
        tvCelana = findViewById(R.id.tvCelana);
        tvJaket = findViewById(R.id.tvJaket);
        tvSprei = findViewById(R.id.tvSprei);
        tvKarpet = findViewById(R.id.tvKarpet);
        
        btnPilihKaos = findViewById(R.id.btnPilihKaos);
        tvKgKaos = findViewById(R.id.tvKgKaos);

        btnPilihCelana = findViewById(R.id.btnPilihCelana);
        tvKgCelana = findViewById(R.id.tvKgCelana);

        btnPilihJaket = findViewById(R.id.btnPilihJaket);
        tvKgJaket = findViewById(R.id.tvKgJaket);

        btnPilihSprei = findViewById(R.id.btnPilihSprei);
        tvKgSprei = findViewById(R.id.tvKgSprei);

        btnPilihKarpet = findViewById(R.id.btnPilihKarpet);
        tvKgKarpet = findViewById(R.id.tvKgKarpet);

        btnCheckout = findViewById(R.id.btnCheckout);

        strTitle = getIntent().getExtras().getString(DATA_TITLE);
        if (strTitle != null) {
            tvTitle.setText(strTitle);
        }

        addDataViewModel = new ViewModelProvider(this, ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(this.getApplication()))
                .get(AddDataViewModel.class);

        tvJumlahBarang.setText("0 items");
        tvTotalPrice.setText("Rp 0");
        tvInfo.setText("Hilangkan kerutan dari pakaian Anda dengan setrika listrik & uap.");
    }

    private void loadAllPricesFromApi() {
        loadHargaItem("Setrika", "Kaos");
        loadHargaItem("Setrika", "Jeans");
        loadHargaItem("Setrika", "Jaket");
    }

    private void loadHargaItem(String category, String item) {

        apiService.getPriceByCategoryAndItem(category, item)
                .enqueue(new Callback<ModelPrice>() {
                    @Override
                    public void onResponse(Call<ModelPrice> call, Response<ModelPrice> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            int harga = response.body().getPrice();

                            switch (item) {
                                case "Kaos":
                                    hargaKaos = harga;
                                    tvKaos.setText(FunctionHelper.rupiahFormat(hargaKaos));
                                    break;

                                case "Jeans":
                                    hargaCelana = harga;
                                    tvCelana.setText(FunctionHelper.rupiahFormat(hargaCelana));
                                    break;

                                case "Jaket":
                                    hargaJaket = harga;
                                    tvJaket.setText(FunctionHelper.rupiahFormat(hargaJaket));
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelPrice> call, Throwable t) {
                        Toast.makeText(IroningActivity.this,
                                "Gagal ambil harga " + item,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showInputKgDialog(String title, OnKgInputListener listener) {
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER |
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Contoh: 0.5");

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (!input.getText().toString().isEmpty()) {
                        double kg = Double.parseDouble(input.getText().toString());
                        listener.onKgInput(kg);
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    interface OnKgInputListener {
        void onKgInput(double kg);
    }

    private void setButtonAction() {

        btnPilihKaos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputKgDialog("Masukkan berat Kaos (kg)", new OnKgInputListener() {
                    @Override
                    public void onKgInput(double kg) {
                        beratKaos = kg;
                        tvKgKaos.setText(kg + " kg");

                        subtotalKaos = hargaKaos * kg;
                        hitungTotalKg();
                    }
                });
            }
        });

        btnPilihCelana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputKgDialog("Masukkan berat Jeans (kg)", new OnKgInputListener() {
                    @Override
                    public void onKgInput(double kg) {
                        beratCelana = kg;
                        tvKgCelana.setText(kg + " kg");

                        subtotalCelana = hargaCelana * kg;
                        hitungTotalKg();
                    }
                });
            }
        });

        btnPilihJaket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputKgDialog("Masukkan berat Jaket (kg)", new OnKgInputListener() {
                    @Override
                    public void onKgInput(double kg) {
                        beratJaket = kg;
                        tvKgJaket.setText(kg + " kg");

                        subtotalJaket = hargaJaket * kg;
                        hitungTotalKg();
                    }
                });
            }
        });

        btnPilihSprei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputKgDialog("Masukkan berat Jeans (kg)", new OnKgInputListener() {
                    @Override
                    public void onKgInput(double kg) {
                        beratSprei = kg;
                        tvKgSprei.setText(kg + " kg");

                        subtotalSprei = hargaSprei * kg;
                        hitungTotalKg();
                    }
                });
            }
        });

        btnPilihKarpet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputKgDialog("Masukkan berat Jeans (kg)", new OnKgInputListener() {
                    @Override
                    public void onKgInput(double kg) {
                        beratKarpet = kg;
                        tvKgKarpet.setText(kg + " kg");

                        subtotalKarpet = hargaKarpet * kg;
                        hitungTotalKg();
                    }
                });
            }
        });
    }

    private void hitungTotalKg() {

        totalPrice = subtotalKaos + subtotalCelana + subtotalJaket
                + subtotalSprei + subtotalKarpet;

        totalItems = 0;
        if (beratKaos > 0) totalItems++;
        if (beratCelana > 0) totalItems++;
        if (beratJaket > 0) totalItems++;
        if (beratSprei > 0) totalItems++;
        if (beratKarpet > 0) totalItems++;

        tvJumlahBarang.setText(totalItems + " items");
        tvTotalPrice.setText(FunctionHelper.rupiahFormat((int) totalPrice));
    }   

    private void setInputData() {
        btnCheckout.setOnClickListener(v -> {
            if (totalItems == 0 || totalPrice == 0) {
                Toast.makeText(this, "Harap pilih jenis barang!", Toast.LENGTH_SHORT).show();
            } else {

                String items = "";
                if (beratKaos > 0) items += "Kaos: " + beratKaos + " kg\n";
                if (beratCelana > 0) items += "Celana: " + beratCelana + " kg\n";
                if (beratJaket > 0) items += "Jaket: " + beratJaket + " kg\n";
                if (beratSprei > 0) items += "Sprei: " + beratSprei + " kg\n";
                if (beratKarpet > 0) items += "Karpet: " + beratKarpet + " kg\n";

                Intent intent = new Intent(IroningActivity.this, KonfirmasiPesananActivity.class);
                intent.putExtra("alamat", "");
                intent.putExtra("items", items);
                intent.putExtra("total", (int) totalPrice);
                intent.putExtra("category", "Setrika");

                startActivity(intent);
            }
        });
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        if (on) {
            layoutParams.flags |= bits;
        } else {
            layoutParams.flags &= ~bits;
        }
        window.setAttributes(layoutParams);
    }

}