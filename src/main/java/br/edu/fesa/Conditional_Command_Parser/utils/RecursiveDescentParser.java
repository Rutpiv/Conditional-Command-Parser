package br.edu.fesa.Conditional_Command_Parser.utils;

import br.edu.fesa.Conditional_Command_Parser.exception.SyntaxException;
import br.edu.fesa.Conditional_Command_Parser.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Recursive-descent parser with error recovery.
 *
 * <p>This class holds parser state (tokens, position, errors) and so is defined as a
 * prototype-scoped Spring bean. Use {@link #parse(List)} for each new input.
 */
@Component
@Scope("prototype")
public class RecursiveDescentParser {

  private List<Token> tokens;
  private int currentPosition;
  private Token currentToken;
  private final List<String> errors = new ArrayList<>();

  public RecursiveDescentParser() {
    // No-arg constructor; initialize state in parse()
  }

  /**
   * Parses a token stream into an AST. Collects syntax errors and attempts to recover.
   *
   * @param tokenList list of tokens from the lexer
   * @return root of the AST (null if parsing failed at top-level)
   */
  public SyntaxNode parse(List<Token> tokenList) {
    this.tokens = tokenList;
    this.currentPosition = 0;
    this.currentToken = tokens.get(0);
    this.errors.clear();

    SyntaxNode root;
    try {
      root = parseS();
    } catch (SyntaxException ex) {
      errors.add(ex.getMessage());
      synchronize(Set.of(Token.Type.IF, Token.Type.ID, Token.Type.EOF));
      root = null;
    }

    try {
      eat(Token.Type.EOF);
    } catch (SyntaxException ex) {
      errors.add(ex.getMessage());
    }
    return root;
  }

  /** Returns collected syntax errors from the last parse. */
  public List<String> getErrors() {
    return List.copyOf(errors);
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
              "Syntax error [line %d, column %d]: expected '%s' but found '%s'",
              currentToken.getLine(), currentToken.getColumn(), expected, currentToken.getType()));
    }
  }

  private void synchronize(Set<Token.Type> syncSet) {
    while (currentToken.getType() != Token.Type.EOF && !syncSet.contains(currentToken.getType())) {
      advance();
    }
  }

  private SyntaxNode parseS() throws SyntaxException {
    if (currentToken.getType() == Token.Type.IF) {
      return parseIfStatement();
    }
    return parseAssignment();
  }

  private IfStatement parseIfStatement() throws SyntaxException {
    Token start = currentToken;
    eat(Token.Type.IF);
    eat(Token.Type.LPAREN);
    SyntaxNode cond = parseE();
    eat(Token.Type.RPAREN);
    SyntaxNode thenBranch = parseS();
    eat(Token.Type.ELSE);
    SyntaxNode elseBranch = parseS();
    return IfStatement.builder()
        .line(start.getLine())
        .column(start.getColumn())
        .condition(cond)
        .thenBranch(thenBranch)
        .elseBranch(elseBranch)
        .build();
  }

  private Assignment parseAssignment() throws SyntaxException {
    Token idTok = currentToken;
    eat(Token.Type.ID);
    eat(Token.Type.EQUALS);
    SyntaxNode expr = parseE();
    return Assignment.builder()
        .line(idTok.getLine())
        .column(idTok.getColumn())
        .identifier(idTok.getValue())
        .expression(expr)
        .build();
  }

  private SyntaxNode parseE() throws SyntaxException {
    SyntaxNode node = parseT();
    while (currentToken.getType() == Token.Type.PLUS
        || currentToken.getType() == Token.Type.MINUS) {
      Token op = currentToken;
      eat(op.getType());
      SyntaxNode right = parseT();
      node =
          BinOp.builder()
              .line(op.getLine())
              .column(op.getColumn())
              .operator(op.getValue())
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
      Token op = currentToken;
      eat(op.getType());
      SyntaxNode right = parseF();
      node =
          BinOp.builder()
              .line(op.getLine())
              .column(op.getColumn())
              .operator(op.getValue())
              .left(node)
              .right(right)
              .build();
    }
    return node;
  }

  private SyntaxNode parseF() throws SyntaxException {
    if (currentToken.getType() == Token.Type.LPAREN) {
      eat(Token.Type.LPAREN);
      SyntaxNode inner = parseE();
      eat(Token.Type.RPAREN);
      return inner;
    }
    if (currentToken.getType() == Token.Type.STRING) {
      Token tok = currentToken;
      eat(Token.Type.STRING);
      return StringLiteral.builder()
          .line(tok.getLine())
          .column(tok.getColumn())
          .value(tok.getValue())
          .build();
    }
    if (currentToken.getType() == Token.Type.CHAR) {
      Token tok = currentToken;
      eat(Token.Type.CHAR);
      return CharLiteral.builder()
          .line(tok.getLine())
          .column(tok.getColumn())
          .value(tok.getValue())
          .build();
    }
    if (currentToken.getType() == Token.Type.FLOAT) {
      Token tok = currentToken;
      eat(Token.Type.FLOAT);
      return FloatLiteral.builder()
          .line(tok.getLine())
          .column(tok.getColumn())
          .value(tok.getValue())
          .build();
    }
    if (currentToken.getType() == Token.Type.NUMBER || currentToken.getType() == Token.Type.ID) {
      Token tok = currentToken;
      eat(tok.getType());
      if (tok.getType() == Token.Type.NUMBER) {
        return NumberLiteral.builder()
            .line(tok.getLine())
            .column(tok.getColumn())
            .value(tok.getValue())
            .build();
      }
      return Identifier.builder()
          .line(tok.getLine())
          .column(tok.getColumn())
          .name(tok.getValue())
          .build();
    }
    throw new SyntaxException(
        String.format(
            "Syntax error [line %d, column %d]: expected '(', id, number, string, char or float but"
                + " found '%s'",
            currentToken.getLine(), currentToken.getColumn(), currentToken.getType()));
  }
}
