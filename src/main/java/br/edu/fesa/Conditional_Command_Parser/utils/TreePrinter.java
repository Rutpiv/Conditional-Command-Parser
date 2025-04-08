package br.edu.fesa.Conditional_Command_Parser.utils;

import br.edu.fesa.Conditional_Command_Parser.model.*;
import java.util.ArrayList;
import java.util.List;

public class TreePrinter {

  public static String generateASCIITree(SyntaxNode node) {
    return generateASCIITree(node, 0, null);
  }

  // Método recursivo que permite adicionar um label opcional para o nó atual
  private static String generateASCIITree(SyntaxNode node, int indent, String childLabel) {
    StringBuilder sb = new StringBuilder();
    if (node == null) {
      return "";
    }

    // Cria a indentação
    String indentStr = "";
    if (indent > 0) {
      for (int i = 0; i < indent - 1; i++) {
        indentStr += "│  ";
      }
      indentStr += "├─ ";
    }

    // Se houver um label para esse nó (por exemplo, "Condition", "Left", etc.)
    String labelPrefix = (childLabel != null) ? childLabel + ": " : "";

    // Imprime a linha atual com a representação do nó
    sb.append(indentStr).append(labelPrefix).append(getNodeRepresentation(node)).append("\n");

    // Imprime os filhos com rótulo, dependendo do tipo de nó
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
      // Para outros nós, se houver filhos (em casos genéricos)
      List<SyntaxNode> children = getChildren(node);
      for (SyntaxNode child : children) {
        sb.append(generateASCIITree(child, indent + 1, null));
      }
    }

    return sb.toString();
  }

  // Representação textual do nó, usando nomes de token em vez do nome da classe, quando possível.
  private static String getNodeRepresentation(SyntaxNode node) {
    // Para nós terminais, imprime um token significativo
    if (node instanceof Identifier) {
      Identifier id = (Identifier) node;
      return "ID(" + id.getName() + ")";
    } else if (node instanceof NumberLiteral) {
      NumberLiteral num = (NumberLiteral) node;
      return "NUMBER(" + num.getValue() + ")";
    } else if (node instanceof BinOp) {
      BinOp bin = (BinOp) node;
      return "BINOP(" + bin.getOperator() + ")";
    } else if (node instanceof Assignment) {
      Assignment asg = (Assignment) node;
      return "ASSIGN(" + asg.getIdentifier() + ")";
    } else if (node instanceof IfStatement) {
      return "IF";
    }
    // Caso padrão: utiliza o nome da classe
    return node.getClass().getSimpleName();
  }

  // Caso exista a necessidade de pegar filhos de nós que não forem tratados especificamente,
  // este método tenta retornar uma lista dos filhos "genéricos"
  private static List<SyntaxNode> getChildren(SyntaxNode node) {
    List<SyntaxNode> children = new ArrayList<>();
    // Se há outros tipos de nós com filhos não especificados, adicione-os aqui.
    return children;
  }
}
