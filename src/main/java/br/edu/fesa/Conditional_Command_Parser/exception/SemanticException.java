package br.edu.fesa.Conditional_Command_Parser.exception;

import java.io.Serial;

/**
 * Thrown when a semantic error is detected during analysis, such as type mismatches, undeclared
 * symbols, or scope violations.
 */
public class SemanticException extends Exception {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Constructs a new SemanticException with the specified detail message.
   *
   * @param message detailed explanation of the semantic error
   */
  public SemanticException(String message) {
    super(message);
  }
}
