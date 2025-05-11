package br.edu.fesa.Conditional_Command_Parser.service;

import br.edu.fesa.Conditional_Command_Parser.exception.LexicalException;
import br.edu.fesa.Conditional_Command_Parser.model.ParserResponse;
import br.edu.fesa.Conditional_Command_Parser.model.SyntaxNode;
import br.edu.fesa.Conditional_Command_Parser.model.Token;
import br.edu.fesa.Conditional_Command_Parser.utils.FirstFollowCalculator;
import br.edu.fesa.Conditional_Command_Parser.utils.Lexer;
import br.edu.fesa.Conditional_Command_Parser.utils.RecursiveDescentParser;
import br.edu.fesa.Conditional_Command_Parser.utils.SemanticAnalyzer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service that orchestrates the complete parsing pipeline:
 *
 * <ol>
 *   <li>Lexical analysis (Lexer)
 *   <li>Syntactic analysis with error recovery (RecursiveDescentParser)
 *   <li>Semantic analysis (SemanticAnalyzer)
 *   <li>Packaging results into a ParserResponse
 * </ol>
 */
@Service
@Slf4j
public class ParserService {

  private final FirstFollowCalculator firstFollowCalculator;
  private final Lexer lexer;
  private final RecursiveDescentParser parser;
  private final SemanticAnalyzer semanticAnalyzer;

  /**
   * Constructs the ParserService with its required components.
   *
   * @param firstFollowCalculator computes FIRST/FOLLOW sets for display
   * @param lexer performs lexical analysis (tokenization)
   * @param parser performs syntactic analysis (prototype-scoped)
   * @param semanticAnalyzer performs semantic checks on the AST
   */
  @Autowired
  public ParserService(
      FirstFollowCalculator firstFollowCalculator,
      Lexer lexer,
      RecursiveDescentParser parser,
      SemanticAnalyzer semanticAnalyzer) {
    this.firstFollowCalculator = firstFollowCalculator;
    this.lexer = lexer;
    this.parser = parser;
    this.semanticAnalyzer = semanticAnalyzer;
  }

  /**
   * Runs the full compile pipeline on the given input string.
   *
   * @param input source code to be analyzed
   * @return a {@link ParserResponse} containing:
   *     <ul>
   *       <li>AST (possibly partial if errors occurred)
   *       <li>All lexical, syntactic, and semantic errors found
   *       <li>FIRST and FOLLOW sets for each nonterminal
   *     </ul>
   */
  public ParserResponse parse(String input) {
    try {
      // 1) Lexical Analysis
      List<Token> tokens = lexer.tokenize(input);
      log.debug("Generated tokens: {}", tokens);

      // 2) Syntactic Analysis (error recovery)
      SyntaxNode ast = parser.parse(tokens);
      List<String> syntaxErrors = parser.getErrors();
      if (!syntaxErrors.isEmpty()) {
        log.error("Syntax errors: {}", syntaxErrors);
      }

      // 3) Semantic Analysis
      semanticAnalyzer.analyze(ast);
      List<String> semanticErrors = semanticAnalyzer.getErrors();
      if (!semanticErrors.isEmpty()) {
        log.error("Semantic errors: {}", semanticErrors);
      }

      // 4) Collect all errors together
      List<String> allErrors = new ArrayList<>(syntaxErrors);
      allErrors.addAll(semanticErrors);

      // 5) Build and return response
      return ParserResponse.builder()
          .ast(ast)
          .errors(allErrors.isEmpty() ? Collections.emptyList() : allErrors)
          .firstSets(firstFollowCalculator.getFirstSets())
          .followSets(firstFollowCalculator.getFollowSets())
          .build();

    } catch (LexicalException lexEx) {
      // On lexical error, abort further analysis and return only this error
      log.error("Lexical error: {}", lexEx.getMessage());
      return ParserResponse.builder()
          .errors(Collections.singletonList("Lexical error: " + lexEx.getMessage()))
          .firstSets(firstFollowCalculator.getFirstSets())
          .followSets(firstFollowCalculator.getFollowSets())
          .build();
    }
  }
}
