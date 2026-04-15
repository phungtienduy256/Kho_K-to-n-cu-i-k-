package com.example.kho_ketoan.activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kho_ketoan.R;
import com.example.kho_ketoan.database.DatabaseHelper;
import android.database.sqlite.SQLiteDatabase;

/**
 * MÀN HÌNH CHÈN DỮ LIỆU MẪU — CHỈ DÙNG ĐỂ TEST.
 * ĐỂ XÓA: Xóa file này + activity_seed_data.xml +
 *         1 dòng <activity> trong AndroidManifest.xml.
 * CHƯƠNG TRÌNH VẪN CHẠY BÌNH THƯỜNG SAU KHI XÓA.
 */
public class SeedDataActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private TextView tvKetQua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed_data);
        setTitle("Dữ Liệu Mẫu");

        db = new DatabaseHelper(this);
        tvKetQua = findViewById(R.id.tvKetQua);

        ((Button) findViewById(R.id.btnInsertSeed))
                .setOnClickListener(v -> insertDuLieuMau());

        ((Button) findViewById(R.id.btnXoaHetDuLieu))
                .setOnClickListener(v -> xacNhanXoaHet());
    }

    private void insertDuLieuMau() {
        SQLiteDatabase sqdb = db.getWritableDatabase();
        StringBuilder log = new StringBuilder();
        int ok = 0, skip = 0;

        // ── Hàm helper: INSERT OR IGNORE ──
        // Nếu mã đã tồn tại → bỏ qua (không báo lỗi)

        try {

            // ════════════ NHA_CUNG_CAP ════════════
            String[][] nccs = {
                    {"NCC001","Cong ty TNHH Thuc Pham An Khang","0901234567","ankhang@email.com","123 Nguyen Trai, HN","Dang hop tac"},
                    {"NCC002","Cong ty CP Hang Tieu Dung Viet","0912345678","htviet@email.com","456 Le Loi, HCM","Dang hop tac"},
                    {"NCC003","Cong ty TNHH Xuat Nhap Khau Minh Duc","0923456789","minhduc@email.com","789 Tran Hung Dao, DN","Ngung hop tac"},
            };
//            for (String[] r : nccs) {
//                long res = sqdb.execSQL(
//                        "INSERT OR IGNORE INTO NHA_CUNG_CAP VALUES(?,?,?,?,?,?)",
//                        r) == null ? 1 : 0;
//                // Dùng cách khác vì execSQL không trả về long
//            }
            // Dùng try-catch riêng cho từng bảng để insert
            try { sqdb.execSQL("INSERT OR IGNORE INTO NHA_CUNG_CAP VALUES('NCC001','Cong ty TNHH Thuc Pham An Khang','0901234567','ankhang@email.com','123 Nguyen Trai HN','Dang hop tac')"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO NHA_CUNG_CAP VALUES('NCC002','Cong ty CP Hang Tieu Dung Viet','0912345678','htviet@email.com','456 Le Loi HCM','Dang hop tac')"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO NHA_CUNG_CAP VALUES('NCC003','Cty XNK Minh Duc','0923456789','minhduc@email.com','789 Tran Hung Dao DN','Ngung hop tac')"); ok++; } catch(Exception e){skip++;}
            log.append("✅ NHA_CUNG_CAP: đã xử lý\n");

            // ════════════ NHAN_VIEN ════════════
            try { sqdb.execSQL("INSERT OR IGNORE INTO NHAN_VIEN VALUES('NV001','Nguyen Van An','0931234567','an.nv@cty.com','079123456789','2000-05-10','Kho','annv')"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO NHAN_VIEN VALUES('NV002','Tran Thi Bich','0942345678','bich.tt@cty.com','079234567890','1998-08-20','Ke toan','bichtt')"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO NHAN_VIEN VALUES('NV003','Le Van Cuong','0953456789','cuong.lv@cty.com','079345678901','1995-12-15','Quan ly','cuonglv')"); ok++; } catch(Exception e){skip++;}
            log.append("✅ NHAN_VIEN: đã xử lý\n");

            // ════════════ SAN_PHAM ════════════
            try { sqdb.execSQL("INSERT OR IGNORE INTO SAN_PHAM VALUES('SP001','Gao Tam Thom 5kg','DM001',null,'Gao thom chat luong cao','Tui',85000,100,'2026-12-31','Dang kinh doanh','NCC001')"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO SAN_PHAM VALUES('SP002','Nuoc Mam Nam Ngu 500ml','DM002',null,'Nuoc mam truyen thong','Chai',25000,200,'2027-06-30','Dang kinh doanh','NCC001')"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO SAN_PHAM VALUES('SP003','Dau An Tuong An 1L','DM002',null,'Dau an thuc vat','Chai',45000,150,'2027-03-15','Dang kinh doanh','NCC002')"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO SAN_PHAM VALUES('SP004','Mi Hao Hao Tom Chua Cay','DM003',null,'Mi an lien pho bien','Thung',125000,80,'2026-09-20','Dang kinh doanh','NCC002')"); ok++; } catch(Exception e){skip++;}
            log.append("✅ SAN_PHAM: đã xử lý\n");

            // ════════════ PHIEU_KHO ════════════
            try { sqdb.execSQL("INSERT OR IGNORE INTO PHIEU_KHO VALUES('PK001','NHAP','2026-04-10',0,'NV001','NCC001','Cho thanh toan')"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO PHIEU_KHO VALUES('PK002','NHAP','2026-04-15',0,'NV001','NCC002','Da thanh toan')"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO PHIEU_KHO VALUES('PK003','XUAT','2026-04-18',0,'NV001','NCC001','Cho thanh toan')"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO PHIEU_KHO VALUES('PK004','NHAP','2026-05-02',0,'NV001','NCC002','Cho thanh toan')"); ok++; } catch(Exception e){skip++;}
            log.append("✅ PHIEU_KHO: đã xử lý\n");

            // ════════════ CHI_TIET_PHIEU_KHO ════════════
            try { sqdb.execSQL("INSERT OR IGNORE INTO CHI_TIET_PHIEU_KHO VALUES('PK001','SP001',50,80000)"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO CHI_TIET_PHIEU_KHO VALUES('PK001','SP002',100,22000)"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO CHI_TIET_PHIEU_KHO VALUES('PK002','SP003',80,42000)"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO CHI_TIET_PHIEU_KHO VALUES('PK002','SP004',40,120000)"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO CHI_TIET_PHIEU_KHO VALUES('PK003','SP001',20,85000)"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO CHI_TIET_PHIEU_KHO VALUES('PK004','SP002',50,23000)"); ok++; } catch(Exception e){skip++;}
            log.append("✅ CHI_TIET_PHIEU_KHO: đã xử lý\n");

            // Cập nhật tongTien cho các phiếu
            db.capNhatTongTienPhieu("PK001");
            db.capNhatTongTienPhieu("PK002");
            db.capNhatTongTienPhieu("PK003");
            db.capNhatTongTienPhieu("PK004");
            log.append("✅ Đã cập nhật tongTien các phiếu\n");

            // ════════════ BANG_LUONG ════════════
            try { sqdb.execSQL("INSERT OR IGNORE INTO BANG_LUONG VALUES('BL001','04-2026',5000000,500000,200000,5300000,'NV001','NV002')"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO BANG_LUONG VALUES('BL002','04-2026',6000000,800000,300000,6500000,'NV002','NV002')"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO BANG_LUONG VALUES('BL003','05-2026',5000000,500000,0,5500000,'NV001','NV002')"); ok++; } catch(Exception e){skip++;}
            log.append("✅ BANG_LUONG: đã xử lý\n");

            // ════════════ HOA_DON ════════════
            try { sqdb.execSQL("INSERT OR IGNORE INTO HOA_DON VALUES('HD001','KH001','2026-04-12','COD',350000,'Cho duyet',null)"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO HOA_DON VALUES('HD002','KH002','2026-04-16','Chuyen khoan',820000,'Da duyet',null)"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO HOA_DON VALUES('HD003','KH001','2026-04-20','COD',175000,'Da huy','Khach doi y')"); ok++; } catch(Exception e){skip++;}
            try { sqdb.execSQL("INSERT OR IGNORE INTO HOA_DON VALUES('HD004','KH003','2026-05-05','Tien mat',630000,'Da duyet',null)"); ok++; } catch(Exception e){skip++;}
            log.append("✅ HOA_DON: đã xử lý\n");

            log.append("\n🎉 Hoàn tất! Có thể chạy thử ứng dụng.");
            tvKetQua.setText(log.toString());
            Toast.makeText(this, "Chèn dữ liệu mẫu thành công!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            tvKetQua.setText("❌ Lỗi: " + e.getMessage());
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /** Xóa toàn bộ dữ liệu (giữ cấu trúc bảng) để test lại từ đầu */
    private void xacNhanXoaHet() {
        new AlertDialog.Builder(this)
                .setTitle("⚠️ Xác nhận xóa hết")
                .setMessage("Xóa TOÀN BỘ dữ liệu trong tất cả bảng?\nHành động này không thể hoàn tác!")
                .setPositiveButton("XÓA HẾT", (d, w) -> {
                    SQLiteDatabase sqdb = db.getWritableDatabase();
                    sqdb.execSQL("DELETE FROM HOA_DON");
                    sqdb.execSQL("DELETE FROM BANG_LUONG");
                    sqdb.execSQL("DELETE FROM CHI_TIET_PHIEU_KHO");
                    sqdb.execSQL("DELETE FROM PHIEU_KHO");
                    sqdb.execSQL("DELETE FROM SAN_PHAM");
                    sqdb.execSQL("DELETE FROM NHAN_VIEN");
                    sqdb.execSQL("DELETE FROM NHA_CUNG_CAP");
                    tvKetQua.setText("✅ Đã xóa hết dữ liệu. DB sạch.");
                    Toast.makeText(this, "Đã xóa hết!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("HỦY", null)
                .show();
    }
}

