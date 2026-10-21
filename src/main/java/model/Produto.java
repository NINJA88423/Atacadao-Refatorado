package model;




import java.math.BigDecimal;




public class Produto {

    
    private int id;
    private String codigo;
    private String nome;
    private String marca;
    private boolean isKG = false;
    private BigDecimal preco;

    
    public Produto(){}

    public Produto(
        int id,
        String codigo,
        String nome,
        String marca,
        boolean isKG,
        BigDecimal preco
    ){
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.marca = marca;
        this.isKG = isKG;
        this.preco = preco;
    }
    
    public Produto(
        int id,
        String codigo,
        String nome,
        boolean isKG,
        BigDecimal preco
    ){
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.isKG = isKG;
        this.preco = preco;
    }

    
    public void setId(int id){this.id = id;}
    public void setCodigo(String codigo){this.codigo = codigo;}
    public void setNome(String nome){this.nome = nome;}
    public void setMarca(String marca){this.marca = marca;}
    public void setIsKG(boolean isKG){this.isKG = isKG;}
    public void setPreco(BigDecimal preco){this.preco = preco;}

    
    public int getId(){return id;}
    public String getCodigo(){return codigo;}
    public String getNome(){return nome;}
    public String getMarca(){return marca;}
    public boolean getIsKG(){return isKG;}
    public BigDecimal getPreco(){return preco;}
    
    
}