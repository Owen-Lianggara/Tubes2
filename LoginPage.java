import java.sql.*;
import java.util.Scanner;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class LoginPage implements Page {
    private Scanner sc = new Scanner(System.in);

    // data
    private String noTLP, OTP = null, getOTP = null;
    private Pengguna p;

    private DBAccess db;

    public LoginPage() {
        db = DBAccess.getInstance();
    }

    public void doAction() {

        while (this.OTP == null || !this.OTP.equals(this.getOTP)) { // cek apakah otp sesuai dengan yang diberikan
            DBAccess.Bold("====LOGIN====\n");
            
            // input username
            System.out.print("Masukkan Nomor Telepon: ");
            this.noTLP = sc.next();

            try {
                if (!searchPengguna()) {
                    System.out.println("Nomor Telepon Tidak Terdaftar\n");
                    continue;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // ambil otp
            this.getOTP = noTLP.substring(noTLP.length() - 3) + String.valueOf((int) (Math.random() * 900) + 100);
            System.out.println("OTP: " + getOTP);

            // input otp
            System.out.print("Masukkan OTP: ");
            this.OTP = sc.next();
            if (!this.OTP.equals(this.getOTP)) {
                DBAccess.Bold("====OTP SALAH====");
            }
        }
        // Print jika sudah keluar while loop
        DBAccess.Bold("====LOGIN BERHASIL====\n");
        tambahLogLogin();
        //di sini

        db.clearConsole();
        
        DBAccess.Bold("====Selamat Datang di Aplikasi sRusun====\n");
    }
    
    public void tambahLogLogin() {
    try {
        LocalDateTime sekarang = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String waktuFormatted = sekarang.format(formatter);

        // Query INSERT ke tabel Logs
        String query = String.format("""
            INSERT INTO Logs (Aksi, Waktu, NIK_Pengguna) 
            VALUES (1, '%s', '%s')
        """, waktuFormatted, p.getNIK());

        db.executeUpdate(query); // Asumsikan db adalah instance DBAccess
        } catch (Exception e) {
            System.out.println("error di baris 70 LoginPage: " + e.getMessage());
        }
    }

    public boolean searchPengguna() throws SQLException { // cari di db
        // menyamakan No_Telp di dalam db untuk mencari tipe user
        //mencari no telepon
        DBAccess db = DBAccess.getInstance();
        String query = String.format("SELECT * FROM Pengguna WHERE No_Ponsel = '%s'", noTLP);
        ResultSet rs = db.executeQuery(query);

        // Check if result set has any rows
        if (rs == null || !rs.next()) {
            return false;
        }

        if (rs.getInt(5) == 1) {
            if (rs.getInt(4) == 0) { // pemilik
                this.p = new Pemilik(rs.getString(1), this.noTLP, rs.getString(3), 0);
            } else if (rs.getInt(4) == 1) { // admin
                this.p = new Admin(rs.getString(1), this.noTLP, rs.getString(3), 1);
            } else { // pengelola
                this.p = new Pengelola(rs.getString(1), this.noTLP, rs.getString(3), 2);
            }
            return true;
        }

        return false;
    }

    public Pengguna getPengguna() {
        return p;
    }
    
}
