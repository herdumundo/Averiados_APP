package Entidades;

public class grilla_desmontaje {
    private String itemcode;
    private String itemname;
    private Integer cantidad;

    public grilla_desmontaje(String itemcode, String itemname, Integer cantidad ) {
        this.itemcode = itemcode;
        this.itemname = itemname;
        this.cantidad = cantidad;
    }

    public grilla_desmontaje(){

    }


    public String getitemcode(){
        return itemcode;
    }
    public void setitemcode(String itemcode) {
        this.itemcode = itemcode;
    }
    public String getitemname(){
        return itemname;
    }
    public void setitemname(String itemname) {
        this.itemname = itemname;
    }

    public Integer getcantidad(){
        return cantidad;
    }
    public void setcantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }



    @Override
    public String toString() {
        return "CODIGO:"+itemcode+" NOMBRE"+itemname+" CANTIDAD"+String.valueOf(cantidad);
    }
}
