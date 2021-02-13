package Entidades;

public class destino {
    private String codigo;
    private String nombre;

    public destino(String codigo, String nombre ) {
        this.codigo = codigo;
        this.nombre = nombre;

    }

    public destino(){

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
