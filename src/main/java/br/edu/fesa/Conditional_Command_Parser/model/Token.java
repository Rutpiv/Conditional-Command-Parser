package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Represents a lexical token with its type, lexeme, and source position. */
@Getter
@AllArgsConstructor
public class Token {
  /** All possible token types in the language, including keywords, literals, operators, and EOF. */
  public enum Type {
    IF, // 'if' keyword
    ELSE, // 'else' keyword
    LPAREN, // '('
    RPAREN, // ')'
    ID, // identifier
    NUMBER, // integer literal
    FLOAT, // floating-point literal
    CHAR, // character literal
    STRING, // string literal
    EQUALS, // '='
    PLUS, // '+'
    MINUS, // '-'
    TIMES, // '*'
    DIVIDE, // '/'
    EOF // end-of-file marker
  }

  /** Type of this token, from the Type enum. */
  private final Type type;

  /** The exact text lexeme of this token. */
  private final String value;

  /** Source line number where the token was found. */
  private final int line;

  /** Source column number where the token was found. */
  private final int column;
}
