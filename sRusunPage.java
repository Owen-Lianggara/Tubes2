import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Scanner;

public class sRusunPage implements Page {
    Scanner sc = new Scanner(System.in);
    // fitur
    // mati nyala air
    // data: no unit sarusun, liter per menit, no serial iot, Akumulasi,
    // data: utilitas air per

    private Pengguna p;
    private String noKamar;
    private DBAccess db = DBAccess.getInstance();

    public sRusunPage(Pengguna p) {
        this.p = p;
    }

    public HashMap<Integer, String> tampilkanKamarPenuh(String NIK) throws SQLException {
        System.out.println("Nomor Sarusun yang Tersedia: ");
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
        return kamar;
    }

    public void pilihSarusun() throws SQLException {
        HashMap<Integer, String> kamar;
        DBAccess.Bold("====SRUSUN====");
        kamar = tampilkanKamarPenuh(p.getNIK());

        if (kamar.size() == 0) {
            System.out.println("Nomor Unit Sarusun Tidak Tersedia");
            return;
        }

        while (true) {
            // tampilkan nomor sarusun
            System.out.print("Pilih Unit Rusun: ");
            int input = sc.nextInt();
            noKamar = kamar.get(input);
            System.out.println();

            if (noKamar == null) {
                System.out.println("Nomor Kamar Salah. Coba Lagi.");
            } else {
                break;
            }
        }
    }

    public void doAction() {
        boolean izinAkses = true;

        try {
            pilihSarusun();
            db.clearConsole();
        } catch (SQLException i) {
            System.out.println("error di baris 74 sRusunPage: " + i);
        }

        while (izinAkses) {
            DBAccess.Bold("====HALAMAN PEMILIK====");
            DBAccess.Bold("====SRUSUN====");
            System.out.println("""
            Pilih Menu: 
            0. Back
            1. Buka/Tutup Saluran Air
            2. Penggunaan Utilitas Air
            3. Tampilkan Histori
            """);

            System.out.print("Isi Perintah: ");
            int input = sc.nextInt();
            System.out.println();

            switch (input) {
                case 0:
                    izinAkses = false;
                    break;
                case 1:
                    try {
                        db.clearConsole();
                        setSaluranAir();
                        db.clearConsole();
                    }catch (SQLException e) {
                        System.out.println("Database error: " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        db.clearConsole();
                        showUtilitasAir();
                        db.clearConsole();
                    }catch (SQLException e) {
                        System.out.println("Database error: " + e.getMessage());
                    }
                    break;
                case 3:
                    db.clearConsole();
                    showLogs();
                    db.clearConsole();
                    break;
                default:
                    break;
            }
        }
    }

    public void setSaluranAir() throws SQLException{
        //lihat status aktuator air ssekarang
        System.out.println("====HALAMAN PEMILIK====");
        System.out.println("====ON/OFF SRUSUN====\n");
        String query = String.format("SELECT TOP 1 ID_Aksi, Aksi FROM LOG_IOT WHERE NomorUnitSarusun = '%s' ORDER BY WaktuAksi DESC", noKamar);
        int aksiSekarang;
        ResultSet rs = db.executeQuery(query);
        rs.next();

        aksiSekarang = rs.getInt(2);
        aksiSekarang = (aksiSekarang == 0)? 1:0;

        LocalDateTime sekarang = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String waktuFormatted = sekarang.format(formatter);
    
        String sql = String.format("INSERT INTO LOG_IOT VALUES (%d, '%s', '%s')", aksiSekarang, noKamar, waktuFormatted);
        db.executeUpdate(sql);

        System.out.println("Buka/Tutup Saluran Air Berhasil!");
        System.out.println("\nKetik x Untuk Keluar");
        sc.next();
    }

    public void showUtilitasAir() throws SQLException {
        while (true) {
            System.out.println("====HALAMAN PEMILIK====");
            System.out.println("====UTILITAS AIR====\n");
            System.out.println("Pilih Utilitas Air");
            System.out.printf("0. Back\n1. Harian\n2. Bulanan\n3. Tahunan\n\nIsi Perintah: ");
            int perintah = sc.nextInt();
            System.out.println();
            String query;
            String time = "",
            word = "";
    
            switch (perintah) {
                case 0:
                    return;
                case 1:
                    time = "UtilitasAirHarian";
                    word = "Utilitas Air Harian";
                    break;
                case 2:
                    time = "UtilitasAirBulanan";
                    word = "Utilitas Air Bulanan";
                    break;
                case 3:
                    time = "UtilitasAirTahunan";
                    word = "Utilitas Air Tahunan";
                    break;
                default:
                    break;
            }
    
            query = String.format("SELECT UtilitasAirPerDetik, %s FROM Unit WHERE NIK_Pengguna = '%s'",time, p.NIK);
            ResultSet rs = db.executeQuery(query);
    
            rs.next();
            System.out.printf("Utilitas Air Per Detik: %s liter per detik\n%s: %s liter", rs.getString(1), word, rs.getString(2));
            System.out.println("\nKetik x Untuk Keluar");
            sc.next();
            db.clearConsole();
        }
    }

    public void showLogs() {
        System.out.println("====HALAMAN PEMILIK====");
        System.out.println("====HISTORI====\n");
        String query = String.format("SELECT ID_Aksi, Aksi, " +
            "CASE Aksi " +
                "WHEN 0 THEN 'TUTUP Saluran Air' " +
                "WHEN 1 THEN 'BUKA Saluran Air' " +
            "END AS Deskripsi_Aksi " + 
            "FROM LOG_IOT "+
            "WHERE NomorUnitSarusun = '%s' "  +
            "ORDER BY ID_Aksi DESC", noKamar);
        try {
            ResultSet rs = db.executeQuery(query);
            DBAccess.printTable(rs);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        System.out.println("\nKetik x Untuk Keluar");
        sc.next();
    }
}
