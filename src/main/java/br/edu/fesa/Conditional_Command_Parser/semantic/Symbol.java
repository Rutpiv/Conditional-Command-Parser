package br.edu.fesa.Conditional_Command_Parser.semantic;

import br.edu.fesa.Conditional_Command_Parser.model.Token;
import lombok.Builder;
import lombok.Getter;

/** Entry in the symbol table: name, declared type, and source position. */
@Getter
@Builder
public class Symbol {
  /** Variable or symbol name. */
  private final String name;

  /** Declared type of the symbol (from Token.Type). */
  private final Token.Type type;

  /** Source line where this symbol was declared. */
  private final int line;

  /** Source column where this symbol was declared. */
  private final int column;
}
