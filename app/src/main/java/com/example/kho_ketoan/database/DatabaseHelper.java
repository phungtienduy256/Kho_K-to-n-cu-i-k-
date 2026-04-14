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
    private static final int    DB_VERSION = 1;

    // ═══════════════ TÊN BẢNG ═══════════════
    public static final String TBL_PHIEU_KHO  = "PHIEU_KHO";
    public static final String TBL_CHI_TIET   = "CHI_TIET_PHIEU_KHO";
    public static final String TBL_BANG_LUONG = "BANG_LUONG";
    public static final String TBL_THONG_KE   = "THONG_KE";

    // ═══════════════ CONSTRUCTOR ═══════════════
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // ═══════════════ TẠO BẢNG LẦN ĐẦU ═══════════════
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");

        // Tạo bảng PHIEU_KHO
        db.execSQL("CREATE TABLE IF NOT EXISTS PHIEU_KHO (" +
                "maPhieu TEXT PRIMARY KEY, " +
                "loaiPhieu TEXT, " +
                "ngayLapPhieu TEXT, " +
                "tongTien REAL DEFAULT 0, " +
                "maNVKho TEXT, " +
                "maNCC TEXT, " +
                "trangThaiTT TEXT DEFAULT 'Cho thanh toan'" +
                ")");

        // Tạo bảng CHI_TIET_PHIEU_KHO
        db.execSQL("CREATE TABLE IF NOT EXISTS CHI_TIET_PHIEU_KHO (" +
                "maPhieu TEXT, " +
                "maSanpham TEXT, " +
                "soLuong INTEGER, " +
                "donGia REAL, " +
                "PRIMARY KEY (maPhieu, maSanpham), " +
                "FOREIGN KEY (maPhieu) REFERENCES PHIEU_KHO(maPhieu)" +
                ")");

        // Tạo bảng BANG_LUONG
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

        // Tạo bảng THONG_KE
        db.execSQL("CREATE TABLE IF NOT EXISTS THONG_KE (" +
                "maThongKe TEXT PRIMARY KEY, " +
                "thang TEXT, " +
                "tongTienPhieuKho REAL, " +
                "tongTienLuong REAL, " +
                "maPhieu TEXT, " +
                "maBangLuong TEXT" +
                ")");
    }

    // ═══════════════ NÂNG CẤP DB ═══════════════
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS THONG_KE");
        db.execSQL("DROP TABLE IF EXISTS CHI_TIET_PHIEU_KHO");
        db.execSQL("DROP TABLE IF EXISTS BANG_LUONG");
        db.execSQL("DROP TABLE IF EXISTS PHIEU_KHO");
        onCreate(db);
    }

    // ════════════════════════════════════════════════
    //              CRUD PHIEU_KHO
    // ════════════════════════════════════════════════

    /** Thêm phiếu kho mới vào DB */
    public boolean themPhieuKho(PhieuKho p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("maPhieu",     p.getMaPhieu());
        cv.put("loaiPhieu",   p.getLoaiPhieu());
        cv.put("ngayLapPhieu",p.getNgayLapPhieu());
        cv.put("tongTien",    p.getTongTien());
        cv.put("maNVKho",     p.getMaNVKho());
        cv.put("maNCC",       p.getMaNCC());
        cv.put("trangThaiTT", p.getTrangThaiTT());
        long result = db.insert("PHIEU_KHO", null, cv);
        db.close();
        return result != -1; // -1 = thất bại
    }

    /** Lấy toàn bộ danh sách phiếu kho */
    public List<PhieuKho> getAllPhieuKho() {
        List<PhieuKho> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM PHIEU_KHO ORDER BY ngayLapPhieu DESC", null);
        if (c.moveToFirst()) {
            do {
                list.add(cursorToPhieuKho(c));
            } while (c.moveToNext());
        }
        c.close(); db.close();
        return list;
    }
    //Lấy mã NhanVien
    public List<String> getAllMaNhanVien() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT maNhanVien FROM NHAN_VIEN", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    //Lấy danh sách NHA_CUNG_CAP
    public List<String> getAllMaNCC() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT maNCC FROM NHA_CUNG_CAP", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    // 1. Thống kê tiền kho (tổng tiền các phiếu nhập)
    public double getTongTienKho() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Giả sử bảng CHI_TIET_PHIEU_KHO có cột thanhTien hoặc (soLuong * donGia)
        // Và lọc theo loại phiếu 'NHAP'
        Cursor cursor = db.rawQuery("SELECT SUM(soLuong * donGia) FROM CHI_TIET_PHIEU_KHO " +
                "JOIN PHIEU_KHO ON CHI_TIET_PHIEU_KHO.maPhieu = PHIEU_KHO.maPhieu " +
                "WHERE loaiPhieu = 'NHAP'", null);
        double tong = 0;
        if (cursor.moveToFirst()) tong = cursor.getDouble(0);
        cursor.close();
        return tong;
    }

    // 2. Thống kê tiền bán hàng từ bảng HOA_DON
    public double getTongTienBanHang() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(tongTienTT) FROM HOA_DON", null);
        double tong = 0;
        if (cursor.moveToFirst()) tong = cursor.getDouble(0);
        cursor.close();
        return tong;
    }
    /** Tìm kiếm phiếu kho theo mã hoặc loại phiếu */
    public List<PhieuKho> timKiemPhieuKho(String keyword) {
        List<PhieuKho> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM PHIEU_KHO WHERE maPhieu LIKE ? OR loaiPhieu LIKE ?";
        String p = "%" + keyword + "%";
        Cursor c = db.rawQuery(q, new String[]{p, p});
        if (c.moveToFirst()) {
            do { list.add(cursorToPhieuKho(c)); } while (c.moveToNext());
        }
        c.close(); db.close();
        return list;
    }

    /** Lấy 1 phiếu kho theo mã (dùng khi mở màn hình sửa) */
    public PhieuKho getPhieuKhoById(String maPhieu) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM PHIEU_KHO WHERE maPhieu=?", new String[]{maPhieu});
        PhieuKho p = null;
        if (c.moveToFirst()) p = cursorToPhieuKho(c);
        c.close(); db.close();
        return p;
    }

    /** Sửa thông tin phiếu kho */
    public boolean suaPhieuKho(PhieuKho p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("loaiPhieu",   p.getLoaiPhieu());
        cv.put("ngayLapPhieu",p.getNgayLapPhieu());
        cv.put("tongTien",    p.getTongTien());
        cv.put("maNVKho",     p.getMaNVKho());
        cv.put("maNCC",       p.getMaNCC());
        cv.put("trangThaiTT", p.getTrangThaiTT());
        int rows = db.update("PHIEU_KHO", cv, "maPhieu=?", new String[]{p.getMaPhieu()});
        db.close();
        return rows > 0;
    }

    /**
     * Xóa phiếu kho.
     * QUAN TRỌNG: Phải xóa CHI_TIET trước để tránh lỗi FK constraint.
     */
    public boolean xoaPhieuKho(String maPhieu) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Bước 1: Xóa chi tiết phiếu kho trước
        db.delete("CHI_TIET_PHIEU_KHO", "maPhieu=?", new String[]{maPhieu});
        // Bước 2: Xóa phiếu kho chính
        int rows = db.delete("PHIEU_KHO", "maPhieu=?", new String[]{maPhieu});
        db.close();
        return rows > 0;
    }

    /** Helper: chuyển Cursor thành đối tượng PhieuKho */
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

    /** Thêm 1 sản phẩm vào chi tiết phiếu, sau đó cập nhật tongTien */
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
            capNhatTongTienPhieu(ct.getMaPhieu()); // Tự cập nhật lại tongTien
            return true;
        }
        return false;
    }

    /** Lấy danh sách chi tiết theo maPhieu */
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

    /** Sửa số lượng và đơn giá của chi tiết, sau đó cập nhật tongTien */
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

    /** Xóa 1 dòng chi tiết, sau đó cập nhật tongTien */
    public boolean xoaChiTiet(String maPhieu, String maSanPham) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("CHI_TIET_PHIEU_KHO",
                "maPhieu=? AND maSanpham=?",
                new String[]{maPhieu, maSanPham});
        db.close();
        if (rows > 0) { capNhatTongTienPhieu(maPhieu); return true; }
        return false;
    }

    /**
     * Tự cập nhật tongTien trong PHIEU_KHO.
     * Gọi mỗi khi thêm/sửa/xóa 1 dòng chi tiết.
     * tongTien = SUM(soLuong * donGia) WHERE maPhieu = ?
     */
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

    /** Thêm bảng lương mới */
    public boolean themBangLuong(BangLuong bl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("maBangLuong",  bl.getMaBangLuong());
        cv.put("thang",        bl.getThang());
        cv.put("luongCoBan",   bl.getLuongCoBan());
        cv.put("tongPhuCap",   bl.getTongPhuCap());
        cv.put("tongKhauTru",  bl.getTongKhauTru());
        cv.put("tongLuong",    bl.getTongLuong());
        cv.put("maNhanVien",   bl.getMaNhanVien());
        cv.put("maNVKeToan",   bl.getMaNVKeToan());
        long r = db.insert("BANG_LUONG", null, cv);
        db.close();
        return r != -1;
    }

    /** Lấy tất cả bảng lương */
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

    /** Lấy 1 bảng lương theo mã */
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
    //              QUERY THỐNG KÊ
    // ════════════════════════════════════════════════

    /**
     * Lấy danh sách tháng duy nhất từ cả PHIEU_KHO và BANG_LUONG.
     * Dùng để đổ vào Spinner chọn tháng ở màn hình ThongKe.
     */
    public List<String> getDanhSachThang() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // strftime('%m-%Y', ngayLapPhieu) chuyển ngày sang định dạng MM-yyyy
        String q = "SELECT DISTINCT strftime('%m-%Y', ngayLapPhieu) AS thang " +
                "FROM PHIEU_KHO WHERE thang IS NOT NULL " +
                "UNION " +
                "SELECT DISTINCT thang FROM BANG_LUONG WHERE thang IS NOT NULL " +
                "ORDER BY thang DESC";
        Cursor c = db.rawQuery(q, null);
        if (c.moveToFirst()) {
            do { list.add(c.getString(0)); } while (c.moveToNext());
        }
        c.close(); db.close();
        return list;
    }
    /**
     * Lấy danh sách tháng chỉ từ PHIEU_KHO.
     * Dùng cho Spinner khối Thống Kê Phiếu Kho.
     * ngayLapPhieu lưu dạng: yyyy-MM-dd
     * strftime('%m-%Y', '2026-04-14') → '04-2026'
     */
    public List<String> getDanhSachThangPhieuKho() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT DISTINCT strftime('%m-%Y', ngayLapPhieu) AS thang " +
                "FROM PHIEU_KHO " +
                "WHERE ngayLapPhieu IS NOT NULL AND ngayLapPhieu != '' " +
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
     * Lấy danh sách tháng chỉ từ BANG_LUONG.
     * Dùng cho Spinner khối Thống Kê Lương.
     * thang đã lưu sẵn dạng MM-yyyy, ví dụ: 04-2026
     */
    public List<String> getDanhSachThangBangLuong() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT DISTINCT thang FROM BANG_LUONG " +
                "WHERE thang IS NOT NULL AND thang != '' " +
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

    /** Tính tổng tongTien của PHIEU_KHO trong tháng chỉ định (MM-yyyy) */
    public double getTongTienPhieuKhoTheoThang(String thang) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT IFNULL(SUM(tongTien),0) FROM PHIEU_KHO " +
                        "WHERE strftime('%m-%Y', ngayLapPhieu) = ?",
                new String[]{thang});
        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close(); db.close();
        return total;
    }

    /** Tính tổng tongLuong của BANG_LUONG trong tháng chỉ định (MM-yyyy) */
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
}

