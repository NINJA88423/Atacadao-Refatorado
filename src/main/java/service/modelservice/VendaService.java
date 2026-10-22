package service.modelservice;



import java.time.LocalDateTime;
import java.util.List;
import model.Cliente;
import model.Intervalo;
import model.Produto;
import model.ProdutoVenda;
import model.Venda;
import repository.ClienteDAO;
import repository.ProdutoVendaDAO;
import repository.VendaDAO;
import service.CalculadoraVenda;
import service.RegraNegocioException;



public class VendaService {
    

    private final VendaDAO vendaRepositorio;
    private final ProdutoVendaDAO itemRepositorio;
    private final ClienteDAO clienteRepositorio;
    private final CalculadoraVenda calculadora;

    
    public VendaService(VendaDAO vendaRepositorio, ProdutoVendaDAO itemRepositorio,
                        ClienteDAO clienteRepositorio, CalculadoraVenda calculadora) {
        this.vendaRepositorio = vendaRepositorio;
        this.itemRepositorio = itemRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.calculadora = calculadora;
    }

    
    public ProdutoVenda criarItem(Produto produto, float quantidade, float desconto) {
        if (!produto.getIsKG() && quantidade != Math.floor(quantidade)) {
            throw new RegraNegocioException("Produtos vendidos por unidade exigem quantidade inteira.");
        }
        ProdutoVenda item = new ProdutoVenda();
        item.setIdProduto(produto.getId());
        item.setIsKG(produto.getIsKG());
        item.setQuantidade(quantidade);
        item.setDesconto(desconto);
        item.setSubtotal(calculadora.calcularSubtotal(produto.getPreco(), quantidade, desconto));
        return item;
    }

    
    public void atualizarSubtotal(ProdutoVenda item, Produto produto) {
        item.setSubtotal(calculadora.calcularSubtotal(
                produto.getPreco(), item.getQuantidade(), item.getDesconto()));
    }

    
    public Venda concluirVenda(String cpfCliente, List<ProdutoVenda> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new RegraNegocioException("Não é possível concluir uma venda sem produtos.");
        }
        Cliente cliente = clienteRepositorio.readByCpf(cpfCliente);
        if (cliente == null) {
            throw new RegraNegocioException("Cliente não encontrado para o CPF informado.");
        }
        Venda venda = new Venda();
        venda.setIdCliente(cliente.getId());
        venda.setDatahora(LocalDateTime.now());
        venda.setTotal(calculadora.calcularTotal(itens));

        Venda gravada = vendaRepositorio.create(venda);
        if (gravada == null) {
            throw new RegraNegocioException("Não foi possível gravar a venda.");
        }
        for (ProdutoVenda item : itens) {
            item.setIdVenda(gravada.getId());
            itemRepositorio.create(item);
        }
        return gravada;
    }

    
    public List<Venda> listarPorIntervalo(Intervalo intervalo) {
        return vendaRepositorio.readByCalendarId(intervalo);
    }
    
    
}
