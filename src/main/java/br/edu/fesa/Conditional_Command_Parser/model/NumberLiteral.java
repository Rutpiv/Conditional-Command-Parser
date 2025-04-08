package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NumberLiteral extends SyntaxNode {
  private String value;

  @Builder
  public NumberLiteral(int line, int column, String value) {
    super(line, column);
    this.value = value;
  }
}
