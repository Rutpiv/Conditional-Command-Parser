package br.edu.fesa.Conditional_Command_Parser.utils;

import br.edu.fesa.Conditional_Command_Parser.model.*;
import java.util.ArrayList;
import java.util.List;

public class TreePrinter {

  public static String generateASCIITree(SyntaxNode node, int indent) {
    StringBuilder sb = new StringBuilder();
    if (node == null) {
      return "";
    }

    if (indent > 0) {
      for (int i = 0; i < indent - 1; i++) {
        sb.append("│  ");
      }
      sb.append("├─ ");
    }

    sb.append(getNodeRepresentation(node));
    sb.append("\n");

    List<SyntaxNode> children = getChildren(node);
    for (SyntaxNode child : children) {
      sb.append(generateASCIITree(child, indent + 1));
    }

    return sb.toString();
  }

  private static String getNodeRepresentation(SyntaxNode node) {
    String rep = node.getClass().getSimpleName();
    if (node instanceof Identifier) {
      Identifier id = (Identifier) node;
      rep += ": " + id.getName();
    } else if (node instanceof NumberLiteral) {
      NumberLiteral num = (NumberLiteral) node;
      rep += ": " + num.getValue();
    } else if (node instanceof BinOp) {
      BinOp bin = (BinOp) node;
      rep += ": " + bin.getOperator();
    } else if (node instanceof Assignment) {
      Assignment asg = (Assignment) node;
      rep += ": " + asg.getIdentifier();
    }
    return rep;
  }

  private static List<SyntaxNode> getChildren(SyntaxNode node) {
    List<SyntaxNode> children = new ArrayList<>();
    if (node instanceof IfStatement) {
      IfStatement ifStmt = (IfStatement) node;
      children.add(ifStmt.getCondition());
      children.add(ifStmt.getThenBranch());
      children.add(ifStmt.getElseBranch());
    } else if (node instanceof Assignment) {
      Assignment assignment = (Assignment) node;
      children.add(assignment.getExpression());
    } else if (node instanceof BinOp) {
      BinOp bin = (BinOp) node;
      children.add(bin.getLeft());
      children.add(bin.getRight());
    }
    return children;
  }
}
