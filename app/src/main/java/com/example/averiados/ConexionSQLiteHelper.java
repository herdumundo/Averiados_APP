package com.example.averiados;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Utilidades.Utilidades;


public class ConexionSQLiteHelper extends SQLiteOpenHelper {


    public ConexionSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }




    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(Utilidades.CREAR_TABLA_CLIENTES);
        db.execSQL(Utilidades.CREAR_TABLA_DEPOSITOS);
        db.execSQL(Utilidades.CREAR_TABLA_REGISTRO_RECUPERADOS);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAntigua, int versionNueva) {

        db.execSQL("DROP TABLE IF EXISTS clientes");
        db.execSQL("DROP TABLE IF EXISTS depositos");
        db.execSQL("DROP TABLE IF EXISTS recuperados");



        onCreate(db);
    }




}




