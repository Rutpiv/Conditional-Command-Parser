package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.Builder;
import lombok.Getter;

/* Represents an identifier (variable name) in the abstract syntax tree (AST). */
@Getter
public class Identifier extends SyntaxNode {
  /* The name of the identifier/variable */
  private String name;

  /*
   * Lombok-generated builder constructor for Identifier node
   *
   * @param line Source code line number for error reporting
   * @param column Source code column number for error reporting
   * @param name Name of the identifier
   */
  @Builder
  public Identifier(int line, int column, String name) {
    super(line, column);
    this.name = name;
  }
}
