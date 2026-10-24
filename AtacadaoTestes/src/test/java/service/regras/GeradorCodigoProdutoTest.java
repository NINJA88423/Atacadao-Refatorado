package service.regras;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Testes unitários do GeradorCodigoProduto.
 * O código é aleatório, então o teste verifica apenas o formato.
 */
class GeradorCodigoProdutoTest {

    @Test
    void codigoGeradoPossuiSomenteDigitos() {
        String codigo = new GeradorCodigoProduto().Gerar();
        assertTrue(codigo.matches("\\d+"));
    }

    @Test
    void codigoGeradoPossuiTrezeDigitosDeTempoMaisQuatroAleatorios() {
        String codigo = new GeradorCodigoProduto().Gerar();
        assertEquals(17, codigo.length());
    }
}
