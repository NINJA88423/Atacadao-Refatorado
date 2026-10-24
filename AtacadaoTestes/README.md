# AtacadaoTestes

Projeto de testes JUnit 5 do sistema Atacadão (AtacadaoRefatorado).

## Como executar
1. No NetBeans, abra o projeto **AtacadaoRefatorado**, clique com o botão direito > *Run Maven* > *Goals...* e execute `clean install`
   (isso disponibiliza o projeto para os testes).
2. Abra o projeto **AtacadaoTestes** e use *Test Project* (Alt+F6), ou rode `mvn test` na pasta do projeto.

## O que é testado (sem acesso ao banco de dados)
- `CalculadoraVenda`: subtotal (preço x quantidade, desconto, arredondamento) e total da venda
- `ValidadorCadastro`: validação de cliente, usuário, credenciais e produto
- `Formatador`: moeda, quantidade, desconto, CPF e data/hora
- `GeradorCodigoProduto`: formato do código gerado
