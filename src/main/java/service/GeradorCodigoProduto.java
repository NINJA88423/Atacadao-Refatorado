package service;


import java.util.Random;


public class GeradorCodigoProduto {
    public String Gerar() {
        long tempo = System.currentTimeMillis();
        int all = new Random().nextInt(9999);
        return tempo + String.format("%04d", all);
    }
}