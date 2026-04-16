package com.example.kho_ketoan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kho_ketoan.R;
import com.example.kho_ketoan.database.DatabaseHelper;
import com.example.kho_ketoan.model.PhieuKho;
import java.util.List;

public class AddEditPhieuKhoActivity extends AppCompatActivity {

    private EditText etMaPhieu, etNgayLap, etMaNVKho;
    private Spinner  spinnerLoai, spinnerTrangThai, spinnerMaNCC;
    private TextView tvTrangThaiLabel;
    private DatabaseHelper db;
    private String   maPhieuEdit = null;
    private List<String> listNCC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_phieu_kho);

        db               = new DatabaseHelper(this);
        etMaPhieu        = findViewById(R.id.etMaPhieu);
        etNgayLap        = findViewById(R.id.etNgayLap);
        etMaNVKho        = findViewById(R.id.etMaNVKho);
        spinnerLoai      = findViewById(R.id.spinnerLoaiPhieu);
        spinnerTrangThai = findViewById(R.id.spinnerTrangThai);
        spinnerMaNCC     = findViewById(R.id.spinnerMaNCC);
        tvTrangThaiLabel = findViewById(R.id.tvTrangThaiLabel);

        // ── Spinner Loại Phiếu ──
        ArrayAdapter<String> adLoai = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"NHAP", "XUAT"});
        adLoai.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoai.setAdapter(adLoai);

        // ── Spinner NCC ──
        listNCC = db.getAllMaNCC();
        if (!listNCC.isEmpty()) {
            ArrayAdapter<String> adNCC = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, listNCC);
            adNCC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMaNCC.setAdapter(adNCC);
        }

        // ── Kiểm tra THÊM MỚI hay SỬA ──
        maPhieuEdit = getIntent().getStringExtra("maPhieu");

        if (maPhieuEdit != null) {
            // ═══ CHẾ ĐỘ SỬA ═══
            setTitle("Sửa Phiếu Kho");
            etMaPhieu.setEnabled(false);
            tvTrangThaiLabel.setVisibility(View.VISIBLE);
            spinnerTrangThai.setVisibility(View.VISIBLE);

            ArrayAdapter<String> adTT = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item,
                    new String[]{"Chờ thanh toán", "Đã thanh toán"});
            adTT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTrangThai.setAdapter(adTT);

            PhieuKho p = db.getPhieuKhoById(maPhieuEdit);
            if (p != null) {
                etMaPhieu.setText(p.getMaPhieu());
                etNgayLap.setText(p.getNgayLapPhieu());
                etMaNVKho.setText(p.getMaNVKho());
                spinnerLoai.setSelection("XUAT".equals(p.getLoaiPhieu()) ? 1 : 0);
                spinnerTrangThai.setSelection(
                        "Đã thanh toán".equals(p.getTrangThaiTT()) ? 1 : 0);
                for (int k = 0; k < listNCC.size(); k++) {
                    if (listNCC.get(k).startsWith(p.getMaNCC() + " - ") ||
                            listNCC.get(k).equals(p.getMaNCC())) {
                        spinnerMaNCC.setSelection(k);
                        break;
                    }
                }
            }
        } else {
            // ═══ CHẾ ĐỘ THÊM MỚI ═══
            setTitle("Thêm Phiếu Kho");
            tvTrangThaiLabel.setVisibility(View.GONE);
            spinnerTrangThai.setVisibility(View.GONE);
        }

        findViewById(R.id.btnLuu).setOnClickListener(v -> luuPhieuKho());
    }

    private void luuPhieuKho() {
        String ma      = etMaPhieu.getText().toString().trim();
        String ngay    = etNgayLap.getText().toString().trim();
        String maNVKho = etMaNVKho.getText().toString().trim();
        String loai    = spinnerLoai.getSelectedItem().toString();
        String maNCC   = listNCC.isEmpty() ? "" :
                DatabaseHelper.layMaTuSpinner(spinnerMaNCC.getSelectedItem().toString());

        String trangThai = (maPhieuEdit != null)
                ? spinnerTrangThai.getSelectedItem().toString()
                : "Chờ thanh toán";

        if (ma.isEmpty() || ngay.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Mã Phiếu và Ngày Lập",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // FIX tongTien: khi sửa, tongTien KHÔNG được truyền (suaPhieuKho không update tongTien).
        // Khi thêm mới, tongTien = 0 là đúng (chưa có chi tiết).
        PhieuKho p = new PhieuKho(ma, loai, ngay, 0, maNVKho, maNCC, trangThai);
        boolean ok;
        if (maPhieuEdit == null) {
            ok = db.themPhieuKho(p);
            Toast.makeText(this, ok ? "Thêm thành công!" : "Lỗi: Mã đã tồn tại",
                    Toast.LENGTH_SHORT).show();
        } else {
            ok = db.suaPhieuKho(p); // tongTien được giữ nguyên trong DB
            Toast.makeText(this, ok ? "Cập nhật thành công!" : "Lỗi cập nhật",
                    Toast.LENGTH_SHORT).show();
        }
        if (ok) finish();
    }
}