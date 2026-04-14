package com.example.kho_ketoan.activity;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kho_ketoan.R;
import com.example.kho_ketoan.database.DatabaseHelper;
import com.example.kho_ketoan.model.PhieuKho;

public class AddEditPhieuKhoActivity extends AppCompatActivity {

    private EditText etMaPhieu, etNgayLap, etMaNVKho, etMaNCC;
    private Spinner  spinnerLoai, spinnerTrangThai;
    private DatabaseHelper db;
    private String maPhieuEdit = null; // null = thêm mới, có giá trị = đang sửa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_phieu_kho);

        db             = new DatabaseHelper(this);
        etMaPhieu      = findViewById(R.id.etMaPhieu);
        etNgayLap      = findViewById(R.id.etNgayLap);
        etMaNVKho      = findViewById(R.id.etMaNVKho);
        etMaNCC        = findViewById(R.id.etMaNCC);
        spinnerLoai    = findViewById(R.id.spinnerLoaiPhieu);
        spinnerTrangThai = findViewById(R.id.spinnerTrangThai);

        // ── Thiết lập Spinner Loại Phiếu ──
        ArrayAdapter<String> adapterLoai = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"NHAP", "XUAT"});
        adapterLoai.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoai.setAdapter(adapterLoai);

        // ── Thiết lập Spinner Trạng Thái ──
        ArrayAdapter<String> adapterTT = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Chờ thanh toán", "Đã thanh toán"});
        adapterTT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTrangThai.setAdapter(adapterTT);

        // ── Kiểm tra có đang sửa không ──
        maPhieuEdit = getIntent().getStringExtra("maPhieu");
        if (maPhieuEdit != null) {
            // Đang SỬA: nạp dữ liệu hiện tại vào form
            setTitle("Sửa Phiếu Kho");
            etMaPhieu.setEnabled(false); // Không cho sửa mã
            PhieuKho p = db.getPhieuKhoById(maPhieuEdit);
            if (p != null) {
                etMaPhieu.setText(p.getMaPhieu());
                etNgayLap.setText(p.getNgayLapPhieu());
                etMaNVKho.setText(p.getMaNVKho());
                etMaNCC.setText(p.getMaNCC());
                // Đặt vị trí spinner theo giá trị hiện tại
                spinnerLoai.setSelection(
                        "XUAT".equals(p.getLoaiPhieu()) ? 1 : 0);
                spinnerTrangThai.setSelection(
                        "Đã thanh toán".equals(p.getTrangThaiTT()) ? 1 : 0);
            }
        } else {
            setTitle("Thêm Phiếu Kho");
        }

        // ── Nút Lưu ──
        findViewById(R.id.btnLuu).setOnClickListener(v -> luuPhieuKho());
    }

    private void luuPhieuKho() {
        // Lấy dữ liệu từ form
        String ma       = etMaPhieu.getText().toString().trim();
        String ngay     = etNgayLap.getText().toString().trim();
        String maNVKho  = etMaNVKho.getText().toString().trim();
        String maNCC    = etMaNCC.getText().toString().trim();
        String loai     = spinnerLoai.getSelectedItem().toString();
        String trangThai = spinnerTrangThai.getSelectedItem().toString();

        // Kiểm tra dữ liệu bắt buộc
        if (ma.isEmpty() || ngay.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ Mã Phiếu và Ngày Lập",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        PhieuKho p = new PhieuKho(ma, loai, ngay, 0, maNVKho, maNCC, trangThai);

        boolean ok;
        if (maPhieuEdit == null) {
            // THÊM MỚI
            ok = db.themPhieuKho(p);
            Toast.makeText(this,
                    ok ? "Thêm thành công!" : "Lỗi: Mã đã tồn tại hoặc dữ liệu sai",
                    Toast.LENGTH_SHORT).show();
        } else {
            // SỬA
            ok = db.suaPhieuKho(p);
            Toast.makeText(this,
                    ok ? "Cập nhật thành công!" : "Lỗi khi cập nhật",
                    Toast.LENGTH_SHORT).show();
        }

        if (ok) finish(); // Đóng màn hình, quay về danh sách
    }
}
