package Entidades;

public class grilla_transferencia_agregar {
    private String codigo;
    private String nombre_huevo;
    private int cantidad;

    public grilla_transferencia_agregar(String codigo, int cantidad,String nombre_huevo ) {
        this.codigo = codigo;
        this.cantidad = cantidad;
        this.nombre_huevo=nombre_huevo;

    }

    public grilla_transferencia_agregar(){

    }


    public String geticodigo(){
        return codigo;
    }
    public void setcodigo(String codigo) {
        this.codigo = codigo;
    }
    public int getcantidad(){
        return cantidad;
    }
    public void setcantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getnombre_huevo(){
        return nombre_huevo;
    }
    public void setnombre_huevo(String nombre_huevo) {
        this.nombre_huevo = nombre_huevo;
    }

}
