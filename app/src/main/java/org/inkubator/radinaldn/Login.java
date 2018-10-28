package org.inkubator.radinaldn;
import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rian on 20/10/2017.
 */
public class Login extends AbsRunTimePermission {
    private static final int REQUEST_PERMISSION = 10;
    ProgressDialog pDialog;
    TextView show_pass, create_user;
    Button login;
    EditText etusername, etpassword;

    // initiate
    Koneksi koneksi = new Koneksi();
    String isi = koneksi.isi_koneksi();

    int success;
    ConnectivityManager conMgr;

    private String url = isi + "login.php";

    private static final String TAG = Login.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public final static String TAG_EMAIL = "email";
    public final static String TAG_USERNAME = "username";
    public final static String TAG_ID = "id_user";

    String tag_json_obj = "json_obj_req";

    SharedPreferences sharedpreferences;
    Boolean session = false;
    String id_user, username, email;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";

    // Untuk login pertama kali, bukan pengecekan saat aplikasi sudah pernah login
    protected void onCreate (Bundle SaveInstanceState){
        super.onCreate(SaveInstanceState);
        setContentView(R.layout.login_asset);

        // request permission here
        requestAppPermissions(new String[]{
                Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE},
                R.string.msg,REQUEST_PERMISSION);


        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        show_pass = (TextView) findViewById(R.id.tvshow);
        login = (Button) findViewById(R.id.btnlogin);
        etusername = (EditText) findViewById(R.id.etusername);
        etpassword = (EditText) findViewById(R.id.etpassword);
        create_user = (TextView) findViewById(R.id.tvcreateuser);

        // Cek session login jika TRUE maka langsung buka MainActivity
        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        id_user = sharedpreferences.getString(TAG_ID, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);
        email = sharedpreferences.getString(TAG_EMAIL, null);

        if (session) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtra(TAG_ID, id_user);
            intent.putExtra(TAG_USERNAME, username);
            intent.putExtra(TAG_EMAIL, email);
            finish();
            startActivity(intent);

        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String username = etusername.getText().toString();
                String password = etpassword.getText().toString();

                // mengecek kolom yang kosong
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        checkLogin(username, password);
                    } else {
                        Toast.makeText(getApplicationContext() ,"Tidak ada koneksi internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext() ,"Kolom username atau password tidak boleh kosong", Toast.LENGTH_LONG).show();
                }
            }
        });

        // skrip agar button show muncul saat form password telah terisi
        etpassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // kondisi
                if (hasFocus){
                    show_pass.setVisibility(View.VISIBLE);
                    // apabila tombol show di klik
                    show_pass.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // kondisi apabila diklik show dia maka password akan tampil
                            if (show_pass.getText().equals("Show")){
                                // Then show user password

                                // Untuk merubah karakter dari ***** menjadi alfabet
                                etpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                show_pass.setText("Hide");
                            } else {

                                // Untuk merubah karakter dari alfabet menjadi *****
                                etpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                show_pass.setText("Show");
                            }
                        }
                    });
                }
            }
        });

        // action click on create user
        create_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Register.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onPermissionGranted(int requestcode) {
        // DO anything when permission granted
        Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();

    }

    private void checkLogin(final String username, final String password) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        String email = jObj.getString(TAG_EMAIL);
                        String username = jObj.getString(TAG_USERNAME);
                        String id_user = jObj.getString(TAG_ID);


                        Log.e("Successfully Login!", jObj.toString());

                        Toast.makeText(getApplicationContext(), "Selamat datang " +username, Toast.LENGTH_LONG).show();

                        // menyimpan login ke session
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_ID, id_user);
                        editor.putString(TAG_USERNAME, username);
                        editor.putString(TAG_EMAIL, email);
                        editor.commit();

                        // Memanggil main activity
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra(TAG_ID, id_user);
                        intent.putExtra(TAG_USERNAME, username);
                        intent.putExtra(TAG_EMAIL, email);
                        finish();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

//    // Method
//    private void show_menuUtama() {
//        Intent i=  new Intent(this,MainActivity.class);
//        startActivity(i);
//    }
}
