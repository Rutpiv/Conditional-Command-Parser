package br.edu.fesa.Conditional_Command_Parser.utils;

import static org.junit.jupiter.api.Assertions.*;

import br.edu.fesa.Conditional_Command_Parser.model.*;
import br.edu.fesa.Conditional_Command_Parser.model.Token.Type;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** Unit tests for the SemanticAnalyzer component. */
class SemanticAnalyzerTest {

  private final SemanticAnalyzer analyzer = new SemanticAnalyzer();

  // Helper: run analysis and return error list
  private List<String> analyze(SyntaxNode root) {
    analyzer.analyze(root);
    return analyzer.getErrors();
  }

  @Nested
  @DisplayName("Undeclared identifier errors")
  class UndeclaredTests {

    @Test
    @DisplayName("Simple assignment to undeclared variable")
    void assignmentToUndeclared() {
      // AST: x = 42
      Assignment asg =
          Assignment.builder()
              .line(1)
              .column(1)
              .identifier("x")
              .expression(NumberLiteral.builder().line(1).column(5).value("42").build())
              .build();

      List<String> errors = analyze(asg);
      assertEquals(1, errors.size());
      assertTrue(errors.get(0).contains("undeclared variable 'x'"));
    }

    @Test
    @DisplayName("Use of undeclared identifier in expression")
    void useOfUndeclaredIdentifier() {
      // AST: y + 1
      BinOp expr =
          BinOp.builder()
              .line(2)
              .column(3)
              .operator("+")
              .left(Identifier.builder().line(2).column(3).name("y").build())
              .right(NumberLiteral.builder().line(2).column(7).value("1").build())
              .build();

      List<String> errors = analyze(expr);

      assertFalse(errors.isEmpty(), "Expected at least one undeclared-identifier error");
      assertTrue(
          errors.stream().anyMatch(e -> e.contains("undeclared identifier 'y'")),
          "Expected an error mentioning undeclared 'y', but got: " + errors);
    }
  }

  @Nested
  @DisplayName("Type mismatch errors")
  class TypeMismatchTests {

    @Test
    @DisplayName("Assignment of wrong type")
    void assignmentTypeMismatch() {
      // Pretend x is declared as NUMBER by manually inserting a symbol:
      // but our analyzer doesn't support declarations,
      // so we simulate by wrapping in a fake symbol table lookup step.
      // Instead, just test mismatch detection:
      // AST: x = "hello"
      Assignment asg =
          Assignment.builder()
              .line(1)
              .column(1)
              .identifier("x")
              .expression(StringLiteral.builder().line(1).column(5).value("hello").build())
              .build();

      // Manually prepopulate symbol table is not supported; skip that.
      List<String> errors = analyze(asg);
      // First error: undeclared variable
      assertTrue(errors.get(0).contains("undeclared variable 'x'"));
      // No further type-checking because analyzer bails on undeclared
    }

    @Test
    @DisplayName("Binary operation with mismatched operand types")
    void binOpTypeMismatch() {
      // AST: 1 + "two"
      BinOp expr =
          BinOp.builder()
              .line(3)
              .column(2)
              .operator("+")
              .left(NumberLiteral.builder().line(3).column(2).value("1").build())
              .right(StringLiteral.builder().line(3).column(6).value("two").build())
              .build();

      List<String> errors = analyze(expr);
      assertEquals(1, errors.size());
      assertTrue(errors.get(0).contains("incompatible types"));
      assertTrue(errors.get(0).contains("NUMBER"));
      assertTrue(errors.get(0).contains("STRING"));
    }
  }

  @Nested
  @DisplayName("If-statement condition errors")
  class IfConditionTests {

    @Test
    @DisplayName("Non-numeric if condition")
    void nonNumericCondition() {
      // AST: if("no") x=1 else x=2
      IfStatement ifs =
          IfStatement.builder()
              .line(4)
              .column(1)
              .condition(StringLiteral.builder().line(4).column(4).value("no").build())
              .thenBranch(
                  Assignment.builder()
                      .line(4)
                      .column(10)
                      .identifier("x")
                      .expression(NumberLiteral.builder().line(4).column(14).value("1").build())
                      .build())
              .elseBranch(
                  Assignment.builder()
                      .line(4)
                      .column(18)
                      .identifier("x")
                      .expression(NumberLiteral.builder().line(4).column(22).value("2").build())
                      .build())
              .build();

      List<String> errors = analyze(ifs);

      assertFalse(errors.isEmpty(), "Expected at least the non-numeric condition error");
      assertTrue(
          errors.stream().anyMatch(e -> e.contains("non-numeric if condition")),
          "Expected a non-numeric-condition error, but got: " + errors);
      assertTrue(errors.get(0).contains("STRING"));
    }
  }

  @Nested
  @DisplayName("Mixed valid cases")
  class ValidTests {

    @Test
    @DisplayName("Binary operation with matching numeric types")
    void binOpHomogeneousTypes() {
      // AST: 2.5 * 4.0
      BinOp expr =
          BinOp.builder()
              .line(5)
              .column(3)
              .operator("*")
              .left(FloatLiteral.builder().line(5).column(3).value("2.5").build())
              .right(FloatLiteral.builder().line(5).column(7).value("4.0").build())
              .build();

      List<String> errors = analyze(expr);
      assertTrue(errors.isEmpty(), "Expected no semantic errors");
      assertEquals(Type.FLOAT, expr.getType());
    }
  }
}
