package com.mycompany.atacadaorefatorado;

import connection.AtacadaoConnection;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import model.Cliente;
import model.Intervalo;
import model.Produto;
import model.ProdutoVenda;
import model.Usuario;
import model.Venda;
import repository.ClienteDAO;
import repository.ProdutoDAO;
import repository.ProdutoVendaDAO;
import repository.UsuarioDAO;
import repository.VendaDAO;
import service.modelservice.ClienteService;
import service.modelservice.ProdutoService;
import service.modelservice.VendaService;
import service.regras.AutenticacaoService;
import service.regras.CalculadoraVenda;
import service.regras.GeradorCodigoProduto;
import service.regras.RegraNegocioException;
import service.regras.ValidadorCadastro;
import util.Formatador;

/**
 * Testes manuais executados pelo main().
 *
 * Parte 1: regras de negócio, sem banco de dados.
 * Parte 2: serviços usando o banco atacadaodatabase que já existe.
 *          A parte 2 SOMENTE LÊ: não insere, altera nem apaga registros.
 *          Ela escolhe os registros de teste a partir do próprio banco (primeiro cliente,
 *          primeiro produto por unidade, primeiro produto por kg, primeiro usuário),
 *          então continua valendo se os dados do banco mudarem.
 */
public class AtacadaoRefatorado {

    /** Diferença aceita entre a soma dos itens e o total da venda (as colunas do banco são float com 2 casas). */
    private static final BigDecimal TOLERANCIA = new BigDecimal("0.05");

    private static int aprovados = 0;
    private static int reprovados = 0;

    public static void main(String[] args) {
        System.out.println("===== PARTE 1: REGRAS DE NEGÓCIO (SEM BANCO) =====");
        testarValidador();
        testarCalculadora();
        testarFormatador();
        testarGeradorCodigo();

        System.out.println();
        System.out.println("===== PARTE 2: SERVIÇOS COM O BANCO EXISTENTE (SOMENTE LEITURA) =====");
        if (bancoDisponivel()) {
            testarComBanco();
        } else {
            System.out.println("Banco indisponível: parte 2 ignorada.");
        }

        System.out.println();
        System.out.println("===== RESULTADO: " + aprovados + " aprovados, " + reprovados + " reprovados =====");
    }

    // ---------------------------------------------------------------- PARTE 1

    private static void testarValidador() {
        System.out.println("-- ValidadorCadastro");
        ValidadorCadastro v = new ValidadorCadastro();

        verificar("cliente válido não gera erros",
                v.validarCliente("Maria Silva", "12345678901", "999999999").isEmpty());
        verificar("CPF com 10 dígitos é rejeitado",
                !v.validarCliente("Maria Silva", "1234567890", "999999999").isEmpty());
        verificar("CPF com letras é rejeitado",
                !v.validarCliente("Maria Silva", "1234567890a", "999999999").isEmpty());
        verificar("nome com 2 letras é rejeitado",
                !v.validarCliente("Ma", "12345678901", "999999999").isEmpty());
        verificar("telefone de 11 dígitos é aceito",
                v.validarCliente("Maria Silva", "12345678901", "51999999999").isEmpty());
        verificar("telefone de 10 dígitos é rejeitado",
                !v.validarCliente("Maria Silva", "12345678901", "5199999999").isEmpty());
        verificar("nome com acento é aceito",
                v.validarCliente("César Miguel Streck Martins", "03033561039", "55999592764").isEmpty());
        verificar("valor nulo é rejeitado sem estourar erro",
                !v.validarCliente(null, null, null).isEmpty());
        verificar("login em formato de e-mail e senha de 8+ caracteres são aceitos",
                v.validarCredenciais("ana@teste.com", "senha1234").isEmpty());
        verificar("login sem @ é rejeitado",
                !v.validarCredenciais("ana", "senha1234").isEmpty());
        verificar("senha curta é rejeitada",
                !v.validarCredenciais("ana@teste.com", "123").isEmpty());
        verificar("preço com vírgula e duas casas é aceito",
                v.validarProduto("Arroz", "12,50").isEmpty());
        verificar("preço sem casas decimais é rejeitado",
                !v.validarProduto("Arroz", "12").isEmpty());
    }

