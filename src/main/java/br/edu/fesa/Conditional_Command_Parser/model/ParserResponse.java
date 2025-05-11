package br.edu.fesa.Conditional_Command_Parser.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Container for the result of parsing and semantic analysis.
 *
 * <p>Includes the AST root, collected errors, and FIRST/FOLLOW sets used by the parser.
 */
@Data
@Builder
@AllArgsConstructor
public class ParserResponse {
  /** Root of the generated abstract syntax tree (AST). */
  private SyntaxNode ast;

  /** All lexical, syntactic, and semantic errors encountered. */
  private List<String> errors;

  /** FIRST sets computed for each nonterminal (for debugging or display). */
  private Map<String, Set<String>> firstSets;

  /** FOLLOW sets computed for each nonterminal (for debugging or display). */
  private Map<String, Set<String>> followSets;
}
