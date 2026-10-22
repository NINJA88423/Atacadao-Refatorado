package model;




import java.time.LocalDateTime;

import java.math.BigDecimal;




public class Venda {

    
    private int id;
    private int id_cliente;
    private LocalDateTime datahora;
    private BigDecimal total;

    
    public Venda(){}

    public Venda(
        int id,
        int id_cliente,
        LocalDateTime datahora,
        BigDecimal total
    ){
        this.id = id;
        this.id_cliente = id_cliente;
        this.datahora = datahora;
        this.total = total;
    }

    
    public void setId(int id){this.id = id;}
    public void setIdCliente(int id_cliente){this.id_cliente = id_cliente;}
    public void setDatahora(LocalDateTime datahora){this.datahora = datahora;}
    public void setTotal(BigDecimal total){this.total = total;}

    
    public int getId(){return id;}
    public int getIdCliente(){return id_cliente;}
    public LocalDateTime getDatahora(){return datahora;}
    public BigDecimal getTotal(){return total;}

    
}