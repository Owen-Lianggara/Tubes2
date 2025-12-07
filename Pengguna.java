import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

public abstract class Pengguna {
    Scanner sc = new Scanner(System.in);
    // 0: Pemilik
    // 1: Administrator
    // 2: Pengelola
    protected String NIK, alamatDomisili, noTlp;
    protected int TipeUser;
    private DBAccess db = DBAccess.getInstance();

    public Pengguna(String NIK, String noTLP, String alamatDomisili, int TipeUser) {
        this.NIK = NIK;
        this.noTlp = noTLP;
        this.alamatDomisili = alamatDomisili;
        this.TipeUser = TipeUser;
    }

    public String getNIK() {
        return this.NIK;
    }

    public void getProfile() {
        DBAccess.Bold("====PROFIL====\n");
        System.out.printf("NIK\t\t\t: %s\nNomor Telepon\t\t: %s\nAlamat Domisili\t\t: %s\nTipe User\t\t: %s",
                this.NIK, this.noTlp, this.alamatDomisili, "Pemilik\n\n");
        
        System.out.println("Tekan x Untuk Keluar");
        sc.next();
    }

    public void pemilikPageAccess(Pengguna pemilik) {
        // page access
        while (true) {
            sRusunPage sRusun = new sRusunPage(pemilik);

            DBAccess.Bold("====HALAMAN PEMILIK====");
            System.out.println("""

            Pilih Menu:
            0. Back
            1. Cek Profil
            2. Halaman Perangkat IOT
            """);

            System.out.print("Isi Perintah: ");
            int input = sc.nextInt();

            switch (input) {
                case 1: // cek Profil
                    db.clearConsole();
                    DBAccess.Bold("====HALAMAN PEMILIK====");
                    pemilik.getProfile();
                    db.clearConsole();
                    break;
                case 2: // Halaman Perangkat IOT
                    db.clearConsole();
                    DBAccess.Bold("====HALAMAN PEMILIK====");
                    sRusunPage srusun = new sRusunPage(pemilik); // pindah ke halaman sRusun
                    srusun.doAction();
                    db.clearConsole();
                    break;
                case 0:
                    db.clearConsole();
                    return;
                default:
                    db.clearConsole();
                    DBAccess.Bold("=====INPUT SALAH====");
                    break;
            }
        }
    }

    public abstract int getTipeUser();
}