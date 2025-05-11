package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.Builder;
import lombok.Getter;

/**
 * AST node for a binary operation.
 *
 * <p>Example: <code>left + right</code>
 */
@Getter
public class BinOp extends SyntaxNode {
  /** Operator symbol, e.g. "+", "-", "*", "/". */
  private final String operator;

  /** Left operand expression. */
  private final SyntaxNode left;

  /** Right operand expression. */
  private final SyntaxNode right;

  /**
   * @param line source line of this node
   * @param column source column of this node
   * @param operator operator symbol
   * @param left left operand
   * @param right right operand
   */
  @Builder
  public BinOp(int line, int column, String operator, SyntaxNode left, SyntaxNode right) {
    super(line, column);
    this.operator = operator;
    this.left = left;
    this.right = right;
  }
}
