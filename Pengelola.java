import java.sql.SQLException;

public class Pengelola extends Pengguna {
    private DBAccess db = DBAccess.getInstance();

    public Pengelola(String NIK, String noTLP, String alamatDomisili, int TipeUser) {
        super(NIK, noTLP, alamatDomisili, TipeUser);
    }

    // public monitoring(int idx) { //index kamar sarusun

    // }

    @Override
    public void getProfile() {
        System.out.printf("NIK\t\t\t: %s\nNomor Telepon\t\t: %s\nAlamat Domisili\t\t: %s\nTipe User\t\t: %s",
                this.NIK, this.noTlp, this.alamatDomisili, "Pengelola\n");
    }

    @Override
    public int getTipeUser() {
        return 2;
    }

    public void MonitoringPageAccess(Pengelola pengelola) {
        while (true) {
            MonitoringPage monitoring = new MonitoringPage(pengelola);
            DBAccess.Bold("====HALAMAN PENGELOLA====\n");
            System.out.println("Pilih Menu: ");
            System.out.println("0. Back");
            System.out.println("1. Halaman Monitoring Pengguna");
            System.out.println("2. Toggle IOT Pengguna");

            System.out.print("Isi Perintah: ");
            int input = sc.nextInt();
            System.out.println();


            switch (input) {
                case 0:
                    return;
                case 1: // cek Profil
                    db.clearConsole();
                    monitoring.doAction();
                    db.clearConsole();
                    break;
                case 2:
                    try {
                        db.clearConsole();
                        monitoring.toggleIOT();
                        db.clearConsole();
                    }catch (SQLException e) {
                        System.out.println("Database error: " + e.getMessage());
                    }
                    break;
                default:
                    DBAccess.Bold("=====INPUT SALAH====");
                    db.clearConsole();
                    break;
            }
        }
    }
}