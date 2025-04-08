package br.edu.fesa.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    public static String[] tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        // Regex para capturar tokens específicos da gramática
        Pattern pattern = Pattern.compile(
            "\\b(if|else)\\b|" +          // Palavras-chave (grupo 1)
            "([a-zA-Z]+)|" +               // Identificadores (grupo 2)
            "([=+\\-*/()])|" +            // Operadores e parênteses (grupo 3)
            "(\\s+)|" +                    // Espaços em branco (grupo 4)
            "(.)"                          // Caracteres inválidos (grupo 5)
        );
        
        Matcher matcher = pattern.matcher(input);
        
        while (matcher.find()) {
            // Verifica qual grupo foi encontrado
            if (matcher.group(1) != null) {
                tokens.add(matcher.group(1)); // "if" ou "else"
            } else if (matcher.group(2) != null) {
                tokens.add(matcher.group(2)); // Identificador (ex: "x", "y")
            } else if (matcher.group(3) != null) {
                tokens.add(matcher.group(3)); // Operadores ou parênteses
            } else if (matcher.group(5) != null) {
                throw new IllegalArgumentException("Erro: caractere inválido '" + matcher.group(5) + "'");
            }
            // Ignora espaços em branco (grupo 4)
        }
        
        return tokens.toArray(new String[0]);
    }
}