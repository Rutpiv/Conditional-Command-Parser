package br.edu.fesa.Conditional_Command_Parser.utils;

import br.edu.fesa.Conditional_Command_Parser.model.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/*
 * Lexer component for tokenizing input strings based on defined lexical rules.
 *
 * <p>This class uses regular expressions to identify numbers, identifiers, and keywords, while also
 * handling operators and punctuation. For each recognized token, a corresponding {@link Token} is
 * created with its type, value, and location (line and column numbers).
 */
@Component
public class Lexer {

  // Pattern for matching a number at the beginning of the string.
  private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+");

  // Pattern for matching an identifier (or keyword) at the beginning of the string.
  private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*");

  // Map of reserved keywords to their token types.
  private static final Map<String, Token.Type> KEYWORDS =
      Map.of(
          "if", Token.Type.IF,
          "else", Token.Type.ELSE);

  /*
   * Tokenizes the given input string.
   *
   * <p>This method scans the input, recognizing tokens including numbers, identifiers, keywords,
   * and various operators or symbols. It also maintains line and column information for error
   * reporting.
   *
   * @param input The input string to tokenize.
   * @return A list of tokens extracted from the input.
   * @throws SyntaxException If an invalid character is encountered during tokenization.
   */
  public List<Token> tokenize(String input) throws SyntaxException {
    List<Token> tokens = new ArrayList<>();
    int line = 1;
    int position = 0;
    int column = 1;

    while (position < input.length()) {
      char current = input.charAt(position);

      // Skip whitespace characters.
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

      // Check if the current section matches a number.
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

      // Check if the current section matches an identifier or keyword (longest match).
      Matcher idMatcher = ID_PATTERN.matcher(substring);
      if (idMatcher.find()) {
        String value = idMatcher.group();
        Token.Type type = KEYWORDS.getOrDefault(value, Token.Type.ID);
        tokens.add(new Token(type, value, line, column));
        position += value.length();
        column += value.length();
        continue;
      }

      // Check for single-character operators and punctuation.
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

      // If the character is unrecognized, throw a syntax exception with detailed location info.
      throw new SyntaxException(
          "Caractere inválido na linha " + line + ", coluna " + column + ": '" + current + "'");
    }

    // Add the end-of-file token at the end of the input.
    tokens.add(new Token(Token.Type.EOF, "", line, column));
    return tokens;
  }
}
