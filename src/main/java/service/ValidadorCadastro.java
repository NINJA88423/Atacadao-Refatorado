package service;



import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;



public class ValidadorCadastro {

    
    private static final Pattern NOME = Pattern.compile("^[A-Za-zÀ-ÿ ]{3,}$");
    private static final Pattern CPF = Pattern.compile("^\\d{11}$");
    private static final Pattern TELEFONE = Pattern.compile("^(\\d{9}|\\d{11})$");
    private static final Pattern LOGIN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern SENHA = Pattern.compile("^\\w{8,20}$");
    private static final Pattern NOME_PRODUTO = Pattern.compile("^.+$");
    private static final Pattern PRECO = Pattern.compile("^\\d+[,.]\\d{2}$");

    
    public List<String> validarCliente(String nome, String cpf, String telefone) {
        List<String> erros = new ArrayList<>();
        verificar(erros, NOME, nome, "Nome inválido (mínimo 3 letras).");
        verificar(erros, CPF, cpf, "CPF inválido (deve ter 11 dígitos).");
        verificar(erros, TELEFONE, telefone, "Telefone inválido (9 ou 11 dígitos).");
        return erros;
    }
    

    public List<String> validarUsuario(String nome, String cpf, String telefone, String login, String senha) {
        List<String> erros = validarCliente(nome, cpf, telefone);
        erros.addAll(validarCredenciais(login, senha));
        return erros;
    }
    

    public List<String> validarCredenciais(String login, String senha) {
        List<String> erros = new ArrayList<>();
        verificar(erros, LOGIN, login, "Login inválido (use um e-mail).");
        verificar(erros, SENHA, senha, "Senha inválida (8 a 20 caracteres, letras, números ou _).");
        return erros;
    }
    

    public List<String> validarProduto(String nome, String preco) {
        List<String> erros = new ArrayList<>();
        verificar(erros, NOME_PRODUTO, nome, "Nome do produto é obrigatório.");
        verificar(erros, PRECO, preco, "Preço inválido (use duas casas decimais, ex.: 12,50).");
        return erros;
    }
    

    private void verificar(List<String> erros, Pattern regra, String valor, String mensagem) {
        if (valor == null || !regra.matcher(valor).matches()) {
            erros.add(mensagem);
        }
    }
    
    
}
