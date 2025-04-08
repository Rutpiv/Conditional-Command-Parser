package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Token {
  public enum Type {
    IF,
    ELSE,
    LPAREN,
    RPAREN,
    ID,
    NUMBER,
    EQUALS,
    PLUS,
    MINUS,
    TIMES,
    DIVIDE,
    EOF
  }

  private final Type type;
  private final String value;
  private final int line;
  private final int column;
}
