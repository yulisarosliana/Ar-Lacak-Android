package org.inkubator.radinaldn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // maps and ar
    //id of the dialog used to choose between maps and augmented reality
    static final int CHOOSE_METHOD_DIALOG = 1;
    static final int ABOUT_DIALOG = 2;

    static int CURRENT_1 = 0;
    static int CURRENT_1_IMAGE = 0;

    private IconContextMenu chooseActionContextMenu = null;

    private final int MENU_ITEM_1_ACTION_MAPS = 1;
    private final int MENU_ITEM_2_ACTION_AUGMENTED_REALITY = 2;
    private final int MENU_ITEM_3_ACTION_LIST = 3;

    //Google Local Search API Query variables
    private final String GOOGLE_SEARCH_API_1 = "AR_konten.php";

    public String lo_Koneksi, isi;
    private LocationManager lm;
    private LocationListener locListener;
    private TextView latTxt, lonTxt, hello;
    private String GOOGLE_SEARCH_API_CUSTOM = "q=";
    private ProgressDialog pDialog;
    int jumlahRadioButton;
    private TextView luka_bakar, patahtulang;
    JSONArray str_json = null;

    double latitude, longitude;
    String arUri;
    // end of maps and ar

    String id_user, username, email;
    SharedPreferences sharedpreferences;

    public static final String TAG_ID = "id_user";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_EMAIL = "email";
    // end of session

    public static final String JUMLAH_RB = "jumlah_rb";


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new getKategoriInfo().execute();

        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);

        id_user = getIntent().getStringExtra(TAG_ID);
        username = getIntent().getStringExtra(TAG_USERNAME);
        email = getIntent().getStringExtra(TAG_EMAIL);

        // maps and ar
        latTxt = (TextView) findViewById(R.id.lat);
        lonTxt = (TextView) findViewById(R.id.lng);
        luka_bakar = (TextView) findViewById(R.id.lukabakar);

        luka_bakar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                luka_bakarac();
            }
        });
        patahtulang = (TextView) findViewById(R.id.patahtulang);
        patahtulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PatahHatiActivity.class);
                startActivity(intent);
            }
        });
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locListener = new MyLocationListener();

        Koneksi lo_Koneksi = new Koneksi();
        isi = lo_Koneksi.isi_koneksi();

        //------------------------------------------------------------------
        //I N I T    T H E    M E N U    W I T H    I T S    E V E N T S
        //------------------------------------------------------------------
        Resources res = getResources();
        chooseActionContextMenu = new IconContextMenu(this, CHOOSE_METHOD_DIALOG);
        chooseActionContextMenu.addItem(res, R.string.choose_action_maps, R.drawable.action_maps, MENU_ITEM_1_ACTION_MAPS);
        chooseActionContextMenu.addItem(res, R.string.choose_action_augmentedreality, R.drawable.action_ar, MENU_ITEM_2_ACTION_AUGMENTED_REALITY);
        chooseActionContextMenu.addItem(res, R.string.choose_action_list, R.drawable.action_list, MENU_ITEM_3_ACTION_LIST);

        //set onclick listener for context menu
        chooseActionContextMenu.setOnClickListener(new IconContextMenu.IconContextMenuOnClickListener() {
            @Override
            public void onClick(int menuId) {

                if (menuId == MENU_ITEM_1_ACTION_MAPS) {
                    switch (CURRENT_1) {
                        case 1:
                            check_1();
                            break;
                        case 7:
                            check_Custom();
                            break;
                    }
                } else if (menuId == MENU_ITEM_2_ACTION_AUGMENTED_REALITY) {
                    Intent arview = new Intent();
                    arview.setAction(Intent.ACTION_VIEW);
                    arUri = isi;
                    switch (CURRENT_1) {
                        case 1:
                            arUri += GOOGLE_SEARCH_API_1;
                            break;
                        case 7:
                            arUri += GOOGLE_SEARCH_API_CUSTOM;

                            break;
                    }
                    //arUri += GOOGLE_SEARCH_API_QUERY;
                    //arview.setDataAndType(Uri.parse(arUri), "application/mixare-json");
                    arview.setDataAndType(Uri.parse(arUri), "application/android10-json");
                    arview.putExtra("imageId", CURRENT_1_IMAGE);
                    startActivity(arview);

                } else if (menuId == MENU_ITEM_3_ACTION_LIST) {
                    switch (CURRENT_1) {
                        case 1:
                            list_1();
                            break;
                        case 7:
                            list_Custom();
                            break;
                    }
                }
            }
        });

        // end of maps and ar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        hello = (TextView) findViewById(R.id.tvhello);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        TextView tvname = (TextView) header.findViewById(R.id.tvnama);
        TextView tvemail = (TextView) header.findViewById(R.id.tvemail);
        tvname.setText(username);
        tvemail.setText(email);


    }

    private void luka_bakarac() {
        Intent intent = new Intent(getApplicationContext(), LukaBakarActivity.class);
        startActivity(intent);
    }

    //------------------------------------------------------------------
    //A C T I V I T Y    R E S U L T
    //------------------------------------------------------------------

    /**
     * Handle the results from the recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_OK && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            if (matches.size() >= 0) {

                CURRENT_1 = 7;
                CURRENT_1_IMAGE = R.drawable.logo_user;
                showDialog(CHOOSE_METHOD_DIALOG);
            } else {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //------------------------------------------------------------------
    //P R E P A R I N G    T H E    C H O O S E    M E T H O D    D I A L O G
    //------------------------------------------------------------------
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case CHOOSE_METHOD_DIALOG:
                return chooseActionContextMenu.createMenu(this.getString(R.string.choose_action_title));
            case ABOUT_DIALOG:
                dialog = createAboutDialog();
                break;
            default:
                dialog = null;
                break;
        }
        return dialog;
    }

    ;

    //------------------------------------------------------------------

    //P R E P A R I N G    T H E    D I A L O G S
    //------------------------------------------------------------------
    private AlertDialog createAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.license_title)
                .setMessage(R.string.license).setIcon(R.drawable.about)
                .setNeutralButton(R.string.about_button, null);
        AlertDialog alert = builder.create();
        return alert;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Close the menu after a period of time.
        // Note that this STARTS the timer when the options menu is being
        // prepared, NOT when the menu is made visible.
        Timer timing = new Timer();
        timing.schedule(new TimerTask() {

            @Override
            public void run() {
                closeOptionsMenu();
            }
        }, 5000);
        return super.onPrepareOptionsMenu(menu);
    }

    // Log out
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Log out and clear session
            // TODO Auto-generated method stub
            // update login session ke FALSE dan mengosongkan` nilai id dan username
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(Login.session_status, false);
            editor.putString(TAG_ID, null);
            editor.putString(TAG_USERNAME, null);
            editor.putString(TAG_EMAIL, null);
            editor.commit();

            Intent intent = new Intent(MainActivity.this, Login.class);
            finish();
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    //------------------------------------------------------------------
    //P R I V A T E     M E T H O D S
    //------------------------------------------------------------------


    private void check_1() {

        Intent l = new Intent(MainActivity.this, MapsView.class);
        //l.putExtra("kategori", "Bank");


        startActivity(l);
    }

    private void check_Custom() {

    }

    //------------------------------------------------------------------
    //P R I V A T E     M E T H O D S
    //------------------------------------------------------------------


    private void list_1() {
        //	double Clat=Double.parseDouble(latTxt.getText().toString().trim());
        //	double Clon=Double.parseDouble(lonTxt.getText().toString().trim());

        double Clat = -5.140219;
        double Clon = 119.483231;

        Intent l = new Intent(MainActivity.this, ListActivity.class);
        l.putExtra("Clat", Clat);
        l.putExtra("Clon", Clon);

        startActivity(l);
    }

    private void list_Custom() {

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_berita) {
            // Handle the action
            Intent i = new Intent(MainActivity.this, News.class);
            startActivity(i);
        } else if (id == R.id.nav_lapor) {
            Intent intent = new Intent(MainActivity.this, LaporKecelakaan.class);
            intent.putExtra(TAG_ID, id_user);
            intent.putExtra(TAG_USERNAME, username);
            intent.putExtra(TAG_EMAIL, email);
            intent.putExtra(JUMLAH_RB, hello.getText().toString());
            finish();
            System.out.println("JUMLAH_RB = " + hello.getText().toString());
            startActivity(intent);
        } else if (id == R.id.nav_kompas) {
            CURRENT_1 = 1;
            CURRENT_1_IMAGE = R.drawable.atm2;
            showDialog(CHOOSE_METHOD_DIALOG);
        } else if (id == R.id.nav_about) {

            AlertDialog alertDialog = new AlertDialog.Builder(
                    MainActivity.this).create();

            // Setting Dialog Title
            alertDialog.setTitle(R.string.license_title);

            // Setting Dialog Message
            alertDialog.setMessage("FIRST AID v1.0\nCopyright ©Tugas Akhir Yulisa Rosliana");

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.logo_user);

            // Setting OK Button
//            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    // Write your code here to execute after dialog closed
//                    Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
//                }
//            });

            // Showing Alert Message
            alertDialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //
    //------------------------------------------------------------------
    //P U B L I C     M E T H O D S
    //------------------------------------------------------------------
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                latTxt.setText(String.valueOf(loc.getLatitude()));
                lonTxt.setText(String.valueOf(loc.getLongitude()));
                latitude = loc.getLatitude();

                // Mendapatkan nilai longitude dari lokasi terbaru
                longitude = loc.getLongitude();
                // Menampilkan info status gps hidup  menggunakan Toast
                //	String message = "GPS ON";
                //	Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
            } else {
                latTxt.setText("-5.140219");
                lonTxt.setText("119.483231");
            }
        }

        @Override
        public void onProviderDisabled(String arg0) {
            String message = "GPS disabled";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String arg0) {
            String message = "GPS enabled";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    // get total kategori
    class getKategoriInfo extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
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
                jumlahRadioButton = str_json.length();

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

                    hello.setText(String.valueOf(jumlahRadioButton));
                }
            });

        }
    }
}
