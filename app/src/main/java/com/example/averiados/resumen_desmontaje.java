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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class resumen_desmontaje extends AppCompatActivity {
    TextView txt_fecha;
    Button btn_fecha,btn_descargar;
    DatePickerDialog picker;
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
        txt_fecha=(TextView)findViewById(R.id.txt_fecha_resumen);
        btn_fecha=(Button) findViewById(R.id.btn_fecha_resumen);
        btn_descargar=(Button) findViewById(R.id.btn_descargar_resumen);

        btn_fecha.setOnClickListener(new View.OnClickListener()
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
        txt_fecha.setEnabled(false);


    }


    public  void Descarga_PDF( View v){

        File path = Environment.getExternalStoragePublicDirectory( "/contenedor_reportes");
        File file = new File(path, "Resumen_desmontaje.pdf");
        if(file.exists()){
            file.delete();
        }
        String url = "http://192.168.6.162:8086/WebServices_reportes/control_resumen_desmontaje.jsp?fecha="+txt_fecha.getText().toString().trim();
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

    }



    }