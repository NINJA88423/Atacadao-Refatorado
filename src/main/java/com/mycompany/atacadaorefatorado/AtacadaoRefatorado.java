package com.mycompany.atacadaorefatorado;




import connection.AtacadaoConnection;

import java.math.BigDecimal;

import java.sql.Connection;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.*;
import repository.*;
import service.modelservice.*;
import service.regras.*;

import util.Formatador;




public class AtacadaoRefatorado {

    private static int aprovados = 0;
    private static int reprovados = 0;

    public static void main(String[] args) {
        System.out.println("===== PARTE 1: REGRAS DE NEGÓCIO (SEM BANCO) =====");
        testarValidador();
        testarCalculadora();
        testarFormatador();
        testarGeradorCodigo();

        System.out.println();
        System.out.println("===== PARTE 2: SERVIÇOS COM BANCO DE DADOS =====");
        if (bancoDisponivel()) {
            testarClienteService();
            testarVendaService();
            testarAutenticacao();
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
            // conexão de teste, sem tratamento
        }
        return true;
    }

    private static void testarClienteService() {
        System.out.println("-- ClienteService");
        ClienteService service = new ClienteService(new ClienteDAO(), new ValidadorCadastro());
        String cpf = cpfAleatorio();
        try {
            service.cadastrar("Cliente Teste", cpf, "999999999");
            verificar("cliente cadastrado é encontrado pelo CPF", cpf.equals(service.buscarPorCpf(cpf).getCpf()));

            verificarExcecao("CPF duplicado é rejeitado",
                    () -> service.cadastrar("Outro Nome", cpf, "888888888"));
            verificarExcecao("dados inválidos são rejeitados antes de ir ao banco",
                    () -> service.cadastrar("A", "123", "1"));

            service.atualizarTelefone(cpf, "51988887777");
            verificar("telefone foi atualizado", "51988887777".equals(service.buscarPorCpf(cpf).getTelefone()));
        } catch (Exception e) {
            falha("ClienteService lançou erro inesperado: " + e);
        } finally {
            limpar(() -> service.remover(cpf));
        }
        verificarExcecao("cliente removido não é mais encontrado", () -> service.buscarPorCpf(cpf));
    }

    private static void testarVendaService() {
        System.out.println("-- VendaService");
        ClienteDAO clienteDao = new ClienteDAO();
        VendaDAO vendaDao = new VendaDAO();
        ProdutoVendaDAO itemDao = new ProdutoVendaDAO();
        ClienteService clientes = new ClienteService(clienteDao, new ValidadorCadastro());
        ProdutoService produtos = new ProdutoService(new ProdutoDAO(), new ValidadorCadastro(), new GeradorCodigoProduto());
        VendaService vendas = new VendaService(vendaDao, itemDao, clienteDao, new CalculadoraVenda());

        String cpf = cpfAleatorio();
        String codigoProduto = null;
        Venda venda = null;
        try {
            clientes.cadastrar("Cliente Venda", cpf, "999999999");
            Produto criado = produtos.cadastrar("Produto Teste", "Marca Teste", "10,00", false);
            codigoProduto = criado.getCodigo();
            Produto produto = produtos.buscarPorCodigo(codigoProduto);

            ProdutoVenda item = vendas.criarItem(produto, 2, 10);
            verificar("subtotal do item: 10,00 x 2 com 10% = 18,00", igual("18.00", item.getSubtotal()));

            final Produto p = produto;
            verificarExcecao("produto por unidade não aceita quantidade fracionada",
                    () -> vendas.criarItem(p, 1.5f, 0));
            verificarExcecao("venda sem itens é rejeitada",
                    () -> vendas.concluirVenda(cpf, new ArrayList<>()));
            List<ProdutoVenda> itens = new ArrayList<>();
            itens.add(item);
            verificarExcecao("venda para CPF inexistente é rejeitada",
                    () -> vendas.concluirVenda("00000000000", itens));

            venda = vendas.concluirVenda(cpf, itens);
            verificar("venda concluída recebe um id", venda != null && venda.getId() > 0);
            verificar("total da venda = 18,00", igual("18.00", venda.getTotal()));
            verificar("item da venda foi gravado", itemDao.readByIdVenda(venda.getId()).size() == 1);

            final int idVenda = venda.getId();
            boolean aparece = false;
            for (Venda v : vendas.listarPorIntervalo(Intervalo.HOJE)) {
                if (v.getId() == idVenda) {
                    aparece = true;
                }
            }
            verificar("venda aparece no intervalo HOJE", aparece);
        } catch (Exception e) {
            falha("VendaService lançou erro inesperado: " + e);
        } finally {
            if (venda != null) {
                final int idVenda = venda.getId();
                limpar(() -> {
                    for (ProdutoVenda pv : itemDao.readByIdVenda(idVenda)) {
                        itemDao.delete(pv.getId());
                    }
                    vendaDao.delete(idVenda);
                });
            }
            final String codigo = codigoProduto;
            if (codigo != null) {
                limpar(() -> produtos.remover(codigo));
            }
            limpar(() -> clientes.remover(cpf));
        }
    }

    private static void testarAutenticacao() {
        System.out.println("-- UsuarioService e AutenticacaoService");
        UsuarioDAO dao = new UsuarioDAO();
        UsuarioService usuarios = new UsuarioService(dao, new ValidadorCadastro());
        AutenticacaoService auth = new AutenticacaoService(dao, new ValidadorCadastro());

        String cpf = cpfAleatorio();
        String login = "teste" + new Random().nextInt(100000) + "@teste.com";
        try {
            usuarios.cadastrar("Usuario Teste", cpf, "999999999", login, "senha1234", "Operador");
            Usuario u = auth.autenticar(login, "senha1234");
            verificar("login correto autentica o usuário", u != null && login.equals(u.getLogin()));
            verificarExcecao("senha errada é rejeitada", () -> auth.autenticar(login, "senhaerrada1"));
            verificarExcecao("login em formato inválido é rejeitado", () -> auth.autenticar("abc", "senha1234"));
        } catch (Exception e) {
            falha("Autenticação lançou erro inesperado: " + e);
        } finally {
            limpar(() -> usuarios.remover(cpf));
        }
    }

    // ---------------------------------------------------------------- APOIO

    private static String cpfAleatorio() {
        return String.format("%011d", (long) (new Random().nextDouble() * 99999999999L));
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
        try {
            acao.run();
            falha(descricao + " (esperava RegraNegocioException, mas nada foi lançado)");
        } catch (RegraNegocioException e) {
            aprovados++;
            System.out.println("  [OK]    " + descricao + " -> \"" + e.getMessage() + "\"");
        } catch (Exception e) {
            falha(descricao + " (lançou " + e.getClass().getSimpleName() + " em vez de RegraNegocioException)");
        }
    }

    private static void falha(String descricao) {
        reprovados++;
        System.out.println("  [FALHA] " + descricao);
    }

    private static void limpar(Runnable acao) {
        try {
            acao.run();
        } catch (Exception e) {
            System.out.println("  (limpeza: " + e.getMessage() + ")");
        }
    }
}