    private static void testarCalculadora() {
        System.out.println("-- CalculadoraVenda");
        CalculadoraVenda c = new CalculadoraVenda();

        verificar("10,00 x 3 sem desconto = 30,00",
                igual("30.00", c.calcularSubtotal(new BigDecimal("10.00"), 3, 0)));
        verificar("10,00 x 3 com 10% de desconto = 27,00",
                igual("27.00", c.calcularSubtotal(new BigDecimal("10.00"), 3, 10)));
        verificar("produto por kg: 8,99 x 1,5 = 13,49 (arredondado)",
                igual("13.49", c.calcularSubtotal(new BigDecimal("8.99"), 1.5f, 0)));
        verificar("desconto de 100% resulta em 0,00",
                igual("0.00", c.calcularSubtotal(new BigDecimal("10.00"), 2, 100)));
        verificarExcecao("desconto acima de 100% é rejeitado",
                () -> c.calcularSubtotal(new BigDecimal("10.00"), 1, 150));
        verificarExcecao("desconto negativo é rejeitado",
                () -> c.calcularSubtotal(new BigDecimal("10.00"), 1, -5));
        verificarExcecao("quantidade zero é rejeitada",
                () -> c.calcularSubtotal(new BigDecimal("10.00"), 0, 0));

        ProdutoVenda a = new ProdutoVenda();
        a.setSubtotal(new BigDecimal("27.00"));
        ProdutoVenda b = new ProdutoVenda();
        b.setSubtotal(new BigDecimal("0.10"));
        List<ProdutoVenda> itens = new ArrayList<>();
        itens.add(a);
        itens.add(b);
        verificar("total soma os subtotais (27,00 + 0,10 = 27,10)", igual("27.10", c.calcularTotal(itens)));
        verificar("total de carrinho vazio é 0,00", igual("0.00", c.calcularTotal(new ArrayList<>())));
    }

    private static void testarFormatador() {
        System.out.println("-- Formatador");
        Formatador f = new Formatador();

        verificar("moeda formata com milhar e vírgula", "R$ 1.234,50".equals(f.moeda(new BigDecimal("1234.5"))));
        verificar("quantidade por kg mostra 3 casas", "1,500 kg".equals(f.quantidade(1.5f, true)));
        verificar("quantidade por unidade não mostra casas", "3".equals(f.quantidade(3, false)));
        verificar("desconto 10 vira -10%", "-10%".equals(f.desconto(10)));
        verificar("desconto 0 vira texto vazio", "".equals(f.desconto(0)));
        verificar("CPF recebe pontos e traço", "123.456.789-01".equals(f.cpf("12345678901")));
        verificar("data e hora no formato brasileiro",
                "21/10/2026 15:30".equals(f.dataHora(LocalDateTime.of(2026, 10, 21, 15, 30))));
    }

    private static void testarGeradorCodigo() {
        System.out.println("-- GeradorCodigoProduto");
        String codigo = new GeradorCodigoProduto().Gerar();
        verificar("código gerado contém só dígitos", codigo != null && codigo.matches("\\d+"));
    }

    // ---------------------------------------------------------------- PARTE 2

    private static boolean bancoDisponivel() {
        Connection con = new AtacadaoConnection().connect();
        if (con == null) {
            return false;
        }
        try {
            con.close();
        } catch (Exception e) {
            // conexão apenas de verificação
        }
        return true;
    }

