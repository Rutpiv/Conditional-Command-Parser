package br.edu.fesa.Conditional_Command_Parser.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/*
 * Container class for parser output including AST and metadata. Used to return parsing results and
 * auxiliary parser information.
 */
@Data
@Builder
@AllArgsConstructor
public class ParserResponse {
  /* Generated abstract syntax tree (AST) root node */
  private SyntaxNode ast;

  /* List of parsing errors encountered during analysis */
  private List<String> errors;

  /* First sets used in parser construction (for debugging/analysis) */
  private Map<String, Set<String>> firstSets;

  /* Follow sets used in parser construction (for debugging/analysis) */
  private Map<String, Set<String>> followSets;
}
