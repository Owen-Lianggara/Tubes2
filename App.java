//pakai cmd untuk run
//javac -cp "lib\mssql-jdbc-12.10.0.jre11.jar" *.java && java -cp ".;lib\mssql-jdbc-12.10.0.jre11.jar" App
//janlup protitype kumpulkan

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class App {
    static Scanner sc = new Scanner(System.in);
    /*
     * 0 Pemilik, 1 Administrator, 2 Pengelola
     * Input
     * 
     * 081234567890 // Pemilik
     * 082345678901 // admin
     * 083456789012 // pengelola
     * 
     * Pengguna
     * 
     * 
     * Pemilik command
     * 1 : Cek Profil
     * 2 : Kendali Perangkat IOT milik Sendiri
     * 
     * Pengelola command
     * 1. Monitor pemakaian utilitas air
     * 2. Kendali Perangkat IOT berdasarkan NIK/Nomor Ponsel Pengguna
     * 
     * Admin command
     * ddl/dml master(sRusunDB)
     * 1. execute queri untuk ngubah data langsung ke DB
     * 2. Mengelola Pemilik
     * 3. Kendali Perangkat IOT berdasarkan NIK/Nomor Ponsel Pengguna
     * 
     * 
     * 
     */

    private static DBAccess db;

    public static void runPemilik(Pengguna pemilik) {
        while (true) {
            DBAccess.Bold("====HALO PEMILIK====");

            System.out.println("""
                
            0. Exit
            1. Pemilik
            """);
            System.out.print("Isi Perintah: ");
            int input = sc.nextInt();
            System.out.println();

            switch (input) {
                case 0:
                    LogOut(pemilik.getNIK());
                    db.clearConsole();
                    DBAccess.Bold("====APP CLOSE====");
                    return;
                case 1:
                    db.clearConsole();
                    pemilik.pemilikPageAccess(pemilik);
                    break;
                default:
                    db.clearConsole();
                    DBAccess.Bold("====INPUT SALAH====");
                    break;
            }
        }
    }

    public static void runAdmin(Pengguna pemilik) {
        Admin admin = (Admin) pemilik;

        while (true) {
            DBAccess.Bold("====HALO ADMIN====");
            System.out.println("""
            0. Exit
            1. Pemilik
            2. Administrator
            """);
            System.out.print("Isi Perintah: ");
            int input = sc.nextInt();
            System.out.println();

            switch (input) {
                case 0:
                    LogOut(pemilik.getNIK());
                    db.clearConsole();
                    DBAccess.Bold("====APP CLOSE====");
                    return;
                case 1:
                    db.clearConsole();
                    pemilik.pemilikPageAccess(pemilik);
                    break;
                case 2:
                    db.clearConsole();
                    admin.adminPageAccess(admin);
                    break;
                default:
                    db.clearConsole();
                    DBAccess.Bold("=====INPUT SALAH====");
                    break;
            }
        }
    }

    public static void runPengelola(Pengguna pemilik) {
        Pengelola pengelola = (Pengelola) pemilik;

        while (true) {
            db.clearConsole();
            System.out.println("====HALO PENGELOLA====");
            System.out.println("""
            
            0. Exit
            1. Pemilik
            2. Pengelola
            """);
            System.out.print("Isi Perintah: ");
            int input = sc.nextInt();
            
            switch (input) {
                case 0:
                    LogOut(pemilik.getNIK());
                    db.clearConsole();
                    DBAccess.Bold("====APP CLOSE====");
                    return;
                case 1:
                    db.clearConsole();
                    pemilik.pemilikPageAccess(pemilik);
                    break;
                case 2:
                    db.clearConsole();
                    pengelola.MonitoringPageAccess(pengelola);
                    break;
                default:
                    db.clearConsole();
                    DBAccess.Bold("=====INPUT SALAH====");
                    break;
            }
        }
    }

    public static void LogOut(String NIK) {
    try {
        LocalDateTime sekarang = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String waktuFormatted = sekarang.format(formatter);

        // Query INSERT ke tabel Logs
        String query = 
        String.format("""
            INSERT INTO 
                Logs (Aksi, Waktu, NIK_Pengguna) VALUES (0, '%s', '%s')
        """, waktuFormatted, NIK);

        db.executeUpdate(query); // Asumsikan db adalah instance DBAccess
        } catch (Exception e) {
            System.out.println("Error di 134 App: " + e.getMessage());
        }
    }

    // running page
    public static void main(String[] args) {
        db.clearConsole();
        // user login
        LoginPage login = new LoginPage();
        db = DBAccess.getInstance();
        login.doAction();
        
        // dapatkan akun setelah login
        Pengguna user = login.getPengguna();

        /*
         * 0 Pemilik, 1 Administrator, 2 Pengelola
         */
        if (user.getTipeUser() == 1) {
            // jalankan admin
            runAdmin(user);
        } else if (user.getTipeUser() == 2) {
            // jalankan pengelola
            runPengelola(user);
        } else {
            // jalankan pemilik
            runPemilik(user);
        }
    }
}