    private static void testarComBanco() {
        ValidadorCadastro validador = new ValidadorCadastro();
        CalculadoraVenda calculadora = new CalculadoraVenda();

        ClienteDAO clienteDao = new ClienteDAO();
        ProdutoDAO produtoDao = new ProdutoDAO();
        UsuarioDAO usuarioDao = new UsuarioDAO();
        VendaDAO vendaDao = new VendaDAO();
        ProdutoVendaDAO itemDao = new ProdutoVendaDAO();

        ClienteService clientes = new ClienteService(clienteDao, validador);
        ProdutoService produtos = new ProdutoService(produtoDao, validador, new GeradorCodigoProduto());
        VendaService vendas = new VendaService(vendaDao, itemDao, clienteDao, calculadora);
        AutenticacaoService auth = new AutenticacaoService(usuarioDao, validador);

        // Fotografia do banco antes dos testes, para provar no final que nada mudou
        int[] antes = contarRegistros(clienteDao, produtoDao, usuarioDao, vendaDao, itemDao);
        System.out.println("Registros no banco: clientes=" + antes[0] + ", produtos=" + antes[1]
                + ", usuarios=" + antes[2] + ", vendas=" + antes[3] + ", itens de venda=" + antes[4]);

        try {
            testarClientes(clientes);
            testarProdutosEVenda(produtos, vendas);
            testarAutenticacao(usuarioDao, auth);
            testarVendasGravadas(vendaDao, itemDao, calculadora);
            testarIntervalos(vendas, vendaDao);
        } catch (Exception e) {
            falha("a parte 2 foi interrompida por um erro inesperado: " + e);
        }

        System.out.println("-- Garantia de que o banco não foi alterado");
        int[] depois = contarRegistros(clienteDao, produtoDao, usuarioDao, vendaDao, itemDao);
        verificar("quantidade de registros igual antes e depois (cliente, produto, usuario, venda, produtovenda)",
                Arrays.equals(antes, depois));
    }

    private static void testarClientes(ClienteService clientes) {
        System.out.println("-- ClienteService");
        List<Cliente> todos = clientes.listar();
        verificar("listagem de clientes não está vazia", todos != null && !todos.isEmpty());
        if (todos == null || todos.isEmpty()) {
            return;
        }
        Cliente existente = todos.get(0);
        System.out.println("   (cliente usado nos testes: " + existente.getNome() + ")");

        verificar("cliente existente é encontrado pelo CPF",
                existente.getNome().equals(clientes.buscarPorCpf(existente.getCpf()).getNome()));
        verificarExcecao("CPF inexistente lança exceção", () -> clientes.buscarPorCpf("00000000000"));
        verificarExcecaoComMensagem("cadastrar com CPF já existente é rejeitado (nada é gravado)", "Já existe",
                () -> clientes.cadastrar("Nome Valido", existente.getCpf(), "55999999999"));
        verificarExcecao("cadastrar com dados inválidos é rejeitado antes de ir ao banco",
                () -> clientes.cadastrar("A", "123", "1"));
    }

