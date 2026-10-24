package model;




public class Usuario {
    
    
    private int id;
    private String nome;
    private String cpf;
    private String telefone;
    private String login;
    private String senha;
    private String cargo;
    
    
    public Usuario(){}
    
    public Usuario(
        int id,
        String nome,
        String cpf,
        String telefone,
        String login,
        String senha,
        String cargo
    ){
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.login = login;
        this.senha = senha;
        this.cargo = cargo;
    }
    
    
    public void setId(int id) {this.id = id;}
    public void setNome(String nome) {this.nome = nome;}
    public void setCpf(String cpf) {this.cpf = cpf;}
    public void setTelefone(String telefone) {this.telefone = telefone;}
    public void setLogin(String login) {this.login = login;}
    public void setSenha(String senha) {this.senha = senha;}
    public void setCargo(String cargo) {this.cargo = cargo;}
    
    
    public int getId() {return id;}
    public String getNome() {return nome;}
    public String getCpf() {return cpf;}
    public String getTelefone() {return telefone;}
    public String getLogin() {return login;}
    public String getSenha() {return senha;}
    public String getCargo() {return cargo;}
    
    
}