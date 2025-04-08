package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/*
 * Abstract base class for all nodes in the abstract syntax tree (AST). Provides source code
 * location information for error reporting.
 */
@Data
@AllArgsConstructor
public abstract class SyntaxNode {
  /* Source code line where this node originated */
  private int line;

  /* Source code column where this node originated */
  private int column;

  /* Default constructor for nodes without position information */
  public SyntaxNode() {
    this(0, 0);
  }
}
