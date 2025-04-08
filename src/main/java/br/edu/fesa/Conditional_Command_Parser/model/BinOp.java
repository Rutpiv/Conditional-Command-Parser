package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BinOp extends SyntaxNode {
  private String operator;
  private SyntaxNode left;
  private SyntaxNode right;

  @Builder
  public BinOp(int line, int column, String operator, SyntaxNode left, SyntaxNode right) {
    super(line, column);
    this.operator = operator;
    this.left = left;
    this.right = right;
  }
}
