package br.edu.fesa.Conditional_Command_Parser.utils;

import br.edu.fesa.Conditional_Command_Parser.model.*;
import br.edu.fesa.Conditional_Command_Parser.semantic.SymbolTable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Component that performs semantic analysis on the AST:
 *
 * <ul>
 *   <li>Symbol table construction and scope management
 *   <li>Type checking for assignments, binary operations, and conditions
 *   <li>Detection of undeclared identifiers and type mismatches
 *   <li>Annotates AST nodes with their semantic type
 * </ul>
 */
@Component
public class SemanticAnalyzer {

  private SymbolTable symTable = new SymbolTable();
  private final List<String> errors = new ArrayList<>();

  /**
   * Returns the list of semantic errors found in the last analysis.
   *
   * @return unmodifiable list of error messages
   */
  public List<String> getErrors() {
    return List.copyOf(errors);
  }

  /**
   * Analyzes the given AST root and collects semantic errors. Clears previous state and
   * reinitializes the symbol table.
   *
   * @param root root of the AST to analyze
   */
  public void analyze(SyntaxNode root) {
    errors.clear();
    symTable = new SymbolTable();
    visit(root);
  }

  // Recursive visitor dispatch:

  private void visit(SyntaxNode node) {
    if (node instanceof Assignment) {
      visitAssignment((Assignment) node);
    } else if (node instanceof IfStatement) {
      visitIf((IfStatement) node);
    } else if (node instanceof BinOp) {
      visitBinOp((BinOp) node);
    } else if (node instanceof NumberLiteral) {
      node.setType(Token.Type.NUMBER);
    } else if (node instanceof FloatLiteral) {
      node.setType(Token.Type.FLOAT);
    } else if (node instanceof StringLiteral) {
      node.setType(Token.Type.STRING);
    } else if (node instanceof CharLiteral) {
      node.setType(Token.Type.CHAR);
    } else if (node instanceof Identifier) {
      visitIdentifier((Identifier) node);
    }
    // Extend here for other node types (e.g., declarations).
  }

  private void visitAssignment(Assignment asg) {
    String name = asg.getIdentifier();
    var sym = symTable.lookup(name);
    if (sym == null) {
      errors.add(
          String.format(
              "Semantic error [line %d, column %d]: undeclared variable '%s'",
              asg.getLine(), asg.getColumn(), name));
      asg.setType(Token.Type.EOF);
      return;
    }
    visit(asg.getExpression());
    Token.Type exprType = asg.getExpression().getType();
    if (exprType != sym.getType()) {
      errors.add(
          String.format(
              "Semantic error [line %d, column %d]: type mismatch on '%s' - expected %s but got %s",
              asg.getLine(), asg.getColumn(), name, sym.getType(), exprType));
    }
    asg.setType(sym.getType());
  }

  private void visitIf(IfStatement ifs) {
    visit(ifs.getCondition());
    var condType = ifs.getCondition().getType();
    if (condType != Token.Type.NUMBER && condType != Token.Type.FLOAT) {
      errors.add(
          String.format(
              "Semantic error [line %d, column %d]: non-numeric if condition of type %s",
              ifs.getLine(), ifs.getColumn(), condType));
    }
    symTable.enterScope();
    visit(ifs.getThenBranch());
    symTable.exitScope();

    symTable.enterScope();
    visit(ifs.getElseBranch());
    symTable.exitScope();

    ifs.setType(Token.Type.EOF);
  }

  private void visitBinOp(BinOp bin) {
    visit(bin.getLeft());
    visit(bin.getRight());
    var lt = bin.getLeft().getType();
    var rt = bin.getRight().getType();
    if (lt == rt && (lt == Token.Type.NUMBER || lt == Token.Type.FLOAT)) {
      bin.setType(lt);
    } else {
      errors.add(
          String.format(
              "Semantic error [line %d, column %d]: incompatible types %s and %s for operator '%s'",
              bin.getLine(), bin.getColumn(), lt, rt, bin.getOperator()));
      bin.setType(Token.Type.EOF);
    }
  }

  private void visitIdentifier(Identifier id) {
    var sym = symTable.lookup(id.getName());
    if (sym == null) {
      errors.add(
          String.format(
              "Semantic error [line %d, column %d]: undeclared identifier '%s'",
              id.getLine(), id.getColumn(), id.getName()));
      id.setType(Token.Type.EOF);
    } else {
      id.setType(sym.getType());
    }
  }
}
