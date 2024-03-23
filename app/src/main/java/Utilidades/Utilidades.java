package Utilidades;

public class Utilidades {

    //Constantes campos tabla usuario
    public static final String TABLA_CLIENTES="clientes";
    public static final String TABLA_DEPOSITOS="depositos";
    public static final String TABLA_RECUPERADOS="recuperados";
    public static final String IP="apisap.yemita.com.py";

    //////////////////////////////////////////////////////

    public static final String CREAR_TABLA_CLIENTES="CREATE TABLE " +
            ""+TABLA_CLIENTES+" (cardcode TEXT,  address TEXT)";

    public static final String CREAR_TABLA_DEPOSITOS="CREATE TABLE "+TABLA_DEPOSITOS+" (whscode TEXT,  whsname TEXT)";
    public static final String CREAR_TABLA_REGISTRO_RECUPERADOS="CREATE TABLE "+TABLA_RECUPERADOS+" (id INTEGER,  cantidad INTEGER, codigo_producto TEXT,pos INTEGER,fecha TEXT)";



}