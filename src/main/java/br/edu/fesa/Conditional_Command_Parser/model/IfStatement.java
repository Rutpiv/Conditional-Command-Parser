package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class IfStatement extends SyntaxNode {
  private SyntaxNode condition;
  private SyntaxNode thenBranch;
  private SyntaxNode elseBranch;

  @Builder
  public IfStatement(
      int line, int column, SyntaxNode condition, SyntaxNode thenBranch, SyntaxNode elseBranch) {
    super(line, column);
    this.condition = condition;
    this.thenBranch = thenBranch;
    this.elseBranch = elseBranch;
  }
}
