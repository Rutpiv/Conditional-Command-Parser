package br.edu.fesa.Conditional_Command_Parser.model;

import lombok.Builder;
import lombok.Getter;

/*
 * Represents an assignment statement in the abstract syntax tree (AST). Contains an identifier and
 * the expression being assigned to it.
 */
@Getter
public class Assignment extends SyntaxNode {
  /* The identifier (variable name) being assigned to */
  private String identifier;

  /* The expression whose value will be assigned to the identifier */
  private SyntaxNode expression;

  /*
   * Lombok-generated builder constructor for Assignment node
   *
   * @param line Source code line number for error reporting
   * @param column Source code column number for error reporting
   * @param identifier Target variable name for assignment
   * @param expression Value expression to be assigned
   */
  @Builder
  public Assignment(int line, int column, String identifier, SyntaxNode expression) {
    super(line, column);
    this.identifier = identifier;
    this.expression = expression;
  }
}
