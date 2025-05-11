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

/**
 * Spring MVC controller handling the parsing web interface.
 *
 * <p>GET "/" renders the input page; POST "/parse" processes the code and returns results.
 */
@Controller
public class ParserController {

  private final ParserService parserService;

  @Autowired
  public ParserController(ParserService parserService) {
    this.parserService = parserService;
  }

  /**
   * Show the main index page.
   *
   * @return view name "index"
   */
  @GetMapping("/")
  public String index() {
    return "index";
  }

  /**
   * Handle code submission, invoke parsing pipeline, and populate the view model.
   *
   * @param input user’s code to parse
   * @param model Spring MVC model for passing attributes to the template
   * @return view name "index"
   */
  @PostMapping("/parse")
  public String parseInput(@RequestParam String input, Model model) {
    try {
      // Run the full pipeline
      ParserResponse response = parserService.parse(input);
      // Render ASCII tree
      String asciiTree = TreePrinter.generateASCIITree(response.getAst());

      // Add attributes for Thymeleaf or JSP
      model.addAttribute("ast", asciiTree);
      model.addAttribute("firstSets", response.getFirstSets());
      model.addAttribute("followSets", response.getFollowSets());
      model.addAttribute("errors", response.getErrors());
    } catch (Exception e) {
      // Unexpected failures
      model.addAttribute("errors", List.of("Unexpected error: " + e.getMessage()));
    }
    return "index";
  }
}
