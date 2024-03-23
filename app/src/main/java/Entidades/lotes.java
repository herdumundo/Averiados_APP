package Entidades;

public class lotes {
    private String ItemCode;
    private String LoteLargo;
    private String LoteCorto;
    private String Lote;


    public lotes(String ItemCode, String LoteLargo, String LoteCorto, String Lote)
    {
        this.ItemCode = ItemCode;
        this.LoteLargo = LoteLargo;
        this.LoteCorto = LoteCorto;
        this.Lote = Lote;
    }

    public lotes()
    {

    }
    public String getItemCode(){
        return ItemCode;
    }
    public void setItemCode(String ItemCode) {
        this.ItemCode = ItemCode;
    }
    public String getLoteLargo(){
        return LoteLargo;
    }
    public void setLoteLargo(String LoteLargo) {
        this.LoteLargo = LoteLargo;
    }

    public String getLoteCorto() {
        return LoteCorto;
    }
    public void setLoteCorto(String loteCorto) {
        LoteCorto = loteCorto;
    }
    public String getLote() {
        return Lote;
    }
    public void setLote(String lote) {
        Lote = lote;
    }
    @Override
    public String toString() {
        return  LoteLargo;
    }
}
