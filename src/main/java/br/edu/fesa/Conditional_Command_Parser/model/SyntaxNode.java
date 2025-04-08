package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class SyntaxNode {
  private int line;
  private int column;

  public SyntaxNode() {
    this(0, 0);
  }
}
