package br.edu.fesa.parser;

import br.edu.fesa.util.Tokenizer;
import sun.tools.jstat.ParserException;

public class Parser {
    private String[] tokens;
    private int currentTokenIndex;

    public Parser(String input) {
        this.tokens = Tokenizer.tokenize(input);
        this.currentTokenIndex = 0;
    }

    private String currentToken() {
        if (currentTokenIndex < tokens.length) {
            return tokens[currentTokenIndex];
        }
        return "";
    }

    private void advance() {
        currentTokenIndex++;
    }

    public void S() throws ParserException {
        if (currentToken().equals("if")) {
            advance();
            if (!currentToken().equals("(")) throw new ParserException("Erro: esperado '('");
            advance();
            E();
            if (!currentToken().equals(")")) throw new ParserException("Erro: esperado ')'");
            advance();
            S();
            if (!currentToken().equals("else")) throw new ParserException("Erro: esperado 'else'");
            advance();
            S();
        } else if (currentToken().matches("[a-zA-Z]+") && nextToken().equals("=")) {
            advance();
            advance();
            E();
        } else {
            throw new ParserException("Erro: sentença inválida em S()");
        }
    }

    public void E() throws ParserException {
        T();
        while (currentToken().equals("+") || currentToken().equals("-")) {
            advance();
            T();
        }
    }

    public void T() throws ParserException {
        F();
        while (currentToken().equals("*") || currentToken().equals("/")) {
            advance();
            F();
        }
    }

    public void F() throws ParserException {
        if (currentToken().equals("(")) {
            advance();
            E();
            if (!currentToken().equals(")")) throw new ParserException("Erro: esperado ')'");
            advance();
        } else if (currentToken().matches("[a-zA-Z]+")) {
            advance();
        } else {
            throw new ParserException("Erro: esperado identificador ou '('");
        }
    }

    public boolean parse() {
        try {
            S();
            return currentTokenIndex == tokens.length;
        } catch (ParserException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    private String nextToken() {
        if (currentTokenIndex + 1 < tokens.length) {
            return tokens[currentTokenIndex + 1];
        }
        return "";
    }
}
