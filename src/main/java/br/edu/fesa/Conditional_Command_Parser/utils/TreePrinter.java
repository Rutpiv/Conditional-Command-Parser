package br.edu.fesa.Conditional_Command_Parser.utils;

import br.edu.fesa.Conditional_Command_Parser.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for generating an ASCII-art representation of the AST.
 *
 * <p>Each node is printed on its own line with Unicode branch characters, and child branches are
 * labeled ("Condition", "Left", "Right", etc.) for clarity.
 */
public class TreePrinter {

  /**
   * Generates a complete ASCII tree from the given AST root.
   *
   * @param node root of the AST
   * @return multi-line string depicting the tree structure
   */
  public static String generateASCIITree(SyntaxNode node) {
    return generateASCIITree(node, 0, null);
  }

  /**
   * Recursive helper that builds the ASCII tree.
   *
   * @param node current AST node (may be null)
   * @param indent depth for branching characters
   * @param childLabel optional label for this branch (e.g. "Left", "Then")
   * @return ASCII lines for this subtree
   */
  private static String generateASCIITree(SyntaxNode node, int indent, String childLabel) {
    if (node == null) {
      return "";
    }

    StringBuilder sb = new StringBuilder();

    // Build indentation prefix ("│  " per level, then "├─ ")
    String indentStr = "";
    for (int i = 0; i < indent - 1; i++) {
      indentStr += "│  ";
    }
    if (indent > 0) {
      indentStr += "├─ ";
    }

    // Optional branch label
    String labelPrefix = (childLabel != null) ? childLabel + ": " : "";

    // Append this node’s representation
    sb.append(indentStr).append(labelPrefix).append(getNodeRepresentation(node)).append("\n");

    // Recurse into known children
    if (node instanceof IfStatement) {
      IfStatement ifs = (IfStatement) node;
      sb.append(generateASCIITree(ifs.getCondition(), indent + 1, "Condition"));
      sb.append(generateASCIITree(ifs.getThenBranch(), indent + 1, "Then"));
      sb.append(generateASCIITree(ifs.getElseBranch(), indent + 1, "Else"));

    } else if (node instanceof Assignment) {
      Assignment asg = (Assignment) node;
      sb.append(generateASCIITree(asg.getExpression(), indent + 1, "Expression"));

    } else if (node instanceof BinOp) {
      BinOp bin = (BinOp) node;
      sb.append(generateASCIITree(bin.getLeft(), indent + 1, "Left"));
      sb.append(generateASCIITree(bin.getRight(), indent + 1, "Right"));

    } else {
      // No children for literals and identifiers; override getChildren() if you add more
      for (SyntaxNode child : getChildren(node)) {
        sb.append(generateASCIITree(child, indent + 1, null));
      }
    }
    return sb.toString();
  }

  /**
   * Returns the one-line representation for each AST node type.
   *
   * @param node AST node
   * @return string label for the node
   */
  private static String getNodeRepresentation(SyntaxNode node) {
    if (node instanceof Identifier) {
      return "ID(" + ((Identifier) node).getName() + ")";
    } else if (node instanceof NumberLiteral) {
      return "NUMBER(" + ((NumberLiteral) node).getValue() + ")";
    } else if (node instanceof FloatLiteral) {
      return "FLOAT(" + ((FloatLiteral) node).getValue() + ")";
    } else if (node instanceof StringLiteral) {
      return "STRING(\"" + ((StringLiteral) node).getValue() + "\")";
    } else if (node instanceof CharLiteral) {
      return "CHAR('" + ((CharLiteral) node).getValue() + "')";
    } else if (node instanceof BinOp) {
      return "BINOP(" + ((BinOp) node).getOperator() + ")";
    } else if (node instanceof Assignment) {
      return "ASSIGN(" + ((Assignment) node).getIdentifier() + ")";
    } else if (node instanceof IfStatement) {
      return "IF";
    }
    // Fallback: use class name
    return node.getClass().getSimpleName();
  }

  /**
   * Extension point for custom AST nodes that have children not handled above.
   *
   * @param node AST node
   * @return list of child nodes (empty by default)
   */
  private static List<SyntaxNode> getChildren(SyntaxNode node) {
    return new ArrayList<>();
  }
}
