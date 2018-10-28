package org.inkubator.radinaldn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rian on 17/10/2017.
 */
public class News extends Activity {

    private String TAG = News.class.getSimpleName();
    private ListView lv;

    String id, judul, konten, gambar, tanggal;
    private ProgressDialog pDialog;

    ArrayList<HashMap<String, String>> contactList;


    // Variable
    TextView tvtitle, tvcontent, tvtanggal;
    ImageView ivimage;
//
//    JSONArray str_json = null;
    Koneksi lo_Koneksi = new Koneksi();
    String isi = lo_Koneksi.isi_koneksi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);

        tvtitle = (TextView) findViewById(R.id.tvtitle);
        tvcontent = (TextView) findViewById(R.id.tvcontent);
        ivimage = (ImageView) findViewById(R.id.ivimage);
        tvtanggal = (TextView) findViewById(R.id.tvtanggal);

        contactList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);



        new getBerita().execute();
    }

    class getBerita extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getBaseContext(),"Menghubungkan ke server....",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting respone
            String url = isi+"detail-berita.php";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("result");


                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        id = c.getString("id");
                        judul = c.getString("judul");
                        tanggal = c.getString("tanggal");
                        konten = c.getString("isi");
                        gambar = c.getString("gambar");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("judul", judul);
                        contact.put("tanggal", tanggal);
                        contact.put("isi", konten);
                        contact.put("gambar", gambar);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            tvtitle.setText(judul);
            tvtanggal.setText(tanggal);
            tvcontent.setText(konten);
            new DownloadImagesTask().execute(gambar);
        }
    }

    public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            return download_Image(urls[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            ivimage.setImageBitmap(result);
        }


        private Bitmap download_Image(String url) {
            Bitmap bm = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
            }
            return bm;
        }
    }
}
