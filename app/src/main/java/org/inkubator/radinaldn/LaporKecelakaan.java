package org.inkubator.radinaldn;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.HashMap;
import java.util.Random;


/**
 * Created by Rian on 06/10/2017.
 */
public class LaporKecelakaan extends Activity {

    static String in_id = "id_kategori";
    static String in_ket = "keterangan";

    JSONArray str_json = null;

    LazyAdapter adapter;
    ArrayList<HashMap<String, String>> data_map = new ArrayList<HashMap<String, String>>();

    String id_user, username, email;
    SharedPreferences sharedpreferences;

    public static final String TAG_ID = "id_user";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_EMAIL = "email";

    // end of session

    public static final String JUMLAH_RB = "jumlah_rb";


    LocationManager lm;
    LocationListener locationListener;

    // object class LaporKecelakaanAction
    LaporKecelakaanAction laporKecelakaan = new LaporKecelakaanAction();

    // Variable
    LinearLayout mLinearLayout;
    EditText alamat, foto, detail, deskripsi;
    Button submit, clear;
    ImageButton btnCamera;
    TextView tvlat, tvlng, judulRadioButton;
    RadioButton laka, kriminal;

    String lat, lng;

    int jumlahRadioButton;
    RadioButton[] rb;

    String idKat[];
    String keteranganKat[];

    // initiate capt cam
    private ImageView ivImage;
    private ConnectionDetector cd;
    private Boolean upflag = false;
    private Uri selectedImage = null;
    private Bitmap bitmap, bitmapRotate;
    private ProgressDialog pDialog;
    String imagepath = "";
    String fname;
    String etfoto;
    File file;


    // class for get current location
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                lat = String.valueOf(loc.getLatitude());
                tvlat.setText(lat);

                lng = String.valueOf(loc.getLongitude());
                tvlng.setText(lng);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            String statusString = "";
            switch (status) {
                case LocationProvider.AVAILABLE:
                    statusString = "available";
                case LocationProvider.OUT_OF_SERVICE:
                    statusString = "out of service";
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    statusString = "temporarily unavailable";
            }

            Toast.makeText(getBaseContext(),
                    provider + " " + statusString,
                    Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(),
                    "Provider: " + provider + " enabled",
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getBaseContext(),
                    "Provider: " + provider + " disabled",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //-use the LocationManager class to obtain location data
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        //        Object initialization
        cd = new ConnectionDetector(LaporKecelakaan.this);

        cd = new ConnectionDetector(getApplicationContext());

        // aktifkan untuk kategori dinamis
        new getKategoriInfo().execute();

        showLayout();


    }

    @Override
    protected void onResume() {
        super.onResume();

        //-request for location update using GPS


        lm.requestLocationUpdates(
                // Ambil lokasi dari BTS
                //LocationManager.NETWORK_PROVIDER,

                // Ambil lokasi dari Satelit
                LocationManager.GPS_PROVIDER,
                0,
                0,
                locationListener);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        // session
        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);

        id_user = getIntent().getStringExtra(TAG_ID);
        username = getIntent().getStringExtra(TAG_USERNAME);
        email = getIntent().getStringExtra(TAG_EMAIL);

