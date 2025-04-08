package br.edu.fesa.Conditional_Command_Parser.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

/* Unit tests for the FirstFollowCalculator component. */
class FirstFollowCalculatorTest {

  private final FirstFollowCalculator calculator = new FirstFollowCalculator();

  /* Test constructor that ensures calculations are executed before tests. */
  public FirstFollowCalculatorTest() {
    calculator.calculateSets();
  }

  // ====================== FIRST Sets Tests ======================

  /*
   * Verifies if FIRST sets were correctly calculated for each non-terminal.
   * 
   * Grammar:
   *   S → if ( E ) S else S | id = E  
   *   E → E + T | E - T | T  
   *   T → T * F | T / F | F  
   *   F → ( E ) | id  
   * 
   * Expected:
   *   FIRST(S) = { "if", "id" }
   *   FIRST(E) = FIRST(T) = FIRST(F) = { "(", "id" }
   */
  @Test
  void testFirstSets() {
    Map<String, Set<String>> first = calculator.getFirstSets();

    assertEquals(Set.of("if", "id"), first.get("S"), "Incorrect FIRST set for S");
    assertEquals(Set.of("(", "id"), first.get("E"), "Incorrect FIRST set for E");
    assertEquals(Set.of("(", "id"), first.get("T"), "Incorrect FIRST set for T");
    assertEquals(Set.of("(", "id"), first.get("F"), "Incorrect FIRST set for F");
  }

  // ====================== FOLLOW Sets Tests ======================

  /*
   * Verifies if FOLLOW sets were correctly calculated for each non-terminal.
   * 
   * Based on grammar and calculation rules:
   *   FOLLOW(S) = { "$", "else" }
   *   FOLLOW(E) = { "$", "else", ")", "+", "-" }
   *   FOLLOW(T) = { "$", "else", ")", "*", "+", "-", "/" }
   *   FOLLOW(F) = { "$", "else", ")", "*", "+", "-", "/" }
   */
  @Test
  void testFollowSets() {
    Map<String, Set<String>> follow = calculator.getFollowSets();

    assertEquals(Set.of("$", "else"), follow.get("S"), "Incorrect FOLLOW set for S");
    assertEquals(Set.of("$", "else", ")", "+", "-"), follow.get("E"), "Incorrect FOLLOW set for E");
    assertEquals(Set.of("$", "else", ")", "*", "+", "-", "/"), follow.get("T"), "Incorrect FOLLOW set for T");
    assertEquals(Set.of("$", "else", ")", "*", "+", "-", "/"), follow.get("F"), "Incorrect FOLLOW set for F");
  }
}