    private static void testarProdutosEVenda(ProdutoService produtos, VendaService vendas) {
        System.out.println("-- ProdutoService e VendaService (somente cálculos e validações, sem gravar)");
        List<Produto> todos = produtos.listar();
        verificar("listagem de produtos não está vazia", todos != null && !todos.isEmpty());
        if (todos == null || todos.isEmpty()) {
            return;
        }
        Produto porUnidade = null;
        Produto porKg = null;
        for (Produto p : todos) {
            if (p.getIsKG() && porKg == null) {
                porKg = p;
            }
            if (!p.getIsKG() && porUnidade == null) {
                porUnidade = p;
            }
        }
        verificarExcecao("código de produto inexistente lança exceção", () -> produtos.buscarPorCodigo("0"));
        verificarExcecao("cadastrar produto com preço inválido é rejeitado (nada é gravado)",
                () -> produtos.cadastrar("Produto", "Marca", "abc", false));

        if (porUnidade != null) {
            final Produto un = porUnidade;
            System.out.println("   (produto por unidade usado: " + un.getNome() + ", preço " + un.getPreco() + ")");
            verificar("produto é encontrado pelo código",
                    un.getNome().equals(produtos.buscarPorCodigo(un.getCodigo()).getNome()));
            ProdutoVenda item = vendas.criarItem(un, 2, 10);
            verificar("item por unidade: preço x 2 com 10% de desconto",
                    esperado(un.getPreco(), "2", "10").compareTo(item.getSubtotal()) == 0);
            verificarExcecao("produto por unidade não aceita quantidade fracionada",
                    () -> vendas.criarItem(un, 1.5f, 0));
            verificarExcecao("venda sem itens é rejeitada",
                    () -> vendas.concluirVenda("00000000000", new ArrayList<>()));
            List<ProdutoVenda> carrinho = new ArrayList<>();
            carrinho.add(item);
            verificarExcecao("venda para CPF inexistente é rejeitada (nada é gravado)",
                    () -> vendas.concluirVenda("00000000000", carrinho));
        } else {
            System.out.println("   (não há produto por unidade no banco: testes correspondentes ignorados)");
        }

        if (porKg != null) {
            final Produto kg = porKg;
            System.out.println("   (produto por kg usado: " + kg.getNome() + ", preço " + kg.getPreco() + ")");
            ProdutoVenda item = vendas.criarItem(kg, 3.5f, 10);
            verificar("item por kg: preço x 3,5 com 10% de desconto",
                    esperado(kg.getPreco(), "3.5", "10").compareTo(item.getSubtotal()) == 0);
        } else {
            System.out.println("   (não há produto por kg no banco: teste correspondente ignorado)");
        }
    }

    private static void testarAutenticacao(UsuarioDAO usuarioDao, AutenticacaoService auth) {
        System.out.println("-- AutenticacaoService");
        ValidadorCadastro validador = new ValidadorCadastro();
        Usuario escolhido = null;
        for (Usuario u : usuarioDao.read()) {
            if (validador.validarCredenciais(u.getLogin(), u.getSenha()).isEmpty()) {
                escolhido = u;
                break;
            }
        }
        if (escolhido == null) {
            System.out.println("   (nenhum usuário com credenciais no formato aceito: testes de login ignorados)");
        } else {
            final Usuario u = escolhido;
            System.out.println("   (usuário usado: " + u.getLogin() + ")");
            verificar("login e senha corretos autenticam o usuário", auth.autenticar(u.getLogin(), u.getSenha()).getId() == u.getId());
            verificarExcecao("senha errada é rejeitada", () -> auth.autenticar(u.getLogin(), u.getSenha() + "X9"));
        }
        verificarExcecao("login em formato inválido é rejeitado", () -> auth.autenticar("abc", "senha1234"));
    }

    /** Em toda venda que tem itens, a soma dos subtotais deve bater com o total gravado. */
    private static void testarVendasGravadas(VendaDAO vendaDao, ProdutoVendaDAO itemDao, CalculadoraVenda calculadora) {
        System.out.println("-- Vendas já gravadas");
        int comItens = 0;
        List<Integer> divergentes = new ArrayList<>();
        for (Venda v : vendaDao.read()) {
            List<ProdutoVenda> itens = itemDao.readByIdVenda(v.getId());
            if (itens.isEmpty()) {
                continue;
            }
            comItens++;
            BigDecimal diferenca = calculadora.calcularTotal(itens).subtract(v.getTotal()).abs();
            if (diferenca.compareTo(TOLERANCIA) > 0) {
                divergentes.add(v.getId());
            }
        }
        verificar("existe ao menos uma venda com itens", comItens > 0);
        verificar("soma dos itens = total gravado nas " + comItens + " vendas com itens (tolerância de 5 centavos)"
                + (divergentes.isEmpty() ? "" : "; divergem: " + divergentes), divergentes.isEmpty());
    }

