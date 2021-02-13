package Entidades;

public class informe_array {
    private String codigo;
    private String nombre;
    private String hora;

    public informe_array(String codigo, String nombre, String hora ) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.hora = hora;

    }

    public informe_array(){

    }


    public String getCodigo(){
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHora(){
        return hora;
    }
    public void setHora(String hora) {
        this.hora = hora;
    }


}
