package util;



import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;



public class Formatador {

    
    private static final Locale BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    
    public String moeda(BigDecimal valor) {
        return String.format(BR, "R$ %,.2f", valor);
    }
    

    public String quantidade(float quantidade, boolean vendidoPorKg) {
        return vendidoPorKg
                ? String.format(BR, "%.3f kg", quantidade)
                : String.format(BR, "%.0f", quantidade);
    }

    
    public String desconto(float desconto) {
        if (desconto == 0) {
            return "";
        }
        return String.format(BR, "-%s%%", semZerosFinais(desconto));
    }
    

    public String cpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }
    

    public String dataHora(LocalDateTime dataHora) {
        return dataHora.format(DATA_HORA);
    }
    

    private String semZerosFinais(float valor) {
        return new BigDecimal(String.valueOf(valor)).stripTrailingZeros().toPlainString().replace('.', ',');
    }
    
    
}
