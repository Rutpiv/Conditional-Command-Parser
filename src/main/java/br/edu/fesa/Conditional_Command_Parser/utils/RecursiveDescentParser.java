package br.edu.fesa.Conditional_Command_Parser.utils;

import br.edu.fesa.Conditional_Command_Parser.model.*;
import br.edu.fesa.Conditional_Command_Parser.utils.SyntaxException;
import java.util.List;

public class RecursiveDescentParser {
    private final List<Token> tokens;
    private int currentPosition;
    private Token currentToken;

    public RecursiveDescentParser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentPosition = 0;
        this.currentToken = tokens.get(0);
    }

    private void advance() {
        currentPosition++;
        if (currentPosition < tokens.size()) {
            currentToken = tokens.get(currentPosition);
        }
    }

    private void eat(Token.Type expected) throws SyntaxException {
        if (currentToken.getType() == expected) {
            advance();
        } else {
            throw new SyntaxException(
                String.format(
                    "Erro sintático [Linha %d, Coluna %d]: Esperado '%s' mas encontrado '%s'",
                    currentToken.getLine(),
                    currentToken.getColumn(),
                    expected,
                    currentToken.getType()));
        }
    }

    public SyntaxNode parse() throws SyntaxException {
        SyntaxNode result = parseS();
        eat(Token.Type.EOF); // Garante que toda a entrada foi consumida
        return result;
    }

    private SyntaxNode parseS() throws SyntaxException {
        if (currentToken.getType() == Token.Type.IF) {
            return parseIfStatement();
        }
        return parseAssignment();
    }

    private IfStatement parseIfStatement() throws SyntaxException {
        Token startToken = currentToken;
        eat(Token.Type.IF);
        eat(Token.Type.LPAREN);
        SyntaxNode condition = parseE();
        eat(Token.Type.RPAREN);
        SyntaxNode thenBranch = parseS();
        eat(Token.Type.ELSE);
        SyntaxNode elseBranch = parseS();

        return IfStatement.builder()
            .line(startToken.getLine())
            .column(startToken.getColumn())
            .condition(condition)
            .thenBranch(thenBranch)
            .elseBranch(elseBranch)
            .build();
    }

    private Assignment parseAssignment() throws SyntaxException {
        Token idToken = currentToken;
        // Presumindo que apenas identificadores sejam válidos para atribuição
        eat(Token.Type.ID);
        eat(Token.Type.EQUALS);
        SyntaxNode expression = parseE();

        return Assignment.builder()
            .line(idToken.getLine())
            .column(idToken.getColumn())
            .identifier(idToken.getValue())
            .expression(expression)
            .build();
    }

    private SyntaxNode parseE() throws SyntaxException {
        SyntaxNode node = parseT();
        while (currentToken.getType() == Token.Type.PLUS
            || currentToken.getType() == Token.Type.MINUS) {
          Token opToken = currentToken;
          eat(opToken.getType());
          SyntaxNode right = parseT();
          node = BinOp.builder()
              .line(opToken.getLine())
              .column(opToken.getColumn())
              .operator(opToken.getValue())
              .left(node)
              .right(right)
              .build();
        }
        return node;
    }

    private SyntaxNode parseT() throws SyntaxException {
        SyntaxNode node = parseF();
        while (currentToken.getType() == Token.Type.TIMES
            || currentToken.getType() == Token.Type.DIVIDE) {
            Token opToken = currentToken;
            eat(opToken.getType());
            SyntaxNode right = parseF();
            node = BinOp.builder()
                .line(opToken.getLine())
                .column(opToken.getColumn())
                .operator(opToken.getValue())
                .left(node)
                .right(right)
                .build();
        }
        return node;
    }

    private SyntaxNode parseF() throws SyntaxException {
        if (currentToken.getType() == Token.Type.LPAREN) {
            eat(Token.Type.LPAREN);
            SyntaxNode node = parseE();
            eat(Token.Type.RPAREN);
            return node;
        }
        
        // Aceita tanto tokens ID quanto NUMBER neste contexto
        if (currentToken.getType() == Token.Type.ID || currentToken.getType() == Token.Type.NUMBER) {
            Token token = currentToken;
            eat(currentToken.getType());
            if (token.getType() == Token.Type.NUMBER) {
                return NumberLiteral.builder()
                    .line(token.getLine())
                    .column(token.getColumn())
                    .value(token.getValue())
                    .build();
            } else {
                return Identifier.builder()
                    .line(token.getLine())
                    .column(token.getColumn())
                    .name(token.getValue())
                    .build();
            }
        }
        
        throw new SyntaxException(
            String.format(
                "Erro sintático [Linha %d, Coluna %d]: Esperado '(', 'id' ou 'number' mas encontrado '%s'",
                currentToken.getLine(),
                currentToken.getColumn(),
                currentToken.getType()));
    }
}

