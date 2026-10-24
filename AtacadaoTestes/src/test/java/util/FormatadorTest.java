package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários do Formatador (formatação de valores para exibição).
 */
class FormatadorTest {

    private Formatador formatador;

    @BeforeEach
    void preparar() {
        formatador = new Formatador();
    }

    @Test
    void moedaUsaVirgulaDecimalEPontoDeMilhar() {
        assertEquals("R$ 1.234,50", formatador.moeda(new BigDecimal("1234.5")));
    }

    @Test
    void moedaDeZeroMostraDuasCasas() {
        assertEquals("R$ 0,00", formatador.moeda(BigDecimal.ZERO));
    }

    @Test
    void quantidadePorQuiloMostraTresCasasEUnidade() {
        assertEquals("1,500 kg", formatador.quantidade(1.5f, true));
    }

    @Test
    void quantidadePorUnidadeNaoMostraCasasDecimais() {
        assertEquals("3", formatador.quantidade(3f, false));
    }

    @Test
    void descontoZeroNaoMostraNada() {
        assertEquals("", formatador.desconto(0f));
    }

    @Test
    void descontoInteiroNaoMostraZerosFinais() {
        assertEquals("-10%", formatador.desconto(10f));
    }

    @Test
    void descontoFracionadoUsaVirgula() {
        assertEquals("-2,5%", formatador.desconto(2.5f));
    }

    @Test
    void cpfValidoRecebeMascara() {
        assertEquals("123.456.789-01", formatador.cpf("12345678901"));
    }

    @Test
    void cpfForaDoPadraoEhDevolvidoSemAlteracao() {
        assertEquals("123", formatador.cpf("123"));
    }

    @Test
    void cpfNuloEhDevolvidoNulo() {
        assertNull(formatador.cpf(null));
    }

    @Test
    void dataHoraUsaPadraoBrasileiro() {
        assertEquals("23/10/2026 14:05", formatador.dataHora(LocalDateTime.of(2026, 10, 23, 14, 5)));
    }
}
