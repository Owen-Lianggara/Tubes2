import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.HashMap;
import java.util.Scanner;

public class KelolaRusunPage implements Page {
    Scanner sc = new Scanner(System.in);
    DBAccess db = DBAccess.getInstance();
    // fitur
    // +- pengguna ok
    // kelola iot
    // kelola sarusun
    Admin admin;

    public KelolaRusunPage(Admin admin) {
        this.admin = admin;
    }

    public void riwayatSemua() {
        db.Bold("====HALAMAN ADMIN====");
        db.Bold("====HISTORI KESELURUHAN====");
        String query = 
        String.format("""
        SELECT 
            CASE Aksi 
                WHEN 0 THEN 'LOGOUT'
                WHEN 1 THEN 'LOGIN'
                ELSE 'UNKNOWN'
            END AS StatusAksi,
            Waktu,NIK_Pengguna
        FROM Logs
        """);
        
        try {
            ResultSet rs = db.executeQuery(query);
            DBAccess.printTable(rs);
        }
        catch (SQLException i) {
            System.out.println("error baris 38 KelolaRusunPage: " + i);
        }

        System.out.println("Ketik x Untuk Keluar");
        sc.next();
    }

    public void riwayatSatuNIK() {
        db.Bold("====HALAMAN ADMIN====");
        db.Bold("====HISTORI INDIVIDU====");
        System.out.print("Masukkan NIK: ");
        String NIK = sc.next();

        String query = String.format("""
        SELECT 
            CASE Aksi 
                WHEN 0 THEN 'LOGOUT'
                WHEN 1 THEN 'LOGIN'
                ELSE 'UNKNOWN'
            END AS StatusAksi, Waktu
            FROM Logs
            WHERE NIK_Pengguna = '%s' """,NIK
        );
        
        try {
            ResultSet rs = db.executeQuery(query);
            DBAccess.printTable(rs);
        }
        catch (SQLException i) {
            System.out.println("error baris 62 KelolaRusunPage: " + i);
        }

        System.out.println("Ketik x Untuk Keluar");
        sc.next();
    }

    public void monitorLogin() {
        while (true) {
            db.Bold("====HALAMAN ADMIN====");
            db.Bold("====HISTORI LOGIN====\n");
            // System.out.print("Masukan NIK Pengguna: ");
            // String NIK = sc.next();
            System.out.print("""
            0. Back
            1. Cari Riwayat Login Pengguna
            2. Tampilkan Seluruh Riwayat Login
            """);
            System.out.print("Pilih Menu: ");
    
            int input = sc.nextInt();
    
            switch (input) {
                case 0:
                    db.clearConsole();
                    return;
                case 1:
                    db.clearConsole();
                    riwayatSatuNIK();
                    db.clearConsole();
                    break;
                case 2:
                    db.clearConsole();
                    riwayatSemua();
                    db.clearConsole();
                    break;
                default:
                    break;
            }
        }
    }

