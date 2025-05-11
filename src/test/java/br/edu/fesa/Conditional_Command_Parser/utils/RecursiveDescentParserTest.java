package br.edu.fesa.Conditional_Command_Parser.utils;

import static org.junit.jupiter.api.Assertions.*;

import br.edu.fesa.Conditional_Command_Parser.exception.LexicalException;
import br.edu.fesa.Conditional_Command_Parser.model.Token;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Unit tests for the RecursiveDescentParser with error recovery. */
class RecursiveDescentParserTest {

  private final Lexer lexer = new Lexer();

  /** Helper to tokenize and parse an input string, returning the parser. */
  private RecursiveDescentParser parse(String input) throws LexicalException {
    List<Token> tokens = lexer.tokenize(input);
    RecursiveDescentParser parser = new RecursiveDescentParser();
    parser.parse(tokens);
    return parser;
  }

  @Nested
  @DisplayName("Valid grammar inputs")
  class ValidInputs {

    static Stream<Arguments> wellFormedInputs() {
      return Stream.of(
          Arguments.of("x=1"),
          Arguments.of("a=2+3*4"),
          Arguments.of("if(x) y=1 else y=2"),
          Arguments.of("if(a) if(b) c=3 else c=4 else c=5"),
          Arguments.of("x=(7-2)/5") // whitespace variation
          );
    }

    @ParameterizedTest(name = "Valid input: \"{0}\"")
    @MethodSource("wellFormedInputs")
    void noSyntaxErrors_ForValidInput(String input) throws LexicalException {
      var parser = parse(input);
      var errors = parser.getErrors();
      assertTrue(
          errors.isEmpty(),
          () -> "Expected no syntax errors for \"" + input + "\", but got: " + errors);

      // Parsing twice should still succeed
      assertNotNull(
          parser.parse(lexer.tokenize(input)),
          "AST root should not be null for valid input: " + input);
    }
  }

  @Nested
  @DisplayName("Invalid grammar inputs")
  class InvalidInputs {

    static Stream<Arguments> malformedInputs() {
      return Stream.of(
          Arguments.of("=1", "expected 'ID'"),
          Arguments.of("x=", "expected '('"),
          Arguments.of("if(x) y=1", "expected 'ELSE'"),
          Arguments.of("if(x y=1 else y=2", "expected 'RPAREN'"),
          Arguments.of("a+*b", "expected 'EQUALS'"),
          Arguments.of("(1+2", "expected 'ID'"),
          Arguments.of("1)", "expected 'ID'"),
          Arguments.of("if() x=1 else x=2", "expected '('"));
    }

    @ParameterizedTest(name = "Malformed input: \"{0}\"")
    @MethodSource("malformedInputs")
    void collectsSyntaxErrors_ForMalformedInput(String input, String expectedMsg)
        throws LexicalException {
      var parser = parse(input);
      var errors = parser.getErrors();

      assertFalse(errors.isEmpty(), () -> "Expected syntax errors for input: \"" + input + "\"");

      // Check for presence of the expected substring (case-insensitive)
      boolean found =
          errors.stream().anyMatch(msg -> msg.toLowerCase().contains(expectedMsg.toLowerCase()));
      assertTrue(
          found,
          () ->
              "Expected at least one syntax error mentioning \""
                  + expectedMsg
                  + "\", but got: "
                  + errors);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("EOF-only input yields error")
    void emptyInput() throws LexicalException {
      var parser = parse("");
      var errors = parser.getErrors();

      assertFalse(
          errors.isEmpty(), () -> "Expected syntax errors for empty input, but none were found");

      // The first error should mention an expected token
      String first = errors.get(0).toLowerCase();
      assertTrue(
          first.contains("expected"),
          () -> "Expected an 'expected' error for empty input, got: " + errors);
    }
  }
}
