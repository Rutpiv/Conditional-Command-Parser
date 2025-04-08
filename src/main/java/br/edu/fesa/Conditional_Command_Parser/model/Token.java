package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/* Represents a lexical token with type, value, and source code position. */
@Getter
@AllArgsConstructor
public class Token {
  /* Enumeration of possible token types in the language */
  public enum Type {
    IF, // 'if' keyword
    ELSE, // 'else' keyword
    LPAREN, // Left parenthesis '('
    RPAREN, // Right parenthesis ')'
    ID, // Identifier
    NUMBER, // Numeric literal
    EQUALS, // Assignment operator '='
    PLUS, // Addition operator '+'
    MINUS, // Subtraction operator '-'
    TIMES, // Multiplication operator '*'
    DIVIDE, // Division operator '/'
    EOF // End-of-file marker
  }

  /* The type of the token from the enum */
  private final Type type;

  /* The literal string value of the token */
  private final String value;

  /* Source code line where token was found */
  private final int line;

  /* Source code column where token was found */
  private final int column;
}
