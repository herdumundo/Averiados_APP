package Entidades;

public class grilla_devolucion_agregar {
    private String codigo_huevo;
    private String nombre_huevo;
    private String codigo_motivo;
    private String nombre_motivo;
    private int cantidad;

    public grilla_devolucion_agregar(String codigo_huevo, int cantidad, String codigo_motivo , String nombre_huevo, String nombre_motivo ) {
        this.codigo_huevo = codigo_huevo;
        this.nombre_huevo = nombre_huevo;
        this.cantidad = cantidad;
        this.codigo_motivo=codigo_motivo;
        this.nombre_motivo=nombre_motivo;

    }

    public grilla_devolucion_agregar(){

    }


    public String getcodigo_huevo(){
        return codigo_huevo;
    }
    public void setcodigo_huevo(String codigo_huevo) {
        this.codigo_huevo = codigo_huevo;
    }

    public String getNombre_huevo(){
        return nombre_huevo;
    }
    public void setNombre_huevo(String nombre_huevo) {
        this.nombre_huevo = nombre_huevo;
    }



    public int getcantidad(){
        return cantidad;
    }
    public void setcantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getcodigo_motivo(){
        return codigo_motivo;
    }
    public void setcodigo_motivo(String codigo_motivo) {
        this.codigo_motivo = codigo_motivo;
    }


    public String getNombre_motivo(){
        return nombre_motivo;
    }
    public void setNombre_motivo(String nombre_motivo) {
        this.nombre_motivo = nombre_motivo;
    }
}
