package com.example.averiados;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Entidades.tipo_huevo;
import Entidades.destino;
import Entidades.grilla_transformacion_agregar;
import Entidades.lote;
import Utilidades.Utilidades;
import cz.msebera.android.httpclient.Header;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;

public class registro_transformacion extends AppCompatActivity {
    grilla_transformacion_agregar grilla_transformacion_agregar=null;
    ListView lv_transformacion;
    List<destino> lista_combo_destino = new ArrayList<>();
    List<tipo_huevo> lista_combo_tipo_huevo = new ArrayList<>();

    ProgressDialog progress;
    SpinnerDialogCustom  sipinnerDialog_huevo,sipinnerDialog_deposito,sipinnerDialog_a_tipo_huevo;
    TextView txt_huevo_transformacion,txt_fecha_transformacion,txt_cant_disponible_transformacion,txt_cantidad_transformacion,txt_transformacion_deposito,txt_a_tipo_huevo;
    DatePickerDialog picker;
    ArrayList<String> list_tipo_text = new ArrayList<String>();
    ArrayList<String> list_deposito_text = new ArrayList<String>();
    ArrayList<String> list_a_tipo_text = new ArrayList<String>();
    String cod_tipo_huevo=null;
    String item_code=null;
    int band=0;
    String mensaje="";
     List<lote> lista_tipo = new ArrayList<>();
    List<tipo_huevo> lista_tipo_huevo = new ArrayList<>();
    ArrayList<grilla_transformacion_agregar> lista_grilla;

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("VOLVER ATRAS.")
                .setMessage("SE PERDERAN TODOS LOS DATOS.")
                .setPositiveButton("SI", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                        Intent List = new Intent(registro_transformacion.this, menu_principal.class);
                        startActivity(List);
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_transformacion);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        txt_huevo_transformacion=(TextView)findViewById(R.id.txt_huevo_transformacion);
        txt_fecha_transformacion=(TextView)findViewById(R.id.txt_fecha_transformacion);
        txt_cant_disponible_transformacion=(TextView)findViewById(R.id.txt_cant_disponible_transformacion);
        txt_cantidad_transformacion=(TextView)findViewById(R.id.txt_cantidad_transformacion);
        txt_transformacion_deposito=(TextView)findViewById(R.id.txt_transformacion_deposito);
        txt_a_tipo_huevo=(TextView)findViewById(R.id.txt_a_tipo_huevo);
        lv_transformacion=(ListView) findViewById(R.id.lv_transformacion);
        getSupportActionBar().setTitle("TRANSFORMACION     USUARIO: "+datos_usuario.nombre_usuario);
        txt_huevo_transformacion.setVisibility(View.GONE);
        lista_grilla=new ArrayList<grilla_transformacion_agregar>();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = sdf.format(c.getTime());
        txt_fecha_transformacion.setText(strDate);

        cargar_depositos();
        cargar_tipo_huevos_a_transformar();
        txt_huevo_transformacion.setOnClickListener(new View.OnClickListener() {  @Override public void onClick(View v) {
            sipinnerDialog_huevo.dTitle="SELECCION DE TIPO DE HUEVO";
            sipinnerDialog_huevo.showSpinerDialog();

                                    sipinnerDialog_huevo.bindOnSpinerListener(new OnSpinerItemClick() {
                                        @Override
                                        public void onClick(String s, int i) {
                                            txt_huevo_transformacion.setText(lista_tipo.get(i).getDistnumber());
                                            item_code= lista_tipo.get(i).getItemcode() ;
                                            txt_cant_disponible_transformacion.setText(String.valueOf(lista_tipo.get(i).getQuantity()));
                                        }
                                    });
        } } );

