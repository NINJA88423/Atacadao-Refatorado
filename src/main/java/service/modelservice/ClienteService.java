package service.modelservice;



import java.util.List;
import model.Cliente;
import repository.ClienteDAO;
import service.RegraNegocioException;
import service.ValidadorCadastro;



public class ClienteService {

    
    private final ClienteDAO repositorio;
    private final ValidadorCadastro validador;

    
    public ClienteService(ClienteDAO repositorio, ValidadorCadastro validador) {
        this.repositorio = repositorio;
        this.validador = validador;
    }
    

    public Cliente cadastrar(String nome, String cpf, String telefone) {
        exigirValido(validador.validarCliente(nome, cpf, telefone));
        if (repositorio.readByCpf(cpf) != null) {
            throw new RegraNegocioException("Já existe um cliente com o mesmo CPF.");
        }
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setCpf(cpf);
        cliente.setTelefone(telefone);
        if (!repositorio.create(cliente)) {
            throw new RegraNegocioException("Não foi possível cadastrar o cliente.");
        }
        return cliente;
    }
    

    public void atualizarTelefone(String cpf, String novoTelefone) {
        Cliente cliente = buscarPorCpf(cpf);
        exigirValido(validador.validarCliente(cliente.getNome(), cpf, novoTelefone));
        cliente.setTelefone(novoTelefone);
        if (!repositorio.update(cliente)) {
            throw new RegraNegocioException("Não foi possível atualizar o cliente.");
        }
    }
    

    public Cliente buscarPorCpf(String cpf) {
        Cliente cliente = repositorio.readByCpf(cpf);
        if (cliente == null) {
            throw new RegraNegocioException("Cliente não encontrado para o CPF informado.");
        }
        return cliente;
    }
    

    public List<Cliente> listar() {
        return repositorio.read();
    }
    

    public void remover(String cpf) {
        buscarPorCpf(cpf);
        repositorio.delete(cpf);
    }
    

    private void exigirValido(List<String> erros) {
        if (!erros.isEmpty()) {
            throw new RegraNegocioException(String.join(" ", erros));
        }
    }
    
    
}
