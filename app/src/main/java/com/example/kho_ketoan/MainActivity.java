package com.example.kho_ketoan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kho_ketoan.activity.BangLuongActivity;
import com.example.kho_ketoan.activity.PhieuKhoActivity;
import com.example.kho_ketoan.activity.ThongKeActivity;

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
    }
}
//PHIEU_KHO
//Mã NCC phải lựa chọn từ bảng NHA_CUNGCAP; Mã sản phẩm lựa chọn từ bảng SAN_PHAM

//LUONG
//mã nhân viên phải lựa chọn từ bảng Nhan_Vien

//THONGKE
//Tháng ở phần tiền phiếu kho không hiển thị lựa chọn nào, nên đang không thấy tổng tiên kho
//Thống kê cả ô tiền trong hóa đơn (HOA_DON)