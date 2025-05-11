package br.edu.fesa.Conditional_Command_Parser.semantic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/** Symbol table supporting nested scopes via a stack of hash maps. */
public class SymbolTable {
  private final Deque<Map<String, Symbol>> scopes = new ArrayDeque<>();

  /** Initializes the table with a global scope. */
  public SymbolTable() {
    enterScope();
  }

  /** Enters a new nested scope. */
  public void enterScope() {
    scopes.push(new HashMap<>());
  }

  /** Exits the current scope, discarding its symbols. */
  public void exitScope() {
    if (scopes.size() > 1) {
      scopes.pop();
    }
  }

  /**
   * Declare a new symbol in the current scope.
   *
   * @param sym symbol to add
   * @return false if a symbol with the same name already exists in this scope
   */
  public boolean declare(Symbol sym) {
    Map<String, Symbol> current = scopes.peek();
    if (current.containsKey(sym.getName())) {
      return false;
    }
    current.put(sym.getName(), sym);
    return true;
  }

  /**
   * Look up a symbol by name, searching from innermost to outermost scope.
   *
   * @param name symbol name to find
   * @return the corresponding Symbol or null if not found
   */
  public Symbol lookup(String name) {
    for (Map<String, Symbol> scope : scopes) {
      if (scope.containsKey(name)) {
        return scope.get(name);
      }
    }
    return null;
  }
}
