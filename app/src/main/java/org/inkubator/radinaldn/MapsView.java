package org.inkubator.radinaldn;


import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsView extends Activity implements OnMapLongClickListener, OnInfoWindowClickListener {

	static final LatLng AWAL = new LatLng(0.5070677, 101.4477793);
	ArrayList<HashMap<String, String>> dataMap = new ArrayList<HashMap<String, String>>();
	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	Koneksi lo_Koneksi = new Koneksi();
	String isi = lo_Koneksi.isi_koneksi();

	JSONArray str_json = null;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"MapsView Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://org.inkubator.radinaldn/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"MapsView Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://org.inkubator.radinaldn/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}

	class MyInfoWindowAdapter implements InfoWindowAdapter {

		private final View myContentsView;

		MyInfoWindowAdapter() {
			myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
		}

		@Override
		public View getInfoContents(Marker marker) {
			TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.title));
			tvTitle.setText(marker.getTitle());
			TextView tvSnippet = ((TextView) myContentsView.findViewById(R.id.snippet));
			tvSnippet.setText(marker.getSnippet());

			return myContentsView;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			return null;
		}

	}

	class getListInfo extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MapsView.this);
			pDialog.setMessage("Menghubungkan ke server...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {

			Bundle extras = getIntent().getExtras();
			//String kat = extras.getString("kategori");

			String link_url = isi + "konten.php";
			JSONObject json = jParser.AmbilJson(link_url);

			try {
				str_json = json.getJSONArray("result");

				for (int i = 0; i < str_json.length(); i++) {
					JSONObject ar = str_json.getJSONObject(i);
					HashMap<String, String> map = new HashMap<String, String>();

					map.put("id", ar.getString("id"));
					map.put("lat", ar.getString("lat"));
					map.put("lng", ar.getString("lng"));
					map.put("altitude", ar.getString("altitude"));
					map.put("title", ar.getString("title"));
					map.put("number", ar.getString("number"));
					map.put("streetAddress", ar.getString("streetAddress"));
					map.put("deskripsi", ar.getString("deskripsi"));
					dataMap.add(map);
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

					for (int i = 0; i < dataMap.size(); i++) {
						HashMap<String, String> map;
						map = dataMap.get(i);
						LatLng POSISI = new LatLng(Double.parseDouble(map.get("lat")), Double.parseDouble(map.get("lng")));

						myMap.addMarker(new MarkerOptions()
								.position(POSISI)
								.title(map.get("id"))
								.snippet(map.get("title")));

					}
				}
			});
		}

	}

	final int RQS_GooglePlayServices = 1;
	private GoogleMap myMap;
	TextView tvLocInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.peta_online);

		FragmentManager myFragmentManager = getFragmentManager();
		MapFragment myMapFragment = (MapFragment) myFragmentManager.findFragmentById(R.id.map);
		myMap = myMapFragment.getMap();

		myMap.setMyLocationEnabled(true);

		myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		myMap.getUiSettings().setZoomControlsEnabled(true);
		myMap.getUiSettings().setCompassEnabled(true);
		myMap.getUiSettings().setMyLocationButtonEnabled(true);

		myMap.getUiSettings().setAllGesturesEnabled(true);

		myMap.setTrafficEnabled(true);

		myMap.setOnMapLongClickListener(this);
		myMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
		myMap.setOnInfoWindowClickListener(this);

		myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AWAL, 20));

		myMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


		new getListInfo().execute();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	@Override
	protected void onResume() {
		super.onResume();

		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

		if (resultCode != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
		}
	}

	@Override
	public void onMapLongClick(LatLng point) {

	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Intent intent = new Intent(this, Detail.class);
		//for (int i = 0; i < dataMap.size(); i++)
		//{
		//HashMap<String, String> map = new HashMap<String, String>();
		//map = dataMap.get(i);


		String replace_string_first = marker.getTitle().replace(" ", "_");

		intent.putExtra("id", replace_string_first);
		//intent.putExtra("number", map.get("number"));
		//intent.putExtra("lat", map.get("lat"));
		//intent.putExtra("lng", map.get("lng"));
		startActivity(intent);

		//}
	}
}
