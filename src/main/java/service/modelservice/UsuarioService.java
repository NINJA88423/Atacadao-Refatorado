package service.modelservice;



import java.util.List;
import model.Usuario;
import repository.UsuarioDAO;
import service.RegraNegocioException;
import service.ValidadorCadastro;



public class UsuarioService {

    
    private final UsuarioDAO repositorio;
    private final ValidadorCadastro validador;

    
    public UsuarioService(UsuarioDAO repositorio, ValidadorCadastro validador) {
        this.repositorio = repositorio;
        this.validador = validador;
    }
    

    public Usuario cadastrar(String nome, String cpf, String telefone, String login, String senha, String cargo) {
        List<String> erros = validador.validarUsuario(nome, cpf, telefone, login, senha);
        if (!erros.isEmpty()) {
            throw new RegraNegocioException(String.join(" ", erros));
        }
        if (repositorio.readByCpf(cpf) != null) {
            throw new RegraNegocioException("Já existe um usuário com o mesmo CPF.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setCpf(cpf);
        usuario.setTelefone(telefone);
        usuario.setLogin(login);
        usuario.setSenha(senha);
        usuario.setCargo(cargo);
        if (!repositorio.create(usuario)) {
            throw new RegraNegocioException("Não foi possível cadastrar o usuário.");
        }
        return usuario;
    }
    

    public List<Usuario> listar() {
        return repositorio.read();
    }
    

    public void remover(String cpf) {
        if (repositorio.readByCpf(cpf) == null) {
            throw new RegraNegocioException("Usuário não encontrado.");
        }
        repositorio.delete(cpf);
    }
    
    
}
