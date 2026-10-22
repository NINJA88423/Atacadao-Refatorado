package service.modelservice;



import java.math.BigDecimal;
import java.util.List;
import model.Produto;
import repository.ProdutoDAO;
import service.GeradorCodigoProduto;
import service.RegraNegocioException;
import service.ValidadorCadastro;



public class ProdutoService {

    
    private final ProdutoDAO repositorio;
    private final ValidadorCadastro validador;
    private final GeradorCodigoProduto gerador;

    
    public ProdutoService(ProdutoDAO repositorio, ValidadorCadastro validador, GeradorCodigoProduto gerador) {
        this.repositorio = repositorio;
        this.validador = validador;
        this.gerador = gerador;
    }

    
    public Produto cadastrar(String nome, String marca, String preco, boolean vendidoPorKg) {
        List<String> erros = validador.validarProduto(nome, preco);
        if (!erros.isEmpty()) {
            throw new RegraNegocioException(String.join(" ", erros));
        }
        Produto produto = new Produto();
        produto.setCodigo(gerador.Gerar());
        produto.setNome(nome);
        produto.setMarca(marca);
        produto.setIsKG(vendidoPorKg);
        produto.setPreco(new BigDecimal(preco.replace(',', '.')));
        if (repositorio.readByCodigo(produto.getCodigo()) != null) {
            throw new RegraNegocioException("Já existe um produto com o mesmo código.");
        }
        if (!repositorio.create(produto)) {
            throw new RegraNegocioException("Não foi possível cadastrar o produto.");
        }
        return produto;
    }
    

    public Produto buscarPorCodigo(String codigo) {
        Produto produto = repositorio.readByCodigo(codigo);
        if (produto == null) {
            throw new RegraNegocioException("Produto não encontrado.");
        }
        return produto;
    }
    

    public List<Produto> listar() {
        return repositorio.read();
    }
    

    public void remover(String codigo) {
        buscarPorCodigo(codigo);
        repositorio.delete(codigo);
    }
    
    
}
