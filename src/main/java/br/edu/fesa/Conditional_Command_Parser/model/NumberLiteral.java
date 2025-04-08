package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.Builder;
import lombok.Getter;

/* Represents a numeric literal value in the abstract syntax tree (AST). */
@Getter
public class NumberLiteral extends SyntaxNode {
  /* String representation of the numeric value */
  private String value;

  /*
   * Lombok-generated builder constructor for NumberLiteral node
   *
   * @param line Source code line number for error reporting
   * @param column Source code column number for error reporting
   * @param value Numeric value as string (to preserve formatting)
   */
  @Builder
  public NumberLiteral(int line, int column, String value) {
    super(line, column);
    this.value = value;
  }
}
