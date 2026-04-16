package com.example.kho_ketoan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.kho_ketoan.model.BangLuong;
import com.example.kho_ketoan.model.ChiTietPhieuKho;
import com.example.kho_ketoan.model.PhieuKho;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // ═══════════════ CẤU HÌNH DATABASE ═══════════════
    private static final String DB_NAME    = "QuanLyKho.db";
    private static final int    DB_VERSION = 4; // Tăng version để reset dữ liệu cũ

    // ═══════════════ TÊN BẢNG ═══════════════
    public static final String TBL_PHIEU_KHO  = "PHIEU_KHO";
    public static final String TBL_CHI_TIET   = "CHI_TIET_PHIEU_KHO";
    public static final String TBL_BANG_LUONG = "BANG_LUONG";
    public static final String TBL_THONG_KE   = "THONG_KE";

    // ═══════════════ BIỂU THỨC SQL TRÍCH THÁNG (MM/yy) ═══════════════
    // Hỗ trợ 3 định dạng ngày: yyyy-MM-dd | dd/MM/yyyy | dd/MM/yy (mới)
    private static final String THANG_PK =
            "(CASE " +
                    "WHEN ngayLapPhieu LIKE '__/__/____' THEN SUBSTR(ngayLapPhieu,4,2)||'/'||SUBSTR(ngayLapPhieu,7,4) " +
                    "ELSE NULL END)";

    // ═══════════════ CONSTRUCTOR ═══════════════
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // ═══════════════ TẠO BẢNG LẦN ĐẦU ═══════════════
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");

        db.execSQL("CREATE TABLE IF NOT EXISTS PHIEU_KHO (" +
                "maPhieu TEXT PRIMARY KEY, " +
                "loaiPhieu TEXT, " +
                "ngayLapPhieu TEXT, " +
                "tongTien REAL DEFAULT 0, " +
                "maNVKho TEXT, " +
                "maNCC TEXT, " +
                "trangThaiTT TEXT DEFAULT 'Cho thanh toan'" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS CHI_TIET_PHIEU_KHO (" +
                "maPhieu TEXT, " +
                "maSanpham TEXT, " +
                "soLuong INTEGER, " +
                "donGia REAL, " +
                "PRIMARY KEY (maPhieu, maSanpham), " +
                "FOREIGN KEY (maPhieu) REFERENCES PHIEU_KHO(maPhieu)" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS BANG_LUONG (" +
                "maBangLuong TEXT PRIMARY KEY, " +
                "thang TEXT, " +
                "luongCoBan REAL, " +
                "tongPhuCap REAL, " +
                "tongKhauTru REAL, " +
                "tongLuong REAL, " +
                "maNhanVien TEXT, " +
                "maNVKeToan TEXT" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS THONG_KE (" +
                "maThongKe TEXT PRIMARY KEY, " +
                "thang TEXT, " +
                "tongTienPhieuKho REAL, " +
                "tongTienLuong REAL, " +
                "maPhieu TEXT, " +
                "maBangLuong TEXT" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS NHA_CUNG_CAP (" +
                "maNCC TEXT PRIMARY KEY, " +
                "tenNCC TEXT NOT NULL, " +
                "sdt TEXT UNIQUE, " +
                "email TEXT UNIQUE, " +
                "diaChi TEXT, " +
                "trangThai TEXT DEFAULT 'Dang hop tac'" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS SAN_PHAM (" +
                "maSanpham TEXT PRIMARY KEY, " +
                "tenSanpham TEXT NOT NULL, " +
                "maDanhMuc TEXT, " +
                "hinhAnh TEXT, " +
                "motaSanpham TEXT, " +
                "donViTinh TEXT, " +
                "giaDon REAL DEFAULT 0, " +
                "soLuongTon INTEGER DEFAULT 0, " +
                "hanSuDung TEXT, " +
                "trangThai TEXT DEFAULT 'Dang kinh doanh', " +
                "maNCC TEXT, " +
                "FOREIGN KEY (maNCC) REFERENCES NHA_CUNG_CAP(maNCC)" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS NHAN_VIEN (" +
                "maNhanVien TEXT PRIMARY KEY, " +
                "hoTen TEXT NOT NULL, " +
                "sdt TEXT UNIQUE, " +
                "email TEXT UNIQUE, " +
                "cccd TEXT UNIQUE, " +
                "ngaySinh TEXT, " +
                "vaiTro TEXT, " +
                "tenDangnhap TEXT" +
                ")");
    }

    // ═══════════════ NÂNG CẤP DB ═══════════════
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS SAN_PHAM");
        db.execSQL("DROP TABLE IF EXISTS NHAN_VIEN");
        db.execSQL("DROP TABLE IF EXISTS NHA_CUNG_CAP");
        db.execSQL("DROP TABLE IF EXISTS THONG_KE");
        db.execSQL("DROP TABLE IF EXISTS CHI_TIET_PHIEU_KHO");
        db.execSQL("DROP TABLE IF EXISTS BANG_LUONG");
        db.execSQL("DROP TABLE IF EXISTS PHIEU_KHO");
        onCreate(db);
    }

    // ════════════════════════════════════════════════
    //              CRUD PHIEU_KHO
    // ════════════════════════════════════════════════

    public boolean themPhieuKho(PhieuKho p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("maPhieu",      p.getMaPhieu());
        cv.put("loaiPhieu",    p.getLoaiPhieu());
        cv.put("ngayLapPhieu", p.getNgayLapPhieu());
        cv.put("tongTien",     p.getTongTien());
        cv.put("maNVKho",      p.getMaNVKho());
        cv.put("maNCC",        p.getMaNCC());
        cv.put("trangThaiTT",  p.getTrangThaiTT());
        long result = db.insert("PHIEU_KHO", null, cv);
        db.close();
        return result != -1;
    }

    public List<PhieuKho> getAllPhieuKho() {
        List<PhieuKho> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM PHIEU_KHO ORDER BY ngayLapPhieu DESC", null);
        if (c.moveToFirst()) {
            do { list.add(cursorToPhieuKho(c)); } while (c.moveToNext());
        }
        c.close(); db.close();
        return list;
    }

    public List<PhieuKho> timKiemPhieuKho(String keyword) {
        List<PhieuKho> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String q = "SELECT * FROM PHIEU_KHO WHERE " +
                "maPhieu LIKE ? OR " +
                "loaiPhieu LIKE ? OR " +
                // tháng
                "SUBSTR(ngayLapPhieu,4,2) LIKE ? OR " +
                // năm
                "SUBSTR(ngayLapPhieu,7,4) LIKE ? OR " +
                // tháng/năm
                "(SUBSTR(ngayLapPhieu,4,2)||'/'||SUBSTR(ngayLapPhieu,7,4)) LIKE ?";

        String p = "%" + keyword + "%";
        Cursor c = db.rawQuery(q, new String[]{p, p, p, p, p});

        if (c.moveToFirst()) {
            do {
                list.add(cursorToPhieuKho(c));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    public PhieuKho getPhieuKhoById(String maPhieu) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM PHIEU_KHO WHERE maPhieu=?", new String[]{maPhieu});
        PhieuKho p = null;
        if (c.moveToFirst()) p = cursorToPhieuKho(c);
        c.close(); db.close();
        return p;
    }

    public boolean suaPhieuKho(PhieuKho p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("loaiPhieu",    p.getLoaiPhieu());
        cv.put("ngayLapPhieu", p.getNgayLapPhieu());
        // KHÔNG cập nhật tongTien ở đây — tránh bug reset về 0
        cv.put("maNVKho",      p.getMaNVKho());
        cv.put("maNCC",        p.getMaNCC());
        cv.put("trangThaiTT",  p.getTrangThaiTT());
        int rows = db.update("PHIEU_KHO", cv, "maPhieu=?", new String[]{p.getMaPhieu()});
        db.close();
        return rows > 0;
    }

    public boolean xoaPhieuKho(String maPhieu) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("CHI_TIET_PHIEU_KHO", "maPhieu=?", new String[]{maPhieu});
        int rows = db.delete("PHIEU_KHO", "maPhieu=?", new String[]{maPhieu});
        db.close();
        return rows > 0;
    }

    private PhieuKho cursorToPhieuKho(Cursor c) {
        return new PhieuKho(
                c.getString(c.getColumnIndexOrThrow("maPhieu")),
                c.getString(c.getColumnIndexOrThrow("loaiPhieu")),
                c.getString(c.getColumnIndexOrThrow("ngayLapPhieu")),
                c.getDouble(c.getColumnIndexOrThrow("tongTien")),
                c.getString(c.getColumnIndexOrThrow("maNVKho")),
                c.getString(c.getColumnIndexOrThrow("maNCC")),
                c.getString(c.getColumnIndexOrThrow("trangThaiTT"))
        );
    }

    // ════════════════════════════════════════════════
    //           CRUD CHI_TIET_PHIEU_KHO
    // ════════════════════════════════════════════════

    public boolean themChiTiet(ChiTietPhieuKho ct) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("maPhieu",   ct.getMaPhieu());
        cv.put("maSanpham", ct.getMaSanPham());
        cv.put("soLuong",   ct.getSoLuong());
        cv.put("donGia",    ct.getDonGia());
        long result = db.insert("CHI_TIET_PHIEU_KHO", null, cv);
        db.close();
        if (result != -1) {
            capNhatTongTienPhieu(ct.getMaPhieu());
            return true;
        }
        return false;
    }

    public List<ChiTietPhieuKho> getChiTietTheoPhieu(String maPhieu) {
        List<ChiTietPhieuKho> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM CHI_TIET_PHIEU_KHO WHERE maPhieu=?",
                new String[]{maPhieu});
        if (c.moveToFirst()) {
            do {
                list.add(new ChiTietPhieuKho(
                        c.getString(c.getColumnIndexOrThrow("maPhieu")),
                        c.getString(c.getColumnIndexOrThrow("maSanpham")),
                        c.getInt(c.getColumnIndexOrThrow("soLuong")),
                        c.getDouble(c.getColumnIndexOrThrow("donGia"))
                ));
            } while (c.moveToNext());
        }
        c.close(); db.close();
        return list;
    }

    public boolean suaChiTiet(ChiTietPhieuKho ct) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("soLuong", ct.getSoLuong());
        cv.put("donGia",  ct.getDonGia());
        int rows = db.update("CHI_TIET_PHIEU_KHO", cv,
                "maPhieu=? AND maSanpham=?",
                new String[]{ct.getMaPhieu(), ct.getMaSanPham()});
        db.close();
        if (rows > 0) { capNhatTongTienPhieu(ct.getMaPhieu()); return true; }
        return false;
    }

    public boolean xoaChiTiet(String maPhieu, String maSanPham) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("CHI_TIET_PHIEU_KHO",
                "maPhieu=? AND maSanpham=?",
                new String[]{maPhieu, maSanPham});
        db.close();
        if (rows > 0) { capNhatTongTienPhieu(maPhieu); return true; }
        return false;
    }

    public void capNhatTongTienPhieu(String maPhieu) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(
                "UPDATE PHIEU_KHO SET tongTien = (" +
                        "  SELECT IFNULL(SUM(soLuong * donGia), 0)" +
                        "  FROM CHI_TIET_PHIEU_KHO WHERE maPhieu = ?" +
                        ") WHERE maPhieu = ?",
                new String[]{maPhieu, maPhieu}
        );
        db.close();
    }

    // ════════════════════════════════════════════════
    //              CRUD BANG_LUONG
    // ════════════════════════════════════════════════

    public boolean themBangLuong(BangLuong bl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("maBangLuong", bl.getMaBangLuong());
        cv.put("thang",       bl.getThang());
        cv.put("luongCoBan",  bl.getLuongCoBan());
        cv.put("tongPhuCap",  bl.getTongPhuCap());
        cv.put("tongKhauTru", bl.getTongKhauTru());
        cv.put("tongLuong",   bl.getTongLuong());
        cv.put("maNhanVien",  bl.getMaNhanVien());
        cv.put("maNVKeToan",  bl.getMaNVKeToan());
        long r = db.insert("BANG_LUONG", null, cv);
        db.close();
        return r != -1;
    }

    public List<BangLuong> getAllBangLuong() {
        List<BangLuong> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM BANG_LUONG ORDER BY thang DESC", null);
        if (c.moveToFirst()) {
            do { list.add(cursorToBangLuong(c)); } while (c.moveToNext());
        }
        c.close(); db.close();
        return list;
    }

    public BangLuong getBangLuongById(String maBangLuong) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM BANG_LUONG WHERE maBangLuong=?",
                new String[]{maBangLuong});
        BangLuong bl = null;
        if (c.moveToFirst()) bl = cursorToBangLuong(c);
        c.close(); db.close();
        return bl;
    }

    private BangLuong cursorToBangLuong(Cursor c) {
        return new BangLuong(
                c.getString(c.getColumnIndexOrThrow("maBangLuong")),
                c.getString(c.getColumnIndexOrThrow("thang")),
                c.getDouble(c.getColumnIndexOrThrow("luongCoBan")),
                c.getDouble(c.getColumnIndexOrThrow("tongPhuCap")),
                c.getDouble(c.getColumnIndexOrThrow("tongKhauTru")),
                c.getDouble(c.getColumnIndexOrThrow("tongLuong")),
                c.getString(c.getColumnIndexOrThrow("maNhanVien")),
                c.getString(c.getColumnIndexOrThrow("maNVKeToan"))
        );
    }

    // ════════════════════════════════════════════════
    //              QUERY THỐNG KÊ — PHIẾU KHO
    // ════════════════════════════════════════════════

    /**
     * Lấy danh sách tháng MM/yy từ các phiếu ĐÃ THANH TOÁN.
     * Hỗ trợ 3 định dạng ngày: yyyy-MM-dd | dd/MM/yyyy | dd/MM/yy
     */
    public List<String> getDanhSachThangPhieuKho() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT DISTINCT " + THANG_PK + " AS thang " +
                "FROM PHIEU_KHO " +
                "WHERE ngayLapPhieu IS NOT NULL AND ngayLapPhieu != '' " +
                "  AND trangThaiTT = 'Đã thanh toán' " +
                "ORDER BY thang DESC";
        Cursor c = db.rawQuery(q, null);
        if (c.moveToFirst()) {
            do {
                String t = c.getString(0);
                if (t != null && !t.isEmpty()) list.add(t);
            } while (c.moveToNext());
        }
        c.close(); db.close();
        return list;
    }

    /**
     * Tổng tongTien của tất cả phiếu ĐÃ THANH TOÁN trong tháng MM/yy.
     */
    public double getTongTienPhieuKhoTheoThang(String thang) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT IFNULL(SUM(tongTien),0) FROM PHIEU_KHO " +
                        "WHERE " + THANG_PK + " = ? " +
                        "  AND trangThaiTT = 'Đã thanh toán'",
                new String[]{thang});
        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close(); db.close();
        return total;
    }

    /**
     * Tổng tongTien phiếu NHẬP đã thanh toán trong tháng MM/yy.
     */
    public double getTongTienNhapTheoThang(String thang) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT IFNULL(SUM(tongTien),0) FROM PHIEU_KHO " +
                        "WHERE " + THANG_PK + " = ? " +
                        "  AND trangThaiTT = 'Đã thanh toán' " +
                        "  AND loaiPhieu = 'NHAP'",
                new String[]{thang});
        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close(); db.close();
        return total;
    }

    /**
     * Tổng tongTien phiếu XUẤT đã thanh toán trong tháng MM/yy.
     */
    public double getTongTienXuatTheoThang(String thang) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT IFNULL(SUM(tongTien),0) FROM PHIEU_KHO " +
                        "WHERE " + THANG_PK + " = ? " +
                        "  AND trangThaiTT = 'Đã thanh toán' " +
                        "  AND loaiPhieu = 'XUAT'",
                new String[]{thang});
        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close(); db.close();
        return total;
    }

    // ════════════════════════════════════════════════
    //              QUERY THỐNG KÊ — LƯƠNG
    // ════════════════════════════════════════════════

    /**
     * Lấy danh sách tháng MM/yy từ BANG_LUONG.
     * thang được lưu thẳng dạng MM/yy (VD: 04/26).
     */
    public List<String> getDanhSachThangBangLuong() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT DISTINCT thang FROM BANG_LUONG " +
                        "WHERE thang IS NOT NULL AND thang != '' " +
                        "ORDER BY thang DESC", null);
        if (c.moveToFirst()) {
            do {
                String t = c.getString(0);
                if (t != null && !t.isEmpty()) list.add(t);
            } while (c.moveToNext());
        }
        c.close(); db.close();
        return list;
    }

    /** Tổng tongLuong trong tháng MM/yy */
    public double getTongTienLuongTheoThang(String thang) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT IFNULL(SUM(tongLuong),0) FROM BANG_LUONG WHERE thang = ?",
                new String[]{thang});
        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close(); db.close();
        return total;
    }
    // ════════════════════════════════════════════════
    //              THỐNG KÊ CHUNG (legacy)
    // ════════════════════════════════════════════════

    public double getTongTienKho() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(soLuong * donGia) FROM CHI_TIET_PHIEU_KHO " +
                        "JOIN PHIEU_KHO ON CHI_TIET_PHIEU_KHO.maPhieu = PHIEU_KHO.maPhieu " +
                        "WHERE loaiPhieu = 'NHAP'", null);
        double tong = 0;
        if (cursor.moveToFirst()) tong = cursor.getDouble(0);
        cursor.close();
        return tong;
    }

    // ════════════════════════════════════════════════
    //   HÀM LẤY DANH SÁCH MÃ CHO SPINNER
    // ════════════════════════════════════════════════

    public List<String> getAllMaNCC() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT maNCC, tenNCC FROM NHA_CUNG_CAP ORDER BY maNCC", null);
        if (c.moveToFirst()) {
            do { list.add(c.getString(0) + " - " + c.getString(1)); } while (c.moveToNext());
        }
        c.close(); db.close();
        return list;
    }

    public List<String> getAllMaSanPham() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT maSanpham, tenSanpham FROM SAN_PHAM ORDER BY maSanpham", null);
        if (c.moveToFirst()) {
            do { list.add(c.getString(0) + " - " + c.getString(1)); } while (c.moveToNext());
        }
        c.close(); db.close();
        return list;
    }

    public List<String> getAllMaNhanVien() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT maNhanVien, hoTen FROM NHAN_VIEN ORDER BY maNhanVien", null);
        if (c.moveToFirst()) {
            do { list.add(c.getString(0) + " - " + c.getString(1)); } while (c.moveToNext());
        }
        c.close(); db.close();
        return list;
    }

    /**
     * Lấy phần mã (trước ' - ') từ chuỗi Spinner.
     * VD: 'NCC001 - Cty ABC' → 'NCC001'
     */
    public static String layMaTuSpinner(String spinnerItem) {
        if (spinnerItem == null || spinnerItem.isEmpty()) return "";
        int idx = spinnerItem.indexOf(" - ");
        if (idx >= 0) return spinnerItem.substring(0, idx).trim();
        return spinnerItem.trim();
    }
}