package br.edu.fesa.Conditional_Command_Parser.model;

import br.edu.fesa.Conditional_Command_Parser.model.Token.Type;
import lombok.Builder;
import lombok.Getter;

/**
 * AST node for an integer numeric literal.
 *
 * <p>Example: <code>42</code>
 */
@Getter
public class NumberLiteral extends SyntaxNode {
  /** Raw string representation of the integer literal. */
  private final String value;

  /**
   * @param line source line of this node
   * @param column source column of this node
   * @param value literal integer value as string
   */
  @Builder
  public NumberLiteral(int line, int column, String value) {
    super(line, column);
    this.value = value;
  }

  @Override
  public Type getType() {
    return Type.NUMBER;
  }
}
