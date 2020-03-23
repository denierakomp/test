package com.erakomp.test;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class login extends AppCompatActivity {
    //Defining views
    private EditText editTextEmail;
    private EditText editTextPassword;

    private Context context;
    private AppCompatButton buttonLogin, buttonRegister, buttonTamu;
    private ProgressDialog pDialog;

    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    //ArrayList<HashMap<String, String>> list_datauser;

   /* public final static String TAG_ADMIN = "admin";
    public final static String TAG_USER = "user";
    public final static String TAG_GUEST = "guest";

    public boolean statuslogin = false;*/

    //tambahan kode dari app sharedpreff
    // Alert Dialog Manager
    com.erakomp.test.AlertDialogManager alert = new com.erakomp.test.AlertDialogManager();
    //tambahan kode dari app sharedpreff
    // Session Manager Class
    com.erakomp.test.SessionManagement session;


    int PERMISSION_ID = 44;
    //FusedLocationProviderClient mFusedLocationClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = login.this;

        // Session Manager
        session = new SessionManagement(getApplicationContext());

        //Initializing views
        pDialog = new ProgressDialog(context);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (AppCompatButton) findViewById(R.id.buttonLogin);
        //buttonRegister = (AppCompatButton) findViewById(R.id.buttonRegister);

        //Adding click listener
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginuser();
            }
        });




    }




    private void loginuser() {
        //Getting values from edit texts
        //final String email = editTextEmail.getText().toString().trim();
        final String username = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();


        pDialog.setMessage("Login Process...");
        showDialog();
        //Creating a string request

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppVar.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String useraccname = null;
                String useridinfo = null;
                String statusid = null;
                //script baru untuk mendapatkan info user saat login
                Log.d("response ", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("infouser");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        //HashMap<String, String> map = new HashMap<String, String>();
                        //map.put("useridinfo", json.getString("user_id"));
                        useridinfo = json.getString("user_id"); //ambil data user dari database saat login
                        statusid = json.getString("user_status"); //status user apakah sebagai admin, guru, murid atau tamu
                        useraccname= json.getString("user_account_name");
                        //list_datauser.add(map);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //txtstatususer.setText = (list_datauser.get(position));
                //If we are getting success from server
                if (response.contains(AppVar.LOGIN_SUCCESS1)) {
                    //Toast.makeText(getApplicationContext(), "Admin Status: " + session.isAdminIn()+ "IS LOGIN : " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
                    //session.createLoginSessionAdm(email, useridinfo, password);
                    session.createLoginSessionAdm(username, useridinfo, password);
                    //session.editor.putBoolean(IS_ADMIN, true);
                    hideDialog();
                    Intent intent = new Intent(login.this, MainActivity.class);
                    //intent.putExtra("leveluser", "admin");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    // Add new Flag to start new Activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //finish();
                    startActivity(intent);

                } else {
                    if (response.contains(AppVar.LOGIN_SUCCESS0)) {  //login as user
                        //Toast.makeText(getApplicationContext(), "Admin Status: " + session.isAdminIn()+ "IS LOGIN : " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
                        session.createLoginSession(username, useridinfo, password);
                        hideDialog();
                        Intent intent = new Intent(login.this, MainActivity.class);
                        //intent.putExtra("leveluser", "user");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        // Add new Flag to start new Activity
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //finish();
                        startActivity(intent);
                    } else {
                        hideDialog();
                        //Displaying an error message on toast
                        Toast.makeText(context, "Nama / Password Salah", Toast.LENGTH_LONG).show();
                    }

                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        hideDialog();
                        Toast.makeText(context, "Kesalahan Jaringan Server", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                //params.put(AppVar.KEY_EMAIL, email);
                params.put(AppVar.KEY_USERNAME, username);
                params.put(AppVar.KEY_PASSWORD, password);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        Volley.newRequestQueue(this).add(stringRequest);
        //newRequestQueue(this).add(stringRequest);

    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }



    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}