    public void doAction() {
        while (true) {
            db.Bold("====HALAMAN ADMIN====");
            db.Bold("====PENGELOLAAN====\n");
            System.out.println("""
            0. Back
            1. Kelola Pengguna
            2. Kelola Sarusun
            3. Atur Keluaran Air
            Pilih Menu:
            """);
        
            System.out.print("Isi Perintah: ");
            int input = sc.nextInt();
            System.out.println();

            switch (input) {
                case 0:
                    return;
                case 1:
                    db.clearConsole();
                    kelolaOrang();
                    db.clearConsole();
                    break;
                case 2:
                    db.clearConsole();
                    kelolaSarusun();
                    db.clearConsole();
                    break;
                case 3:
                    try {
                        db.clearConsole();
                        AturKeluaranAir();
                        db.clearConsole();
                    } catch (SQLException e) {
                        System.out.println("Database error: " + e.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void kelolaSarusun() {
        while(true){
            db.Bold("====HALAMAN ADMIN====");
            db.Bold("====ATUR JUMLAH SARUSUN====\n");
            System.out.println("""
                
            Pilih Menu:
            0. Back
            1. Tambah Sarusun
            2. Kurangi Sarusun
            """);
            System.out.print("Isi Perintah: ");
            int input = sc.nextInt();
            System.out.println();

            switch (input) {
                case 0:
                    return;
                case 1:
                    try {
                        db.clearConsole();
                        tambahUnit();
                        db.clearConsole();
                    } catch (SQLException e) {
                        System.out.println("Database error: " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        db.clearConsole();
                        kurangiUnit();
                        db.clearConsole();
                    } catch (SQLException e) {
                        System.out.println("Database error: " + e.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void tambahUnit() throws SQLException {
        HashMap<Integer, String> kamar;
        DBAccess.Bold("====HALAMAN ADMIN====");
        DBAccess.Bold("====ISI DATA SARUSUN====");
        kamar = tampilkanKamarKosong();

        System.out.print("Masukkan Nomor Unit Sarusun: ");
        int key = sc.nextInt();
        String Sarusun = kamar.get(key);
        System.out.print("Masukkan NIK Pengguna: ");
        String NIK = sc.next();

        String sql = String.format("""
            UPDATE Unit 
            SET NIK_Pengguna = '%s', Kondisi = 1 
            WHERE NomorUnitSarusun = '%s'""", NIK, Sarusun);
        System.out.println("Berhasil!");

        try {
            db.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void kurangiUnit() throws SQLException {
        HashMap<Integer, String> kamar;
        DBAccess.Bold("====HALAMAN ADMIN====");
        DBAccess.Bold("====ISI DATA PENGURANGAN SARUSUN====");
        System.out.print("Masukkan NIK Untuk Memilih Nomor Unit Sarusun: ");
        String NIK = sc.next();
        kamar = tampilkanKamarPenuh(NIK);

        System.out.print("Masukkan Nomor Unit Sarusun: ");
        int key = sc.nextInt();
        String inputKamar = kamar.get(key);

        if (inputKamar == null) {
            System.out.println("Unit tidak tersedia!");
        }
        else {

            String sql = String.format("""
                UPDATE Unit 
                SET NIK_Pengguna = NULL, Kondisi = 0 
                WHERE NomorUnitSarusun = '%s'
            """, inputKamar);
            System.out.println("Berhasil!");

            try {
                db.executeUpdate(sql);
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }
    
    public void AturKeluaranAir() throws SQLException {
        HashMap<Integer, String> kamar;
        DBAccess.Bold("====HALAMAN ADMIN====");
        DBAccess.Bold("====ISI DATA PENGATURAN AIR====\n");
        System.out.print("Masukkan NIK Untuk Memilih Nomor Unit Sarusun: ");
        String NIK = sc.next();
        kamar = tampilkanKamarPenuh(NIK);
        int key = sc.nextInt();
        String inputKamar = kamar.get(key);

        if (inputKamar == null) {
            System.out.println("Unit tidak tersedia!");
        }
        else {
            System.out.print("Masukkan Utilitas Air (Liter/Detik): ");
            double literPerDetik = sc.nextDouble();
            String query = String.format("""
                UPDATE Unit 
                SET UtilitasAirPerDetik = %.2f 
                WHERE NomorUnitSarusun = '%s'
            """, literPerDetik, inputKamar);

            db.executeUpdate(query);
        }
    }
     
    public HashMap<Integer, String> tampilkanKamarPenuh(String NIK) throws SQLException {
        HashMap<Integer, String> kamar = new HashMap<>();
            
        //liter per detik
        String query = String.format("""
            SELECT 
                NomorUnitSarusun 
            FROM 
                Unit 
            WHERE 
                kondisi = 1 AND NIK_Pengguna = '%s'
        """, NIK);
        
        ResultSet rs = db.executeQuery(query);
        
        int i = 1;
        while (rs.next()) {
            System.out.println(i + ". " + rs.getString(1));
            kamar.put(i, rs.getString(1));
            i++;
        }
        System.out.print("Pilih Unit Rusun: ");
        return kamar;
    }

    public HashMap<Integer, String> tampilkanKamarKosong() throws SQLException {
        System.out.println("Pilih Unit Rusun: ");
        HashMap<Integer, String> kamar = new HashMap<>();

        //liter per detik
        String query = String.format("""
        SELECT NomorUnitSarusun FROM Unit WHERE kondisi = 0
        """);

        ResultSet rs = db.executeQuery(query);

        int i = 1;
        while (rs.next()) {
            System.out.println(i + ". " + rs.getString(1));
            kamar.put(i, rs.getString(1));
            i++;
        }
        return kamar;
    }

    public void kurangiPengguna() {
        db.Bold("====HALAMAN ADMIN====");
        db.Bold("====ISI DATA PENGGUNA====\n");
        System.out.print("Input NIK Pengguna yang ingin Dihapus: ");
        String NIK = sc.next();

        String sql = String.format("""
            UPDATE Pengguna 
            SET kondisi = 0 WHERE NIK = '%s'
        """, NIK);
        try {
            db.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        
        sql = String.format("""
            UPDATE Unit 
            SET kondisi = 0 WHERE NIK_Pengguna = '%s'
        """, NIK);

        try {
            db.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        System.out.println("Pengguna Berhasil Dihapus!!"); //unhandled ketika NIK tidak ada
        System.out.println("\nKetik x Untuk Keluar");
        sc.next();
    }

    public void tambahPengguna() {
        db.Bold("====HALAMAN ADMIN====");
        DBAccess.Bold("====ISI DATA PENGGUNA====\n");
        System.out.print("Masukkan NIK: ");
        String NIK = sc.next();

        System.out.print("Masukkan Nomor Ponsel: ");
        String No_Ponsel = sc.next();
        System.out.println("""
        Masukkan Tipe User: 
        1. Administrator
        2. Pengelola
        3. Pemilik
        """);
        System.out.print("Jenis User: ");
        int TipeUser = sc.nextInt();
        if(TipeUser == 3) TipeUser = 0;
        
        sc.nextLine();
        System.out.print("Masukkan Alamat Domisili: ");
        String Alamat_Domisili = sc.nextLine();

        String sql = String.format(
        "INSERT INTO Pengguna (NIK, No_Ponsel, Alamat_Domisili, TipeUser, Kondisi) VALUES ('%s', '%s', '%s', %d, %d)",
            NIK, No_Ponsel, Alamat_Domisili, TipeUser, 1
        );

        try {
            db.executeUpdate(sql);
            System.out.println("Berhasil!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void lihatPengguna() throws SQLException {
        db.Bold("====HALAMAN ADMIN====");
        String query = """
            SELECT 
                NIK, No_Ponsel, Alamat_Domisili, 
            CASE 
                TipeUser
                    WHEN 0 THEN 'Pemilik'
                    WHEN 1 THEN 'Administrator'
                    ELSE 'Pengelola' END AS JenisPengguna
            FROM Pengguna 
            Where Kondisi = 1 
        """;
        
        ResultSet rs = db.executeQuery(query);
        DBAccess.Bold("====Tabel Pengguna Aktif====\n");
        DBAccess.printTable(rs);
        System.out.println("\nKetik x Untuk Keluar");
        sc.next();
    }

    public void kelolaOrang() {
        boolean izinAkses = true;

        while (izinAkses) {
            db.Bold("====HALAMAN ADMIN====");
            db.Bold("====ATUR PENGGUNA====\n");
            System.out.println("""    
            Pilih Menu: 
            0. Back
            1. Lihat Penguna
            2. Tambahkan Pengguna
            3. Hilangkan Pengguna
            """);
            System.out.print("Isi Perintah: ");
            int input = sc.nextInt();
            System.out.println();

            switch (input) {
                case 0:
                    db.clearConsole();
                    izinAkses = false;
                    break;
                case 1:
                    try {
                        db.clearConsole();
                        lihatPengguna();
                        db.clearConsole();
                    } catch (SQLException e) {
                        System.out.println("Database error: " + e.getMessage());
                    }
                    break;
                case 2:
                    db.clearConsole();
                    tambahPengguna();
                    db.clearConsole();
                    break;
                case 3:
                    db.clearConsole();
                    kurangiPengguna();
                    db.clearConsole();
                    break;
                default:
                    break;
            }
        }
    }
}