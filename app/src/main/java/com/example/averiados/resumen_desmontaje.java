package com.example.averiados;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import Entidades.lotes;
import Utilidades.Utilidades;
import cz.msebera.android.httpclient.Header;

public class resumen_desmontaje extends AppCompatActivity {
    TextView txt_fecha;
    Button btn_descargar;
    DatePickerDialog picker;
    WebView webViewCont ; // Reemplaza R.id.webView con el ID de tu WebView

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
        setContentView(R.layout.activity_resumen_desmontaje);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        txt_fecha=findViewById(R.id.txt_fecha_resumen);
        btn_descargar=findViewById(R.id.btn_descargar_resumen);
        webViewCont   = findViewById(R.id.webView);
        txt_fecha.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(resumen_desmontaje.this,
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
// Deshabilitar la capacidad de editar
        txt_fecha.setFocusable(false);
        txt_fecha.setFocusableInTouchMode(false);
   }


    public  void Descarga_PDF( View v){

        File path = Environment.getExternalStoragePublicDirectory( "/contenedor_reportes");
        File file = new File(path, "Resumen_desmontaje.pdf");
        if(file.exists()){
            file.delete();
        }
        String url = "http://yemsys.yemita.com.py/cruds/webServiceReportes/control_resumen_desmontaje.jsp?fecha="+txt_fecha.getText().toString().trim();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Descargando...");
        request.setTitle("Resumen de desmontaje");
         String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir("/contenedor_reportes", "Resumen_desmontaje.pdf");
        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        manager.enqueue(request);
        obtenerDesmontaje();
    }

    private void obtenerDesmontaje( ){
        StringBuilder htmlContent = new StringBuilder();

        // Construir la estructura HTML
        htmlContent.append("<html><head><style>table {border-collapse: collapse; width: 100%;} th, td {border: 1px solid black; padding: 8px; text-align: left;}</style></head><body><table>");
        htmlContent.append("<tr><th>Código</th><th>Cantidad</th></tr>");

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("fecha", txt_fecha.getText());
        client.post("http://" + Utilidades.IP + "/ws/Test/control_select_desmontaje.aspx", params, new TextHttpResponseHandler() {
                    @Override
                    public void onStart() {

                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        if (statusCode == 200) {
                            try {

                                JSONObject respuesta_json = new JSONObject(res);
                                JSONArray jsonArray = respuesta_json.getJSONArray("contenido_grilla");


                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String codigo = jsonArray.getJSONObject(i).getString("Codigo");
                                    String cantidad = jsonArray.getJSONObject(i).getString("Cantidad");

                                    // Agregar fila a la tabla
                                    htmlContent.append("<tr><td>").append(codigo).append("</td><td>").append(cantidad).append("</td></tr>");

                                }

                                // Cerrar la estructura HTML
                                htmlContent.append("</table></body></html>");

                                // Mostrar en la WebView
                                webViewCont.loadDataWithBaseURL(null, htmlContent.toString(), "text/html", "utf-8", null);


                            } catch (Exception e) {
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



    }