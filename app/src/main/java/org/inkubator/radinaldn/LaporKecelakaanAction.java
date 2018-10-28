package org.inkubator.radinaldn;

/**
 * Created by Rian on 09/10/2017.
 */
public class LaporKecelakaanAction extends Koneksi2 {


    Koneksi lo_Koneksi = new Koneksi();
    String isi = lo_Koneksi.isi_koneksi();
    String URL = isi+"server.php";
    String url = "";
    String reponse = "";

    public String insertKecelakaan(String id_user, String lokasi, String foto, String waktu_pelaporan, String detail, String lat, String lng, String deskripsi, String id_kategori){

        lokasi = lokasi.replace(" ","%20");
        detail = detail.replace(" ","%20");
        deskripsi = deskripsi.replace(" ","%20");
        waktu_pelaporan = waktu_pelaporan.replace(" ","%20");

        try {
            url = URL + "?operasi=insert&id_user=" +id_user+ "&lokasi=" +lokasi+ "&foto=" +foto+ "&waktu_pelaporan=" +waktu_pelaporan+ "&detail=" +detail+ "&lat=" +lat+ "&lng=" +lng+ "&deskripsi=" +deskripsi+ "&id_kategori=" +id_kategori;
            System.out.println("URL insert kecelakaan : "+ url);
            reponse = call(url);
        } catch (Exception e){

        }
        return reponse;
    }

    public String insertUser(String username, String password, String nik, String nama_lengkap, String tempat_lahir, String tanggal_lahir, String jenis_kelamin, String alamat, String pekerjaan, String email, String agama, String nohp){

        username = username.replace(" ","%20");
        password = password.replace(" ","%20");
        nik = nik.replace(" ","%20");
        nama_lengkap = nama_lengkap.replace(" ","%20");
        tempat_lahir = tempat_lahir.replace(" ","%20");
        tanggal_lahir = tanggal_lahir.replace(" ","%20");
        jenis_kelamin = jenis_kelamin.replace(" ","%20");
        alamat = alamat.replace(" ","%20");
        pekerjaan = pekerjaan.replace(" ","%20");
        email = email.replace(" ","%20");
        agama = agama.replace(" ","%20");
        nohp = nohp.replace(" ","%20");

        try {
            url = URL + "?operasi=insertUser&username=" +username+ "&password=" +password+ "&nik=" +nik+ "&nama_lengkap=" +nama_lengkap+ "&tempat_lahir=" +tempat_lahir+ "&tanggal_lahir=" +tanggal_lahir+ "&jenis_kelamin=" +jenis_kelamin+ "&alamat=" +alamat+ "&pekerjaan=" +pekerjaan+ "&email=" +email+ "&agama=" +agama+ "&no_hp=" +nohp;
            System.out.println("URL insert user : "+ url);
            reponse = call(url);
        } catch (Exception e){

        }
        return reponse;
    }
}
