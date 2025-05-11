package br.edu.fesa.Conditional_Command_Parser.model;

import br.edu.fesa.Conditional_Command_Parser.model.Token.Type;
import lombok.Builder;
import lombok.Getter;

/**
 * AST node for a floating-point literal.
 *
 * <p>Example: <code>3.14</code>
 */
@Getter
public class FloatLiteral extends SyntaxNode {
  /** Raw string representation of the float literal. */
  private final String value;

  /**
   * @param line source line of this node
   * @param column source column of this node
   * @param value literal float value as string
   */
  @Builder
  public FloatLiteral(int line, int column, String value) {
    super(line, column);
    this.value = value;
  }

  @Override
  public Type getType() {
    return Type.FLOAT;
  }
}
