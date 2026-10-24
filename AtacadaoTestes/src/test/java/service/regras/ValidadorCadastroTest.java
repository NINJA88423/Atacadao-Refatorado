package service.regras;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários do ValidadorCadastro (regras de validação de cliente,
 * usuário, credenciais e produto). Cada método devolve a lista de erros;
 * lista vazia significa dados válidos.
 */
class ValidadorCadastroTest {

    private ValidadorCadastro validador;

    @BeforeEach
    void preparar() {
        validador = new ValidadorCadastro();
    }

    // ---------- Cliente ----------

    @Test
    void clienteValidoNaoGeraErros() {
        List<String> erros = validador.validarCliente("Maria Silva", "12345678901", "999999999");
        assertTrue(erros.isEmpty());
    }

    @Test
    void clienteAceitaTelefoneComOnzeDigitos() {
        List<String> erros = validador.validarCliente("João Souza", "12345678901", "51999999999");
        assertTrue(erros.isEmpty());
    }

    @Test
    void nomeComMenosDeTresLetrasEhInvalido() {
        List<String> erros = validador.validarCliente("Al", "12345678901", "999999999");
        assertEquals(1, erros.size());
        assertEquals("Nome inválido (mínimo 3 letras).", erros.get(0));
    }

    @Test
    void nomeComNumerosEhInvalido() {
        List<String> erros = validador.validarCliente("Maria 123", "12345678901", "999999999");
        assertEquals(1, erros.size());
    }

    @Test
    void cpfComDezDigitosEhInvalido() {
        List<String> erros = validador.validarCliente("Maria Silva", "1234567890", "999999999");
        assertEquals(1, erros.size());
        assertEquals("CPF inválido (deve ter 11 dígitos).", erros.get(0));
    }

    @Test
    void cpfComLetrasEhInvalido() {
        List<String> erros = validador.validarCliente("Maria Silva", "1234567890a", "999999999");
        assertEquals(1, erros.size());
    }

    @Test
    void telefoneComDezDigitosEhInvalido() {
        List<String> erros = validador.validarCliente("Maria Silva", "12345678901", "5199999999");
        assertEquals(1, erros.size());
        assertEquals("Telefone inválido (9 ou 11 dígitos).", erros.get(0));
    }

    @Test
    void camposNulosGeramUmErroParaCadaCampo() {
        List<String> erros = validador.validarCliente(null, null, null);
        assertEquals(3, erros.size());
    }

    @Test
    void variosCamposInvalidosAcumulamErros() {
        List<String> erros = validador.validarCliente("Al", "123", "1");
        assertEquals(3, erros.size());
    }

    // ---------- Credenciais ----------

    @Test
    void credenciaisValidasNaoGeramErros() {
        List<String> erros = validador.validarCredenciais("usuario@email.com", "senha1234");
        assertTrue(erros.isEmpty());
    }

    @Test
    void loginSemArrobaEhInvalido() {
        List<String> erros = validador.validarCredenciais("usuarioemail.com", "senha1234");
        assertEquals(1, erros.size());
        assertEquals("Login inválido (use um e-mail).", erros.get(0));
    }

    @Test
    void senhaComSeteCaracteresEhInvalida() {
        List<String> erros = validador.validarCredenciais("usuario@email.com", "abc1234");
        assertEquals(1, erros.size());
    }

    @Test
    void senhaComMaisDeVinteCaracteresEhInvalida() {
        List<String> erros = validador.validarCredenciais("usuario@email.com", "a".repeat(21));
        assertEquals(1, erros.size());
    }

    @Test
    void senhaComVinteCaracteresEhValida() {
        List<String> erros = validador.validarCredenciais("usuario@email.com", "a".repeat(20));
        assertTrue(erros.isEmpty());
    }

    // ---------- Usuário (cliente + credenciais) ----------

    @Test
    void usuarioValidoNaoGeraErros() {
        List<String> erros = validador.validarUsuario(
                "Carlos Lima", "12345678901", "999999999", "carlos@email.com", "senha1234");
        assertTrue(erros.isEmpty());
    }

    @Test
    void usuarioSomaErrosDeCadastroECredenciais() {
        List<String> erros = validador.validarUsuario(
                "Al", "12345678901", "999999999", "sem-arroba", "senha1234");
        assertEquals(2, erros.size());
    }

    // ---------- Produto ----------

    @Test
    void produtoValidoNaoGeraErros() {
        List<String> erros = validador.validarProduto("Arroz 5kg", "12,50");
        assertTrue(erros.isEmpty());
    }

    @Test
    void precoComPontoDecimalEhAceito() {
        List<String> erros = validador.validarProduto("Arroz 5kg", "12.50");
        assertTrue(erros.isEmpty());
    }

    @Test
    void precoSemCasasDecimaisEhInvalido() {
        List<String> erros = validador.validarProduto("Arroz 5kg", "12");
        assertEquals(1, erros.size());
    }

    @Test
    void precoComUmaCasaDecimalEhInvalido() {
        List<String> erros = validador.validarProduto("Arroz 5kg", "12,5");
        assertEquals(1, erros.size());
    }

    @Test
    void nomeDeProdutoVazioEhInvalido() {
        List<String> erros = validador.validarProduto("", "12,50");
        assertEquals(1, erros.size());
        assertEquals("Nome do produto é obrigatório.", erros.get(0));
    }
}
