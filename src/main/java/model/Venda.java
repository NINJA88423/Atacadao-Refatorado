package model;




import java.time.LocalDateTime;




public class Venda {

    
    private int id;
    private int id_cliente;
    private LocalDateTime datahora;
    private float total;

    
    public Venda(){}

    public Venda(
        int id,
        int id_cliente,
        LocalDateTime datahora,
        float total
    ){
        this.id = id;
        this.id_cliente = id_cliente;
        this.datahora = datahora;
        this.total = total;
    }

    
    public void setId(int id){this.id = id;}
    public void setIdCliente(int id_cliente){this.id_cliente = id_cliente;}
    public void setDatahora(LocalDateTime datahora){this.datahora = datahora;}
    public void setTotal(float total){this.total = total;}

    
    public int getId(){return id;}
    public int getIdCliente(){return id_cliente;}
    public LocalDateTime getDatahora(){return datahora;}
    public float getTotal(){return total;}

    
}