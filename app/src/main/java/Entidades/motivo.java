package Entidades;

public class motivo {
    private String codigo;
    private String nombre;

    public motivo(String codigo, String nombre ) {
        this.codigo = codigo;
        this.nombre = nombre;

    }

    public motivo(){

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

    @Override
    public String toString() {
        return  nombre;
    }
}
