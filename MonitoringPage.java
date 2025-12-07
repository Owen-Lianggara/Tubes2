import java.util.HashMap;
import java.util.Scanner;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MonitoringPage implements Page {
    Scanner sc = new Scanner(System.in);
    DBAccess db = DBAccess.getInstance();

    private Pengelola pengelola;

    public MonitoringPage(Pengelola pengelola) {
        this.pengelola = pengelola;
    }

    public void doAction() {
        // monitoring sRusun sarusun (tampilkan utilitas air)
        // mengendalikan iot setiap sRusun

        while (true) {
              DBAccess.Bold("====HALAMAN PENGELOLA====");
             DBAccess.Bold("===HALAMAN MONITORING PENGGUNA===\n");
            System.out.println("Pilih Menu:");
            System.out.println("0. Back");
            System.out.println("1. Monitoring sRusun Pengguna");
            System.out.println("2. Monitoring Akumulasi Air");
            System.out.print("Isi Perintah: ");
            int input = sc.nextInt();
            System.out.println();

            switch (input) {
                case 1:
                    try {
                        db.clearConsole();
                        monitorIOT();
                        db.clearConsole();
                    } catch (SQLException e) {
                        System.out.println("Database error: " + e.getMessage());
                    }
                    break;

                case 2:
                    try{
                        db.clearConsole();
                        monitorAkumulasi();
                        db.clearConsole();
                    } catch (SQLException e){
                        System.out.println("Database error: " + e.getMessage());
                    }
                    break;
                case 0:
                    return;
                default:
                    break;
            }
        }
    }
    
    public void monitorAkumulasi() throws SQLException {
        HashMap<Integer, String> kamar;
        db.Bold("===HALAMAN PENGELOLA===");
        db.Bold("===MONITOR AKUMULASI PENGGUNA===\n");
        System.out.print("Masukkan NIK Untuk Memilih Nomor Unit Sarusun: ");
        String NIK = sc.next();
        kamar = tampilkanKamarPenuh(NIK);
        int key = sc.nextInt();
        String inputKamar = kamar.get(key);

        if (inputKamar == null) {
            System.out.println("Unit tidak tersedia!");
        }
        else {
            String query = String.format("""
            SELECT
                NomorUnitSarusun, Akumulasi
            FROM 
                Unit
            WHERE 
                NomorUnitSarusun = '%s'
            """, inputKamar);

            ResultSet rs = db.executeQuery(query);
            db.clearConsole();
            System.out.printf("====TABEL KAMAR %s====\n", inputKamar);
            DBAccess.printTable(rs);
            System.out.println("Ketik x Untuk Keluar");
            sc.next();
        }
    }

    public void toggleIOT() throws SQLException{
        HashMap<Integer, String> kamar;
        db.Bold("====HALAMAN PENGELOLA====");
        db.Bold("====MONITORING AKUMULASI AIR====\n");
        System.out.print("Masukkan NIK Untuk Memilih Nomor Unit Sarusun: ");
        String NIK = sc.next();
        kamar = tampilkanKamarPenuh(NIK);
        int key = sc.nextInt();
        String inputKamar = kamar.get(key);

        if (inputKamar == null) {
            System.out.println("Unit tidak tersedia!");
        }
        else {
            //lihat status  aktuator air sekarang
            String query = String.format("SELECT TOP 1 Aksi FROM LOG_IOT WHERE NomorUnitSarusun = '%s' ORDER BY WaktuAksi DESC", inputKamar);
            int aksiSekarang;
            ResultSet rs = db.executeQuery(query);
            rs.next();

            aksiSekarang = rs.getInt(1);
            aksiSekarang = (aksiSekarang == 0)? 1:0;
        
            
            LocalDateTime sekarang = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String waktuFormatted = sekarang.format(formatter);
            
            String sql = String.format("INSERT INTO LOG_IOT VALUES (%d, '%s', '%s')", aksiSekarang, inputKamar, waktuFormatted);
            db.executeUpdate(sql);

            System.out.println("Toggle IoT Pengguna Berhasil!");
            System.out.println("Ketik x Untuk Keluar");
            sc.next();
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

    public void monitorIOT() throws SQLException {
        HashMap<Integer, String> kamar;
        db.clearConsole();
        db.Bold("====HALAMAN PENGELOLA====");
        db.Bold("===MONITORING SRUSUN PENGGUNA===\n");
        System.out.print("Masukkan NIK Untuk Memilih Nomor Unit Sarusun: ");
        String NIK = sc.next();
        kamar = tampilkanKamarPenuh(NIK);
        int key = sc.nextInt();
        String inputKamar = kamar.get(key);

        if (inputKamar == null) {
            System.out.println("Unit tidak tersedia!");
        }
        else {
            String query = String.format("""
            SELECT 
                ID_Aksi,
                NomorUnitSarusun,
                CASE Aksi 
                    WHEN 0 THEN 'TUTUP'
                    WHEN 1 THEN 'BUKA'
                    ELSE 'UNKNOWN'
                END AS StatusAksi,
                WaktuAksi
            FROM LOG_IOT
            WHERE NomorUnitSarusun = '%s'
            """, inputKamar);
            
            //output 
            ResultSet rs = db.executeQuery(query);
            db.clearConsole();
            DBAccess.Bold("====Tabel IOT sRusun====");
            DBAccess.printTable(rs);
            System.out.println("Ketik x Untuk Keluar");
            sc.next();
        }
    }
}
