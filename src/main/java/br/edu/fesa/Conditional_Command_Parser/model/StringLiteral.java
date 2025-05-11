package br.edu.fesa.Conditional_Command_Parser.model;

import br.edu.fesa.Conditional_Command_Parser.model.Token.Type;
import lombok.Builder;
import lombok.Getter;

/**
 * AST node for a string literal.
 *
 * <p>Example: <code>"hello"</code>
 */
@Getter
public class StringLiteral extends SyntaxNode {
  /** Raw string content without surrounding quotes. */
  private final String value;

  /**
   * @param line source line of this node
   * @param column source column of this node
   * @param value literal string content
   */
  @Builder
  public StringLiteral(int line, int column, String value) {
    super(line, column);
    this.value = value;
  }

  @Override
  public Type getType() {
    return Type.STRING;
  }
}
