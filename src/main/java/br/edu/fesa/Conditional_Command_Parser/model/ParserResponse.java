package br.edu.fesa.Conditional_Command_Parser.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ParserResponse {
  private SyntaxNode ast;
  private List<String> errors;
  private Map<String, Set<String>> firstSets;
  private Map<String, Set<String>> followSets;
}