        Intent intent = new Intent(LaporKecelakaan.this, MainActivity.class);
        intent.putExtra(TAG_ID, id_user);
        intent.putExtra(TAG_USERNAME, username);
        intent.putExtra(TAG_EMAIL, email);
        finish();
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }

        //--remove the location listener--
        lm.removeUpdates(locationListener);
    }

    public void showLayout() {
        setContentView(R.layout.layout);


        // tambahan agar bisa insert data ke server
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // end of tambahan

        alamat = (EditText) findViewById(R.id.etalamat);
        detail = (EditText) findViewById(R.id.etdetail);
        deskripsi = (EditText) findViewById(R.id.etdeskripsi);

        // tes resize edit text
        deskripsi.setHeight(150);

        mLinearLayout = (LinearLayout) findViewById(R.id.linear1);

        // bikin radio button dinamis
        jumlahRadioButton = Integer.parseInt(getIntent().getStringExtra(JUMLAH_RB));

        rb = new RadioButton[jumlahRadioButton];
        idKat = new String[jumlahRadioButton];
        keteranganKat = new String[jumlahRadioButton];

        RadioGroup rg = new RadioGroup(this);
        rg.setOrientation(RadioGroup.VERTICAL);
        for (int i = 0; i < jumlahRadioButton; i++) {
            rb[i] = new RadioButton(this);
            rg.addView(rb[i]);

        }

        rb[0].setChecked(true);

        mLinearLayout.addView(rg);

        submit = (Button) findViewById(R.id.btsubmit);
        clear = (Button) findViewById(R.id.btclear);

        tvlat = (TextView) findViewById(R.id.tvlat);
        tvlng = (TextView) findViewById(R.id.tvlng);

        ivImage = (ImageView) findViewById(R.id.ivImage);

        btnCamera = (ImageButton) findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCamera();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tambahKecelaakaan();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ivImage.setImageResource(android.R.color.transparent);
                ivImage.setVisibility(v.GONE);
                fname = null;
                System.out.println("gambar dihilangkan");
                detail.setText("");
                System.out.println("detail dihilangkan");
                alamat.setText("");
                System.out.println("alamat dihilangkan");
                deskripsi.setText("");
                System.out.println("deskripsi dihilangkan");
            }
        });

    }

    private void clickCamera() {

        Intent cameraintent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraintent, 101);

    }


    public void tambahKecelaakaan() {

        // get datetime
        Date currentTime = Calendar.getInstance().getTime();
        // convert to dateTime format
        SimpleDateFormat date24Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // session
        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);

        id_user = getIntent().getStringExtra(TAG_ID);
        username = getIntent().getStringExtra(TAG_USERNAME);
        email = getIntent().getStringExtra(TAG_EMAIL);

        String etalamat = alamat.getText().toString();
        //String etfoto = foto.getText().toString();
        etfoto = fname;
        String etdetail = detail.getText().toString();
        String etdeskripsi = deskripsi.getText().toString();

        String waktupelaporan = date24Format.format(currentTime);
        String id_kategori = null;

        for (int i = 0; i < jumlahRadioButton; i++) {
            if (rb[i].isChecked()) {
                id_kategori = idKat[i];
            } else {
                System.out.println("Kategori belum dipilih");
            }
        }

        System.out.println("ID User : " + id_user + " Alamat : " + etalamat + " Foto : " + etfoto + " Tanggal Waktu : " + waktupelaporan + " Detail : " + etdetail + " Lat : " + lat + " Lng : " + lng + " Deskripsi : " + etdeskripsi + " Id Kategori : " + id_kategori);

        // pengecekkan form tidak boleh kosong
        if ((etalamat).equals("") || (etfoto) == null || (etdetail).equals("")) {

            Toast.makeText(LaporKecelakaan.this, "Data laporan tidak boleh kosong", Toast.LENGTH_SHORT).show();
        } else if ((lat) == null || (lng) == null) {
            Toast.makeText(LaporKecelakaan.this, "Mohon menunggu, AR Lacak sedang membaca lokasi anda. ", Toast.LENGTH_SHORT).show();
            Toast.makeText(LaporKecelakaan.this, "Pastikan koneksi internet dan GPS anda sudah diaktfikan. ", Toast.LENGTH_SHORT).show();
        } else {

            // upload gambar dilakukan saat tombol kirim ditekan
            if (cd.isConnectingToInternet()) {
                if (!upflag) {
                    Toast.makeText(getApplicationContext(), "Anda belum mengambil gambar..!", Toast.LENGTH_LONG).show();
                } else {
                    // lakukan upload gambar
                    saveFile(bitmapRotate, file);

                    // lakukan insert data kecelakaan
                    String laporan = laporKecelakaan.insertKecelakaan(id_user, etalamat, etfoto, waktupelaporan, etdetail, lat, lng, etdeskripsi, id_kategori);
                    Toast.makeText(LaporKecelakaan.this, laporan, Toast.LENGTH_SHORT).show();

                    // selesaikan activity
                    finish();
                    startActivity(getIntent());
                }
            } else {
                Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet !", Toast.LENGTH_LONG).show();
            }


        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            switch (requestCode) {
                case 101:
                    if (resultCode == Activity.RESULT_OK) {
                        if (data != null) {
                            selectedImage = data.getData(); // the uri of the image taken
                            if (String.valueOf((Bitmap) data.getExtras().get("data")).equals("null")) {
                                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                            } else {
                                bitmap = (Bitmap) data.getExtras().get("data");
                            }
                            if (Float.valueOf(getImageOrientation()) >= 0) {
                                bitmapRotate = rotateImage(bitmap, Float.valueOf(getImageOrientation()));
                            } else {
                                bitmapRotate = bitmap;
                                bitmap.recycle();
                            }

                            ivImage.setVisibility(View.VISIBLE);
                            ivImage.setImageBitmap(bitmapRotate);

//                            Saving image to mobile internal memory for sometime
                            String root = getApplicationContext().getFilesDir().toString();
                            File myDir = new File(root + "/androidlift");
                            myDir.mkdirs();

                            Random generator = new Random();
                            int n = 10000;
                            n = generator.nextInt(n);

//                            Give the file name that u want
                            fname = "laka" + n + ".png";

                            imagepath = root + "/androidlift/" + fname;
                            file = new File(myDir, fname);
                            upflag = true;
                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    //    In some mobiles image will get rotate so to correting that this code will help us
    private int getImageOrientation() {
        final String[] imageColumns = {MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageColumns, null, null, imageOrderBy);

        if (cursor.moveToFirst()) {
            int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
            System.out.println("orientation===" + orientation);
            cursor.close();
            return orientation;
        } else {
            return 0;
        }
    }

    //    Saving file to the mobile internal memory
    private void saveFile(Bitmap sourceUri, File destination) {
        if (destination.exists()) destination.delete();
        try {
            FileOutputStream out = new FileOutputStream(destination);
            sourceUri.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            if (cd.isConnectingToInternet()) {
                new DoFileUpload().execute();
            } else {
                Toast.makeText(LaporKecelakaan.this, "Tidak ada koneksi internet..", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DoFileUpload extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(LaporKecelakaan.this);
            pDialog.setMessage("mohon menunggu, sedang mengupload gambar..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            Koneksi lo_Koneksi = new Koneksi();
            String isi = lo_Koneksi.isi_koneksi();

            try {
                // Set your file path here
                FileInputStream fstrm = new FileInputStream(imagepath);
                // Set your server page url (and the file title/description)
                HttpFileUpload hfu = new HttpFileUpload(isi + "file_upload.php", "ftitle", "fdescription", fname);
                upflag = hfu.Send_Now(fstrm);
            } catch (FileNotFoundException e) {
                // Error: File not found
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (upflag) {
                Toast.makeText(getApplicationContext(), "Upload gambar berhasil", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Sayangnya gambar tidak bisa diupload..", Toast.LENGTH_LONG).show();
            }
        }
    }

    class getKategoriInfo extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LaporKecelakaan.this);
            pDialog.setMessage("Menghubungkan ke server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Koneksi lo_Koneksi = new Koneksi();
            String isi = lo_Koneksi.isi_koneksi();

            String link_url = isi + "get-kategori.php";
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.AmbilJson(link_url);

            try {
                str_json = json.getJSONArray("result");


                for (int i = 0; i < str_json.length(); i++) {
                    JSONObject kat = str_json.getJSONObject(i);

                    String id_kategori = kat.getString("id_kategori");
                    String nama = kat.getString("nama");
                    String keterangan = kat.getString("keterangan");

                    idKat[i] = id_kategori;
                    keteranganKat[i] = keterangan;

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(in_id, id_kategori);
                    map.put(in_ket, keterangan);


                    data_map.add(map);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    for (int i = 0; i < data_map.size(); i++) {
                        System.out.println("Data map size : " + data_map.size());
                        rb[i].setText("Laporan " + keteranganKat[i]);
                    }
                }
            });

        }
    }
}