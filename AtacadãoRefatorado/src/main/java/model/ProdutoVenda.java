package model;




import java.math.BigDecimal;




public class ProdutoVenda {

    
    private int id;
    private int id_produto;
    private int id_venda;
    private boolean isKG;
    private float quantidade;
    private float desconto;
    private BigDecimal subtotal;

    
    public ProdutoVenda(){}

    public ProdutoVenda(
        int id,
        int id_produto,
        int id_venda,
        boolean isKG,
        float quantidade,
        float desconto,
        BigDecimal subtotal
    ){
        this.id = id;
        this.id_produto = id_produto;
        this.id_venda = id_venda;
        this.isKG = isKG;
        this.quantidade = quantidade;
        this.desconto = desconto;
        this.subtotal = subtotal;
    }

    
    public void setId(int id){this.id = id;}
    public void setIdProduto(int id_produto){this.id_produto = id_produto;}
    public void setIdVenda(int id_venda){this.id_venda = id_venda;}
    public void setIsKG(boolean isKG){this.isKG = isKG;}
    public void setQuantidade(float quantidade){this.quantidade = quantidade;}
    public void setDesconto(float desconto){this.desconto = desconto;}
    public void setSubtotal(BigDecimal subtotal){this.subtotal = subtotal;}

    
    public int getId(){return id;}
    public int getIdProduto(){return id_produto;}
    public int getIdVenda(){return id_venda;}
    public boolean getIsKG(){return isKG;}
    public float getQuantidade(){return quantidade;}
    public float getDesconto(){return desconto;}
    public BigDecimal getSubtotal(){return subtotal;}
    
    
}