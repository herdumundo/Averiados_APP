package Entidades;

public class lote {
    private String itemcode;
    private int quantity;
    private String distnumber;

    public lote(String itemcode, Integer quantity , String distnumber ) {
        this.itemcode = itemcode;
        this.quantity = quantity;
        this.distnumber = distnumber;

    }

    public lote(){

    }


    public String getItemcode(){
        return itemcode;
    }
    public void setItemcode(String itemcode) {
        this.itemcode = itemcode;
    }

    public Integer getQuantity(){
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public String getDistnumber(){
        return distnumber;
    }
    public void setDistnumber(String distnumber) {
        this.distnumber = distnumber;
    }


}
