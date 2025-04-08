package br.edu.fesa.parser;

import br.edu.fesa.util.Tokenizer;

public class Parser {
    private String[] tokens;
    private int currentTokenIndex;

    public Parser(String input) {
        this.tokens = Tokenizer.tokenize(input);
        this.currentTokenIndex = 0;
    }

    private String currentToken() {
        return (currentTokenIndex < tokens.length) ? tokens[currentTokenIndex] : "";
    }

    private void advance() {
        currentTokenIndex++;
    }

    public void S() throws ParserException {
        if (currentToken().equals("if")) {
            // Regra: S → if ( E ) S else S
            advance(); // Consome 'if'
            
            if (!currentToken().equals("(")) 
                throw new ParserException("Erro: '(' esperado após 'if'");
            advance(); // Consome '('
            
            E(); // Parse expressão
            
            if (!currentToken().equals(")")) 
                throw new ParserException("Erro: ')' esperado após expressão");
            advance(); // Consome ')'
            
            S(); // Parse bloco then
            
            if (!currentToken().equals("else")) 
                throw new ParserException("Erro: 'else' esperado");
            advance(); // Consome 'else'
            
            S(); // Parse bloco else
            
        } else if (currentToken().matches("[a-zA-Z]+") && nextToken().equals("=")) {
            // Regra: S → id = E
            advance(); // Consome identificador
            advance(); // Consome '='
            E(); // Parse expressão
        } else {
            throw new ParserException("Erro de sintaxe em S()");
        }
    }

    private void E() throws ParserException {
        T(); // E → T
        while (currentToken().equals("+") || currentToken().equals("-")) {
            advance(); // Consome operador
            T(); // E → E op T
        }
    }

    private void T() throws ParserException {
        F(); // T → F
        while (currentToken().equals("*") || currentToken().equals("/")) {
            advance(); // Consome operador
            F(); // T → T op F
        }
    }

    private void F() throws ParserException {
        if (currentToken().equals("(")) {
            advance(); // Consome '('
            E(); // Parse expressão interna
            if (!currentToken().equals(")")) 
                throw new ParserException("Erro: ')' esperado");
            advance(); // Consome ')'
        } else if (currentToken().matches("[a-zA-Z]+")) {
            advance(); // Consome identificador
        } else {
            throw new ParserException("Erro: identificador ou '(' esperado");
        }
    }

    public boolean parse() {
        try {
            S();
            // Verifica se todos os tokens foram consumidos
            return currentTokenIndex == tokens.length;
        } catch (ParserException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    private String nextToken() {
        return (currentTokenIndex + 1 < tokens.length) ? tokens[currentTokenIndex + 1] : "";
    }
}