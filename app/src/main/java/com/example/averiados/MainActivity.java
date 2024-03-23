package com.example.averiados;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Utilidades.Utilidades;
import core.data.VolleyManager;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    TextView txt_usuario, txt_pass;
    String usuario = "";
    String pass = "";
    int band = 0;
    String nombre_usuario = "";
    String mensaje = "";

    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        txt_usuario = (TextView) findViewById(R.id.input_usuario);
        txt_pass = (TextView) findViewById(R.id.input_password);
        txt_usuario.requestFocus();

    }


    public void ir_login(View view) {
        login_ws();
    }

    private void login_ws() {
        VolleyManager volleyManager = VolleyManager.getInstance(this);
        String endpoint = "control_login.aspx";
        String url = volleyManager.getBaseUrl() + endpoint;
        progress = ProgressDialog.show(MainActivity.this, "INGRESANDO", "ESPERE...", true);
        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject respuesta_json = new JSONObject(response);
                            usuario = respuesta_json.getString("usuario");
                            pass = respuesta_json.getString("pass");
                            band = respuesta_json.getInt("band");
                            nombre_usuario = respuesta_json.getString("nombre_usuario");
                            mensaje = respuesta_json.getString("mensaje");

                            if (band == 0) {
                                showErrorDialog(mensaje);
                            } else if (band == 1) {
                                proceedToNextActivity();
                            }

                        } catch (Exception e) {
                            handleException(e);
                        } finally {
                            progress.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleException(error);
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("txtPassword", txt_pass.getText().toString());
                params.put("txtUsuario", txt_usuario.getText().toString());
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(
                800000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        volleyManager.addToRequestQueue(strRequest);
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("ATENCION!!")
                .setMessage(message)
                .setNegativeButton("CERRAR", null).show();
    }

    private void proceedToNextActivity() {
        Intent i = new Intent(MainActivity.this, menu_principal.class);
        startActivity(i);
        finish();
        datos_usuario.usuario = usuario;
        datos_usuario.pass = pass;
        datos_usuario.nombre_usuario = nombre_usuario;
    }

    private void handleException(Exception e) {
        e.printStackTrace();
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("ATENCION!!")
                .setMessage(e.toString())
                .setNegativeButton("CERRAR", null).show();
    }

        /*
    private void login_ws() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + Utilidades.IP + "/ws/control_login.aspx";
        progress = ProgressDialog.show(MainActivity.this, "INGRESANDO",
                "ESPERE...", true);
        StringRequest strRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject respuesta_json = new JSONObject(response);
                    usuario = respuesta_json.getString("usuario");
                    pass = respuesta_json.getString("pass");
                    band = respuesta_json.getInt("band");
                    nombre_usuario = respuesta_json.getString("nombre_usuario");
                    mensaje = respuesta_json.getString("mensaje");
                    progress.dismiss();

                    if (band == 0) {

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("ATENCION!!")
                                .setMessage(mensaje)
                                .setNegativeButton("CERRAR", null).show();
                        progress.dismiss();
                    } else if (band == 1) {
                        finish();
                        Intent i = new Intent(MainActivity.this, menu_principal.class);
                        startActivity(i);
                        progress.dismiss();
                        datos_usuario.usuario = usuario;
                        datos_usuario.pass = pass;
                        datos_usuario.nombre_usuario = nombre_usuario;
                    }

                } catch (Exception e) {
                    progress.dismiss();
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("ATENCION!!")
                            .setMessage(e.toString())
                            .setNegativeButton("CERRAR", null).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            progress.dismiss();
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("ATENCION!!")
                                    .setMessage(error.toString())
                                    .setNegativeButton("CERRAR", null).show();
                            progress.dismiss();
                        } catch (Exception e) {
                            progress.dismiss();

                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("ATENCION!!")
                                    .setMessage(e.toString())
                                    .setNegativeButton("CERRAR", null).show();
                            progress.dismiss();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("txtPassword", txt_pass.getText().toString());
                params.put("txtUsuario", txt_usuario.getText().toString());

                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(
                800000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(strRequest);
    }*/
}

