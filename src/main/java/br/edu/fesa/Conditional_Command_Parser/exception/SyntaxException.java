package br.edu.fesa.Conditional_Command_Parser.exception;

import java.io.Serial;

/**
 * Thrown when a syntactic error is detected during parsing, occurs if the parser encounters an
 * unexpected token or a structure mismatch.
 */
public class SyntaxException extends Exception {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Constructs a new SyntaxException with the specified detail message.
   *
   * @param message explanation of the syntax error
   */
  public SyntaxException(String message) {
    super(message);
  }
}
