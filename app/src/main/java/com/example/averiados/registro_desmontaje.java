package com.example.averiados;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.levitnudi.legacytableview.LegacyTableView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import Entidades.grilla_desmontaje;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import Entidades.informe_array_global;
import Utilidades.Utilidades;
import cz.msebera.android.httpclient.Header;

public class registro_desmontaje extends AppCompatActivity {
    private  ListView ListView;
     DatePickerDialog picker;
    TextView txt_fecha;
    int band=0;
    String mensaje="";
    private ProgressDialog progress;
    List<informe_array_global> lista_array_global = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_desmontaje);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ListView=(ListView)findViewById(R.id.lv_desmontaje);
         txt_fecha=(TextView)findViewById(R.id.txt_fecha_desmontaje);

        getSupportActionBar().setTitle("DESMONTAJE USUARIO: "+datos_usuario.nombre_usuario);
        cargar_grilla();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = sdf.format(c.getTime());
        txt_fecha.setText(strDate);
        txt_fecha.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(registro_desmontaje.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                DecimalFormat df = new DecimalFormat("00");
                                SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");
                                cldr.set(year, monthOfYear, dayOfMonth);
                                String strDate = format.format(cldr.getTime());
                                txt_fecha.setText(df.format((dayOfMonth))+ "/" + df.format((monthOfYear + 1))  +"/"+ year  );
                            }
                        }, year, month, day);
                picker.show();
            }
        });

    }


    public void registrar_devolucion(View view) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("txtfecha", txt_fecha.getText());
        params.put("txtdeposito", datos_usuario.tipo_registro);
        params.put("txtpassword", datos_usuario.pass);
        params.put("txtusuario", datos_usuario.usuario);
        client.setTimeout(2000000);
        //  client.post("http://"+ Utilidades.IP +"/ws/control_insert.aspx", params, new TextHttpResponseHandler() {
            client.post("http://"+ Utilidades.IP +"/ws/Test/control_desmontaje.aspx", params, new TextHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        progress = ProgressDialog.show(registro_desmontaje.this, "PROCESANDO",
                                "ESPERE...", true);
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        if (statusCode == 200) {
                            try
                            {
                                try
                                {
                                    JSONObject respuesta_json =new JSONObject(res);
                                    band=respuesta_json.getInt("band");
                                    mensaje=respuesta_json.getString("mensaje");
                                }
                                catch (Exception e){

                                }
                            }
                            catch (Exception e){
                                band=0;
                                mensaje=e.toString();
                            }
                        }
                    }
                    @Override
                    public void onFinish() {
                        if(band==0){
                            new AlertDialog.Builder(registro_desmontaje.this)
                                    .setTitle("ATENCION!!!")
                                    .setMessage(mensaje)
                                    .setNegativeButton("CERRAR", null).show();
                            cargar_grilla();
                        }
                        else    if(band==1){
                            new AlertDialog.Builder(registro_desmontaje.this)
                                    .setTitle("ATENCION!!!")
                                    .setMessage(mensaje)
                                    .setNegativeButton("CERRAR", null).show();
                            cargar_grilla();
                        }
                        progress.dismiss();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                    }
                }
        );
    }

    private void cargar_grilla() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("txtdeposito", datos_usuario.tipo_registro);

        try {
            client.post("http://"+ Utilidades.IP +"/ws/control_select.aspx", params, new TextHttpResponseHandler() {
                        @Override
                        public void onStart() {
                        }
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String res) {
                            if (statusCode == 200) {
                                try
                                {
                                    JSONObject respuesta_json =new JSONObject(res);
                                    JSONArray jsonArray_list = respuesta_json.getJSONArray("contenido_grilla");
                                    List<String> total = new ArrayList<String>();
                                    total.clear();
                                    lista_array_global.clear();
                                    for (int i=0; i<jsonArray_list.length();i++) {
                                        informe_array_global gd = new informe_array_global();
                                        gd.setValor1(jsonArray_list.getJSONObject(i).getString("itemcode"));
                                        gd.setValor2(jsonArray_list.getJSONObject(i).getString("itemname"));
                                        gd.setValor3(jsonArray_list.getJSONObject(i).getString("onhand"));

                                        lista_array_global.add(gd);
                                    }
                                    ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.simple_list_item_8_fila, R.id.text1_fila, lista_array_global) {
                                        @Override
                                        public View getView(int position, View convertView, ViewGroup parent) {
                                            View view = super.getView(position, convertView, parent);
                                            TextView text1 =    (TextView) view.findViewById(R.id.text1_fila);
                                            TextView text2=     (TextView) view.findViewById(R.id.text2_fila);
                                            TextView text3 =    (TextView) view.findViewById(R.id.text3_fila);
                                            TextView text4 =    (TextView) view.findViewById(R.id.text4_fila);
                                            TextView text5 =    (TextView) view.findViewById(R.id.text5_fila);
                                            TextView text6 =    (TextView) view.findViewById(R.id.text6_fila);
                                            TextView text7 =    (TextView) view.findViewById(R.id.text7_fila);
                                            TextView text8 =    (TextView) view.findViewById(R.id.text8_fila);
                                            text1.setText("CODIGO: "     +lista_array_global.get(position).getValor1());
                                            text2.setText("DESCRIPCION: "     +lista_array_global.get(position).getValor2());
                                            text3.setText("CANTIDAD: "     +lista_array_global.get(position).getValor3());
                                            text4.setVisibility(View.GONE);
                                            text5.setVisibility(View.GONE);
                                            text6.setVisibility(View.GONE);
                                            text7.setVisibility(View.GONE);
                                            text8.setVisibility(View.GONE);

                                            if((position%2)==0)
                                            {
                                                view.setBackgroundColor(Color.LTGRAY);
                                            }
                                            else
                                            {
                                                view.setBackgroundColor(Color.WHITE);
                                            }

                                            return view;
                                        }
                                    };
                                    ListView.setAdapter(adapter);
                                }
                                catch (Exception e){

                                }
                            }
                        }

                        @Override
                        public void onFinish() {

                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        }
                    }
            );
        }catch (Exception e){
            String vari=e.toString();
        }

    }

    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("VOLVER ATRAS.")
                .setMessage("SE PERDERAN TODOS LOS DATOS.")
                .setPositiveButton("SI", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                        Intent List = new Intent(getApplicationContext(), menu_principal.class);
                        startActivity(List);
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }
}