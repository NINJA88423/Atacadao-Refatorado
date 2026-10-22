package service;



import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import model.ProdutoVenda;



public class CalculadoraVenda {

    
    private static final BigDecimal CEM = new BigDecimal("100");

    
    public BigDecimal calcularSubtotal(BigDecimal preco, float quantidade, float desconto) {
        if (preco == null || preco.signum() < 0) {
            throw new RegraNegocioException("Preço inválido.");
        }
        if (quantidade <= 0) {
            throw new RegraNegocioException("A quantidade deve ser maior que zero.");
        }
        if (desconto < 0 || desconto > 100) {
            throw new RegraNegocioException("O desconto deve estar entre 0 e 100%.");
        }
        BigDecimal qtd = new BigDecimal(String.valueOf(quantidade));
        BigDecimal fator = BigDecimal.ONE.subtract(
            new BigDecimal(String.valueOf(desconto)).divide(CEM));
        return preco.multiply(qtd).multiply(fator).setScale(2, RoundingMode.HALF_UP);
    }

    
    public BigDecimal calcularTotal(List<ProdutoVenda> itens) {
        BigDecimal total = BigDecimal.ZERO;
        for (ProdutoVenda item : itens) {
            total = total.add(item.getSubtotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
    
    
}
