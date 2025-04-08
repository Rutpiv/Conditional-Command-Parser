package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.Builder;

public class Assignment extends SyntaxNode {
  private String identifier;
  private SyntaxNode expression;

  @Builder
  public Assignment(int line, int column, String identifier, SyntaxNode expression) {
    super(line, column);
    this.identifier = identifier;
    this.expression = expression;
  }
}
