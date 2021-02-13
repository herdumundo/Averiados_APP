package com.example.averiados;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import Entidades.cliente;
import Entidades.informe_array;
import Entidades.informe_array_global;
import Entidades.motivo;

import cz.msebera.android.httpclient.Header;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class informe extends AppCompatActivity {
    Connection connect;
    TextView txt_fecha;
    Button btn_fecha,btn_buscar,btn_descargar;
    DatePickerDialog picker;
    private  ListView ListView;
    String url="";
    int nro_registro=0;
    ConexionSQLiteHelper conn;
    private ProgressDialog progress;
    List<informe_array> lista_array = new ArrayList<>();
    List<informe_array_global> lista_array_global = new ArrayList<>();
    String valor_JSON1="";String valor_JSON2="";String valor_JSON3="";String valor_JSON4="";String valor_JSON5=""; String valor_JSON6="";
    String valor_JSON7=""; String valor_JSON8="";String valor_JSON9="";
    String valor_nombre1="";String valor_nombre2="";String valor_nombre3="";String valor_nombre4="";String valor_nombre5="";String valor_nombre6="";
    String valor_nombre7="";String valor_nombre8="";String valor_nombre9="";
    ArrayAdapter adapter;
    @Override
    public void onBackPressed()
    {
        finish();
        Intent List = new Intent(getApplicationContext(), menu_informe.class);
        startActivity(List);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informe);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        txt_fecha=(TextView)findViewById(R.id.txt_fecha_informe);
        btn_fecha=(Button) findViewById(R.id.btn_fecha_informe);
        btn_buscar=(Button) findViewById(R.id.btn_buscar_informe);
        btn_descargar=(Button) findViewById(R.id.btn_descargar);
        ListView=(ListView)findViewById(R.id.listView_informe);
        getSupportActionBar().setTitle(datos_usuario.titulo_cuadro );
        conn=new ConexionSQLiteHelper(getApplicationContext(),"bd_usuarios",null,1);
        if(datos_usuario.tipo_informe.equals("DEVOLUCION")){

        }
        else {
            btn_descargar.setVisibility(View.GONE);
        }
        btn_fecha.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(informe.this,
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
        txt_fecha.setEnabled(false);



if(datos_usuario.tipo_informe.equals("TRANSFERENCIA")||datos_usuario.tipo_informe.equals("CAMBIO")) {

   ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String  informacion="NRO: "+lista_array_global.get(pos).getValor1()+"\n"; //VALOR DOC NUM
                informacion+="REPARTIDOR: "+lista_array_global.get(pos).getValor2()+"\n";
                informacion+="Hora de registro: "+lista_array_global.get(pos).getValor3()+"\n";
                nro_registro= Integer.parseInt(lista_array_global.get(pos).getValor4());// VALOR DOCENTRY

                new AlertDialog.Builder(informe.this)
                        .setTitle("DESEA DESCARGAR EL DOCUMENTO?")
                        .setCancelable(false)
                        .setMessage(informacion)
                        .setNegativeButton("CERRAR", null)
                        .setPositiveButton("SI, DESCARGAR ", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Descarga_PDF(nro_registro);
                            }
                        }).show();

            }
        });



    }
