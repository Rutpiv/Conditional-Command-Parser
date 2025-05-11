package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.Builder;
import lombok.Getter;

/**
 * AST node for an assignment statement.
 *
 * <p>Example: <code>x = expr;</code>
 */
@Getter
public class Assignment extends SyntaxNode {
  /** Variable name on the left-hand side of the assignment. */
  private final String identifier;

  /** Expression whose value will be assigned. */
  private final SyntaxNode expression;

  /**
   * @param line source line of this node
   * @param column source column of this node
   * @param identifier target variable name
   * @param expression expression to assign
   */
  @Builder
  public Assignment(int line, int column, String identifier, SyntaxNode expression) {
    super(line, column);
    this.identifier = identifier;
    this.expression = expression;
  }
}
