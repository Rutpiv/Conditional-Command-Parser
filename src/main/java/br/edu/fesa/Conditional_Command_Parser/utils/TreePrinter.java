package br.edu.fesa.Conditional_Command_Parser.utils;

import br.edu.fesa.Conditional_Command_Parser.model.*;
import java.util.ArrayList;
import java.util.List;

/*
 * Utility class for generating an ASCII representation of the abstract syntax tree (AST).
 *
 * <p>This class provides methods to generate an ASCII tree from an AST node. It prints each node
 * along with labels for specific branches (e.g., "Condition", "Left", "Right") for a clearer
 * representation. If a node does not have a predefined structure, it attempts to extract its
 * children in a generic manner.
 */
public class TreePrinter {

  /*
   * Generates an ASCII representation of the AST starting from the given node.
   *
   * @param node the root node of the AST.
   * @return a string representing the ASCII tree.
   */
  public static String generateASCIITree(SyntaxNode node) {
    return generateASCIITree(node, 0, null);
  }

  /*
   * Recursively generates an ASCII tree representation for the given node with a specified
   * indentation and optional child label.
   *
   * @param node the AST node.
   * @param indent the current indentation level.
   * @param childLabel an optional label to describe the relationship to the parent node.
   * @return a string representing the ASCII tree starting from the provided node.
   */
  private static String generateASCIITree(SyntaxNode node, int indent, String childLabel) {
    StringBuilder sb = new StringBuilder();
    if (node == null) {
      return "";
    }

    // Generate the indentation string.
    String indentStr = "";
    if (indent > 0) {
      for (int i = 0; i < indent - 1; i++) {
        indentStr += "│  ";
      }
      indentStr += "├─ ";
    }

    // If a child label is provided (e.g., "Condition", "Left"), add it before the node
    // representation.
    String labelPrefix = (childLabel != null) ? childLabel + ": " : "";

    // Append the current node's string representation.
    sb.append(indentStr).append(labelPrefix).append(getNodeRepresentation(node)).append("\n");

    // Recursively print children based on the node type.
    if (node instanceof IfStatement) {
      IfStatement ifStmt = (IfStatement) node;
      sb.append(generateASCIITree(ifStmt.getCondition(), indent + 1, "Condition"));
      sb.append(generateASCIITree(ifStmt.getThenBranch(), indent + 1, "Then"));
      sb.append(generateASCIITree(ifStmt.getElseBranch(), indent + 1, "Else"));
    } else if (node instanceof Assignment) {
      Assignment asg = (Assignment) node;
      sb.append(generateASCIITree(asg.getExpression(), indent + 1, "Expression"));
    } else if (node instanceof BinOp) {
      BinOp bin = (BinOp) node;
      sb.append(generateASCIITree(bin.getLeft(), indent + 1, "Left"));
      sb.append(generateASCIITree(bin.getRight(), indent + 1, "Right"));
    } else {
      // For other node types, attempt to retrieve any generic children.
      List<SyntaxNode> children = getChildren(node);
      for (SyntaxNode child : children) {
        sb.append(generateASCIITree(child, indent + 1, null));
      }
    }

    return sb.toString();
  }

  /*
   * Returns a string representation for the given AST node. For terminal nodes, it shows a
   * token-like representation.
   *
   * @param node the AST node.
   * @return a string description of the node.
   */
  private static String getNodeRepresentation(SyntaxNode node) {
    // For identifier nodes, print the identifier name.
    if (node instanceof Identifier) {
      Identifier id = (Identifier) node;
      return "ID(" + id.getName() + ")";
    } else if (node instanceof NumberLiteral) {
      // For number literals, print the numeric value.
      NumberLiteral num = (NumberLiteral) node;
      return "NUMBER(" + num.getValue() + ")";
    } else if (node instanceof BinOp) {
      // For binary operations, print the operator.
      BinOp bin = (BinOp) node;
      return "BINOP(" + bin.getOperator() + ")";
    } else if (node instanceof Assignment) {
      // For assignments, print the identifier being assigned.
      Assignment asg = (Assignment) node;
      return "ASSIGN(" + asg.getIdentifier() + ")";
    } else if (node instanceof IfStatement) {
      // For if statements, print a simple label.
      return "IF";
    }
    // Default case: use the simple name of the class.
    return node.getClass().getSimpleName();
  }

  /*
   * Retrieves generic children from the given AST node.
   *
   * <p>This method can be extended to support additional node types that have children not
   * specifically handled.
   *
   * @param node the AST node.
   * @return a list of child nodes, or an empty list if none.
   */
  private static List<SyntaxNode> getChildren(SyntaxNode node) {
    List<SyntaxNode> children = new ArrayList<>();
    // If there are other types of nodes with children that are not explicitly handled, add them
    // here.
    return children;
  }
}
