package com.example.kho_ketoan.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kho_ketoan.R;
import com.example.kho_ketoan.adapter.BangLuongAdapter;
import com.example.kho_ketoan.database.DatabaseHelper;
import com.example.kho_ketoan.model.BangLuong;
import java.util.List;

public class BangLuongActivity extends AppCompatActivity {

    private EditText etMaBL, etThang, etMaNVKT;
    private Spinner  spinnerMaNV;
    private List<String> listNV;

    private EditText etLuongCoBan, etPhuCap, etKhauTru;
    private TextView tvTongLuong;
    private ListView lvBangLuong;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bang_luong);
        setTitle("Bảng Lương");

        db           = new DatabaseHelper(this);
        etMaBL       = findViewById(R.id.etMaBL);
        etThang      = findViewById(R.id.etThang);
        spinnerMaNV = findViewById(R.id.spinnerMaNV);

// Load danh sách Nhân Viên vào Spinner
        listNV = db.getAllMaNhanVien();
        if (listNV.isEmpty()) {
            spinnerMaNV.setVisibility(android.view.View.GONE);
            TextView tvNVEmpty = findViewById(R.id.tvNVEmpty);
            tvNVEmpty.setVisibility(android.view.View.VISIBLE);
        } else {
            ArrayAdapter<String> adNV = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, listNV);
            adNV.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            spinnerMaNV.setAdapter(adNV);
        }

        etMaNVKT     = findViewById(R.id.etMaNVKT);
        etLuongCoBan = findViewById(R.id.etLuongCoBan);
        etPhuCap     = findViewById(R.id.etPhuCap);
        etKhauTru    = findViewById(R.id.etKhauTru);
        tvTongLuong  = findViewById(R.id.tvTongLuong);
        lvBangLuong  = findViewById(R.id.lvBangLuong);

        // ── Theo dõi thay đổi để tính tongLuong realtime ──
        TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            public void onTextChanged(CharSequence s,int a,int b,int c){
                tinhTongLuong(); // Tự động tính khi người dùng gõ
            }
            public void afterTextChanged(Editable s){}
        };
        etLuongCoBan.addTextChangedListener(watcher);
        etPhuCap.addTextChangedListener(watcher);
        etKhauTru.addTextChangedListener(watcher);

        // ── Nút Thanh Toán (lưu vào DB) ──
        findViewById(R.id.btnThanhToan).setOnClickListener(v -> luuBangLuong());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDanhSach();
    }

    /**
     * Tính tổng lương realtime:
     * tongLuong = luongCoBan + tongPhuCap - tongKhauTru
     */
    private void tinhTongLuong() {
        double co  = parseDouble(etLuongCoBan.getText().toString());
        double phu = parseDouble(etPhuCap.getText().toString());
        double kha = parseDouble(etKhauTru.getText().toString());
        double tong = co + phu - kha;
        tvTongLuong.setText("Tổng lương: " + String.format("%,.0f", tong) + " đ");
    }

    /** Lưu bảng lương vào DB sau khi nhấn Thanh Toán */
    private void luuBangLuong() {
        String ma    = etMaBL.getText().toString().trim();
        String thang = etThang.getText().toString().trim();
        String maNV = listNV.isEmpty() ? "" :
                DatabaseHelper.layMaTuSpinner(
                        spinnerMaNV.getSelectedItem().toString());

        String maNVKT = etMaNVKT.getText().toString().trim();

        if (ma.isEmpty() || thang.isEmpty() || listNV.isEmpty())  {
            Toast.makeText(this,
                    "Vui lòng nhập Mã BL, Tháng và Mã NV",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double co   = parseDouble(etLuongCoBan.getText().toString());
        double phu  = parseDouble(etPhuCap.getText().toString());
        double kha  = parseDouble(etKhauTru.getText().toString());
        double tong = co + phu - kha;

        BangLuong bl = new BangLuong(ma, thang, co, phu, kha, tong, maNV, maNVKT);
        boolean ok = db.themBangLuong(bl);

        Toast.makeText(this,
                ok ? "Đã thanh toán!" : "Lỗi: Mã đã tồn tại hoặc dữ liệu sai",
                Toast.LENGTH_SHORT).show();

        if (ok) {
            // Xóa form sau khi lưu thành công
            etMaBL.setText(""); etThang.setText(""); etMaNVKT.setText("");
            etMaNVKT.setText(""); etLuongCoBan.setText("");
            etPhuCap.setText(""); etKhauTru.setText("");
            loadDanhSach();
        }
    }

    private void loadDanhSach() {
        List<BangLuong> list = db.getAllBangLuong();
        lvBangLuong.setAdapter(new BangLuongAdapter(this, list));
    }

    /** Chuyển String sang double an toàn, trả về 0 nếu rỗng */
    private double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}

