package com.example.kho_ketoan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kho_ketoan.R;
import com.example.kho_ketoan.database.DatabaseHelper;
import java.util.List;

public class ThongKeActivity extends AppCompatActivity {

    // ── KHỐI 1: Phiếu Kho ──
    private Spinner  spinnerThangPK;
    private TextView tvTongTienPhieuKho;
    private List<String> dsThangPK; // Danh sách tháng từ PHIEU_KHO

    // ── KHỐI 2: Lương ──
    private Spinner  spinnerThangLuong;
    private TextView tvTongTienLuong;
    private List<String> dsThangLuong; // Danh sách tháng từ BANG_LUONG
    // ── KHỐI 3: Hóa Đơn ──
    private Spinner  spinnerThangHD;
    private TextView tvTongTienBanHang;
    private List<String> dsThangHD;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);
        setTitle("Thống Kê");

        db = new DatabaseHelper(this);

        // ── Ánh xạ View ──
        spinnerThangPK     = findViewById(R.id.spinnerThangPK);
        spinnerThangHD     = findViewById(R.id.spinnerThangHD);
        tvTongTienBanHang  = findViewById(R.id.tvTongTienBanHang);
        tvTongTienPhieuKho = findViewById(R.id.tvTongTienPhieuKho);
        spinnerThangLuong  = findViewById(R.id.spinnerThangLuong);
        tvTongTienLuong    = findViewById(R.id.tvTongTienLuong);

        // ════════════════════════════════════
        //  KHỐI 1: THỐNG KÊ PHIẾU KHO
        // ════════════════════════════════════
        dsThangPK = db.getDanhSachThangPhieuKho(); // Chỉ lấy tháng từ PHIEU_KHO

        if (dsThangPK.isEmpty()) {
            // Không có dữ liệu phiếu kho
            spinnerThangPK.setEnabled(false);
            tvTongTienPhieuKho.setText("Tổng tiền phiếu kho: Chưa có dữ liệu");
        } else {
            ArrayAdapter<String> adapterPK = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, dsThangPK);
            adapterPK.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            spinnerThangPK.setAdapter(adapterPK);

            // Khi chọn tháng → cập nhật tổng tiền phiếu kho
            spinnerThangPK.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int pos, long id) {
                            String thang = dsThangPK.get(pos);
                            double tong = db.getTongTienPhieuKhoTheoThang(thang);
                            tvTongTienPhieuKho.setText(
                                    "Tổng tiền phiếu kho (" + thang + "):\n" +
                                            String.format("%,.0f", tong) + " đ");
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    }
            );
        }

        // ════════════════════════════════════
        //  KHỐI 2: THỐNG KÊ LƯƠNG
        // ════════════════════════════════════
        dsThangLuong = db.getDanhSachThangBangLuong(); // Chỉ lấy tháng từ BANG_LUONG

        if (dsThangLuong.isEmpty()) {
            // Không có dữ liệu bảng lương
            spinnerThangLuong.setEnabled(false);
            tvTongTienLuong.setText("Tổng tiền lương: Chưa có dữ liệu");
        } else {
            ArrayAdapter<String> adapterLuong = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, dsThangLuong);
            adapterLuong.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            spinnerThangLuong.setAdapter(adapterLuong);

            // Khi chọn tháng → cập nhật tổng tiền lương
            spinnerThangLuong.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int pos, long id) {
                            String thang = dsThangLuong.get(pos);
                            double tong = db.getTongTienLuongTheoThang(thang);
                            tvTongTienLuong.setText(
                                    "Tổng tiền lương (" + thang + "):\n" +
                                            String.format("%,.0f", tong) + " đ");
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    }
            );
        }
        // ════════════════════════════════════
//  KHỐI 3: THỐNG KÊ BÁN HÀNG HOA_DON
// ════════════════════════════════════
        dsThangHD = db.getDanhSachThangHoaDon();

        if (dsThangHD.isEmpty()) {
            spinnerThangHD.setEnabled(false);
            tvTongTienBanHang.setText("Tổng tiền bán hàng: Chưa có dữ liệu");
        } else {
            ArrayAdapter<String> adapterHD = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, dsThangHD);
            adapterHD.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            spinnerThangHD.setAdapter(adapterHD);

            spinnerThangHD.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int pos, long id) {
                            String thang = dsThangHD.get(pos);
                            double tong = db.getTongTienBanHangTheoThang(thang);
                            tvTongTienBanHang.setText(
                                    "Tổng tiền bán hàng (" + thang + "):\n" +
                                            String.format("%,.0f", tong) + " đ");
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    }
            );
        }

    }
}
