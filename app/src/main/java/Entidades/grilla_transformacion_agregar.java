package Entidades;

public class grilla_transformacion_agregar {
    private String codigo;
    private String nombre_huevo;
    private int cantidad;

    public grilla_transformacion_agregar(String codigo, int cantidad, String nombre_huevo ) {
        this.codigo = codigo;
        this.cantidad = cantidad;
        this.nombre_huevo=nombre_huevo;

    }

    public grilla_transformacion_agregar(){

    }


    public String getCodigo(){
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public int getCantidad(){
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombre_huevo(){
        return nombre_huevo;
    }
    public void setNombre_huevo(String nombre_huevo) {
        this.nombre_huevo = nombre_huevo;
    }

}
