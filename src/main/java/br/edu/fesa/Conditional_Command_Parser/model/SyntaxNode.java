package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Base class for all AST nodes, holding source-location and semantic-type annotation. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class SyntaxNode {
  /** Semantic type inferred by the semantic analyzer. */
  private Token.Type type;

  /** Source line number for error reporting. */
  private int line;

  /** Source column number for error reporting. */
  private int column;

  /**
   * Convenience constructor for subclasses that only pass line/column.
   *
   * @param line source line of this node
   * @param column source column of this node
   */
  public SyntaxNode(int line, int column) {
    this(null, line, column);
  }
}
