public class Admin extends Pengguna {
    private DBAccess db = DBAccess.getInstance();

    public Admin(String NIK, String noTLP, String alamatDomisili, int TipeUser) {
        super(NIK, noTLP, alamatDomisili, TipeUser);
    }

    @Override
    public void getProfile() {
        System.out.printf("NIK\t\t\t: %s\nNomor Telepon\t\t: %s\nAlamat Domisili\t\t: %s\nTipe User\t\t: %s",
                this.NIK, this.noTlp, this.alamatDomisili, "Administrator\n");
        System.out.println("Tekan x Untuk Keluar");
        sc.next();
    }

    @Override
    public int getTipeUser() {
        return 1;
    }

    public void adminPageAccess(Admin admin) {
        while (true) {
            System.out.println("====HALAMAN ADMIN====\n");
            KelolaRusunPage kelola = new KelolaRusunPage(admin);

            System.out.println("""
            Pilih Menu: 
            0. Back
            1. Halaman Pengelolaan sRusun
            2. Monitoring Log In/Out
            """);

            System.out.print("Isi Perintah: ");
            int input = sc.nextInt();
            System.out.println();

            switch (input) {
                case 0 :
                    return;
                case 1:
                    db.clearConsole();
                    kelola.doAction();
                    db.clearConsole();
                    break;
                case 2:
                    db.clearConsole();
                    kelola.monitorLogin();
                    db.clearConsole();
                    break;
                default:
                    db.clearConsole();
                    System.out.println("=====INPUT SALAH====");
                    break;
            }
        }
    }
}