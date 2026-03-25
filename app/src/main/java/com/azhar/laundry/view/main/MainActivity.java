package com.azhar.laundry.view.main;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.azhar.laundry.utils.SessionManager;
import com.azhar.laundry.view.auth.LoginActivity;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.ImageView;

import com.azhar.laundry.R;
import com.azhar.laundry.view.cucibasah.CuciBasahActivity;
import com.azhar.laundry.view.dryclean.DryCleanActivity;
import com.azhar.laundry.view.history.HistoryActivity;
import com.azhar.laundry.view.ironing.IroningActivity;
import com.azhar.laundry.view.profile.ProfileActivity;
import com.azhar.laundry.view.premiumwash.PremiumWashActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int REQ_PERMISSION = 100;
    ProgressDialog progressDialog;
    MenuAdapter menuAdapter;
    ModelMenu modelMenu;
    RecyclerView rvMenu;
    LinearLayout layoutHistory;
    List<ModelMenu> modelMenuList = new ArrayList<>();
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);

        ImageView btnProfileMenu = findViewById(R.id.btnProfileMenu);

        btnProfileMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, btnProfileMenu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_profile, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menuProfile) {

                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));

                } else if (item.getItemId() == R.id.menuLogout) {

                    sessionManager.logout();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            });

            popupMenu.show();
        });

        String namaUser = sessionManager.getUserName();

        TextView tvUserName = findViewById(R.id.tvUserName);  
        tvUserName.setText(namaUser);

        setStatusbar();
        setInitLayout();
        setMenu();
    }

    private void setInitLayout() {
        rvMenu = findViewById(R.id.rvMenu);
        layoutHistory = findViewById(R.id.layoutHistory);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Tunggu…");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("sedang menampilkan lokasi");

        rvMenu.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        rvMenu.setHasFixedSize(true);

        layoutHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
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

    private void setMenu() {
        modelMenu = new ModelMenu("Cuci Basah", R.drawable.ic_cuci_basah);
        modelMenuList.add(modelMenu);
        modelMenu = new ModelMenu("Dry Cleaning", R.drawable.ic_dry_cleaning);
        modelMenuList.add(modelMenu);
        modelMenu = new ModelMenu("Premium Wash", R.drawable.ic_premium_wash);
        modelMenuList.add(modelMenu);
        modelMenu = new ModelMenu("Setrika", R.drawable.ic_setrika);
        modelMenuList.add(modelMenu);

        menuAdapter = new MenuAdapter(this, modelMenuList);
        rvMenu.setAdapter(menuAdapter);

        menuAdapter.setOnItemClickListener(modelMenu -> {
            if (modelMenu.getTvTitle().equals("Cuci Basah")) {
                Intent intent = new Intent(new Intent(MainActivity.this, CuciBasahActivity.class));
                intent.putExtra(CuciBasahActivity.DATA_TITLE, modelMenu.getTvTitle());
                startActivity(intent);
            } else if (modelMenu.getTvTitle().equals("Dry Cleaning")) {
                Intent intent = new Intent(new Intent(MainActivity.this, DryCleanActivity.class));
                intent.putExtra(DryCleanActivity.DATA_TITLE, modelMenu.getTvTitle());
                startActivity(intent);
            } else if (modelMenu.getTvTitle().equals("Premium Wash")) {
                Intent intent = new Intent(new Intent(MainActivity.this, PremiumWashActivity.class));
                intent.putExtra(PremiumWashActivity.DATA_TITLE, modelMenu.getTvTitle());
                startActivity(intent);
            } else if (modelMenu.getTvTitle().equals("Setrika")) {
                Intent intent = new Intent(new Intent(MainActivity.this, IroningActivity.class));
                intent.putExtra(IroningActivity.DATA_TITLE, modelMenu.getTvTitle());
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