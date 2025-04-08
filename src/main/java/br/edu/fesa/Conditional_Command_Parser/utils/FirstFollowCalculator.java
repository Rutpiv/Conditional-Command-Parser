package br.edu.fesa.Conditional_Command_Parser.utils;

import java.util.*;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class FirstFollowCalculator {

  private final Map<String, Set<String>> firstSets = new HashMap<>();
  private final Map<String, Set<String>> followSets = new HashMap<>();

  // Representação da gramática: cada não terminal mapeia para uma lista de produções,
  // onde cada produção é uma lista de símbolos (terminals ou não-terminals).
  private final Map<String, List<List<String>>> grammar = new HashMap<>();
  private final Set<String> nonTerminals = new HashSet<>();

  @PostConstruct
  public void calculateSets() {
    // Define os não-terminais
    nonTerminals.add("S");
    nonTerminals.add("E");
    nonTerminals.add("T");
    nonTerminals.add("F");

    // Define as produções com base na gramática:
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

    // Inicializa os conjuntos FIRST para cada não terminal (começando como vazio)
    for (String nt : nonTerminals) {
      firstSets.put(nt, new HashSet<>());
    }

    // Algoritmo iterativo para calcular FIRST
    boolean changed = true;
    while (changed) {
      changed = false;
      // Para cada não terminal da gramática
      for (String nt : grammar.keySet()) {
        Set<String> first = firstSets.get(nt);
        for (List<String> production : grammar.get(nt)) {
          // Percorre os símbolos da produção
          for (String symbol : production) {
            // Se o símbolo for terminal (ou seja, não está no conjunto nonTerminals), adicione-o e
            // pare
            if (!nonTerminals.contains(symbol)) {
              if (first.add(symbol)) {
                changed = true;
              }
              break;
            } else {
              // Caso contrário, se for não terminal, adicione os elementos de FIRST desse símbolo
              // Obs.: Em gramáticas com produções que geram ε, seria necessário remover o ε e
              // continuar para o próximo símbolo se ε estiver presente.
              int beforeSize = first.size();
              first.addAll(firstSets.get(symbol));
              if (first.size() > beforeSize) {
                changed = true;
              }
              // Como não há ε na gramática, interrompemos a verificação da produção
              break;
            }
          }
        }
      }
    }

    // Inicializa os conjuntos FOLLOW para cada não terminal (começam vazios)
    for (String nt : nonTerminals) {
      followSets.put(nt, new HashSet<>());
    }
    // O símbolo inicial (S) recebe o marcador de fim de cadeia: "$"
    followSets.get("S").add("$");

    // Algoritmo iterativo para calcular FOLLOW
    changed = true;
    while (changed) {
      changed = false;
      // Para cada produção A -> α
      for (String A : grammar.keySet()) {
        for (List<String> production : grammar.get(A)) {
          for (int i = 0; i < production.size(); i++) {
            String symbol = production.get(i);
            // Se o símbolo for não terminal, verifica os símbolos seguintes
            if (nonTerminals.contains(symbol)) {
              Set<String> follow = followSets.get(symbol);
              int beforeSize = follow.size();
              boolean nextCanDeriveEpsilon = true; // Em gramáticas com ε

              // Verifica os símbolos após o símbolo corrente na produção
              for (int j = i + 1; j < production.size() && nextCanDeriveEpsilon; j++) {
                String nextSymbol = production.get(j);
                if (nonTerminals.contains(nextSymbol)) {
                  // Adiciona FIRST do não terminal seguinte
                  follow.addAll(firstSets.get(nextSymbol));
                  // Como não temos ε, interrompemos aqui
                  nextCanDeriveEpsilon = false;
                } else {
                  // Se o símbolo seguinte é terminal, adiciona-o e para
                  follow.add(nextSymbol);
                  nextCanDeriveEpsilon = false;
                }
              }
              // Se não houver símbolos seguintes, ou se eles puderem derivar ε, adiciona FOLLOW de
              // A
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

  public Map<String, Set<String>> getFirstSets() {
    return Collections.unmodifiableMap(firstSets);
  }

  public Map<String, Set<String>> getFollowSets() {
    return Collections.unmodifiableMap(followSets);
  }
}
