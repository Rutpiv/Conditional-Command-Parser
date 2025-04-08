package br.edu.fesa.Conditional_Command_Parser.service;

import br.edu.fesa.Conditional_Command_Parser.model.*;
import br.edu.fesa.Conditional_Command_Parser.utils.*;
import br.edu.fesa.Conditional_Command_Parser.utils.RecursiveDescentParser;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * Service class responsible for orchestrating the parsing process.
 *
 * <p>This service performs the following steps:
 *
 * <ol>
 *   <li>Lexical Analysis: Tokenizes the input string using the {@link Lexer}.
 *   <li>Syntactic Analysis: Constructs an abstract syntax tree (AST) using the {@link
 *       RecursiveDescentParser}.
 *   <li>Response Assembly: Prepares a {@link ParserResponse} containing the AST and the computed
 *       FIRST and FOLLOW sets.
 * </ol>
 */
@Service
@Slf4j
public class ParserService {

  // The lexer component responsible for tokenizing the input.
  private final Lexer lexer;
  // The calculator for computing FIRST and FOLLOW sets.
  private final FirstFollowCalculator firstFollowCalculator;

  /*
   * Constructs the ParserService with the required dependencies.
   *
   * @param lexer the lexer used for lexical analysis.
   * @param firstFollowCalculator the component to compute FIRST and FOLLOW sets.
   */
  @Autowired
  public ParserService(Lexer lexer, FirstFollowCalculator firstFollowCalculator) {
    this.lexer = lexer;
    this.firstFollowCalculator = firstFollowCalculator;
  }

  /*
   * Parses the given input string and returns a {@link ParserResponse} object that contains the
   * AST, FIRST sets, FOLLOW sets, and any syntax errors encountered during parsing.
   *
   * @param input the input string to parse.
   * @return the parser response containing the results of the parsing process.
   */
  public ParserResponse parse(String input) {
    try {
      // Step 1: Lexical Analysis - Tokenize the input
      List<Token> tokens = lexer.tokenize(input);
      log.debug("Generated tokens: {}", tokens);

      // Step 2: Syntactic Analysis - Parse the tokens into an AST
      SyntaxNode ast = new RecursiveDescentParser(tokens).parse();

      // Step 3: Build the response containing the AST and computed sets
      return ParserResponse.builder()
          .ast(ast)
          .firstSets(firstFollowCalculator.getFirstSets())
          .followSets(firstFollowCalculator.getFollowSets())
          .build();

    } catch (SyntaxException ex) {
      // Log and return the syntax error details along with computed sets
      log.error("Syntax error: {}", ex.getMessage());
      return ParserResponse.builder()
          .errors(Collections.singletonList(ex.getMessage()))
          .firstSets(firstFollowCalculator.getFirstSets())
          .followSets(firstFollowCalculator.getFollowSets())
          .build();
    }
  }
}
