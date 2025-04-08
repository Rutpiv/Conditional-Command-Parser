package br.edu.fesa.Conditional_Command_Parser.utils;

import java.util.*;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/*
 * Component that calculates the FIRST and FOLLOW sets for a given grammar.
 *
 * <p>This class implements an algorithm to compute the FIRST and FOLLOW sets for a simple
 * grammar. The grammar is defined internally with its productions, and the sets are computed using
 * iterative methods. Note that this implementation assumes there is no production generating the empty string (ε).
 */
@Component
public class FirstFollowCalculator {

  // Map storing the FIRST set for each non-terminal symbol.
  private final Map<String, Set<String>> firstSets = new HashMap<>();

  // Map storing the FOLLOW set for each non-terminal symbol.
  private final Map<String, Set<String>> followSets = new HashMap<>();

  // Representation of the grammar: each non-terminal maps to a list of productions,
  // where each production is represented as a list of symbols (terminals or non-terminals).
  private final Map<String, List<List<String>>> grammar = new HashMap<>();

  // Set of non-terminal symbols used in the grammar.
  private final Set<String> nonTerminals = new HashSet<>();

  /*
   * Initializes and calculates the FIRST and FOLLOW sets.
   * <p>
   * This method is executed once after dependency injection (PostConstruct). It first sets up the
   * non-terminals and productions that define the grammar. Then, it initializes the FIRST and FOLLOW
   * sets and iteratively computes them until no further changes occur.
   */
  @PostConstruct
  public void calculateSets() {
    // Define non-terminal symbols.
    nonTerminals.add("S");
    nonTerminals.add("E");
    nonTerminals.add("T");
    nonTerminals.add("F");

    // Define the productions based on the grammar:
    // S → if ( E ) S else S | id = E
    grammar.put(
        "S",
        Arrays.asList(
            Arrays.asList("if", "(", "E", ")", "S", "else", "S"), Arrays.asList("id", "=", "E")));

    // E → E + T | E - T | T
    grammar.put(
        "E",
        Arrays.asList(
            Arrays.asList("E", "+", "T"), Arrays.asList("E", "-", "T"), Arrays.asList("T")));

    // T → T * F | T / F | F
    grammar.put(
        "T",
        Arrays.asList(
            Arrays.asList("T", "*", "F"), Arrays.asList("T", "/", "F"), Arrays.asList("F")));

    // F → ( E ) | id
    grammar.put("F", Arrays.asList(Arrays.asList("(", "E", ")"), Arrays.asList("id")));

    // Initialize the FIRST sets for each non-terminal as empty sets.
    for (String nt : nonTerminals) {
      firstSets.put(nt, new HashSet<>());
    }

    // Iterative algorithm to compute the FIRST sets.
    boolean changed = true;
    while (changed) {
      changed = false;
      // For each non-terminal in the grammar.
      for (String nt : grammar.keySet()) {
        Set<String> first = firstSets.get(nt);
        for (List<String> production : grammar.get(nt)) {
          // Process each symbol in the production.
          for (String symbol : production) {
            // If the symbol is a terminal (i.e., not contained in nonTerminals), add it and break.
            if (!nonTerminals.contains(symbol)) {
              if (first.add(symbol)) {
                changed = true;
              }
              break;
            } else {
              // If the symbol is non-terminal, add all symbols from its FIRST set.
              // Note: In grammars with ε-productions, ε should be removed and the next symbol
              // should be processed if present.
              int beforeSize = first.size();
              first.addAll(firstSets.get(symbol));
              if (first.size() > beforeSize) {
                changed = true;
              }
              // Since there is no ε in the grammar, we break out of processing the production.
              break;
            }
          }
        }
      }
    }

    // Initialize the FOLLOW sets for each non-terminal as empty sets.
    for (String nt : nonTerminals) {
      followSets.put(nt, new HashSet<>());
    }
    // The starting non-terminal "S" receives the end-of-input marker "$".
    followSets.get("S").add("$");

    // Iterative algorithm to compute the FOLLOW sets.
    changed = true;
    while (changed) {
      changed = false;
      // For each production A -> α in the grammar.
      for (String A : grammar.keySet()) {
        for (List<String> production : grammar.get(A)) {
          for (int i = 0; i < production.size(); i++) {
            String symbol = production.get(i);
            // Process only non-terminal symbols.
            if (nonTerminals.contains(symbol)) {
              Set<String> follow = followSets.get(symbol);
              int beforeSize = follow.size();
              boolean nextCanDeriveEpsilon = true; // Applicable for grammars with ε productions.

              // Check symbols following the current non-terminal in the production.
              for (int j = i + 1; j < production.size() && nextCanDeriveEpsilon; j++) {
                String nextSymbol = production.get(j);
                if (nonTerminals.contains(nextSymbol)) {
                  // Add FIRST of the non-terminal that follows.
                  follow.addAll(firstSets.get(nextSymbol));
                  // As there is no ε in this grammar, break out of the loop.
                  nextCanDeriveEpsilon = false;
                } else {
                  // If the next symbol is terminal, add it and break.
                  follow.add(nextSymbol);
                  nextCanDeriveEpsilon = false;
                }
              }
              // If no symbols follow, or they can derive ε, add the FOLLOW of the production's
              // left-hand side.
              if (nextCanDeriveEpsilon) {
                follow.addAll(followSets.get(A));
              }
              if (follow.size() > beforeSize) {
                changed = true;
              }
            }
          }
        }
      }
    }
  }

  /*
   * Returns an unmodifiable view of the computed FIRST sets.
   *
   * @return Map of non-terminals to their FIRST sets.
   */
  public Map<String, Set<String>> getFirstSets() {
    return Collections.unmodifiableMap(firstSets);
  }

  /*
   * Returns an unmodifiable view of the computed FOLLOW sets.
   *
   * @return Map of non-terminals to their FOLLOW sets.
   */
  public Map<String, Set<String>> getFollowSets() {
    return Collections.unmodifiableMap(followSets);
  }
}
