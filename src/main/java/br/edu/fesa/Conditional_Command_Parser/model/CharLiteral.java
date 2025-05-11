package br.edu.fesa.Conditional_Command_Parser.model;

import br.edu.fesa.Conditional_Command_Parser.model.Token.Type;
import lombok.Builder;
import lombok.Getter;

/**
 * AST node for a character literal.
 *
 * <p>Example: <code>'a'</code> or <code>'\n'</code>
 */
@Getter
public class CharLiteral extends SyntaxNode {
  /** Raw string value (unescaped) of the character literal. */
  private final String value;

  /**
   * @param line source line of this node
   * @param column source column of this node
   * @param value literal character (as a one-character string or escape code)
   */
  @Builder
  public CharLiteral(int line, int column, String value) {
    super(line, column);
    this.value = value;
  }

  @Override
  public Type getType() {
    return Type.CHAR;
  }
}
