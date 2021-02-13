package com.example.averiados;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class menu_principal extends AppCompatActivity {
int contador_barra=0;
    private ProgressDialog prodialog,progress;
    List<String> codigo_cliente = new ArrayList<String>();
    List<String> desc_cliente = new ArrayList<String>();
    List<String> whscode = new ArrayList<String>();
    List<String> whsname = new ArrayList<String>();

    ConexionSQLiteHelper conn;
String mensaje="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setTitle("Usuario: "+datos_usuario.nombre_usuario);
        conn=new ConexionSQLiteHelper(getApplicationContext(),"bd_usuarios",null,1);


    }

    public void ir_menu_informes(View view) {
        finish();
        Intent i=new Intent(menu_principal.this, menu_informe.class);
        startActivity(i);

    }
    public void ir_transformacion(View view) {
        finish();
        Intent i=new Intent(menu_principal.this, registro_transformacion.class);
        startActivity(i);

    }
    public void ir_desmontaje(View view) {
        finish();
        Intent i=new Intent(menu_principal.this, registro_desmontaje.class);
        startActivity(i);
        datos_usuario.tipo_registro="AVE001";

    }

    public void ir_desmontaje_CEN7(View view) {
        finish();
        Intent i=new Intent(menu_principal.this, registro_desmontaje.class);
        startActivity(i);
        datos_usuario.tipo_registro="CEN007";

    }



    public void ir_transferencia(View view) {
        finish();
      Intent i=new Intent(menu_principal.this, registro_transferencia.class);
        // Intent i=new Intent(menu_principal.this, pdf.class);
        startActivity(i);
    }
    public void ir_devolucion(View view) {
        finish();
        Intent i=new Intent(menu_principal.this, registro_devolucion.class);
        startActivity(i);
    }

    public void ir_cambio(View view) {
        finish();
        Intent i=new Intent(menu_principal.this, registro_cambio.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("ATENCION!!.")
                .setMessage("DESEA CERRAR SESION?.")
                .setPositiveButton("SI", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                        Intent List = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(List);
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }


    public void sincronizar_clientes(View view) {

        AsyncHttpClient client = new AsyncHttpClient();


        RequestParams params = new RequestParams();
        params.put("usuario", "test");



        client.post("http://192.168.6.162/ws/control_select_clientes.aspx", params, new TextHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        progress = ProgressDialog.show(menu_principal.this, "PREPARANDO SINCRONIZACION",
                                "ESPERE...", true);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        if (statusCode == 200) {


                            try
                            {
                                codigo_cliente.clear();
                                desc_cliente.clear();
                                JSONObject respuesta_json =new JSONObject(res);
                                JSONArray jsonArray_list = respuesta_json.getJSONArray("contenido_grilla");
                                 for (int i=0; i<jsonArray_list.length();i++)

                                 {

                                    codigo_cliente.add(jsonArray_list.getJSONObject(i).getString("cardcode"));
                                    desc_cliente.add(jsonArray_list.getJSONObject(i).getString("address"));

                                 }
                            }
                            catch (Exception e){


                            }
                        }

                    }



                    @Override
                    public void onFinish() {

                        progress.dismiss();

                        prodialog =  new ProgressDialog(menu_principal.this);
                        LayerDrawable progressBarDrawable = new LayerDrawable(
                                new Drawable[]{
                                        new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                                new int[]{Color.parseColor("black"),Color.parseColor("black")}),
                                        new ClipDrawable(
                                                new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                                        new int[]{Color.parseColor("yellow"),Color.parseColor("yellow")}),
                                                Gravity.START,
                                                ClipDrawable.HORIZONTAL),
                                        new ClipDrawable(
                                                new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                                        new int[]{Color.parseColor("#ffff0000"),Color.parseColor("#ffff0000")}),
                                                Gravity.START,
                                                ClipDrawable.HORIZONTAL)
                                });
                        progressBarDrawable.setId(0,android.R.id.background);
                        progressBarDrawable.setId(1,android.R.id.secondaryProgress);
                        progressBarDrawable.setId(2,android.R.id.progress);
                        prodialog.setMax(codigo_cliente.size());
                        prodialog.setTitle("SINCRONIZANDO CLIENTES");
                        prodialog.setMessage("ESPERE...");
                        prodialog.setProgressDrawable(progressBarDrawable);
                        prodialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        prodialog.show();
                        prodialog.setCanceledOnTouchOutside(false);
                        prodialog.setCancelable(false);
                        new menu_principal.hilo_importar_cliente().start();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        progress.dismiss();
                         new AlertDialog.Builder(menu_principal.this)
                                .setTitle("ATENCION!!!")
                                .setMessage("ERROR DE CONEXION, FAVOR INTENTE DE NUEVO.")
                                .setNegativeButton("CERRAR", null).show();
                    }

                }
        );
    }