    /** Compara o resultado do filtro feito no SQL com o mesmo filtro calculado em Java. */
    private static void testarIntervalos(VendaService vendas, VendaDAO vendaDao) {
        System.out.println("-- Filtro de vendas por intervalo (SQL comparado com cálculo em Java)");
        LocalDate hoje = LocalDate.now();
        LocalDate segunda = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        compararIntervalo(vendas, vendaDao, "HOJE", Intervalo.HOJE,
                d -> d.toLocalDate().equals(hoje));
        compararIntervalo(vendas, vendaDao, "SEMANA_ATUAL (segunda a domingo)", Intervalo.SEMANA_ATUAL,
                d -> !d.toLocalDate().isBefore(segunda) && d.toLocalDate().isBefore(segunda.plusDays(7)));
        compararIntervalo(vendas, vendaDao, "MES_ATUAL", Intervalo.MES_ATUAL,
                d -> d.getYear() == hoje.getYear() && d.getMonth() == hoje.getMonth());
        compararIntervalo(vendas, vendaDao, "ANO_ATUAL", Intervalo.ANO_ATUAL,
                d -> d.getYear() == hoje.getYear());
    }

    private static void compararIntervalo(VendaService vendas, VendaDAO vendaDao, String nome,
                                          Intervalo intervalo, Predicate<LocalDateTime> deveEstar) {
        Set<Integer> esperado = new HashSet<>();
        for (Venda v : vendaDao.read()) {
            if (deveEstar.test(v.getDatahora())) {
                esperado.add(v.getId());
            }
        }
        Set<Integer> obtido = new HashSet<>();
        for (Venda v : vendas.listarPorIntervalo(intervalo)) {
            obtido.add(v.getId());
        }
        verificar("intervalo " + nome + ": o banco devolve as mesmas vendas do cálculo em Java ("
                + esperado.size() + " vendas)", esperado.equals(obtido));
    }

    private static int[] contarRegistros(ClienteDAO c, ProdutoDAO p, UsuarioDAO u, VendaDAO v, ProdutoVendaDAO i) {
        return new int[]{c.read().size(), p.read().size(), u.read().size(), v.read().size(), i.read().size()};
    }

    // ---------------------------------------------------------------- APOIO

    /** preço x quantidade x (1 - desconto/100), calculado de forma independente da CalculadoraVenda. */
    private static BigDecimal esperado(BigDecimal preco, String quantidade, String descontoPercentual) {
        BigDecimal fator = new BigDecimal("100").subtract(new BigDecimal(descontoPercentual)).movePointLeft(2);
        return preco.multiply(new BigDecimal(quantidade)).multiply(fator).setScale(2, RoundingMode.HALF_UP);
    }

    private static boolean igual(String esperado, BigDecimal obtido) {
        return obtido != null && new BigDecimal(esperado).compareTo(obtido) == 0;
    }

    private static void verificar(String descricao, boolean condicao) {
        if (condicao) {
            aprovados++;
            System.out.println("  [OK]    " + descricao);
        } else {
            falha(descricao);
        }
    }

    private static void verificarExcecao(String descricao, Runnable acao) {
        verificarExcecaoComMensagem(descricao, null, acao);
    }

    private static void verificarExcecaoComMensagem(String descricao, String trechoDaMensagem, Runnable acao) {
        try {
            acao.run();
            falha(descricao + " (esperava RegraNegocioException, mas nada foi lançado)");
        } catch (RegraNegocioException e) {
            if (trechoDaMensagem != null && !e.getMessage().contains(trechoDaMensagem)) {
                falha(descricao + " (mensagem inesperada: \"" + e.getMessage() + "\")");
            } else {
                aprovados++;
                System.out.println("  [OK]    " + descricao + " -> \"" + e.getMessage() + "\"");
            }
        } catch (Exception e) {
            falha(descricao + " (lançou " + e.getClass().getSimpleName() + " em vez de RegraNegocioException)");
        }
    }

    private static void falha(String descricao) {
        reprovados++;
        System.out.println("  [FALHA] " + descricao);
    }
}