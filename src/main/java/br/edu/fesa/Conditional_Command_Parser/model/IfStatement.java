package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.Builder;
import lombok.Getter;

/**
 * AST node for an if-else conditional statement.
 *
 * <p>Example:
 *
 * <pre>
 * if (condition) thenBranch else elseBranch
 * </pre>
 */
@Getter
public class IfStatement extends SyntaxNode {
  /** Expression to evaluate for branching. */
  private final SyntaxNode condition;

  /** AST subtree to execute when the condition is true. */
  private final SyntaxNode thenBranch;

  /** AST subtree to execute when the condition is false. */
  private final SyntaxNode elseBranch;

  /**
   * @param line source line of this node
   * @param column source column of this node
   * @param condition boolean (or numeric) expression
   * @param thenBranch subtree for the “then” branch
   * @param elseBranch subtree for the “else” branch
   */
  @Builder
  public IfStatement(
      int line, int column, SyntaxNode condition, SyntaxNode thenBranch, SyntaxNode elseBranch) {
    super(line, column);
    this.condition = condition;
    this.thenBranch = thenBranch;
    this.elseBranch = elseBranch;
  }
}
