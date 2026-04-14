package com.example.kho_ketoan.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kho_ketoan.R;
import com.example.kho_ketoan.adapter.PhieuKhoAdapter;
import com.example.kho_ketoan.database.DatabaseHelper;
import com.example.kho_ketoan.model.PhieuKho;
import java.util.List;

public class PhieuKhoActivity extends AppCompatActivity {

    private ListView      lvPhieuKho;
    private EditText      etTimKiem;
    private DatabaseHelper db;
    private List<PhieuKho> danhSach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phieu_kho);
        setTitle("Phiếu Kho");

        db         = new DatabaseHelper(this);
        lvPhieuKho = findViewById(R.id.lvPhieuKho);
        etTimKiem  = findViewById(R.id.etTimKiem);

        // ── Nút thêm mới ──
        findViewById(R.id.btnThem).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditPhieuKhoActivity.class)));

        // ── Tìm kiếm realtime khi gõ ──
        etTimKiem.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            public void onTextChanged(CharSequence s,int a,int b,int c){
                loadDuLieu(s.toString().trim());
            }
            public void afterTextChanged(Editable s){}
        });

        // ── Click 1 lần → mở chi tiết phiếu ──
//        lvPhieuKho.setOnItemClickListener((parent, view, pos, id) -> {
//            PhieuKho p = danhSach.get(pos);
//            // Mở màn hình chi tiết phiếu kho
//            Intent i = new Intent(this, ChiTietPhieuKhoActivity.class);
//            i.putExtra("maPhieu", p.getMaPhieu());
//            startActivity(i);
//        });
        // ── Click 1 lần → hiện menu chọn hành động ──
        lvPhieuKho.setOnItemClickListener((parent, view, pos, id) -> {
            PhieuKho p = danhSach.get(pos);

            // Kiểm tra trạng thái để quyết định hiện nút Sửa hay không
            boolean coTheSua = "Chờ thanh toán".equals(p.getTrangThaiTT());

            // Tạo danh sách tùy chọn
            String[] options;
            if (coTheSua) {
                options = new String[]{"📋  Xem chi tiết sản phẩm", "✏️  Sửa thông tin phiếu"};
            } else {
                options = new String[]{"📋  Xem chi tiết sản phẩm", "🔒  Phiếu đã thanh toán (không sửa được)"};
            }

            new AlertDialog.Builder(this)
                    .setTitle("Phiếu: " + p.getMaPhieu() + "  [" + p.getTrangThaiTT() + "]")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            // Lựa chọn 1: Xem chi tiết sản phẩm
                            Intent i = new Intent(this, ChiTietPhieuKhoActivity.class);
                            i.putExtra("maPhieu", p.getMaPhieu());
                            startActivity(i);
                        } else if (which == 1 && coTheSua) {
                            // Lựa chọn 2: Sửa phiếu (chỉ khi Chờ thanh toán)
                            Intent i = new Intent(this, AddEditPhieuKhoActivity.class);
                            i.putExtra("maPhieu", p.getMaPhieu()); // truyền mã → AddEdit biết là SỬA
                            startActivity(i);
                        }
                        // Nếu chọn mục 2 mà phiếu đã thanh toán → không làm gì, dialog tự đóng
                    })
                    .show();
        });

        // ── Long click → xóa (có kiểm tra điều kiện) ──
        lvPhieuKho.setOnItemLongClickListener((parent, view, pos, id) -> {
            PhieuKho p = danhSach.get(pos);

            if ("Đã thanh toán".equals(p.getTrangThaiTT())) {
                // KHÔNG cho xóa nếu đã thanh toán
                Toast.makeText(this,
                        "Không thể xóa phiếu đã thanh toán",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Hiện hộp thoại xác nhận xóa
                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Xóa phiếu '" + p.getMaPhieu() + "'?\n" +
                                "Chi tiết liên quan cũng sẽ bị xóa!")
                        .setPositiveButton("XÓA", (d, w) -> {
                            db.xoaPhieuKho(p.getMaPhieu());
                            Toast.makeText(this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                            loadDuLieu(""); // Reload lại danh sách
                        })
                        .setNegativeButton("HỦY", null)
                        .show();
            }
            return true; // Tiêu thụ event, không kích hoạt click thường
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload khi quay lại từ AddEdit hoặc ChiTiet
        loadDuLieu(etTimKiem.getText().toString().trim());
    }

    /** Load và đổ dữ liệu vào ListView */
    private void loadDuLieu(String keyword) {
        if (keyword.isEmpty()) {
            danhSach = db.getAllPhieuKho();
        } else {
            danhSach = db.timKiemPhieuKho(keyword);
        }
        lvPhieuKho.setAdapter(new PhieuKhoAdapter(this, danhSach));
    }
}



