package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.Builder;
import lombok.Getter;

/*
 * Represents an if-else conditional statement in the abstract syntax tree (AST). Contains condition
 * expression and both execution branches.
 */
@Getter
public class IfStatement extends SyntaxNode {
  /* Conditional expression determining which branch to execute */
  private SyntaxNode condition;

  /* Statements to execute when condition is true */
  private SyntaxNode thenBranch;

  /* Statements to execute when condition is false (optional) */
  private SyntaxNode elseBranch;

  /*
   * Lombok-generated builder constructor for IfStatement node
   *
   * @param line Source code line number for error reporting
   * @param column Source code column number for error reporting
   * @param condition Boolean expression for conditional check
   * @param thenBranch Code block for true condition case
   * @param elseBranch Code block for false condition case (nullable)
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
