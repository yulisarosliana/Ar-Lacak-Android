package org.inkubator.radinaldn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ListActivity extends Activity {
    static String AR_ID = "id";
    static String in_title = "title";
    static String in_address = "streetAddress";
    static String in_number = "number";
    static String in_image = "image";
    static String in_lat = "lat";
    static String in_lng = "lng";
    static String in_jarak = "jarak";
    static String miles = "hh";
    private double Clat, Clon;
    private ProgressDialog pDialog;

    JSONArray str_json = null;
    Koneksi lo_Koneksi = new Koneksi();
    String isi = lo_Koneksi.isi_koneksi();

    ListView list;
    LazyAdapter adapter;
    ArrayList<HashMap<String, String>> data_map = new ArrayList<HashMap<String, String>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlist);

        new getListInfo().execute();

    }


    class getListInfo extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ListActivity.this);
            pDialog.setMessage("Menghubungkan ke server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            Bundle extras = getIntent().getExtras();
//			String kat = extras.getString("kategori");
            Clat = getIntent().getExtras().getDouble("Clat");
            Clon = getIntent().getExtras().getDouble("Clon");


            String link_url = isi + "konten.php";
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.AmbilJson(link_url);


            try {
                str_json = json.getJSONArray("result");

                for (int i = 0; i < str_json.length(); i++) {
                    JSONObject ar = str_json.getJSONObject(i);

                    String id = ar.getString("id");
                    String title = ar.getString("title");
                    String image = ar.getString("image");
                    String address = ar.getString("streetAddress");
                    String number = ar.getString("number");
                    String lat = ar.getString("lat");
                    String lng = ar.getString("lng");
                    Double lat1 = ar.getDouble("lat");
                    Double lng1 = ar.getDouble("lng");


                    double R = 6371; // earth's radius (mean radius = 6,371km)
                    double dLat = Math.toRadians(Clat - lat1);

                    double dLon = Math.toRadians(Clon - lng1);
                    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                            Math.cos(Math.toRadians(Clat)) * Math.cos(Math.toRadians(lat1)) *
                                    Math.sin(dLon / 2) * Math.sin(dLon / 2);
                    double c1 = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

                    double d = R * c1;

                    double gg = d * 1000;


                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(AR_ID, id);
                    map.put(in_title, title);
                    map.put(in_image, image);
                    map.put(in_address, address);
                    map.put(in_number, number);
                    map.put(in_lat, lat);
                    map.put(in_lng, lng);

                    if (d < 1) {
                        map.put(in_jarak, String.format("%.2f", gg));
                        map.put(miles, " M");
                    } else {
                        map.put(in_jarak, String.format("%.2f", d));
                        map.put(miles, " KM");
                    }

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
                public void run() {

                    for (int i = 0; i < data_map.size(); i++) {

                        list = (ListView) findViewById(R.id.listView1);
                        adapter = new LazyAdapter(ListActivity.this, data_map);
                        list.setAdapter(adapter);
                    }
                    // Click event for single list row
                    list.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            String kode = ((TextView) view.findViewById(R.id.kode)).getText().toString();
                            String number = ((TextView) view.findViewById(R.id.number)).getText().toString();
                            String lat = ((TextView) view.findViewById(R.id.lat)).getText().toString();
                            String lng = ((TextView) view.findViewById(R.id.lng)).getText().toString();

                            Intent in = new Intent(ListActivity.this, Detail.class);
                            in.putExtra(AR_ID, kode);
                            in.putExtra(in_number, number);
                            in.putExtra(in_lat, lat);
                            in.putExtra(in_lng, lng);
                            startActivity(in);

                        }
                    });
                }
            });
        }


        public class DriverComparator implements Comparator<HashMap<String, String>> {

            public int compare(HashMap<String, String> o1,
                               HashMap<String, String> o2) {
                double distance1 = -1;
                double distance2 = -1;
                try {
                    distance1 = Double.parseDouble(o1.get(in_jarak));
                } catch (NumberFormatException ex) {

                } catch (NullPointerException nex) {

                }
                try {
                    distance2 = Double.parseDouble(o1.get(in_jarak));
                } catch (NumberFormatException ex) {

                } catch (NullPointerException nex) {

                }
                if (distance1 == distance2) {
                    return 0;
                }
                if (distance1 > distance2) {
                    return 1;
                }
                return -1;
            }
        }

        public LayoutInflater getSystemService(String layoutInflaterService) {
            // TODO Auto-generated method stub
            return null;
        }

        public Context getApplicationContext() {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
