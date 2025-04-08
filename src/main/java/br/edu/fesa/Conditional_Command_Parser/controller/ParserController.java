package br.edu.fesa.Conditional_Command_Parser.controller;

import br.edu.fesa.Conditional_Command_Parser.model.ParserResponse;
import br.edu.fesa.Conditional_Command_Parser.service.ParserService;
import br.edu.fesa.Conditional_Command_Parser.utils.TreePrinter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*
 * Controller class that handles web requests for parsing user input.
 *
 * <p>This controller provides endpoints for displaying the main page and processing parsing
 * requests. It utilizes the {@link ParserService} to perform lexical and syntactical analysis on
 * input code, and the {@link TreePrinter} to generate an ASCII representation of the resulting
 * abstract syntax tree (AST). The parsed data, including the AST, FIRST sets, FOLLOW sets, and any
 * parsing errors, are added to the model for rendering in the view.
 */
@Controller
public class ParserController {

  // The parser service that encapsulates the logic for parsing input.
  @Autowired private ParserService parserService;

  /*
   * Handles the GET request for the main page.
   *
   * @return the view name for the index page.
   */
  @GetMapping("/")
  public String index() {
    return "index";
  }

  /*
   * Processes the POST request to parse the provided input string.
   *
   * <p>This method receives the input string from the HTTP request, calls the {@link ParserService}
   * to perform the parsing, and then generates an ASCII tree representation of the abstract syntax
   * tree (AST) using the {@link TreePrinter}. The resulting AST, FIRST sets, FOLLOW sets, and any
   * parsing errors are added to the model for rendering on the index page.
   *
   * @param input the input string to be parsed.
   * @param model the model object that holds attributes to be rendered by the view.
   * @return the view name for displaying the parsing results.
   */
  @PostMapping("/parse")
  public String parseInput(@RequestParam String input, Model model) {
    try {
      // Parse the input and build the response containing the AST and computed sets.
      ParserResponse response = parserService.parse(input);
      // Generate an ASCII representation of the AST for display purposes.
      String asciiTree = TreePrinter.generateASCIITree(response.getAst());

      // Add parsed attributes to the model.
      model.addAttribute("ast", asciiTree);
      model.addAttribute("firstSets", response.getFirstSets());
      model.addAttribute("followSets", response.getFollowSets());
      model.addAttribute("errors", response.getErrors());
    } catch (Exception e) {
      // In case of any unexpected error, add an error message to the model.
      model.addAttribute("errors", List.of("Unexpected error: " + e.getMessage()));
    }
    return "index";
  }
}
