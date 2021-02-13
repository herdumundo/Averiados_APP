package com.example.averiados;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class menu_informe extends AppCompatActivity {
    @Override
    public void onBackPressed()
    {
        finish();
        Intent List = new Intent(getApplicationContext(), menu_principal.class);
        startActivity(List);
     }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_informe);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    public void ir_informe_cambios(View view) {
        datos_usuario.tipo_informe="CAMBIO";
        datos_usuario.deposito="CEN007";
        datos_usuario.titulo_cuadro="INFORME DE CAMBIOS DIRECTOS";
        finish();
        Intent i=new Intent(menu_informe.this, informe.class);
        startActivity(i);

    }
    public void ir_informe_desmontajes(View view) {
        datos_usuario.tipo_informe="DESMONTAJE";
        datos_usuario.titulo_cuadro="INFORME DE DESMONTAJES";

        finish();
        Intent i=new Intent(menu_informe.this, informe.class);
        startActivity(i);

    }
    public void ir_informe_transferencias(View view) {
        datos_usuario.tipo_informe="TRANSFERENCIA";
        datos_usuario.titulo_cuadro="INFORME DE TRANSFERENCIAS";

        datos_usuario.deposito="AVE001";

        finish();
        Intent i=new Intent(menu_informe.this, informe.class);
        startActivity(i);

    }
    public void ir_informe_devoluciones(View view) {
        datos_usuario.tipo_informe="DEVOLUCION";
        datos_usuario.titulo_cuadro="INFORME DE DEVOLUCIONES";

        finish();
        Intent i=new Intent(menu_informe.this, informe.class);
        startActivity(i);

    }


    public void ir_informe_stock(View view) {
        datos_usuario.tipo_informe="STOCK";
        datos_usuario.titulo_cuadro="INFORME DE STOCK DISPONIBLE";
        datos_usuario.deposito="AVE001";

        finish();
        Intent i=new Intent(menu_informe.this, informe.class);
        startActivity(i);

    }
    public void ir_informe_stock_averiados(View view) {
        datos_usuario.tipo_informe="STOCK_AVERIADOS";
        datos_usuario.titulo_cuadro="INFORME DE STOCK DISPONIBLE";
        datos_usuario.deposito="AVE002";

        finish();
        Intent i=new Intent(menu_informe.this, informe.class);
        startActivity(i);

    }

    public void ir_resumen_desmontaje(View view) {

        finish();
        Intent i=new Intent(menu_informe.this, resumen_desmontaje.class);
        startActivity(i);

    }
}