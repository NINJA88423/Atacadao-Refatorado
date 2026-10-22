package service;

import java.util.List;
import model.Usuario;
import repository.UsuarioDAO;

/** Regra de login, separada do cadastro de usuários (responsabilidade única). */
public class AutenticacaoService {

    private final UsuarioDAO repositorio;
    private final ValidadorCadastro validador;

    public AutenticacaoService(UsuarioDAO repositorio, ValidadorCadastro validador) {
        this.repositorio = repositorio;
        this.validador = validador;
    }

    public Usuario autenticar(String login, String senha) {
        List<String> erros = validador.validarCredenciais(login, senha);
        if (!erros.isEmpty()) {
            throw new RegraNegocioException(String.join(" ", erros));
        }
        Usuario usuario = repositorio.readByLogin(login, senha);
        if (usuario == null) {
            throw new RegraNegocioException("Login ou senha incorretos.");
        }
        return usuario;
    }
}
