package com.example.averiados;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Entidades.destino;
import Entidades.lote;
import Entidades.grilla_transferencia_agregar;
import Entidades.tipo_huevo;
import cz.msebera.android.httpclient.Header;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
public class registro_cambio extends AppCompatActivity {
    ListView lv_cambio;
    grilla_transferencia_agregar grilla_transferencia_agregar=null;
    DatePickerDialog picker;
    TextView txt_cantidad_trans,txt_fecha_trans,txt_spinner_huevo,txt_spinner_deposito,txt_spinner_lote,txt_cantidad_disponible,txt_cant_deposito;
    Button btn_cargar,btn_fecha;
    int cantidad_validacion_grilla=0;
    String tipo_huevo=null;
    int band=0;
    String nombre_huevo="";
    String destino="";
    String mensaje="";
    ArrayList<grilla_transferencia_agregar> listacambio;
    ArrayList<String> list_tipo_text = new ArrayList<String>();
    ArrayList<String> list_deposito_text = new ArrayList<String>();
    ArrayList<String> list_lote_text = new ArrayList<String>();
    List<destino> lista_destino = new ArrayList<>();
    List<lote> sub_lista_lote  = new ArrayList<>();
    List<tipo_huevo> lista_tipo = new ArrayList<>();
    List<lote> lista_lote  = new ArrayList<>();
    ProgressDialog progress;
    ConexionSQLiteHelper conn;
    SpinnerDialogCustom  sipinnerDialog_huevo,sipinnerDialog_deposito,sipinnerDialog_lote;

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
        setContentView(R.layout.registro_cambio_directo);
        getSupportActionBar().setTitle("CAMBIO DIRECTO     USUARIO: "+datos_usuario.nombre_usuario);
        conn=new ConexionSQLiteHelper(getApplicationContext(),"bd_usuarios",null,1);
        txt_spinner_deposito=(TextView)findViewById(R.id.txt_spinner_deposito);
        txt_spinner_huevo=(TextView) findViewById(R.id.txt_huevo);
        txt_spinner_lote=(TextView) findViewById(R.id.txt_spinner_lote);
        txt_cantidad_trans=(TextView) findViewById(R.id.txt_cantidad_cambio);
        txt_cant_deposito=(TextView) findViewById(R.id.txt_cant_deposito);
        txt_cantidad_disponible=(TextView) findViewById(R.id.txt_cant_disponible);
        txt_fecha_trans=(TextView) findViewById(R.id.txt_fecha_cambio);
        btn_cargar=(Button) findViewById(R.id.btn_ingresar_cambio);
        btn_fecha=(Button) findViewById(R.id.btn_fecha_cambio );
        lv_cambio=(ListView)findViewById(R.id.lv_cambio);
        listacambio=new ArrayList<grilla_transferencia_agregar>();

        txt_spinner_huevo.setVisibility(View.INVISIBLE);
        txt_spinner_lote.setVisibility(View.INVISIBLE);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = sdf.format(c.getTime());
        txt_fecha_trans.setText(strDate);
        cargar_destino();

