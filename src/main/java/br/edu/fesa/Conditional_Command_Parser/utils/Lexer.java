package br.edu.fesa.Conditional_Command_Parser.utils;

import br.edu.fesa.Conditional_Command_Parser.model.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class Lexer {

  private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+");
  private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*");

  private static final Map<String, Token.Type> KEYWORDS =
      Map.of(
          "if", Token.Type.IF,
          "else", Token.Type.ELSE);

  public List<Token> tokenize(String input) throws SyntaxException {
    List<Token> tokens = new ArrayList<>();
    int line = 1;
    int position = 0;
    int column = 1;

    while (position < input.length()) {
      char current = input.charAt(position);

      // Ignora espaços em branco
      if (Character.isWhitespace(current)) {
        if (current == '\n') {
          line++;
          column = 1;
        } else {
          column++;
        }
        position++;
        continue;
      }

      String substring = input.substring(position);

      // Verifica se é número
      if (Character.isDigit(current)) {
        Matcher numberMatcher = NUMBER_PATTERN.matcher(substring);
        if (numberMatcher.find()) {
          String value = numberMatcher.group();
          tokens.add(new Token(Token.Type.NUMBER, value, line, column));
          position += value.length();
          column += value.length();
          continue;
        }
      }

      // Verifica se é identificador ou palavra-chave (match mais longo)
      Matcher idMatcher = ID_PATTERN.matcher(substring);
      if (idMatcher.find()) {
        String value = idMatcher.group();
        Token.Type type = KEYWORDS.getOrDefault(value, Token.Type.ID);
        tokens.add(new Token(type, value, line, column));
        position += value.length();
        column += value.length();
        continue;
      }

      // Verifica operadores e símbolos individuais
      Token.Type type = null;
      switch (current) {
        case '(':
          type = Token.Type.LPAREN;
          break;
        case ')':
          type = Token.Type.RPAREN;
          break;
        case '=':
          type = Token.Type.EQUALS;
          break;
        case '+':
          type = Token.Type.PLUS;
          break;
        case '-':
          type = Token.Type.MINUS;
          break;
        case '*':
          type = Token.Type.TIMES;
          break;
        case '/':
          type = Token.Type.DIVIDE;
          break;
      }

      if (type != null) {
        tokens.add(new Token(type, String.valueOf(current), line, column));
        position++;
        column++;
        continue;
      }

      throw new SyntaxException(
          "Caractere inválido na linha " + line + ", coluna " + column + ": '" + current + "'");
    }

    tokens.add(new Token(Token.Type.EOF, "", line, column));
    return tokens;
  }
}
