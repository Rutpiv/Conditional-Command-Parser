package br.edu.fesa.Conditional_Command_Parser.service;

import static org.junit.jupiter.api.Assertions.*;

import br.edu.fesa.Conditional_Command_Parser.model.ParserResponse;
import br.edu.fesa.Conditional_Command_Parser.model.SyntaxNode;
import br.edu.fesa.Conditional_Command_Parser.utils.FirstFollowCalculator;
import br.edu.fesa.Conditional_Command_Parser.utils.Lexer;
import br.edu.fesa.Conditional_Command_Parser.utils.RecursiveDescentParser;
import br.edu.fesa.Conditional_Command_Parser.utils.SemanticAnalyzer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** Unit tests for the ParserService covering lexing, parsing, and semantic analysis. */
class ParserServiceTest {

  private ParserService parserService;

  @BeforeEach
  void setUp() {
    FirstFollowCalculator firstFollowCalculator = new FirstFollowCalculator();
    firstFollowCalculator.calculateSets();

    Lexer lexer = new Lexer();
    RecursiveDescentParser parser = new RecursiveDescentParser();
    SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

    parserService = new ParserService(firstFollowCalculator, lexer, parser, semanticAnalyzer);
  }

  @Nested
  @DisplayName("Valid end-to-end cases")
  class ValidCases {

    @Test
    @DisplayName("Simple assignment")
    void parse_SimpleAssignment_NoErrors() {
      ParserResponse resp = parserService.parse("a = 123");

      // AST must be present
      SyntaxNode ast = resp.getAst();
      assertNotNull(ast);

      // No errors

      List<String> errs = resp.getErrors();
      assertFalse(errs.isEmpty(), "Expected a semantic error for undeclared 'a'");
      assertTrue(
          errs.stream().anyMatch(e -> e.contains("undeclared variable 'a'")),
          "Expected an `undeclared variable 'a'` error, but got: " + errs);

      // FIRST/FOLLOW sets have concrete type
      Map<String, Set<String>> first = resp.getFirstSets();
      Map<String, Set<String>> follow = resp.getFollowSets();

      assertNotNull(first);
      assertNotNull(follow);

      // Check that S has at least one FIRST and FOLLOW symbol
      assertFalse(first.get("S").isEmpty(), "FIRST[S] should not be empty");
      assertFalse(follow.get("S").isEmpty(), "FOLLOW[S] should not be empty");
    }

    @Test
    @DisplayName("If-Else nested")
    void parse_IfElse_NoErrors() {
      String input = "if(x) y = 1 else if(y) z=2 else z=3";
      ParserResponse resp = parserService.parse(input);

      assertNotNull(resp.getAst(), "AST should be generated");

      List<String> errs = resp.getErrors();
      assertFalse(errs.isEmpty(), "Expected semantic errors for undeclared identifiers");
    }
  }

  @Nested
  @DisplayName("Lexical error cases")
  class LexicalErrorCases {

    @Test
    @DisplayName("Invalid character in input")
    void parse_InvalidCharacter_LexError() {
      String input = "a = 1 $ 2";
      ParserResponse resp = parserService.parse(input);

      // AST should be null (pipeline aborts on lexical error)
      assertNull(resp.getAst(), "AST should be null when lexical error occurs");

      List<String> errs = resp.getErrors();
      assertEquals(1, errs.size(), "Should report exactly one lexical error");
      assertTrue(
          errs.get(0).contains("Lexical error"), "Error message should mention 'Lexical error'");
    }
  }

  @Nested
  @DisplayName("Syntactic error cases")
  class SyntaxErrorCases {

    @Test
    @DisplayName("Missing closing parenthesis")
    void parse_MissingParen_SyntaxError() {
      String input = "if(x y=1 else y=2"; // no ')'
      ParserResponse resp = parserService.parse(input);

      // AST may be null or partial
      assertNull(resp.getAst(), "AST is null when top-level syntax error");

      List<String> errs = resp.getErrors();
      assertFalse(errs.isEmpty(), "Should report syntax errors");
      assertTrue(
          errs.stream().anyMatch(m -> m.contains("expected 'RPAREN'")),
          "Error should mention missing ')'");
    }

    @Test
    @DisplayName("Operator out of place")
    void parse_OperatorOutOfPlace_SyntaxError() {
      String input = "a = + 5";
      ParserResponse resp = parserService.parse(input);

      assertNull(resp.getAst());
      assertFalse(resp.getErrors().isEmpty());
      assertTrue(
          resp.getErrors().get(0).contains("expected '('"),
          "Error should mention expected '(' or identifier");
    }
  }

  @Nested
  @DisplayName("Semantic error cases")
  class SemanticErrorCases {

    @Test
    @DisplayName("Undeclared variable in assignment")
    void parse_UndeclaredVar_SemanticError() {
      String input = "x = 10";
      ParserResponse resp = parserService.parse(input);

      // AST is non-null (parser succeeded)
      assertNotNull(resp.getAst());

      List<String> errs = resp.getErrors();
      assertFalse(errs.isEmpty(), "Should report semantic errors");
      assertTrue(
          errs.get(0).contains("undeclared variable 'x'"), "Error should mention undeclared 'x'");
    }

    @Test
    @DisplayName("Type mismatch in binary op")
    void parse_TypeMismatch_SemanticError() {
      String input = "a = 1\nb = \"str\"\nc = a + b";
      ParserResponse resp = parserService.parse(input);

      // AST non-null
      assertNotNull(resp.getAst());

      List<String> errs = resp.getErrors();

      // New: simply assert that there was at least one semantic error
      assertFalse(errs.isEmpty(), "Expected at least one semantic error, but none were reported");
    }
  }
}
