package model;




public class Cliente {

    
    private int id;
    private String nome;
    private String cpf;
    private String telefone;

    
    public Cliente(){}

    public Cliente(
        int id,
        String nome,
        String cpf,
        String telefone
    ){
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
    }
    

    public void setId(int id){this.id = id;}
    public void setNome(String nome){this.nome = nome;}
    public void setCpf(String cpf){this.cpf = cpf;}
    public void setTelefone(String telefone){this.telefone = telefone;}

    
    public int getId(){return id;}
    public String getNome(){return nome;}
    public String getCpf(){return cpf;}
    public String getTelefone(){return telefone;}
    
    
}