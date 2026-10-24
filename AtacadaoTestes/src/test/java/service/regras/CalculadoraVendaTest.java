package service.regras;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import model.ProdutoVenda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários da CalculadoraVenda (cálculo de subtotal e total da venda).
 * Não acessam o banco de dados.
 */
class CalculadoraVendaTest {

    private CalculadoraVenda calculadora;

    @BeforeEach
    void preparar() {
        calculadora = new CalculadoraVenda();
    }

    // ---------- calcularSubtotal ----------

    @Test
    void subtotalSemDescontoMultiplicaPrecoPelaQuantidade() {
        BigDecimal resultado = calculadora.calcularSubtotal(new BigDecimal("10.00"), 2f, 0f);
        assertEquals(new BigDecimal("20.00"), resultado);
    }

    @Test
    void subtotalComDescontoDeDezPorCento() {
        BigDecimal resultado = calculadora.calcularSubtotal(new BigDecimal("10.00"), 2f, 10f);
        assertEquals(new BigDecimal("18.00"), resultado);
    }

    @Test
    void subtotalDeProdutoVendidoPorQuiloArredondaParaCima() {
        // 7,99 x 1,5 = 11,985 -> arredondado (HALF_UP) para 11,99
        BigDecimal resultado = calculadora.calcularSubtotal(new BigDecimal("7.99"), 1.5f, 0f);
        assertEquals(new BigDecimal("11.99"), resultado);
    }

    @Test
    void subtotalComDescontoDeCemPorCentoEhZero() {
        BigDecimal resultado = calculadora.calcularSubtotal(new BigDecimal("50.00"), 3f, 100f);
        assertEquals(new BigDecimal("0.00"), resultado);
    }

    @Test
    void subtotalComPrecoZeroEhZero() {
        BigDecimal resultado = calculadora.calcularSubtotal(BigDecimal.ZERO, 5f, 0f);
        assertEquals(new BigDecimal("0.00"), resultado);
    }

    @Test
    void precoNuloLancaExcecao() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> calculadora.calcularSubtotal(null, 1f, 0f));
        assertEquals("Preço inválido.", erro.getMessage());
    }

    @Test
    void precoNegativoLancaExcecao() {
        assertThrows(RegraNegocioException.class,
                () -> calculadora.calcularSubtotal(new BigDecimal("-1.00"), 1f, 0f));
    }

    @Test
    void quantidadeZeroLancaExcecao() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> calculadora.calcularSubtotal(new BigDecimal("10.00"), 0f, 0f));
        assertEquals("A quantidade deve ser maior que zero.", erro.getMessage());
    }

    @Test
    void quantidadeNegativaLancaExcecao() {
        assertThrows(RegraNegocioException.class,
                () -> calculadora.calcularSubtotal(new BigDecimal("10.00"), -2f, 0f));
    }

    @Test
    void descontoNegativoLancaExcecao() {
        assertThrows(RegraNegocioException.class,
                () -> calculadora.calcularSubtotal(new BigDecimal("10.00"), 1f, -5f));
    }

    @Test
    void descontoMaiorQueCemLancaExcecao() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> calculadora.calcularSubtotal(new BigDecimal("10.00"), 1f, 100.5f));
        assertEquals("O desconto deve estar entre 0 e 100%.", erro.getMessage());
    }

    // ---------- calcularTotal ----------

    @Test
    void totalSomaOsSubtotaisDosItens() {
        List<ProdutoVenda> itens = new ArrayList<>();
        itens.add(item("18.00"));
        itens.add(item("11.99"));
        itens.add(item("0.01"));

        assertEquals(new BigDecimal("30.00"), calculadora.calcularTotal(itens));
    }

    @Test
    void totalDeListaVaziaEhZero() {
        assertEquals(new BigDecimal("0.00"), calculadora.calcularTotal(new ArrayList<>()));
    }

    @Test
    void totalDeUmUnicoItemEhOSubtotalDele() {
        List<ProdutoVenda> itens = List.of(item("25.50"));
        assertEquals(new BigDecimal("25.50"), calculadora.calcularTotal(itens));
    }

    private ProdutoVenda item(String subtotal) {
        return new ProdutoVenda(0, 0, 0, false, 1f, 0f, new BigDecimal(subtotal));
    }
}
