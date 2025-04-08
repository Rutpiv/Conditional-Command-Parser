package br.edu.fesa.Conditional_Command_Parser.service;

import static org.junit.jupiter.api.Assertions.*;

import br.edu.fesa.Conditional_Command_Parser.model.ParserResponse;
import br.edu.fesa.Conditional_Command_Parser.model.SyntaxNode;
import br.edu.fesa.Conditional_Command_Parser.utils.FirstFollowCalculator;
import br.edu.fesa.Conditional_Command_Parser.utils.Lexer;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/* Unit tests for ParserService. */
class ParserServiceTest {

  private ParserService parserService;

  /* Test environment setup: instantiates dependencies and initializes FIRST and FOLLOW sets. */
  @BeforeEach
  void setUp() {
    // Instantiate and calculate FIRST and FOLLOW sets
    FirstFollowCalculator firstFollowCalculator = new FirstFollowCalculator();
    firstFollowCalculator.calculateSets();

    // Create Lexer instance
    Lexer lexer = new Lexer();

    // Instantiate the service under test
    parserService = new ParserService(lexer, firstFollowCalculator);
  }

  /*
   * Test for simple assignment command. Example: "a = 123" Expected: AST generated without errors
   * and presence of FIRST/FOLLOW sets.
   */
  @Test
  void parse_Assignment_ShouldReturnAssignmentAst() {
    String input = "a = 123";
    ParserResponse response = parserService.parse(input);

    // Verify AST construction
    SyntaxNode ast = response.getAst();
    assertNotNull(ast, "AST should not be null for valid input");

    // Verify no error messages (accept null or empty list)
    assertTrue(
        response.getErrors() == null || response.getErrors().isEmpty(),
        "No errors should exist for valid input");

    // Verify FIRST and FOLLOW sets presence
    Map<String, ?> firstSets = response.getFirstSets();
    Map<String, ?> followSets = response.getFollowSets();
    assertNotNull(firstSets, "FIRST sets should not be null");
    assertNotNull(followSets, "FOLLOW sets should not be null");
  }

  /*
   * Test for conditional statement (if/else). Example: "if (a) b = 456 else c = 789" The 'a' token
   * in parentheses is treated as a simple expression (identifier).
   */
  @Test
  void parse_IfStatement_ShouldReturnIfStatementAst() {
    String input = "if (a) b = 456 else c = 789";
    ParserResponse response = parserService.parse(input);

    // Verify AST construction
    SyntaxNode ast = response.getAst();
    assertNotNull(ast, "AST should not be null for valid input");

    // Verify no error messages
    assertTrue(
        response.getErrors() == null || response.getErrors().isEmpty(),
        "No errors should exist for valid input");
  }

  /*
   * Test for syntax error handling. Example: Input with syntax error (e.g., missing closing
   * parenthesis).
   */
  @Test
  void parse_SyntaxError_ShouldReturnErrors() {
    // Invalid input example: missing closing parenthesis in if condition
    String input = "if (a b = 456 else c = 789";
    ParserResponse response = parserService.parse(input);

    // For errors: AST should be null and error messages present
    assertNull(response.getAst(), "AST should be null for invalid input");
    assertNotNull(response.getErrors(), "Error messages should exist for invalid input");
    assertFalse(response.getErrors().isEmpty(), "Should contain syntax error message");

    // Optional: Verify error message contains relevant info (e.g., location)
    String errorMessage = response.getErrors().get(0);
    assertTrue(errorMessage.contains("Syntax error"), "Error message should indicate syntax error");
  }
}
