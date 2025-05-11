package br.edu.fesa.Conditional_Command_Parser.utils;

import java.util.*;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * Component that computes the FIRST and FOLLOW sets for a fixed grammar.
 *
 * <p>The grammar is defined internally, and sets are computed iteratively. ε-productions are not
 * supported in this implementation.
 */
@Component
public class FirstFollowCalculator {

  /** FIRST sets for each nonterminal. */
  private final Map<String, Set<String>> firstSets = new HashMap<>();

  /** FOLLOW sets for each nonterminal. */
  private final Map<String, Set<String>> followSets = new HashMap<>();

  /** Grammar productions: Nonterminal → list of alternative right-hand sides. */
  private final Map<String, List<List<String>>> grammar = new HashMap<>();

  /** All nonterminal symbols in the grammar. */
  private final Set<String> nonTerminals = new HashSet<>();

  /**
   * Initializes grammar definitions and computes FIRST and FOLLOW sets.
   *
   * <p>This method runs once after bean construction.
   */
  @PostConstruct
  public void calculateSets() {
    defineGrammar();
    computeFirstSets();
    computeFollowSets();
  }

  /**
   * Returns an unmodifiable view of the computed FIRST sets.
   *
   * @return map of nonterminal → FIRST set
   */
  public Map<String, Set<String>> getFirstSets() {
    return Collections.unmodifiableMap(firstSets);
  }

  /**
   * Returns an unmodifiable view of the computed FOLLOW sets.
   *
   * @return map of nonterminal → FOLLOW set
   */
  public Map<String, Set<String>> getFollowSets() {
    return Collections.unmodifiableMap(followSets);
  }

  // ─── Internal helpers ───────────────────────────────────────────────────────

  private void defineGrammar() {
    // Nonterminals
    nonTerminals.addAll(List.of("S", "E", "T", "F"));

    // Productions for S → if ( E ) S else S | id = E
    grammar.put(
        "S", List.of(List.of("if", "(", "E", ")", "S", "else", "S"), List.of("id", "=", "E")));

    // Productions for E → E + T | E - T | T
    grammar.put("E", List.of(List.of("E", "+", "T"), List.of("E", "-", "T"), List.of("T")));

    // Productions for T → T * F | T / F | F
    grammar.put("T", List.of(List.of("T", "*", "F"), List.of("T", "/", "F"), List.of("F")));

    // Productions for F → ( E ) | id
    grammar.put("F", List.of(List.of("(", "E", ")"), List.of("id")));
  }

  private void computeFirstSets() {
    // Initialize empty FIRST sets
    nonTerminals.forEach(nt -> firstSets.put(nt, new HashSet<>()));

    boolean changed;
    do {
      changed = false;
      for (String nt : grammar.keySet()) {
        Set<String> first = firstSets.get(nt);
        for (List<String> production : grammar.get(nt)) {
          for (String symbol : production) {
            if (!nonTerminals.contains(symbol)) {
              // terminal → add and stop
              if (first.add(symbol)) changed = true;
              break;
            } else {
              // nonterminal → add its FIRST, then stop (no ε support)
              if (first.addAll(firstSets.get(symbol))) changed = true;
              break;
            }
          }
        }
      }
    } while (changed);
  }

  private void computeFollowSets() {
    // Initialize empty FOLLOW sets
    nonTerminals.forEach(nt -> followSets.put(nt, new HashSet<>()));
    // Start symbol S gets end-marker "$"
    followSets.get("S").add("$");

    boolean changed;
    do {
      changed = false;
      for (String A : grammar.keySet()) {
        for (List<String> production : grammar.get(A)) {
          for (int i = 0; i < production.size(); i++) {
            String symbol = production.get(i);
            if (nonTerminals.contains(symbol)) {
              Set<String> follow = followSets.get(symbol);
              int before = follow.size();

              boolean canPassOn = true;
              // look at symbols after symbol
              for (int j = i + 1; j < production.size() && canPassOn; j++) {
                String next = production.get(j);
                if (nonTerminals.contains(next)) {
                  // add FIRST(next) then stop
                  follow.addAll(firstSets.get(next));
                  canPassOn = false;
                } else {
                  // terminal → add and stop
                  follow.add(next);
                  canPassOn = false;
                }
              }
              // if at end or all could derive ε (not supported), add FOLLOW(A)
              if (canPassOn) {
                follow.addAll(followSets.get(A));
              }
              if (follow.size() > before) {
                changed = true;
              }
            }
          }
        }
      }
    } while (changed);
  }
}
