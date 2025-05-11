package br.edu.fesa.Conditional_Command_Parser.exception;

import java.io.Serial;

/**
 * Thrown when a lexical error is encountered, for example an unrecognized character or an
 * unterminated string/char literal.
 */
public class LexicalException extends Exception {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Constructs a new LexicalException with the specified detail message.
   *
   * @param message detailed explanation of the lexical error
   */
  public LexicalException(String message) {
    super(message);
  }
}
