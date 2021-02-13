package com.example.averiados;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.levitnudi.legacytableview.LegacyTableView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import Entidades.cliente;
import Entidades.destino;
import Entidades.grilla_desmontaje;
import Entidades.grilla_devolucion_agregar;
import Entidades.tipo_huevo;
import Entidades.motivo;
import Entidades.vendedores;
import Entidades.grilla_transferencia_agregar;
import cz.msebera.android.httpclient.Header;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;


public class registro_devolucion extends AppCompatActivity {
    SpinnerDialogCustom  sipinnerDialog_huevo,sipinnerDialog_motivo,sipinnerDialog_vendedor ,sipinnerDialog_deposito,sipinnerDialog_cliente,sipinnerDialog_responsable;
    ArrayList<String> list_tipo_huevo_text = new ArrayList<String>();
    ArrayList<String> list_motivo_text = new ArrayList<String>();
    ArrayList<String> list_vendedor_text = new ArrayList<String>();
    ArrayList<String> list_cliente_text = new ArrayList<String>();

    TextView txt_cantidad_devolucion,txt_fecha,txt_nro_nota,txt_spinner_tipo_huevo,txt_spinner_motivo,txt_spinner_vendedor,txt_spinner_deposito,txt_spinner_cliente,txt_spinner_responsable;
    ConexionSQLiteHelper conn;
    DatePickerDialog picker;
    ListView lv_devolucion;
    String codigo_cliente=null,nombre_cliente=null,codigo_tipo=null,nombre_tipo=null ,codigo_motivo=null,nombre_motivo=null,codigo_vendedor=null,codigo_responsable=null;
    grilla_devolucion_agregar grilla_devolucion_agregar=null;
    ArrayList<grilla_devolucion_agregar> listadevolucion;
    int band=0;
    String mensaje="";
    private ProgressDialog progress;
    List<cliente> lista_combo_responsable = new ArrayList<>();
    List<motivo> lista_motivo = new ArrayList<>();
    List<tipo_huevo> lista_tipo = new ArrayList<>();
    List<vendedores> lista_vendedores = new ArrayList<>();
    List<destino> lista_combo_destino = new ArrayList<>();
    List<cliente> lista_combo_cliente = new ArrayList<>();
    ArrayList<String> list_deposito_text = new ArrayList<String>();
    ArrayList<String> list_responsable_text = new ArrayList<String>();

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
        setContentView(R.layout.registro_devolucion);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        conn=new ConexionSQLiteHelper(getApplicationContext(),"bd_usuarios",null,1);
        getSupportActionBar().setTitle("DEVOLUCION     USUARIO: "+datos_usuario.nombre_usuario);
        txt_spinner_cliente=    (TextView) findViewById(R.id.txt_spinner_cliente);
        txt_spinner_motivo=     (TextView) findViewById(R.id.txt_spinner_motivo);
        txt_spinner_vendedor=   (TextView) findViewById(R.id.txt_spinner_vendedor);
        txt_spinner_responsable=(TextView) findViewById(R.id.txt_spinner_responsable);
        txt_spinner_tipo_huevo= (TextView) findViewById(R.id.txt_spinner_tipo_huevo);
        txt_spinner_deposito=   (TextView) findViewById(R.id.txt_spinner_deposito);
        lv_devolucion=          (ListView) findViewById(R.id.lv_devolucion);
        txt_cantidad_devolucion=(TextView) findViewById(R.id.txt_cantidad_devolucion);
        txt_nro_nota=           (TextView) findViewById(R.id.txt_nro_nota);
        txt_fecha=              (TextView) findViewById(R.id.txt_fecha_devolucion);
        listadevolucion=new ArrayList<grilla_devolucion_agregar>();

        cargar_todos();


