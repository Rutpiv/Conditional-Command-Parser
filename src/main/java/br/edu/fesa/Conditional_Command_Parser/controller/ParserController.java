package br.edu.fesa.Conditional_Command_Parser.controller;

import br.edu.fesa.Conditional_Command_Parser.model.ParserResponse;
import br.edu.fesa.Conditional_Command_Parser.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ParserController {

  @Autowired private ParserService parserService;

  @GetMapping("/")
  public String index() {
    return "index";
  }

  @PostMapping("/parse")
  public String parseInput(@RequestParam String input, Model model) {
    try {
      ParserResponse response = parserService.parse(input);
      model.addAttribute("ast", response.getAst());
      model.addAttribute("firstSets", response.getFirstSets());
      model.addAttribute("followSets", response.getFollowSets());
      model.addAttribute("errors", response.getErrors());
    } catch (Exception e) {
      model.addAttribute("errors", List.of("Erro inesperado: " + e.getMessage()));
    }
    return "index";
  }
}
