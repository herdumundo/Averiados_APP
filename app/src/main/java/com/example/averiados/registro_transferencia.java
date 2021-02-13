package com.example.averiados;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import Entidades.grilla_transferencia_agregar;

import Entidades.destino;
import Entidades.deposito;
import Entidades.lote;
import Entidades.tipo_huevo;
import cz.msebera.android.httpclient.Header;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class registro_transferencia extends AppCompatActivity {

    SpinnerDialogCustom  sipinnerDialog_huevo,sipinnerDialog_motivo,sipinnerDialog_deposito ,sipinnerDialog_destino;
     ListView lv_transferencia;
    ArrayList<String> list_tipo_huevo_text = new ArrayList<String>();
    ArrayList<String> listaInformacion;
    private ArrayAdapter<String> adaptador1;
    List<destino> lista_combo_destino = new ArrayList<>();
    ArrayList<String> list_deposito_text = new ArrayList<String>();
    ArrayList<String> list_destino_text = new ArrayList<String>();
    grilla_transferencia_agregar grilla_transferencia_agregar=null;
    DatePickerDialog picker;
    private ArrayList<String> cajas;
    ArrayList<grilla_transferencia_agregar> listatransferencia;
    TextView txt_cantidad_trans,txt_fecha_trans,txt_spinner_tipo,txt_cant_disponible_transferencia,txt_spinner_deposito,txt_spinner_destino,txt_comentario;
    Button btn_cargar,btn_fecha;
    String tipo_huevo=null;
    String nombre_huevo="";
    String destino=null;
    private ProgressDialog progress,progress2;
    int doc_entry=0;
    int sum=0;
    int band=0;
    String mensaje="";
    List<lote> lista_tipo = new ArrayList<>();
    List<destino> lista_destino = new ArrayList<>();
    List<deposito> lista_deposito = new ArrayList<>();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_transferencia);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setTitle("TRANSFERENCIA     USUARIO: "+datos_usuario.nombre_usuario);
        txt_spinner_destino=(TextView) findViewById(R.id.txt_spinner_destino);
        txt_spinner_tipo=(TextView) findViewById(R.id.spinner_tipo);
        btn_cargar=(Button) findViewById(R.id.btn_ingresar);
        btn_fecha=(Button) findViewById(R.id.btn_fecha_trans );
        txt_spinner_deposito=   (TextView) findViewById(R.id.txt_spinner_deposito2);
        txt_comentario=   (TextView) findViewById(R.id.txt_comentario);
        txt_cantidad_trans=(TextView) findViewById(R.id.txt_cantidad_trans);
        txt_fecha_trans=(TextView) findViewById(R.id.txt_fecha_trans);
        txt_cant_disponible_transferencia=(TextView) findViewById(R.id.txt_cant_disponible_transferencia);
        lv_transferencia=(ListView)findViewById(R.id.lv_transferencia);
        adaptador1=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_2,cajas);
        String currentDateTimeString = java.text.SimpleDateFormat.getDateInstance().format(new Date());
      //  txt_fecha_trans.setText(currentDateTimeString);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = sdf.format(c.getTime());
        cargar_depositos();
        cargar_destino("AVE001");
       // Date currentTime = Calendar.getInstance().getTime();
        txt_fecha_trans.setText(strDate);

        listatransferencia=new ArrayList<grilla_transferencia_agregar>();
        txt_spinner_deposito.setOnClickListener(new View.OnClickListener() {  @Override public void onClick(View v) {
            sipinnerDialog_deposito.dTitle="SELECCION DE DEPOSITO";
            sipinnerDialog_deposito.showSpinerDialog();

            sipinnerDialog_deposito.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String s, int i) {
                    txt_spinner_deposito.setText(lista_deposito.get(i).getnombre());
                    cargar_destino(lista_deposito.get(i).getnombre().toString());
                    txt_spinner_destino.setText("SELECCIONE EL DESTINO");
                    destino=null;
                    cargar_tipo();
                    txt_cant_disponible_transferencia.setText("0");
                    refrescar_lv(0);//SI ES CERO ENTONCES LIMPIA LA GRILLA.
                }
            });
        } } );

        txt_spinner_tipo.setOnClickListener(new View.OnClickListener() {  @Override public void onClick(View v) {
            sipinnerDialog_huevo.dTitle="SELECCION DE TIPO DE HUEVO";
            sipinnerDialog_huevo.showSpinerDialog();

            sipinnerDialog_huevo.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String s, int i) {


                    tipo_huevo= lista_tipo.get(i).getItemcode().toString();
                    nombre_huevo=lista_tipo.get(i).getDistnumber().toString();
                    txt_spinner_tipo.setText(nombre_huevo);
                    txt_cant_disponible_transferencia.setText(lista_tipo.get(i).getQuantity().toString());
                }
            });
        } } );



        txt_spinner_destino.setOnClickListener(new View.OnClickListener() {  @Override public void onClick(View v) {
            sipinnerDialog_destino.dTitle="SELECCION DE DESTINO";
            sipinnerDialog_destino.showSpinerDialog();

            sipinnerDialog_destino.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String s, int i) {

                    txt_spinner_destino.setText(list_destino_text.get(i));
                    destino=lista_destino.get(i).geticodigo();
                }
            });
        } } );


        btn_cargar.setOnClickListener(new View.OnClickListener() {  @Override
            public void onClick(View v) {
                cargar_array();
                refrescar_lv(1);
             } }

             );



        txt_cantidad_trans.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                        case KeyEvent.KEYCODE_TAB:

                            cargar_array();
                            refrescar_lv(1);
                    }
                }
                return false;
            }
        });


        btn_fecha.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(registro_transferencia.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                DecimalFormat df = new DecimalFormat("00");
                                SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");

                                cldr.set(year, monthOfYear, dayOfMonth);
                                String strDate = format.format(cldr.getTime());
                                txt_fecha_trans.setText(df.format((dayOfMonth))+ "/" + df.format((monthOfYear + 1))  +"/"+ year  );



                            }
                        }, year, month, day);
                picker.show();


            }
        });




        lv_transferencia.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int posicion=i;

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(registro_transferencia.this);
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿ Eliminar esta fila ?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        listatransferencia.remove(posicion);
                        refrescar_lv(1);
                                                                                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                    }
                });
                dialogo1.show();

                return false;
            }
        });
    }


    private     void cargar_tipo() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("txtDeposito",txt_spinner_deposito.getText().toString());

        client.post("http://192.168.6.162/ws/consulta_stock_averiados.aspx", params, new TextHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        progress = ProgressDialog.show(registro_transferencia.this, "CONSULTANDO DATOS", "ESPERE...", true);
                        txt_spinner_tipo.setVisibility(View.GONE);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        if (statusCode == 200) {
                            try
                            {

                                list_tipo_huevo_text.clear();
                                lista_tipo.clear();
                                JSONObject respuesta_json =new JSONObject(res);
                                JSONArray jsonArray_list = respuesta_json.getJSONArray("contenido_grilla");
                                for (int i=0; i<jsonArray_list.length();i++)
                                {
                                    lote p= new lote();
                                    list_tipo_huevo_text.add(jsonArray_list.getJSONObject(i).getString("itemname"));
                                    p.setDistnumber(jsonArray_list.getJSONObject(i).getString("itemname"));
                                    p.setItemcode(jsonArray_list.getJSONObject(i).getString("itemcode"));
                                    p.setQuantity(Integer.parseInt(jsonArray_list.getJSONObject(i).getString("onhand")));
                                    lista_tipo.add(p);
                                }
                                //    Toast.makeText(registro_transformacion.this,""+list_tipo_text.size(),Toast.LENGTH_LONG).show();


                                txt_spinner_tipo.setText("SELECCIONE TIPO DE HUEVO");
                                 sipinnerDialog_huevo = new SpinnerDialogCustom(registro_transferencia.this,list_tipo_huevo_text,"SELECCIONE TIPO DE HUEVO");



                            }
                            catch (Exception e)
                            {
                                new AlertDialog.Builder(registro_transferencia.this)
                                        .setTitle("ATENCION!!!")
                                        .setMessage(e.toString())
                                        .setNegativeButton("CERRAR", null).show();
                                progress.dismiss();
                            }
                        }
                    }
                    @Override
                    public void onFinish() {
                        progress.dismiss();
                        txt_spinner_tipo.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        progress.dismiss();
                        new AlertDialog.Builder(registro_transferencia.this)
                                .setTitle("ATENCION!!!")
                                .setMessage("ERROR DE CONEXION, FAVOR INTENTE DE NUEVO.")
                                .setNegativeButton("CERRAR", null).show();
                        txt_spinner_tipo.setVisibility(View.INVISIBLE);
                    }
                }
        );


    }

    private     void cargar_destino(String deposito) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("txtDeposito",deposito.trim());
        client.post("http://192.168.6.162/ws/control_select_depositos.aspx", params, new TextHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        progress2 = ProgressDialog.show(registro_transferencia.this, "CONSULTANDO DATOS DESTINO", "ESPERE...", true);
                       // txt_spinner_tipo.setVisibility(View.GONE);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        if (statusCode == 200) {
                            try
                            {

                                list_destino_text.clear();
                                lista_destino.clear();
                                JSONObject respuesta_json =new JSONObject(res);
                                JSONArray jsonArray_list = respuesta_json.getJSONArray("contenido_grilla");
                                for (int i=0; i<jsonArray_list.length();i++)
                                {
                                     destino p= new destino();
                                    list_destino_text.add(jsonArray_list.getJSONObject(i).getString("whsname"));

                                    p.setcodigo(jsonArray_list.getJSONObject(i).getString("whscode"));
                                    p.setnombre(jsonArray_list.getJSONObject(i).getString("whsname"));
                                    lista_destino.add(p);
                                        }
                                sipinnerDialog_destino = new SpinnerDialogCustom(registro_transferencia.this,list_destino_text,"SELECCIONE DESTINO");
                               // progress.dismiss();
                                }
                            catch (Exception e)
                            {
                                new AlertDialog.Builder(registro_transferencia.this)
                                        .setTitle("ATENCION!!!")
                                        .setMessage(e.toString())
                                        .setNegativeButton("CERRAR", null).show();
                                progress2.dismiss();
                            }
                        }
                    }
                    @Override
                    public void onFinish() {
                        progress2.dismiss();
                       // txt_spinner_tipo.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        progress2.dismiss();
                        new AlertDialog.Builder(registro_transferencia.this)
                                .setTitle("ATENCION!!!")
                                .setMessage("ERROR DE CONEXION, FAVOR INTENTE DE NUEVO.")
                                .setNegativeButton("CERRAR", null).show();
                       // txt_spinner_tipo.setVisibility(View.INVISIBLE);
                    }
                }
        );


    }

    private     void cargar_array(){


        if(txt_cantidad_trans.getText().length()==0){

            new AlertDialog.Builder(registro_transferencia.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("INGRESE LA CANTIDAD")
                    .setNegativeButton("CERRAR", null).show();
        }
        else if(Integer.parseInt(txt_cantidad_trans.getText().toString())>Integer.parseInt(txt_cant_disponible_transferencia.getText().toString())){
            new AlertDialog.Builder(registro_transferencia.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("CANTIDAD EXCEDIDA.")
                    .setNegativeButton("CERRAR", null).show();
        }
        else if (tipo_huevo==null){
            new AlertDialog.Builder(registro_transferencia.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("SELECCIONE EL TIPO DE HUEVO")
                    .setNegativeButton("CERRAR", null).show();
        }
        else if (Integer.parseInt(txt_cantidad_trans.getText().toString())==0){
            new AlertDialog.Builder(registro_transferencia.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("CANTIDAD INGRESADA DEBE SER MAYOR A CERO")
                    .setNegativeButton("CERRAR", null).show();
        }

        else {




if (listatransferencia.size()>0){
     int registro_duplicado=0;

    for (int i = 0; i < listatransferencia.size(); i++)
    {
        if (listatransferencia.get(i).geticodigo().equals(tipo_huevo))
        {
            registro_duplicado++;
        }

    }

    if(registro_duplicado>0){
        new AlertDialog.Builder(registro_transferencia.this)
                .setTitle("ATENCION!!!")
                .setMessage("CODIGO DE HUEVO DUPLICADO")
                .setNegativeButton("CERRAR", null).show();
    }
    else {
        grilla_transferencia_agregar=new grilla_transferencia_agregar();
        grilla_transferencia_agregar.setcodigo(tipo_huevo);
        grilla_transferencia_agregar.setcantidad(Integer.parseInt(txt_cantidad_trans.getText().toString()));
        grilla_transferencia_agregar.setnombre_huevo(nombre_huevo);

        listatransferencia.add(grilla_transferencia_agregar);
        txt_cantidad_trans.setText("");
        txt_spinner_tipo.setText("SELECCIONE TIPO DE HUEVO");
        txt_cant_disponible_transferencia.setText("0");
        tipo_huevo=null;

    }



}
else {

    grilla_transferencia_agregar=new grilla_transferencia_agregar();
    grilla_transferencia_agregar.setcodigo(tipo_huevo);
    grilla_transferencia_agregar.setcantidad(Integer.parseInt(txt_cantidad_trans.getText().toString()));
    grilla_transferencia_agregar.setnombre_huevo(nombre_huevo);

    listatransferencia.add(grilla_transferencia_agregar);
    txt_cantidad_trans.setText("");
    txt_spinner_tipo.setText("SELECCIONE TIPO DE HUEVO");
    txt_cant_disponible_transferencia.setText("0");
    tipo_huevo=null;
}


     }


    }

    private     void refrescar_lv(Integer tipo_fila){
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.simple_list_item_2, R.id.text1, listatransferencia) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(R.id.text1);
                TextView text2 = (TextView) view.findViewById(R.id.text2);


                text1.setText(listatransferencia.get(position).getnombre_huevo());
                text2.setText(""+listatransferencia.get(position).getcantidad());
                return view;
            }
        };

        if(tipo_fila==0){
            listatransferencia.clear();
            adapter.notifyDataSetChanged();
        }
        else{
            lv_transferencia.setAdapter(adapter);
        }
    }

    public      void registrar(View view)  {

        if(destino==null){
            new AlertDialog.Builder(registro_transferencia.this)
                .setTitle("ATENCION!!!")
                .setMessage("DEBE INGRESAR EL DESTINO")
                .setNegativeButton("CERRAR", null).show();
        }
        else if (listatransferencia.size()==0){

            new AlertDialog.Builder(registro_transferencia.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("NO HA INGRESADO DATOS A LA GRILLA")
                    .setNegativeButton("CERRAR", null).show();
        }

        else {


            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("REGISTRO DE TRANSFERENCIA.")
                    .setMessage("DESEA REGISTRAR LA TRANSFERENCIA?.")
                    .setPositiveButton("SI", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            registrar();
                        }
                    })
                    .setNegativeButton("NO", null)
                    .show();
                } // AQUI TERMINA EL ELSE.



            }

    private     void registrar(){

                String contenedor_grilla="";
                int c=0;
                for (int i = 0; i < listatransferencia.size(); i++)
                {
                    if(c==0) {

                        contenedor_grilla=   listatransferencia.get(i).geticodigo()+"_"+listatransferencia.get(i).getcantidad();
                    }
                    else{

                        contenedor_grilla=contenedor_grilla+","+   listatransferencia.get(i).geticodigo()+"_"+listatransferencia.get(i).getcantidad();
                    }
                    c++;
                }

                AsyncHttpClient client = new AsyncHttpClient();

                RequestParams params = new RequestParams();
                params.put("txtfecha", txt_fecha_trans.getText());
                params.put("destino", destino);
                params.put("txtDeposito", txt_spinner_deposito.getText().toString().trim());
                params.put("grilla", contenedor_grilla);
                params.put("txtpassword", datos_usuario.pass);
                params.put("txtusuario", datos_usuario.usuario);
                params.put("txtComentarios", txt_comentario.getText().toString());
                client.setTimeout(800000);
                client.post("http://192.168.6.162/ws/control_transferencias.aspx", params, new TextHttpResponseHandler() {
                            @Override
                            public void onStart()   {
                                progress = ProgressDialog.show(registro_transferencia.this, "REGISTRANDO",
                                        "ESPERE...", true);
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String res)
                            {
                                if (statusCode == 200) {
                                    try {
                                        JSONObject respuesta_json =new JSONObject(res);
                                        band=respuesta_json.getInt("band");
                                        mensaje=respuesta_json.getString("mensaje");
                                        doc_entry= Integer.parseInt(respuesta_json.getString("docentry"));

                                    }
                                    catch (Exception e){
                                        band=0;
                                        mensaje=e.toString();
                                    }
                                }
                            }

                            @Override
                            public void onFinish()
                            {
                                if(band==0){
                                    new AlertDialog.Builder(registro_transferencia.this)
                                            .setTitle("ATENCION!!!")
                                            .setMessage(mensaje)
                                            .setNegativeButton("CERRAR", null).show();
                                }
                                else if(band==1){

                                        new AlertDialog.Builder(registro_transferencia.this)
                                            .setTitle("INFORMACION")
                                            .setCancelable(false)
                                            .setMessage("TRANSFERENCIA NRO."+doc_entry+ " REALIZADA CON EXITO")
                                            .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                   // Descarga_PDF(doc_entry);
                                                    finish();
                                                    Intent i=new Intent(getApplicationContext(),menu_principal.class);
                                                    startActivity(i);
                                                }
                                            }).show();
                                }
                                progress.dismiss();
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String res, Throwable t)
                            {
                                progress.dismiss();
                                new AlertDialog.Builder(registro_transferencia.this)
                                        .setTitle("ATENCION!!!")
                                        .setMessage("ERROR DE CONEXION, FAVOR INTENTE DE NUEVO.")
                                        .setNegativeButton("CERRAR", null).show();
                            }
                        }
                );


            }

    private     void Descarga_PDF(Integer codigo){

        File path = Environment.getExternalStoragePublicDirectory( "/contenedor_reportes");
        File file = new File(path, "transferencia.pdf");
        if(file.exists()){
            file.delete();
        }
        String url = "http://192.168.6.162:8086/WebServices_reportes/control.jsp?codigo="+codigo;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Descargando...");
        request.setTitle("Reportes impresion");
        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir("/contenedor_reportes", "transferencia.pdf");

        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        manager.enqueue(request);

    }

    private     void cargar_depositos(){

        String codigo="AVE001,CEN007";
        String [] arr_codigo=codigo.split(",");

        for(int i=0; i < arr_codigo.length; i++){
            deposito p= new deposito();
            p.setnombre(arr_codigo[i]);
            lista_deposito.add(p);
            list_deposito_text.add(arr_codigo[i]);
        }

        sipinnerDialog_deposito = new SpinnerDialogCustom(registro_transferencia.this,list_deposito_text,"SELECCION DEPOSITO");
        cargar_tipo();
        }
}