        txt_spinner_cliente.setOnClickListener(new View.OnClickListener() {  @Override public void onClick(View v) {
            sipinnerDialog_cliente.dTitle="SELECCION DE CLIENTE";
            sipinnerDialog_cliente.showSpinerDialog();

            sipinnerDialog_cliente.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String s, int i) {

                    codigo_cliente= lista_combo_cliente.get(i).geticodigo();
                    nombre_cliente=lista_combo_cliente.get(i).getnombre();
                    txt_spinner_cliente.setText(lista_combo_cliente.get(i).getnombre());
                }
            });
        } } );



        txt_spinner_deposito.setOnClickListener(new View.OnClickListener() {  @Override public void onClick(View v) {
            sipinnerDialog_deposito.dTitle="SELECCION DE DEPOSITO";
            sipinnerDialog_deposito.showSpinerDialog();

            sipinnerDialog_deposito.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String s, int i) {
                    txt_spinner_deposito.setText(lista_combo_destino.get(i).getnombre());
                 }
            });
        } } );

        txt_spinner_vendedor.setOnClickListener(new View.OnClickListener() {  @Override public void onClick(View v) {
            sipinnerDialog_vendedor.dTitle="SELECCION DE VENDEDOR";
            sipinnerDialog_vendedor.showSpinerDialog();

            sipinnerDialog_vendedor.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String s, int i) {

                    codigo_vendedor= lista_vendedores.get(i).geticodigo();

                    txt_spinner_vendedor.setText(lista_vendedores.get(i).getnombre().toString());
                }
            });
        } } );

        txt_spinner_tipo_huevo.setOnClickListener(new View.OnClickListener() {  @Override public void onClick(View v) {
            sipinnerDialog_huevo.dTitle="SELECCION DE TIPO DE HUEVO";
            sipinnerDialog_huevo.showSpinerDialog();

            sipinnerDialog_huevo.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String s, int i) {

                    codigo_tipo= lista_tipo.get(i).geticodigo();
                    nombre_tipo=lista_tipo.get(i).getnombre();
                    txt_spinner_tipo_huevo.setText(nombre_tipo);
                 }
            });
        } } );

        txt_spinner_motivo.setOnClickListener(new View.OnClickListener() {  @Override public void onClick(View v) {
            sipinnerDialog_motivo.dTitle="SELECCION DE MOTIVO";
            sipinnerDialog_motivo.showSpinerDialog();

            sipinnerDialog_motivo.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String s, int i) {

                    codigo_motivo= lista_motivo.get(i).geticodigo();
                    nombre_motivo=  lista_motivo.get(i).getnombre();
                    txt_spinner_motivo.setText(nombre_motivo);
                }
            });
        } } );



        txt_spinner_responsable.setOnClickListener(new View.OnClickListener() {  @Override public void onClick(View v) {
            sipinnerDialog_responsable.dTitle="SELECCION DE RESPONSABLE";
            sipinnerDialog_responsable.showSpinerDialog();

            sipinnerDialog_responsable.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String s, int i) {
                    codigo_responsable=lista_combo_responsable.get(i).geticodigo() ;
                    txt_spinner_responsable.setText(lista_combo_responsable.get(i).getnombre());
                }
            });
        } } );


        lv_devolucion.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { @Override  public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            final int posicion=i;
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(registro_devolucion.this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("¿ Eliminar esta fila ?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    listadevolucion.remove(posicion);
                    refrescar_lv();
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
    private     void    cargar_cliente(){

    SQLiteDatabase db=conn.getReadableDatabase();
    Cursor cursor=db.rawQuery("SELECT * FROM clientes "   ,null);
    while (cursor.moveToNext())
    {
        list_cliente_text.add(cursor.getString(1));
        cliente p= new cliente();
        p.setcodigo(cursor.getString(0));
        p.setnombre(cursor.getString(1));

        lista_combo_cliente.add(p);
    }
    cursor.close();
        txt_spinner_cliente.setText("SELECCIONE EL CLIENTE");
        sipinnerDialog_cliente = new SpinnerDialogCustom(registro_devolucion.this,list_cliente_text,"SELECCIONE EL CLIENTE");


 }
    private     void    refrescar_lv(){
    ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.simple_list_item_3, R.id.text1, listadevolucion) {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView text1 = (TextView) view.findViewById(R.id.text1);
            TextView text2 = (TextView) view.findViewById(R.id.text2);
            TextView text3 = (TextView) view.findViewById(R.id.text3);


            text1.setText(listadevolucion.get(position).getNombre_huevo());
            text2.setText(""+listadevolucion.get(position).getcantidad());
            text3.setText(""+listadevolucion.get(position).getNombre_motivo());
            return view;
        }
    };
    lv_devolucion.setAdapter(adapter);
}
    private     void    cargar_responsable(){

        String codigo="NA,R,P,D,R/P,FR,FP,FR/FP,SP";
        String nombre="NO APLICA,REPARTIDOR,PROMOTORA,DYNAMUS,REPARTIDOR/PROMOTORA,FACTURADO A REPARTIDOR,FACTURADO A PROMOTORA,FACTURADO A REPARTIDOR/PROMOTORA,SUPERVISORA";
        String [] arr_codigo=codigo.split(",");
        String [] arr_nombre=nombre.split(",");


        for(int i=0; i < arr_codigo.length; i++){
            list_responsable_text.add(arr_nombre[i]);
            cliente p= new cliente();
            p.setcodigo(arr_codigo[i]);
            p.setnombre(arr_nombre[i]);
            lista_combo_responsable.add(p);
        }
        txt_spinner_responsable.setText("SELECCIONE EL RESPONSABLE");
        sipinnerDialog_responsable = new SpinnerDialogCustom(registro_devolucion.this,list_responsable_text,"SELECCIONE EL RESPONSABLE");

    }
    private     void    cargar_paquetes(){


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("test", "test");
        client.post("http://192.168.6.162/ws/control_select_paquetes.aspx", params, new TextHttpResponseHandler() {
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
                                for (int i=0; i<jsonArray_list.length();i++)
                                {
                                    String a=String.valueOf(jsonArray_list.length());
                                    tipo_huevo gd = new tipo_huevo();
                                    list_tipo_huevo_text.add(jsonArray_list.getJSONObject(i).getString("itemname"));
                                    gd.setcodigo(jsonArray_list.getJSONObject(i).getString("itemcode"));
                                    gd.setnombre(jsonArray_list.getJSONObject(i).getString("itemname"));

                                    lista_tipo.add(gd);
                                }
                                txt_spinner_tipo_huevo.setText("SELECCIONE TIPO DE HUEVO");
                                sipinnerDialog_huevo = new SpinnerDialogCustom(registro_devolucion.this,list_tipo_huevo_text,"SELECCIONE TIPO DE HUEVO");


                               /* ArrayAdapter<CharSequence> adaptador_tipo=new ArrayAdapter (registro_devolucion.this,R.layout.spinner_item,lista_tipo) ;
                                tipo.setAdapter(adaptador_tipo);*/
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
    }
    private     void    cargar_motivos(){


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("test", "test");
        client.post("http://192.168.6.162/ws/control_select_motivos.aspx", params, new TextHttpResponseHandler() {
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
                                for (int i=0; i<jsonArray_list.length();i++)
                                {
                                    motivo gd = new motivo();
                                    list_motivo_text.add(jsonArray_list.getJSONObject(i).getString("name"));

                                    gd.setcodigo(jsonArray_list.getJSONObject(i).getString("code"));
                                    gd.setnombre(jsonArray_list.getJSONObject(i).getString("name"));
                                    lista_motivo.add(gd);
                                }
                                txt_spinner_tipo_huevo.setText("SELECCIONE TIPO DE HUEVO");
                                sipinnerDialog_motivo = new SpinnerDialogCustom(registro_devolucion.this,list_motivo_text,"SELECCIONE MOTIVO");

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
    }
    public      void    seleccionar_fecha(View view) {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(registro_devolucion.this,
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
    public      void    cargar_grilla(View view) {
        llenar_grilla_final();
    }
    public      void    registrar(View view) {


        if(txt_nro_nota.getText().length()==0){
            new AlertDialog.Builder(registro_devolucion.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("INGRESE EL NRO DE NOTA DE CREDITO.")
                    .setNegativeButton("CERRAR", null).show();
        }
        else if (codigo_vendedor==null){
            new AlertDialog.Builder(registro_devolucion.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("SELECCIONE EL VENDEDOR.")
                    .setNegativeButton("CERRAR", null).show();
        }

        else if (codigo_cliente==null){
            new AlertDialog.Builder(registro_devolucion.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("SELECCIONE EL CLIENTE.")
                    .setNegativeButton("CERRAR", null).show();
        }

        else if (codigo_responsable==null){
            new AlertDialog.Builder(registro_devolucion.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("SELECCIONE EL RESPONSABLE.")
                    .setNegativeButton("CERRAR", null).show();
        }

        else if (listadevolucion.size()==0){
            new AlertDialog.Builder(registro_devolucion.this)
                    .setTitle("ATENCION!!!")
                    .setMessage("INGRESE DATOS A LA GRILLA.")
                    .setNegativeButton("CERRAR", null).show();
        }

        else {

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("REGISTRO DE DEVOLUCION.")
                    .setMessage("DESEA REGISTRAR LA DEVOLUCION?.")
                    .setPositiveButton("SI", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String contenedor_grilla="";
                            int c=0;
                            for (int i = 0; i < listadevolucion.size(); i++)
                            {
                                if(c==0) {

                                    contenedor_grilla=   listadevolucion.get(i).getcodigo_huevo()+"_"+listadevolucion.get(i).getcantidad()+"_"+listadevolucion.get(i).getcodigo_motivo();
                                }
                                else{

                                    contenedor_grilla=contenedor_grilla+","+  listadevolucion.get(i).getcodigo_huevo()+"_"+listadevolucion.get(i).getcantidad()+"_"+listadevolucion.get(i).getcodigo_motivo();
                                }
                                c++;
                            }
                            AsyncHttpClient client = new AsyncHttpClient();
                            RequestParams params = new RequestParams();
                            params.put("txtCodclie",codigo_cliente );
                            params.put("txtsucursal", nombre_cliente);
                            params.put("txtnrosolnc", txt_nro_nota.getText());
                            params.put("txtslpcode", codigo_vendedor);
                            params.put("txtfecha", txt_fecha.getText());
                            params.put("grilla", contenedor_grilla);
                            params.put("txtpassword", datos_usuario.pass);
                            params.put("txtusuario", datos_usuario.usuario);
                            params.put("txtResponsable", codigo_responsable);
                            params.put("txtDeposito", txt_spinner_deposito.getText().toString());
                            client.setTimeout(800000);
                            client.post("http://192.168.6.162/ws/control_devoluciones.aspx", params, new TextHttpResponseHandler() {
                                        @Override
                                        public void onStart()   {
                                            progress = ProgressDialog.show(registro_devolucion.this, "REGISTRANDO",
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
                                                new AlertDialog.Builder(registro_devolucion.this)
                                                        .setTitle("ATENCION!!!")
                                                        .setMessage(mensaje)
                                                        .setNegativeButton("CERRAR", null).show();
                                            }
                                            else if(band==1){

                                                new AlertDialog.Builder(registro_devolucion.this)
                                                        .setTitle("INFORMACION")
                                                        .setCancelable(false)
                                                        .setMessage(mensaje)
                                                        .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener()
                                                        {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

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
                                            new AlertDialog.Builder(registro_devolucion.this)
                                                    .setTitle("ATENCION!!!")
                                                    .setMessage("ERROR DE CONEXION, FAVOR INTENTE DE NUEVO.")
                                                    .setNegativeButton("CERRAR", null).show();
                                        }
                                    }
                            );                        }
                    })
                    .setNegativeButton("NO", null)
                    .show();

        }









    }
    private     void    cargar_vendedor(){


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("test", "test");
        client.post("http://192.168.6.162/ws/control_select_vendedores.aspx", params, new TextHttpResponseHandler() {
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
                                for (int i=0; i<jsonArray_list.length();i++)
                                {
                                    vendedores gd = new vendedores();
                                    list_vendedor_text.add(jsonArray_list.getJSONObject(i).getString("slpname"));

                                    gd.setcodigo(jsonArray_list.getJSONObject(i).getString("slpcode"));
                                    gd.setnombre(jsonArray_list.getJSONObject(i).getString("slpname"));
                                    lista_vendedores.add(gd);
                                }

                                txt_spinner_vendedor.setText("SELECCIONE EL VENDEDOR");
                                sipinnerDialog_vendedor = new SpinnerDialogCustom(registro_devolucion.this,list_vendedor_text,"SELECCIONE EL VENDEDOR");

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
    }
    private     void    llenar_grilla_final(){
        try {



            if(txt_cantidad_devolucion.getText().length()==0){

                new AlertDialog.Builder(registro_devolucion.this)
                        .setTitle("ATENCION!!!")
                        .setMessage("INGRESE LA CANTIDAD")
                        .setNegativeButton("CERRAR", null).show();
            }

            else if (Integer.parseInt(txt_cantidad_devolucion.getText().toString())==0){
                new AlertDialog.Builder(registro_devolucion.this)
                        .setTitle("ATENCION!!!")
                        .setMessage("CANTIDAD INGRESADA DEBE SER MAYOR A CERO")
                        .setNegativeButton("CERRAR", null).show();
            }
            else if ( codigo_tipo==null){
                new AlertDialog.Builder(registro_devolucion.this)
                        .setTitle("ATENCION!!!")
                        .setMessage("INGRESE TIPO DE HUEVO")
                        .setNegativeButton("CERRAR", null).show();
            }

            else if ( codigo_motivo==null){
                new AlertDialog.Builder(registro_devolucion.this)
                        .setTitle("ATENCION!!!")
                        .setMessage("SELECCIONE MOTIVO")
                        .setNegativeButton("CERRAR", null).show();
            }



            else {

                if(listadevolucion.size()>0){
                    int registro_duplicado=0;
                    for (int i = 0; i < listadevolucion.size(); i++)
                    {
                        if (listadevolucion.get(i).getcodigo_huevo()==codigo_tipo)
                        {
                            registro_duplicado++;
                        }

                    }
                    if(registro_duplicado>0){
                        new AlertDialog.Builder(registro_devolucion.this)
                                .setTitle("ATENCION!!!")
                                .setMessage("CODIGO DE HUEVO DUPLICADO")
                                .setNegativeButton("CERRAR", null).show();
                    }

                    else {

                        grilla_devolucion_agregar=new grilla_devolucion_agregar();
                        grilla_devolucion_agregar.setcodigo_huevo(codigo_tipo);
                        grilla_devolucion_agregar.setNombre_huevo(nombre_tipo);
                        grilla_devolucion_agregar.setcantidad(Integer.parseInt(txt_cantidad_devolucion.getText().toString()));
                        grilla_devolucion_agregar.setcodigo_motivo(codigo_motivo);
                        grilla_devolucion_agregar.setNombre_motivo(nombre_motivo);
                        listadevolucion.add(grilla_devolucion_agregar);
                        refrescar_lv();
                        txt_cantidad_devolucion.setText("");
                        txt_spinner_motivo.setText("SELECCIONE MOTIVO");
                        txt_spinner_tipo_huevo.setText("SELECCIONE TIPO DE HUEVO");
                        codigo_tipo=null;
                        codigo_motivo=null;
                    }


                }

                else {
                    grilla_devolucion_agregar=new grilla_devolucion_agregar();
                    grilla_devolucion_agregar.setcodigo_huevo(codigo_tipo);
                    grilla_devolucion_agregar.setNombre_huevo(nombre_tipo);
                    grilla_devolucion_agregar.setcantidad(Integer.parseInt(txt_cantidad_devolucion.getText().toString()));
                    grilla_devolucion_agregar.setcodigo_motivo(codigo_motivo);
                    grilla_devolucion_agregar.setNombre_motivo(nombre_motivo);
                    listadevolucion.add(grilla_devolucion_agregar);
                    refrescar_lv();
                    txt_cantidad_devolucion.setText("");
                    codigo_motivo=null;
                    txt_spinner_motivo.setText("SELECCIONE MOTIVO");
                    txt_spinner_tipo_huevo.setText("SELECCIONE TIPO DE HUEVO");
                    codigo_tipo=null;
                }



            }



        }
        catch (Exception e){
            String a=e.toString();
        }
    }
    private     void    cargar_depositos(){

        String codigo="AVE001,CEN007";
        String [] arr_codigo=codigo.split(",");

        for(int i=0; i < arr_codigo.length; i++){
            destino p= new destino();
            p.setnombre(arr_codigo[i]);
            lista_combo_destino.add(p);
            list_deposito_text.add(arr_codigo[i]);
        }

        sipinnerDialog_deposito = new SpinnerDialogCustom(registro_devolucion.this,list_deposito_text,"SELECCION DEPOSITO");
    }
    private     void    cargar_todos(){

        progress = ProgressDialog.show(registro_devolucion.this, "CARGANDO",
                "ESPERE...", true);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = sdf.format(c.getTime());
        txt_fecha.setText(strDate);
        cargar_responsable();
        cargar_cliente();
        cargar_paquetes();
        cargar_motivos();
        cargar_vendedor();
        cargar_depositos();
        progress.dismiss();


        txt_cantidad_devolucion.setOnKeyListener(new View.OnKeyListener()
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
    }
}