        txt_spinner_deposito.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
                sipinnerDialog_deposito.dTitle="SELECCION DE DEPOSITO";
                sipinnerDialog_deposito.showSpinerDialog();
                                            } });
        txt_spinner_huevo.setOnClickListener(new View.OnClickListener() {  @Override public void onClick(View v) {
        sipinnerDialog_huevo.dTitle="SELECCIONAR HUEVO";
        sipinnerDialog_huevo.showSpinerDialog();
        sipinnerDialog_lote = new SpinnerDialogCustom(registro_cambio.this,list_lote_text,"SELECCION DE LOTE");
            sipinnerDialog_lote.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String s, int i) {

                    txt_spinner_lote.setText(sub_lista_lote.get(i).getDistnumber());
                    cantidad_validacion_grilla=sub_lista_lote.get(i).getQuantity();
                    txt_cantidad_disponible.setText(String.valueOf(cantidad_validacion_grilla));


                }
            });

            } } );
        txt_spinner_lote.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View v) {
                sipinnerDialog_lote.dTitle="SELECCION DE LOTE";
                sipinnerDialog_lote.showSpinerDialog();



            } }  );
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

                            llenar_grilla();
                    }
                }
                return false;
            }
        });
        btn_cargar.setOnClickListener(new View.OnClickListener() {  @Override public void onClick(View v) {  llenar_grilla();  } } );
        btn_fecha.setOnClickListener(new View.OnClickListener()  {   public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(registro_cambio.this,
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


            } });

        lv_cambio.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { @Override  public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int posicion=i;

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(registro_cambio.this);
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿ Eliminar esta fila ?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        listacambio.remove(posicion);
                        refrescar_lv(1);
                                                                                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                    }
                });
                dialogo1.show();

                return false;
            } });

        sipinnerDialog_deposito = new SpinnerDialogCustom(registro_cambio.this,list_deposito_text,"SELECCION DE DEPOSITO");
        sipinnerDialog_deposito.bindOnSpinerListener(new OnSpinerItemClick() {  @Override public void onClick(String s, int i) {
            txt_spinner_deposito.setText( lista_destino.get(i).getnombre());
            destino= lista_destino.get(i).geticodigo();
            buscar_lotes_ws();  } });
    }

    private  void cargar_destino() {
         SQLiteDatabase db=conn.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM depositos "   ,null);
        while (cursor.moveToNext())
        {
            destino p= new destino();
            list_deposito_text.add(cursor.getString(1));
            p.setcodigo(cursor.getString(0));
            p.setnombre(cursor.getString(1));
            lista_destino.add(p);
          }
        cursor.close();

    }

    private  void llenar_grilla(){
        if(txt_cantidad_trans.getText().length()==0){
            new AlertDialog.Builder(registro_cambio.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("INGRESE LA CANTIDAD")
                    .setNegativeButton("CERRAR", null).show();
        }
        else if(cantidad_validacion_grilla<Integer.parseInt(txt_cantidad_trans.getText().toString())){
            new AlertDialog.Builder(registro_cambio.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("CANTIDAD EXCEDIDA PARA EL LOTE "+txt_spinner_lote.getText())
                    .setNegativeButton("CERRAR", null).show();
        }

        else if(Integer.parseInt(txt_cant_deposito.getText().toString())<Integer.parseInt(txt_cantidad_trans.getText().toString())){
            new AlertDialog.Builder(registro_cambio.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("CANTIDAD INSUFICIENTE EN DEPOSITO")
                    .setNegativeButton("CERRAR", null).show();
        }

        else if (Integer.parseInt(txt_cantidad_trans.getText().toString())==0){
            new AlertDialog.Builder(registro_cambio.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("CANTIDAD INGRESADA DEBE SER MAYOR A CERO")
                    .setNegativeButton("CERRAR", null).show();
        }
        else if (txt_spinner_lote.getText().length()==0){
            new AlertDialog.Builder(registro_cambio.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("INGRESE LOTE")
                    .setNegativeButton("CERRAR", null).show();
        }
        else {

    if (listacambio.size()>0){
            int registro_duplicado=0;
        for (int i = 0; i < listacambio.size(); i++)
        {
            if (listacambio.get(i).getnombre_huevo().equals(txt_spinner_lote.getText().toString()))
            {
            registro_duplicado++;
            }
        }

    if(registro_duplicado>0){
        new AlertDialog.Builder(registro_cambio.this)
                .setTitle("ATENCION!!!")
                .setMessage("LOTE DUPLICADO")
                .setNegativeButton("CERRAR", null).show();
    }

    else {
        grilla_transferencia_agregar=new grilla_transferencia_agregar();
        grilla_transferencia_agregar.setcantidad(Integer.parseInt(txt_cantidad_trans.getText().toString()));
        grilla_transferencia_agregar.setnombre_huevo(txt_spinner_lote.getText().toString());
        grilla_transferencia_agregar.setcodigo(tipo_huevo);
        listacambio.add(grilla_transferencia_agregar);
        txt_cantidad_trans.setText("");
        txt_spinner_huevo.setText("");
        txt_spinner_huevo.setHint("SELECCIONE TIPO DE HUEVO");
        tipo_huevo=null;
        nombre_huevo="";
        txt_spinner_lote.setText("");
        txt_cantidad_disponible.setText("0");
        txt_spinner_lote.setHint("SELECCIONE LOTE");
        txt_spinner_lote.setVisibility(View.INVISIBLE);
     }
                   }
    else {

    grilla_transferencia_agregar=new grilla_transferencia_agregar();
    grilla_transferencia_agregar.setcantidad(Integer.parseInt(txt_cantidad_trans.getText().toString()));
    grilla_transferencia_agregar.setnombre_huevo(txt_spinner_lote.getText().toString());
        grilla_transferencia_agregar.setcodigo(tipo_huevo);

        listacambio.add(grilla_transferencia_agregar);
    txt_cantidad_trans.setText("");
    txt_spinner_huevo.setText("");
    txt_spinner_huevo.setHint("SELECCIONE TIPO DE HUEVO");
    tipo_huevo=null;
    nombre_huevo="";
    txt_spinner_lote.setText("");
    txt_cantidad_disponible.setText("0");
    txt_spinner_lote.setHint("SELECCIONE LOTE");
    txt_spinner_lote.setVisibility(View.INVISIBLE);
        }
            }
        refrescar_lv(1);
        }

    private void refrescar_lv(Integer tipo_fila){
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.simple_list_item_2, R.id.text1, listacambio) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(R.id.text1);
                TextView text2 = (TextView) view.findViewById(R.id.text2);
                text1.setText(listacambio.get(position).getnombre_huevo());
                text2.setText(""+listacambio.get(position).getcantidad());
                return view;
            }
        };
        if(tipo_fila==0){
            listacambio.clear();
            adapter.notifyDataSetChanged();
        }
        else{
            lv_cambio.setAdapter(adapter);
        }
     }

    private void buscar_lotes_ws(){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("txtDeposito", destino);
        client.post("http://192.168.6.162/ws/control_select_lotes.aspx", params, new TextHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        progress = ProgressDialog.show(registro_cambio.this, "CONSULTANDO DATOS", "ESPERE...", true);
                        txt_spinner_huevo.setVisibility(View.INVISIBLE);
                        txt_spinner_lote.setVisibility(View.INVISIBLE);
                        sub_lista_lote.clear();
                        txt_cantidad_disponible.setText("0");
                        refrescar_lv(0);//SI ES CERO ENTONCES LIMPIA LA GRILLA.
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        if (statusCode == 200) {
                              try
                            {
                                lista_lote.clear();//LIMPIAR LA LISTA ARRAY PARA COMENZAR DE NUEVO
                                list_lote_text.clear();//LIMPIAR LA LISTA ARRAY PARA COMENZAR DE NUEVO
                                lista_tipo.clear();//LIMPIAR LA LISTA ARRAY PARA COMENZAR DE NUEVO
                                list_tipo_text.clear();//LIMPIAR LA LISTA ARRAY PARA COMENZAR DE NUEVO
                                JSONObject respuesta_json =new JSONObject(res);
                                JSONArray jsonArray_list = respuesta_json.getJSONArray("contenido_grilla");
                                JSONArray jarr_huevos_disp = respuesta_json.getJSONArray("contenido_huevos_disponibles");
                                for (int i=0; i<jsonArray_list.length();i++)
                                {
                                    lote p= new lote();
                                    list_lote_text.add("LOTE: "+jsonArray_list.getJSONObject(i).getString("distnumber")+" CANTIDAD: "+jsonArray_list.getJSONObject(i).getString("quantity"));
                                    p.setDistnumber(jsonArray_list.getJSONObject(i).getString("distnumber"));
                                    p.setItemcode(jsonArray_list.getJSONObject(i).getString("itemcode"));
                                    p.setQuantity(Integer.parseInt(jsonArray_list.getJSONObject(i).getString("quantity")));
                                    lista_lote.add(p);
                                }
                                for (int i=0; i<jarr_huevos_disp.length();i++){
                                    tipo_huevo th=new tipo_huevo();
                                    list_tipo_text.add( jarr_huevos_disp.getJSONObject(i).getString("nombre"));
                                    th.setcodigo(jarr_huevos_disp.getJSONObject(i).getString("codigo"));
                                    th.setnombre(jarr_huevos_disp.getJSONObject(i).getString("nombre"));
                                    th.setcantidad(jarr_huevos_disp.getJSONObject(i).getString("cantidad"));
                                    lista_tipo.add(th);
                                }
                                sipinnerDialog_huevo = new SpinnerDialogCustom(registro_cambio.this,list_tipo_text,"SELECCION DE TIPO DE HUEVO");
                                sipinnerDialog_huevo.bindOnSpinerListener(new OnSpinerItemClick() {
                                    @Override
                                    public void onClick(String s, int i) {
                                        txt_spinner_huevo.setText(s);
                                        tipo_huevo= lista_tipo.get(i).geticodigo() ;
                                        nombre_huevo=lista_tipo.get(i).getnombre();
                                        txt_spinner_lote.setVisibility(View.VISIBLE);
                                        txt_cant_deposito.setText(lista_tipo.get(i).getcantidad().toString());
                                        sub_filtro_lote(tipo_huevo);
                                    }
                                });
                            }
                            catch (Exception e)
                            {
                                new AlertDialog.Builder(registro_cambio.this)
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
                        txt_spinner_huevo.setVisibility(View.VISIBLE);

                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        progress.dismiss();
                        new AlertDialog.Builder(registro_cambio.this)
                                .setTitle("ATENCION!!!")
                                .setMessage("ERROR DE CONEXION, FAVOR INTENTE DE NUEVO.")
                                .setNegativeButton("CERRAR", null).show();

                        txt_spinner_huevo.setVisibility(View.INVISIBLE);
                        txt_spinner_lote.setVisibility(View.INVISIBLE);

                    }
                }
        );
    }

    private void sub_filtro_lote(String codigo){
    sub_lista_lote.clear();
    list_lote_text.clear();
    for(int i=0; i < lista_lote.size(); i++){
        if( lista_lote.get(i).getItemcode().equals(codigo)){
            lote param= new lote();
            list_lote_text.add("LOTE: "+lista_lote.get(i).getDistnumber()+" CANTIDAD: "+lista_lote.get(i).getQuantity());
            param.setDistnumber(lista_lote.get(i).getDistnumber());
            param.setItemcode(lista_lote.get(i).getItemcode());
            param.setQuantity(lista_lote.get(i).getQuantity());
            sub_lista_lote.add(param);              }
                                            }
                                             }

    private  void Descarga_PDF(Integer codigo){

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

    public void registrar(View view) {


        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("REGISTRAR CAMBIO.")
                .setMessage("DESEA REGISTRAR EL CAMBIO?.")
                .setPositiveButton("SI", new DialogInterface.OnClickListener()
                {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progress = ProgressDialog.show(registro_cambio.this, "REGISTRANDO",
                                "ESPERE...", true);

                        if(destino.length()==0){
                            progress.dismiss();
                            new AlertDialog.Builder(registro_cambio.this)
                                    .setTitle("ATENCION!!!")
                                    .setMessage("DEBE INGRESAR EL DESTINO")
                                    .setNegativeButton("CERRAR", null).show();
                        }
                        else if (listacambio.size()==0){
                            progress.dismiss();
                            new AlertDialog.Builder(registro_cambio.this)
                                    .setTitle("ATENCION!!!")
                                    .setMessage("NO HA INGRESADO DATOS A LA GRILLA")
                                    .setNegativeButton("CERRAR", null).show();
                        }

                        else {
                            RequestQueue queue = Volley.newRequestQueue(registro_cambio.this);
                            String url ="http://192.168.6.162/ws/control_tr_devoluciones.aspx";
                            StringRequest strRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>()
                             {
                                @Override
                                public void onResponse(String response)
                                {
                                    try {
                                        JSONObject respuesta_json =new JSONObject(response);
                                        band=respuesta_json.getInt("band");
                                        mensaje=respuesta_json.getString("mensaje");
                                        final int doc_entry= Integer.parseInt(respuesta_json.getString("docentry"));
                                         if(band==0){
                                            new AlertDialog.Builder(registro_cambio.this)
                                                    .setTitle("ATENCION!")
                                                    .setMessage(mensaje)
                                                    .setNegativeButton("CERRAR", null).show();
                                                    }
                                        else if(band==1){
                                            new AlertDialog.Builder(registro_cambio.this)
                                                    .setTitle("INFORMACION")
                                                    .setCancelable(false)
                                                    .setMessage("CAMBIO NRO."+doc_entry+ "REALIZADO CON EXITO")
                                                    .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener()
                                                    {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //Descarga_PDF(doc_entry);
                                                            finish();
                                                            Intent i=new Intent(getApplicationContext(),menu_principal.class);
                                                            startActivity(i);
                                                        }
                                                    }).show();
                                        }
                                        progress.dismiss();
                                  }
                                    catch (Exception e)
                                    {
                                        progress.dismiss();
                                        new AlertDialog.Builder(registro_cambio.this)
                                                .setTitle("ATENCION!!")
                                                .setMessage(e.toString())
                                                .setNegativeButton("CERRAR", null).show();
                                    }
                                }
                            },
                                    new Response.ErrorListener()
                                    {
                                        @Override
                                        public void onErrorResponse(VolleyError error)
                                        {
                                            try {
                                                new AlertDialog.Builder(registro_cambio.this)
                                                        .setTitle("ATENCION!!")
                                                        .setMessage(error.toString() )
                                                        .setNegativeButton("CERRAR", null).show();
                                                progress.dismiss();
                                            }
                                            catch (Exception e)
                                            {

                                                new AlertDialog.Builder(registro_cambio.this)
                                                        .setTitle("ATENCION!!")
                                                        .setMessage(e.toString() )
                                                        .setNegativeButton("CERRAR", null).show();
                                                progress.dismiss();
                                            }
                                        }
                                    })

                            {

                                @Override
                                protected Map<String, String> getParams()
                                {
                                    String contenedor_grilla="";
                                    int c=0;
                                    for (int i = 0; i < listacambio.size(); i++)
                                    {
                                        String codigo=listacambio.get(i).geticodigo();
                                        if(c==0) {

                                            contenedor_grilla=   codigo+"-"+listacambio.get(i).getcantidad()+"-"+listacambio.get(i).getnombre_huevo();
                                        }
                                        else{

                                            contenedor_grilla=contenedor_grilla+","+ codigo+"-"+listacambio.get(i).getcantidad()+"-"+listacambio.get(i).getnombre_huevo();
                                        }
                                        c++;
                                    }
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("txtfecha", txt_fecha_trans.getText().toString());
                                    params.put("destino", destino);
                                    params.put("grilla", contenedor_grilla);
                                    params.put("txtpassword", datos_usuario.pass);
                                    params.put("txtusuario", datos_usuario.usuario);

                                    return params;
                                }
                            };
                            strRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    400000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            queue.add(strRequest);
                        }
                    }
                })
            .setNegativeButton("NO", null)
                .show();
            }

}