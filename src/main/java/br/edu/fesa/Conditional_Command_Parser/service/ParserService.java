package br.edu.fesa.Conditional_Command_Parser.service;

import br.edu.fesa.Conditional_Command_Parser.model.*;
import br.edu.fesa.Conditional_Command_Parser.utils.*;
import br.edu.fesa.Conditional_Command_Parser.utils.RecursiveDescentParser;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ParserService {

  private final Lexer lexer;
  private final FirstFollowCalculator firstFollowCalculator;

  @Autowired
  public ParserService(Lexer lexer, FirstFollowCalculator firstFollowCalculator) {
    this.lexer = lexer;
    this.firstFollowCalculator = firstFollowCalculator;
  }

  public ParserResponse parse(String input) {
    try {
      // Etapa 1: Análise Léxica
      List<Token> tokens = lexer.tokenize(input);
      log.debug("Tokens gerados: {}", tokens);

      // Etapa 2: Análise Sintática
      SyntaxNode ast = new RecursiveDescentParser(tokens).parse();

      // Etapa 3: Montagem da Resposta
      return ParserResponse.builder()
          .ast(ast)
          .firstSets(firstFollowCalculator.getFirstSets())
          .followSets(firstFollowCalculator.getFollowSets())
          .build();

    } catch (SyntaxException ex) {
      log.error("Erro de sintaxe: {}", ex.getMessage());
      return ParserResponse.builder()
          .errors(Collections.singletonList(ex.getMessage()))
          .firstSets(firstFollowCalculator.getFirstSets())
          .followSets(firstFollowCalculator.getFollowSets())
          .build();
    }
  }
}