        txt_cantidad_transformacion.setOnKeyListener(new View.OnKeyListener()
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

                            llenar_grilla_final();
                    }
                }
                return false;
            }
        });
        txt_transformacion_deposito.setOnClickListener(new View.OnClickListener() {  @Override public void onClick(View v) {
            sipinnerDialog_deposito.dTitle="SELECCION DE DEPOSITO";
            sipinnerDialog_deposito.showSpinerDialog();

            sipinnerDialog_deposito.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String s, int i) {

                    txt_transformacion_deposito.setText(lista_combo_destino.get(i).getnombre());
                    refrescar_lv(0);//SI ES CERO ENTONCES LIMPIA LA GRILLA.
                    txt_huevo_transformacion.setText("SELECCIONE TIPO DE HUEVO");
                    txt_huevo_transformacion.setVisibility(View.GONE);
                    txt_cant_disponible_transformacion.setText("0");
                }
            });
        } } );


        txt_a_tipo_huevo.setOnClickListener(new View.OnClickListener() {  @Override public void onClick(View v) {
            sipinnerDialog_a_tipo_huevo.dTitle="SELECCION DE TIPO DE HUEVO";
            sipinnerDialog_a_tipo_huevo.showSpinerDialog();

            sipinnerDialog_a_tipo_huevo.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String s, int i) {

                    txt_a_tipo_huevo.setText(lista_combo_tipo_huevo.get(i).getnombre());
                    cod_tipo_huevo=lista_combo_tipo_huevo.get(i).geticodigo();
                    cargar_tipos_disponibles();
                }
            });
        } } );

        lv_transformacion.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { @Override  public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            final int posicion=i;
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(registro_transformacion.this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("¿ Eliminar esta fila ?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    lista_grilla.remove(posicion);
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

    }

    public void  agregar_fila(View v){
        llenar_grilla_final();
  }

    public void buscar_fecha(View v){

      final Calendar cldr = Calendar.getInstance();
      int day = cldr.get(Calendar.DAY_OF_MONTH);
      int month = cldr.get(Calendar.MONTH);
      int year = cldr.get(Calendar.YEAR);
      // date picker dialog
      picker = new DatePickerDialog(registro_transformacion.this,
              new DatePickerDialog.OnDateSetListener() {
                  @Override
                  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                      DecimalFormat df = new DecimalFormat("00");
                      SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");

                      cldr.set(year, monthOfYear, dayOfMonth);
                      String strDate = format.format(cldr.getTime());
                      txt_fecha_transformacion.setText(df.format((dayOfMonth))+ "/" + df.format((monthOfYear + 1))  +"/"+ year  );



                  }
              }, year, month, day);
      picker.show();
  }

    private void refrescar_lv ( int tipo_fila){
        ArrayAdapter adapter = new ArrayAdapter(registro_transformacion.this, R.layout.simple_list_item_2, R.id.text1, lista_grilla) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(R.id.text1);
                TextView text2 = (TextView) view.findViewById(R.id.text2);
                text1.setText(lista_grilla.get(position).getNombre_huevo());
                text2.setText(""+lista_grilla.get(position).getCantidad());
                return view;
            }
        };
        if(tipo_fila==0){
            lista_grilla.clear();
            adapter.notifyDataSetChanged();
        }
        else{
            lv_transformacion.setAdapter(adapter);
        }
        txt_huevo_transformacion.setText("SELECCIONE TIPO DE HUEVO");
    }

    private void cargar_tipos_disponibles(){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("txtDeposito",txt_transformacion_deposito.getText().toString().trim());
        params.put("itemcode",cod_tipo_huevo);

        client.post("http://"+ Utilidades.IP+"/ws/Test/consulta_stock_averiados.aspx", params, new TextHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        progress = ProgressDialog.show(registro_transformacion.this, "CONSULTANDO DATOS", "ESPERE...", true);
                        txt_huevo_transformacion.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        if (statusCode == 200) {
                            try
                            {
                                list_tipo_text.clear();//LIMPIAR LA LISTA ARRAY PARA COMENZAR DE NUEVO
                                lista_tipo.clear();
                                JSONObject respuesta_json =new JSONObject(res);
                                JSONArray jsonArray_list = respuesta_json.getJSONArray("contenido_grilla");
                                 for (int i=0; i<jsonArray_list.length();i++)
                                {
                                    lote p= new lote();
                                    list_tipo_text.add(jsonArray_list.getJSONObject(i).getString("itemname")+" CANTIDAD: "+jsonArray_list.getJSONObject(i).getString("onhand"));
                                    p.setDistnumber(jsonArray_list.getJSONObject(i).getString("itemname"));
                                    p.setItemcode( jsonArray_list.getJSONObject(i).getString("itemcode") );
                                    p.setQuantity(Integer.parseInt(jsonArray_list.getJSONObject(i).getString("onhand")));
                                    lista_tipo.add(p);
                                }
                                txt_huevo_transformacion.setText("SELECCIONE TIPO DE HUEVO");
                                txt_cant_disponible_transformacion.setText("0");
                                sipinnerDialog_huevo = new SpinnerDialogCustom(registro_transformacion.this,list_tipo_text,"SELECCION DE TIPO DE HUEVO");
                            }
                            catch (Exception e)
                            {
                                new AlertDialog.Builder(registro_transformacion.this)
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
                        txt_huevo_transformacion.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        progress.dismiss();
                        new AlertDialog.Builder(registro_transformacion.this)
                                .setTitle("ATENCION!!!")
                                .setMessage("ERROR DE CONEXION, FAVOR INTENTE DE NUEVOs.")
                                .setNegativeButton("CERRAR", null).show();
                        txt_huevo_transformacion.setVisibility(View.INVISIBLE);
                    }
                }
        );
    }

    public void registrar(View v){

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("REGISTRAR CAMBIO.")
                .setMessage("DESEA REGISTRAR EL CAMBIO?.")
                .setPositiveButton("SI", new DialogInterface.OnClickListener()
                {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progress = ProgressDialog.show(registro_transformacion.this, "REGISTRANDO",
                                "ESPERE...", true);


                          if (lista_grilla.size()==0){
                            progress.dismiss();
                            new AlertDialog.Builder(registro_transformacion.this)
                                    .setTitle("ATENCION!!!")
                                    .setMessage("NO HA INGRESADO DATOS A LA GRILLA")
                                    .setNegativeButton("CERRAR", null).show();
                        }

                        else {
                            RequestQueue queue = Volley.newRequestQueue(registro_transformacion.this);
                            String url ="http://"+ Utilidades.IP+"/ws/Test/control_transformaciones.aspx";
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
                                            new AlertDialog.Builder(registro_transformacion.this)
                                                    .setTitle("ATENCION!")
                                                    .setMessage(mensaje)
                                                    .setNegativeButton("CERRAR", null).show();
                                        }
                                        else if(band==1){
                                            new AlertDialog.Builder(registro_transformacion.this)
                                                    .setTitle("INFORMACION")
                                                    .setCancelable(false)
                                                    .setMessage("TRANSFORMACION NRO."+doc_entry+ "CON EXITO")
                                                    .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener()
                                                    {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                          //  Descarga_PDF(doc_entry);
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
                                        new AlertDialog.Builder(registro_transformacion.this)
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
                                                new AlertDialog.Builder(registro_transformacion.this)
                                                        .setTitle("ATENCION!!")
                                                        .setMessage(error.toString() )
                                                        .setNegativeButton("CERRAR", null).show();
                                                progress.dismiss();
                                            }
                                            catch (Exception e)
                                            {

                                                new AlertDialog.Builder(registro_transformacion.this)
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
                                    for (int i = 0; i < lista_grilla.size(); i++)
                                    {
                                        String codigo=lista_grilla.get(i).getCodigo();
                                        if(c==0) {

                                            contenedor_grilla=   codigo+"_"+lista_grilla.get(i).getCantidad();
                                        }
                                        else{

                                            contenedor_grilla=contenedor_grilla+","+ codigo+"_"+lista_grilla.get(i).getCantidad();
                                        }
                                        c++;
                                    }
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("txtfecha", txt_fecha_transformacion.getText().toString());
                                    params.put("destino", txt_transformacion_deposito.getText().toString());
                                    params.put("tipo_huevo", cod_tipo_huevo);
                                    params.put("grilla", contenedor_grilla);
                                    params.put("txtpassword", datos_usuario.pass);
                                    params.put("txtusuario", datos_usuario.usuario);

                                    return params;
                                }
                            };
                            strRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    800000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            queue.add(strRequest);
                        }
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

    private void cargar_depositos(){

        String codigo="AVE001,CEN007";
        String [] arr_codigo=codigo.split(",");

        for(int i=0; i < arr_codigo.length; i++){
            destino p= new destino();
            p.setnombre(arr_codigo[i]);
            lista_combo_destino.add(p);
           list_deposito_text.add(arr_codigo[i]);
        }

        sipinnerDialog_deposito = new SpinnerDialogCustom(registro_transformacion.this,list_deposito_text,"SELECCION DEPOSITO");
    }


    private void cargar_tipo_huevos_a_transformar(){


        String nombre="HUEVO TIPO GIGANTE,HUEVO TIPO JUMBO,HUEVO TIPO SUPER,HUEVO TIPO A,HUEVO TIPO B,HUEVO TIPO C,HUEVO TIPO D,HUEVO TIPO PICADO";
        String codigo="1,2,3,4,5,6,7,8";
        String [] arr_codigo=codigo.split(",");
        String [] arr_nombre=nombre.split(",");

        for(int i=0; i < arr_codigo.length; i++){
            tipo_huevo th= new tipo_huevo();
            th.setcodigo(arr_codigo[i]);
            th.setnombre(arr_nombre[i]);

            lista_combo_tipo_huevo.add(th);

            list_a_tipo_text.add(arr_nombre[i]);
        }

        sipinnerDialog_a_tipo_huevo = new SpinnerDialogCustom(registro_transformacion.this,list_a_tipo_text,"SELECCION DE TIPO DE HUEVO");
    }


    private  void llenar_grilla_final (){


        if(txt_cantidad_transformacion.getText().length()==0){
            new AlertDialog.Builder(registro_transformacion.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("INGRESE LA CANTIDAD")
                    .setNegativeButton("CERRAR", null).show();
        }
        else if(Integer.parseInt(txt_cant_disponible_transformacion.getText().toString())<Integer.parseInt(txt_cantidad_transformacion.getText().toString())){
            new AlertDialog.Builder(registro_transformacion.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("CANTIDAD EXCEDIDA ")
                    .setNegativeButton("CERRAR", null).show();
        }

        else if (Integer.parseInt(txt_cantidad_transformacion.getText().toString())==0){
            new AlertDialog.Builder(registro_transformacion.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("CANTIDAD INGRESADA DEBE SER MAYOR A CERO")
                    .setNegativeButton("CERRAR", null).show();
        }
        else if (txt_huevo_transformacion.getText().length()==0){
            new AlertDialog.Builder(registro_transformacion.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("INGRESE LOTE")
                    .setNegativeButton("CERRAR", null).show();
        }
        else {

            if (lista_grilla.size()>0){
                int registro_duplicado=0;
                for (int i = 0; i < lista_grilla.size(); i++)
                {
                    if (lista_grilla.get(i).getCodigo().equals(item_code))
                    {
                        registro_duplicado++;
                    }
                }

                if(registro_duplicado>0){
                    new AlertDialog.Builder(registro_transformacion.this)
                            .setTitle("ATENCION!!!")
                            .setMessage("CODIGO DE HUEVO DUPLICADO")
                            .setNegativeButton("CERRAR", null).show();
                }

                else {
                    grilla_transformacion_agregar=new grilla_transformacion_agregar();
                    grilla_transformacion_agregar.setCantidad(Integer.parseInt(txt_cantidad_transformacion.getText().toString()));
                    grilla_transformacion_agregar.setCodigo(item_code);
                    grilla_transformacion_agregar.setNombre_huevo(txt_huevo_transformacion.getText().toString());
                    lista_grilla.add(grilla_transformacion_agregar);
                    txt_cantidad_transformacion.setText("");
                    txt_huevo_transformacion.setHint("SELECCIONE TIPO DE HUEVO");
                    item_code=null;
                    txt_huevo_transformacion.setText("");
                    txt_cant_disponible_transformacion.setText("0");
                    txt_cantidad_transformacion.setText("");
                }
            }
            else {

                grilla_transformacion_agregar=new grilla_transformacion_agregar();
                grilla_transformacion_agregar.setCantidad(Integer.parseInt(txt_cantidad_transformacion.getText().toString()));
                grilla_transformacion_agregar.setCodigo(item_code);
                grilla_transformacion_agregar.setNombre_huevo(txt_huevo_transformacion.getText().toString());
                lista_grilla.add(grilla_transformacion_agregar);
                txt_cant_disponible_transformacion.setText("");
                txt_huevo_transformacion.setHint("SELECCIONE TIPO DE HUEVO");
                item_code=null;
                txt_cant_disponible_transformacion.setText("0");
                txt_cantidad_transformacion.setText("");
            }
        }

        refrescar_lv(1);
    }
}

