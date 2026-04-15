package com.example.kho_ketoan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kho_ketoan.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Nút chuyển màn hình Phiếu Kho
        ((Button) findViewById(R.id.btnPhieuKho)).setOnClickListener(v ->
                startActivity(new Intent(this, PhieuKhoActivity.class)));

        // Nút chuyển màn hình Bảng Lương
        ((Button) findViewById(R.id.btnBangLuong)).setOnClickListener(v ->
                startActivity(new Intent(this, BangLuongActivity.class)));

        // Nút chuyển màn hình Thống Kê
        ((Button) findViewById(R.id.btnThongKe)).setOnClickListener(v ->
                startActivity(new Intent(this, ThongKeActivity.class)));

        ((Button) findViewById(R.id.btnSeedData)).setOnClickListener(v ->
                startActivity(new Intent(this, SeedDataActivity.class)));

    }
}

