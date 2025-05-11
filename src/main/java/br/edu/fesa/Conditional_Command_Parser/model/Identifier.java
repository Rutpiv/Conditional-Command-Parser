package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.Builder;
import lombok.Getter;

/** AST node for an identifier (variable name). */
@Getter
public class Identifier extends SyntaxNode {
  /** Name of the identifier. */
  private final String name;

  /**
   * @param line source line of this node
   * @param column source column of this node
   * @param name variable name
   */
  @Builder
  public Identifier(int line, int column, String name) {
    super(line, column);
    this.name = name;
  }
}
