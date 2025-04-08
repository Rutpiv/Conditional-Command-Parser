package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.Builder;
import lombok.Getter;

/*
 * Represents a binary operation in the abstract syntax tree (AST). Contains left and right operands
 * along with the operator.
 */
@Getter
public class BinOp extends SyntaxNode {
  /* The operator symbol (e.g., "+", "-", "*", "/") */
  private String operator;

  /* Left-hand side operand of the operation */
  private SyntaxNode left;

  /* Right-hand side operand of the operation */
  private SyntaxNode right;

  /*
   * Lombok-generated builder constructor for BinOp node
   *
   * @param line Source code line number for error reporting
   * @param column Source code column number for error reporting
   * @param operator Operator used in the binary operation
   * @param left Left operand expression
   * @param right Right operand expression
   */
  @Builder
  public BinOp(int line, int column, String operator, SyntaxNode left, SyntaxNode right) {
    super(line, column);
    this.operator = operator;
    this.left = left;
    this.right = right;
  }
}
