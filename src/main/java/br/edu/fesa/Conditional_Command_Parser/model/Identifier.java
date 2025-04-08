package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Identifier extends SyntaxNode {
  private String name;

  @Builder
  public Identifier(int line, int column, String name) {
    super(line, column);
    this.name = name;
  }
}