private void calcular_depositos(){

    AsyncHttpClient client = new AsyncHttpClient();


    RequestParams params = new RequestParams();
    params.put("usuario", "test");
    params.put("txtDeposito", "CEN007");



    client.post("http://192.168.6.162/ws/control_select_depositos.aspx", params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    progress = ProgressDialog.show(menu_principal.this, "PREPARANDO SINCRONIZACION DE DEPOSITOS",
                            "ESPERE...", true);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String res) {
                    if (statusCode == 200) {


                        try
                        {
                            whscode.clear();
                            whsname.clear();
                            JSONObject respuesta_json =new JSONObject(res);
                            JSONArray jsonArray_list = respuesta_json.getJSONArray("contenido_grilla");
                            for (int i=0; i<jsonArray_list.length();i++)

                            {

                                whscode.add(jsonArray_list.getJSONObject(i).getString("whscode"));
                                whsname.add(jsonArray_list.getJSONObject(i).getString("whsname"));

                            }
                        }
                        catch (Exception e){


                        }
                    }

                }



                @Override
                public void onFinish() {

                    progress.dismiss();

                    prodialog =  new ProgressDialog(menu_principal.this);
                    LayerDrawable progressBarDrawable = new LayerDrawable(
                            new Drawable[]{
                                    new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                            new int[]{Color.parseColor("black"),Color.parseColor("black")}),
                                    new ClipDrawable(
                                            new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                                    new int[]{Color.parseColor("yellow"),Color.parseColor("yellow")}),
                                            Gravity.START,
                                            ClipDrawable.HORIZONTAL),
                                    new ClipDrawable(
                                            new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                                    new int[]{Color.parseColor("#ffff0000"),Color.parseColor("#ffff0000")}),
                                            Gravity.START,
                                            ClipDrawable.HORIZONTAL)
                            });
                    progressBarDrawable.setId(0,android.R.id.background);
                    progressBarDrawable.setId(1,android.R.id.secondaryProgress);
                    progressBarDrawable.setId(2,android.R.id.progress);
                    prodialog.setMax(whsname.size());
                    prodialog.setTitle("SINCRONIZANDO DEPOSITOS");
                    prodialog.setMessage("ESPERE...");
                    prodialog.setProgressDrawable(progressBarDrawable);
                    prodialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    prodialog.show();
                    prodialog.setCanceledOnTouchOutside(false);
                    prodialog.setCancelable(false);
                    new menu_principal.hilo_importar_depositos().start();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                    progress.dismiss();
                    new AlertDialog.Builder(menu_principal.this)
                            .setTitle("ATENCION!!!")
                            .setMessage("ERROR DE CONEXION, FAVOR INTENTE DE NUEVO.")
                            .setNegativeButton("CERRAR", null).show();
                }

            }
    );

}
    private void sincronizar_clientes(){
        try {
            SQLiteDatabase db=conn.getWritableDatabase();
            SQLiteDatabase db1=conn.getReadableDatabase();
            db1.execSQL("delete from clientes");
            for (int i=0; i<codigo_cliente.size();i++)
            {


                ContentValues values=new ContentValues();
                values.put("cardcode", codigo_cliente.get(i));
                values.put("address",desc_cliente.get(i));

                db.insert("clientes" ,null,values);
                contador_barra++;
                prodialog.setProgress(contador_barra);
            }
            db.close();
            db1.close();
            mensaje="CLIENTES SINCRONIZADOS CON EXITO.";
        }
        catch (Exception e){
            mensaje=e.toString();
        }

    }


    class hilo_importar_cliente extends Thread {

        @Override
        public void run() {
            sincronizar_clientes();
            try {
                 runOnUiThread(new Runnable() {
                    @Override

            public void run() {

            prodialog.dismiss();
            contador_barra=0;
         /*   new AlertDialog.Builder(menu_principal.this)
            .setTitle("ATENCION!!!")
            .setMessage(mensaje)
            .setNegativeButton("CERRAR", null).show();
                    */

        calcular_depositos();

                    }
                });
            } catch ( Exception e) {
                e.printStackTrace();
            }
        }
    }




    class hilo_importar_depositos extends Thread {

        @Override
        public void run() {
            sincronizar_depositos();
            try {
                runOnUiThread(new Runnable() {
                    @Override

                    public void run() {

                        prodialog.dismiss();
                        contador_barra=0;
                        new AlertDialog.Builder(menu_principal.this)
                                .setTitle("ATENCION!!!")
                                .setMessage(mensaje)
                                .setNegativeButton("CERRAR", null).show();
                    }
                });
            } catch ( Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void sincronizar_depositos(){
        try {
            SQLiteDatabase db=conn.getWritableDatabase();
            SQLiteDatabase db1=conn.getReadableDatabase();
            db1.execSQL("delete from depositos");
            for (int i=0; i<whscode.size();i++)
            {


                ContentValues values=new ContentValues();
                values.put("whscode", whscode.get(i));
                values.put("whsname",whsname.get(i));

                db.insert("depositos" ,null,values);
                contador_barra++;
                prodialog.setProgress(contador_barra);
            }
            db.close();
            db1.close();
            mensaje="DEPOSITOS SINCRONIZADOS CON EXITO.";
        }
        catch (Exception e){
            mensaje=e.toString();
        }

    }
}