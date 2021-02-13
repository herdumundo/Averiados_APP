package Entidades;

public class tipo_huevo {
    private String codigo;
    private String nombre;
    private String cantidad;

    public tipo_huevo(String codigo, String nombre, String cantidad ) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.cantidad = cantidad;

    }

    public tipo_huevo(){

    }


    public String geticodigo(){
        return codigo;
    }
    public void setcodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getnombre(){
        return nombre;
    }
    public void setnombre(String nombre) {
        this.nombre = nombre;
    }

    public String getcantidad(){
        return cantidad;
    }
    public void setcantidad(String cantidad) {
        this.cantidad = cantidad;
    }
    @Override
    public String toString() {
        return  nombre;
    }
}
