package br.edu.fesa.Conditional_Command_Parser.utils;

/*
 * Exception class representing a syntax error during parsing.
 *
 * <p>This exception is thrown when the parser encounters an unexpected token or an error in the
 * structure of the input code during the lexical or syntactic analysis phases.
 */
public class SyntaxException extends Exception {

  /*
   * Constructs a new SyntaxException with the specified detail message.
   *
   * @param message the detail message explaining the error.
   */
  public SyntaxException(String message) {
    super(message);
  }
}