else if (datos_usuario.tipo_informe.equals("STOCK")||datos_usuario.tipo_informe.equals("STOCK_AVERIADOS")){

    txt_fecha.setVisibility(View.GONE);
    btn_fecha.setVisibility(View.GONE);
    btn_buscar.setVisibility(View.GONE);
    cargar_grilla();
}
else if (datos_usuario.tipo_informe.equals("DEVOLUCION")){


    ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, final int pos, long l) {
            final int numero=Integer.parseInt(lista_array_global.get(pos).getValor1());
            final int posicion_array=pos;
            String  informacion="NRO: "+lista_array_global.get(pos).getValor1()+"\n";
            informacion+="SUCURSAL: "+lista_array_global.get(pos).getValor4()+"\n";
            informacion+="ARTICULO: "+lista_array_global.get(pos).getValor5()+"\n";
            informacion+="CANTIDAD: "+lista_array_global.get(pos).getValor9()+"\n";
            final android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(informe.this);
            final View mView = getLayoutInflater().inflate(R.layout.activity_cuadro_recuperados, null);
            final TextView txt_descripcion = (TextView) mView.findViewById(R.id.txt_nro);
            final TextView txt_cantidad_cuadro = (TextView) mView.findViewById(R.id.txt_cantidad_recuperada);
            final TextView txt_fecha_cuadro = (TextView) mView.findViewById(R.id.txt_fecha);
            final Button btn_registrar_cuadro = (Button) mView.findViewById(R.id.btn_registrar_cuadro);
            txt_descripcion.setText(informacion);


            txt_fecha_cuadro.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    picker = new DatePickerDialog(informe.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                    DecimalFormat df = new DecimalFormat("00");
                                    SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");

                                    cldr.set(year, monthOfYear, dayOfMonth);
                                    String strDate = format.format(cldr.getTime());
                                    txt_fecha_cuadro.setText(df.format((dayOfMonth))+ "/" + df.format((monthOfYear + 1))  +"/"+ year  );
                                }
                            }, year, month, day);
                    picker.show();
                }
            });

            mBuilder.setView(mView);
            final android.support.v7.app.AlertDialog dialog = mBuilder.create();
            dialog.show();

            btn_registrar_cuadro.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {

                    try {
                        if(txt_cantidad_cuadro.getText().length()==0){
                            new AlertDialog.Builder(informe.this)
                                    .setTitle("ATENCION!!!")
                                    .setMessage("DEBES INGRESAR CANTIDAD")
                                    .setNegativeButton("CERRAR", null).show();
                        }
                        else if (Integer.parseInt(txt_cantidad_cuadro.getText().toString())>Integer.parseInt(lista_array_global.get(pos).getValor9())){

                            new AlertDialog.Builder(informe.this)
                                    .setTitle("ATENCION!!!")
                                    .setMessage("CANTIDAD EXCEDIDA.")
                                    .setNegativeButton("CERRAR", null).show();
                        }

                        else if(txt_fecha_cuadro.getText().length()==0){
                            new AlertDialog.Builder(informe.this)
                                    .setTitle("ATENCION!!!")
                                    .setMessage("DEBES INGRESAR LA FECHA DE CONSUMO PREFERENTE")
                                    .setNegativeButton("CERRAR", null).show();
                        }
                        else {


                            progress = ProgressDialog.show(informe.this, "REGISTRANDO",
                                    "ESPERE...", true);
                            SQLiteDatabase db=conn.getWritableDatabase();
                            ContentValues values=new ContentValues();
                            values.put("id", numero);
                            values.put("cantidad",Integer.parseInt(txt_cantidad_cuadro.getText().toString()));
                            values.put("pos",posicion_array) ;
                            values.put("fecha",txt_fecha.getText().toString()) ;
                            values.put("codigo_producto",lista_array_global.get(pos).getValor2()) ;
                            String mensaje_sp="";
                            int res=0;
                            ConexionBD conexion = new ConexionBD();
                            connect = conexion.Connections();
                            connect.setAutoCommit(false);
                            CallableStatement callableStatement=null;
                            callableStatement = connect.prepareCall("{call pa_averiados_recuperados( ?,?,?,?,?,?,?,?,?,?,?)}");
                            callableStatement .setString(           "@itemcode",lista_array_global.get(pos).getValor2());
                            callableStatement .setString(           "@fecha",txt_fecha.getText().toString());
                            callableStatement .setInt(              "@cantidad",Integer.parseInt(txt_cantidad_cuadro.getText().toString()));
                            callableStatement .setInt(              "@nro_registro_sap",numero);
                            callableStatement .setInt(              "@total",Integer.parseInt(lista_array_global.get(pos).getValor9()));
                            callableStatement .setString(           "@sucursal",lista_array_global.get(pos).getValor4());
                            callableStatement .setString(           "@item_name",lista_array_global.get(pos).getValor5());
                            callableStatement .setString(           "@repartidor",lista_array_global.get(pos).getValor8());
                            callableStatement .setString(           "@fecha_consumo",txt_fecha_cuadro.getText().toString());
                            callableStatement.registerOutParameter( "@mensaje", Types.VARCHAR);
                            callableStatement.registerOutParameter( "@tipo_mensaje", Types.INTEGER);
                            callableStatement.execute();
                            res = callableStatement.getInt("@tipo_mensaje");
                            if(res==40){
                                mensaje_sp = callableStatement.getString("@mensaje");
                                connect.commit();
                                progress.dismiss();
                                db.insert("recuperados" ,null,values);
                                db.close();
                            }
                            else{
                                connect.rollback();
                                db.close();
                            }

                            new AlertDialog.Builder(informe.this)
                                    .setTitle("ATENCION!!!")
                                    .setMessage(mensaje_sp)
                                    .setNegativeButton("CERRAR", null).show();
                            dialog.dismiss();
                            refrescar_grilla();
                            progress.dismiss();


                        }
                    }
                    catch (Exception e){

                        progress.dismiss();
                        new AlertDialog.Builder(informe.this)
                                .setTitle("ATENCION!!!")
                                .setMessage(e.toString())
                                .setNegativeButton("CERRAR", null).show();

                    }

                }

            });






        }
    });
}

    }

    private     void refrescar_grilla(){
            if(datos_usuario.tipo_informe.equals("TRANSFERENCIA")||datos_usuario.tipo_informe.equals("CAMBIO")) {
                cargar_grilla();
            }
            else if(datos_usuario.tipo_informe.equals("DESMONTAJE")||datos_usuario.tipo_informe.equals("DEVOLUCION")) {
                cargar_grilla_devoluciones_demontajes();
            }

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

    public      void boton_buscar_informe (View v){
        if(datos_usuario.tipo_informe.equals("TRANSFERENCIA")||datos_usuario.tipo_informe.equals("CAMBIO")) {
            cargar_grilla();
        }
        else if(datos_usuario.tipo_informe.equals("DESMONTAJE")||datos_usuario.tipo_informe.equals("DEVOLUCION")) {
            cargar_grilla_devoluciones_demontajes();
        }

       // llenar();
    }

    private     void cargar_grilla (){
        if(datos_usuario.tipo_informe.equals("CAMBIO")||datos_usuario.tipo_informe.equals("TRANSFERENCIA")){
            url="http://192.168.6.162/ws/rep_select_cambio.aspx";
            valor_JSON1="docnum";valor_JSON2="slpname"; valor_JSON3="doctime"; valor_JSON4="docentry";
            valor_nombre1="Nro.: ";  valor_nombre2="Repartidor: ";  valor_nombre3="Hora: ";
                    }
        else if(datos_usuario.tipo_informe.equals("STOCK")||datos_usuario.tipo_informe.equals("STOCK_AVERIADOS")){
            url="http://192.168.6.162/ws/consulta_stock_averiados.aspx";
            valor_JSON1="itemcode";valor_JSON2="itemname"; valor_JSON3="onhand";
            valor_nombre1="Codigo.: ";  valor_nombre2="Descripcion: ";  valor_nombre3="Cantidad: ";
                    }
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("txtfecha", txt_fecha.getText());
        params.put("txtdeposito", datos_usuario.deposito);
        client.post(url, params, new TextHttpResponseHandler() {
                    @Override
                    public void onStart()   {
                        progress = ProgressDialog.show(informe.this, "CONSULTANDO",
                                "ESPERE...", true);
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res)
                    {
                        if (statusCode == 200) {
                            try {
                                    JSONObject respuesta_json =new JSONObject(res);
                                    JSONArray jsonArray_list = respuesta_json.getJSONArray("contenido_grilla");
                                    lista_array_global.clear();
                                    for (int i=0; i<jsonArray_list.length();i++) {
                                    informe_array_global gd = new informe_array_global();
                                    gd.setValor1(jsonArray_list.getJSONObject(i).getString(valor_JSON1));
                                    gd.setValor2(jsonArray_list.getJSONObject(i).getString(valor_JSON2));
                                    gd.setValor3(jsonArray_list.getJSONObject(i).getString(valor_JSON3));
                                    if(datos_usuario.tipo_informe.equals("CAMBIO")||datos_usuario.tipo_informe.equals("TRANSFERENCIA")){
                                    gd.setValor4(jsonArray_list.getJSONObject(i).getString(valor_JSON4));

                                    }
                                    lista_array_global.add(gd);
                                }
                                adaptador_grilla(txt_fecha.getText().toString());
                                }
                            catch (Exception e){
                            }
                        }
                    }

                    @Override
                    public void onFinish()
                    {
                      progress.dismiss();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t)
                    {
                        progress.dismiss();
                        new AlertDialog.Builder(informe.this)
                                .setTitle("ATENCION!!!")
                                .setMessage("ERROR DE CONEXION, FAVOR INTENTE DE NUEVO.")
                                .setNegativeButton("CERRAR", null).show();
                    }
                }
        );
    }

    private     void cargar_grilla_devoluciones_demontajes (){

        if(datos_usuario.tipo_informe.equals("DEVOLUCION")){
            url="http://192.168.6.162/ws/rep_select_devoluciones.aspx";
              valor_nombre1="Nro interno: "; valor_nombre2="Codigo de producto: ";valor_nombre5="Articulo: ";
              valor_nombre6="Cantidad: "; valor_nombre7="Motivo: "; valor_nombre8="Repartidor: "; valor_nombre9="Cantidad total: ";

              valor_JSON1="nrointerno"; valor_JSON2="itemCode";valor_JSON4="sucursal";valor_JSON5="articulo";valor_JSON6="cantidad";
              valor_JSON7="motivo";valor_JSON8="repartidor";   valor_JSON9="cantH";
        }
        else if(datos_usuario.tipo_informe.equals("DESMONTAJE")){
            url="http://192.168.6.162/ws/rep_select_desmontajes.aspx";
            valor_nombre1="Nro documento: "; valor_nombre2="Codigo: "; valor_nombre4="Cantidad: ";  valor_nombre5="Tipo de huevo: ";
            valor_nombre6="Unidad: "; valor_nombre7="Deposito: ";

            valor_JSON1="nroorden"; valor_JSON2="codigo"; valor_JSON3="articulo"; valor_JSON4="cantidad";
            valor_JSON5="tipohuevo"; valor_JSON6="unidad"; valor_JSON7="deposito";
         }
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("txtfecha", txt_fecha.getText());
        client.post(url, params, new TextHttpResponseHandler() {
                    @Override
                    public void onStart()   {
                        progress = ProgressDialog.show(informe.this, "CONSULTANDO",
                                "ESPERE...", true);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res)
                    {
                        if (statusCode == 200) {
                            try {
                                JSONObject respuesta_json =new JSONObject(res);

                                JSONArray jsonArray_list = respuesta_json.getJSONArray("contenido_grilla");



                                    lista_array_global.clear();
                                if(datos_usuario.tipo_informe.equals("DEVOLUCION")){
                                    for (int i=0; i<jsonArray_list.length();i++) {
                                        informe_array_global gd = new informe_array_global();
                                        gd.setValor1(jsonArray_list.getJSONObject(i).getString(valor_JSON1));
                                        gd.setValor2(jsonArray_list.getJSONObject(i).getString(valor_JSON2));
                                        //gd.setValor3(jsonArray_list.getJSONObject(i).getString(valor_JSON3));
                                        gd.setValor4(jsonArray_list.getJSONObject(i).getString(valor_JSON4));
                                        gd.setValor5(jsonArray_list.getJSONObject(i).getString(valor_JSON5));
                                        gd.setValor6(jsonArray_list.getJSONObject(i).getString(valor_JSON6));
                                        gd.setValor7(jsonArray_list.getJSONObject(i).getString(valor_JSON7));
                                        gd.setValor8(jsonArray_list.getJSONObject(i).getString(valor_JSON8));
                                        gd.setValor9(jsonArray_list.getJSONObject(i).getString(valor_JSON9));
                                        lista_array_global.add(gd);
                                    }
                                }

                               else if(datos_usuario.tipo_informe.equals("DESMONTAJE"))
                               {
                                    for (int i=0; i<jsonArray_list.length();i++) {
                                        informe_array_global gd = new informe_array_global();
                                        gd.setValor1(jsonArray_list.getJSONObject(i).getString(valor_JSON1));
                                        gd.setValor2(jsonArray_list.getJSONObject(i).getString(valor_JSON2));
                                        gd.setValor3(jsonArray_list.getJSONObject(i).getString(valor_JSON3));
                                        gd.setValor4(jsonArray_list.getJSONObject(i).getString(valor_JSON4));
                                        gd.setValor5(jsonArray_list.getJSONObject(i).getString(valor_JSON5));
                                        gd.setValor6(jsonArray_list.getJSONObject(i).getString(valor_JSON6));
                                        gd.setValor7(jsonArray_list.getJSONObject(i).getString(valor_JSON7));
                                       lista_array_global.add(gd);
                                    }
                                }

                                adaptador_grilla(txt_fecha.getText().toString());


                            }
                            catch (Exception e){

                            }
                        }
                    }

                    @Override
                    public void onFinish()
                    {
                        ///
                        progress.dismiss();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t)
                    {
                        progress.dismiss();
                        new AlertDialog.Builder(informe.this)
                                .setTitle("ATENCION!!!")
                                .setMessage("ERROR DE CONEXION, FAVOR INTENTE DE NUEVO.")
                                .setNegativeButton("CERRAR", null).show();
                    }
                }
        );


    }

    private     void adaptador_grilla(final String fecha){

    adapter = new ArrayAdapter(getApplicationContext(), R.layout.simple_list_item_8_fila, R.id.text1_fila, lista_array_global) {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            if(datos_usuario.tipo_informe.equals("DEVOLUCION")){
                TextView text1 =    (TextView) view.findViewById(R.id.text1_fila);
                TextView text2=     (TextView) view.findViewById(R.id.text2_fila);
                TextView text3 =    (TextView) view.findViewById(R.id.text3_fila);
                TextView text4 =    (TextView) view.findViewById(R.id.text4_fila);
                TextView text5 =    (TextView) view.findViewById(R.id.text5_fila);
                TextView text6 =    (TextView) view.findViewById(R.id.text6_fila);
                TextView text7 =    (TextView) view.findViewById(R.id.text7_fila);
                TextView text8 =    (TextView) view.findViewById(R.id.text8_fila);

                text1.setText(valor_nombre1     +lista_array_global.get(position).getValor1());
                text2.setText(valor_nombre2     +lista_array_global.get(position).getValor2());
                text3.setVisibility(view.GONE);
                text4.setText(valor_nombre4     +lista_array_global.get(position).getValor4());
                text5.setText(valor_nombre5     +lista_array_global.get(position).getValor5());
                text6.setText(valor_nombre9     +lista_array_global.get(position).getValor9());
                text7.setText(valor_nombre7     +lista_array_global.get(position).getValor7());
                text8.setText(valor_nombre8     +lista_array_global.get(position).getValor8());
                if((position%2)==0){
                    view.setBackgroundColor(Color.LTGRAY);
                }

                else{
                    view.setBackgroundColor(Color.WHITE);
                }
             }

            else if(datos_usuario.tipo_informe.equals("DESMONTAJE")){
                TextView text1 =    (TextView) view.findViewById(R.id.text1_fila);
                TextView text2=     (TextView) view.findViewById(R.id.text2_fila);
                TextView text3 =    (TextView) view.findViewById(R.id.text3_fila);
                TextView text4 =    (TextView) view.findViewById(R.id.text4_fila);
                TextView text5 =    (TextView) view.findViewById(R.id.text5_fila);
                TextView text6 =    (TextView) view.findViewById(R.id.text6_fila);
                TextView text7 =    (TextView) view.findViewById(R.id.text7_fila);

                text1.setText(valor_nombre1     +lista_array_global.get(position).getValor1());
                text2.setText(valor_nombre2     +lista_array_global.get(position).getValor2());
                text3.setText(valor_nombre3     +lista_array_global.get(position).getValor3());
                text4.setText(valor_nombre4     +lista_array_global.get(position).getValor4());
                text5.setText(valor_nombre5     +lista_array_global.get(position).getValor5());
                text6.setText(valor_nombre6     +lista_array_global.get(position).getValor6());
                text7.setText(valor_nombre7     +lista_array_global.get(position).getValor7());
                if((position%2)==0){
                    view.setBackgroundColor(Color.LTGRAY);
                }

                else{
                    view.setBackgroundColor(Color.WHITE);
                }


            }


            else if(datos_usuario.tipo_informe.equals("STOCK")||datos_usuario.tipo_informe.equals("STOCK_AVERIADOS")||datos_usuario.tipo_informe.equals("TRANSFERENCIA")||datos_usuario.tipo_informe.equals("CAMBIO")){
                TextView text1 =    (TextView) view.findViewById(R.id.text1_fila);
                TextView text2=     (TextView) view.findViewById(R.id.text2_fila);
                TextView text3 =    (TextView) view.findViewById(R.id.text3_fila);
                TextView text4 =    (TextView) view.findViewById(R.id.text4_fila);
                TextView text5 =    (TextView) view.findViewById(R.id.text5_fila);
                TextView text6 =    (TextView) view.findViewById(R.id.text6_fila);
                TextView text7 =    (TextView) view.findViewById(R.id.text7_fila);

                text1.setText(valor_nombre1     +lista_array_global.get(position).getValor1());
                text2.setText(valor_nombre2     +lista_array_global.get(position).getValor2());
                text3.setText(valor_nombre3     +lista_array_global.get(position).getValor3());
                text4.setVisibility(view.GONE);
                text5.setVisibility(view.GONE);
                text6.setVisibility(view.GONE);
                text7.setVisibility(view.GONE);
                if((position%2)==0){
                    view.setBackgroundColor(Color.LTGRAY);
                }

                else{
                    view.setBackgroundColor(Color.WHITE);
                }


            }



            return view;
        }
    };
    ListView.setAdapter(adapter);
}

    public      void Descarga_PDF_recuperados(View v){

        File path = Environment.getExternalStoragePublicDirectory( "/contenedor_reportes");
        File file = new File(path, "Resumen_recuperados.pdf");
        if(file.exists()){
            file.delete();
        }
        String url = "http://192.168.6.162:8086/WebServices_reportes/control_resumen_recuperados.jsp?fecha="+txt_fecha.getText().toString();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Descargando...");
        request.setTitle("Reporte de recuperados");


        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir("/contenedor_reportes", "Resumen_recuperados.pdf");

        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        manager.enqueue(request);

    }
}