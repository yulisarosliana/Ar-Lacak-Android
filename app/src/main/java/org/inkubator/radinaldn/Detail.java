package org.inkubator.radinaldn;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Detail extends Activity {

	ArrayList<HashMap<String, String>> dataMap = new ArrayList<HashMap<String, String>>();
	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();


	private static final String AR_ID = "id";
	Button btnCall,btnHere;

	JSONArray artikel = null;
	JSONArray str_json;
	Koneksi lo_Koneksi = new Koneksi();
	String isi = lo_Koneksi.isi_koneksi();

	TextView id,title,kategori, alamat,telepon,deskripsi;
	ImageView gambar_set;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_list_item);

		id = (TextView) findViewById(R.id.judul);
		title = (TextView) findViewById(R.id.title);


		alamat = (TextView) findViewById(R.id.alamat);
		telepon = (TextView) findViewById(R.id.telepon);
		deskripsi = (TextView) findViewById(R.id.deskripsi);
		gambar_set = (ImageView) findViewById(R.id.image);

		new getListInfo().execute();
	}

	class getListInfo extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Detail.this);
			pDialog.setMessage("Menghubungkan ke server...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {

			final Bundle b = getIntent().getExtras();
			String kode = b.getString(AR_ID);

			String link_url = isi+"detail-konten.php?kode="+kode;

			JSONObject json = jParser.AmbilJson(link_url);

			try {
				str_json = json.getJSONArray("result");

				for(int i = 0; i < str_json.length(); i++)
				{
					JSONObject ar = str_json.getJSONObject(i);
					HashMap<String, String> map = new HashMap<String, String>();

					map.put("id", ar.getString("id"));
					map.put("title", ar.getString("title"));
					map.put("number", ar.getString("number"));
					//map.put("kategori", ar.getString("kategori"));
					map.put("streetAddress", ar.getString("streetAddress"));
					map.put("deskripsi",  ar.getString("deskripsi"));
					map.put("image",  ar.getString("image"));

					dataMap.add(map);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			btnCall = (Button) findViewById(R.id.button1);
			btnCall.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					try {
						for(int i = 0; i < str_json.length(); i++)
						{
							JSONObject ar = str_json.getJSONObject(i);
							// TODO Auto-generated method stub
							Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ar.getString("number")));
							startActivity(intent);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			});

			btnHere = (Button) findViewById(R.id.button2);
			btnHere.setOnClickListener(new View.OnClickListener() {


				@Override
				public void onClick(View v) {

					try {
						for(int i = 0; i < str_json.length(); i++)
						{
							JSONObject ar = str_json.getJSONObject(i);
							// TODO Auto-generated method stub
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr="+ar.getString("lat")+","+ar.getString("lng")+"&z=17"));
							startActivity(intent);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			});
			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {

					for (int i = 0; i < dataMap.size(); i++)
					{
						HashMap<String, String> map = new HashMap<String, String>();
						map = dataMap.get(i);
						id.setText(map.get("id"));
						title.setText(map.get("title"));
						//kategori.setText(map.get("kategori"));
						alamat.setText(map.get("streetAddress"));
						telepon.setText(map.get("number"));
						title.setText(map.get("title"));
						deskripsi.setText(map.get("deskripsi"));

						new DownloadImagesTask().execute(map.get("image"));
					}


				}
			});
		}

	}
	public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... urls) {
			return download_Image(urls[0]);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			gambar_set.setImageBitmap(result);
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
