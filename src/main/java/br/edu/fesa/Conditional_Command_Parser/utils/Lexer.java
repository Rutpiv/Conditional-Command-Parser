package br.edu.fesa.Conditional_Command_Parser.utils;

import br.edu.fesa.Conditional_Command_Parser.exception.LexicalException;
import br.edu.fesa.Conditional_Command_Parser.model.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Component responsible for lexical analysis (tokenization) of the input string.
 *
 * <p>Supports multiple states:
 *
 * <ul>
 *   <li>DEFAULT: normal scanning
 *   <li>STRING: inside a double-quoted literal
 *   <li>LINE_COMMENT: after "//" until end-of-line
 *   <li>BLOCK_COMMENT: between "/*" and "*&#47;"
 * </ul>
 *
 * Emits tokens of type {@link Token.Type} and tracks line/column positions. Throws {@link
 * LexicalException} on unrecognized characters or unterminated literals.
 */
@Component
public class Lexer {

  private enum State {
    DEFAULT,
    STRING,
    LINE_COMMENT,
    BLOCK_COMMENT
  }

  private static final Pattern FLOAT_PATTERN = Pattern.compile("^\\d+\\.\\d+");
  private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+");
  private static final Pattern CHAR_PATTERN = Pattern.compile("^'(\\\\.|[^\\\\'])'");
  private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*");
  private static final Map<String, Token.Type> KEYWORDS =
      Map.of(
          "if", Token.Type.IF,
          "else", Token.Type.ELSE);

  /**
   * Tokenizes the entire input, producing a list of tokens and one EOF token.
   *
   * @param input raw source code to tokenize
   * @return list of tokens including EOF
   * @throws LexicalException if an invalid character or unterminated literal is encountered
   */
  public List<Token> tokenize(String input) throws LexicalException {
    List<Token> tokens = new ArrayList<>();
    State state = State.DEFAULT;

    int pos = 0, line = 1, column = 1;
    int startLine = 1, startCol = 1;

    while (pos < input.length()) {
      char c = input.charAt(pos);

      switch (state) {
        case DEFAULT:
          // Skip whitespace and track line/column
          if (Character.isWhitespace(c)) {
            if (c == '\n') {
              line++;
              column = 1;
            } else {
              column++;
            }
            pos++;
            continue;
          }

          // Enter string literal state
          if (c == '"') {
            state = State.STRING;
            startLine = line;
            startCol = column;
            pos++;
            column++;
            continue;
          }

          // Enter line comment state
          if (c == '/' && pos + 1 < input.length() && input.charAt(pos + 1) == '/') {
            state = State.LINE_COMMENT;
            pos += 2;
            column += 2;
            continue;
          }

          // Enter block comment state
          if (c == '/' && pos + 1 < input.length() && input.charAt(pos + 1) == '*') {
            state = State.BLOCK_COMMENT;
            pos += 2;
            column += 2;
            continue;
          }

          // Remaining substring for regex matching
          String rest = input.substring(pos);

          // Try floating-point literal
          if (Character.isDigit(c)) {
            Matcher fm = FLOAT_PATTERN.matcher(rest);
            if (fm.lookingAt()) {
              String lex = fm.group();
              tokens.add(new Token(Token.Type.FLOAT, lex, line, column));
              pos += lex.length();
              column += lex.length();
              continue;
            }
          }

          // Try integer literal
          if (Character.isDigit(c)) {
            Matcher nm = NUMBER_PATTERN.matcher(rest);
            if (nm.lookingAt()) {
              String lex = nm.group();
              tokens.add(new Token(Token.Type.NUMBER, lex, line, column));
              pos += lex.length();
              column += lex.length();
              continue;
            }
          }

          // Try character literal
          if (c == '\'') {
            Matcher cm = CHAR_PATTERN.matcher(rest);
            if (cm.find()) {
              String lex = cm.group();
              String val = lex.substring(1, lex.length() - 1);
              tokens.add(new Token(Token.Type.CHAR, val, line, column));
              pos += lex.length();
              column += lex.length();
              continue;
            } else {
              throw new LexicalException(
                  String.format("Unterminated or invalid char literal at %d:%d", line, column));
            }
          }

          // Identifier or keyword
          Matcher idm = ID_PATTERN.matcher(rest);
          if (idm.find()) {
            String lex = idm.group();
            Token.Type type = KEYWORDS.getOrDefault(lex, Token.Type.ID);
            tokens.add(new Token(type, lex, line, column));
            pos += lex.length();
            column += lex.length();
            continue;
          }

          // Single-character operators/punctuation
          Token.Type t =
              switch (c) {
                case '(' -> Token.Type.LPAREN;
                case ')' -> Token.Type.RPAREN;
                case '=' -> Token.Type.EQUALS;
                case '+' -> Token.Type.PLUS;
                case '-' -> Token.Type.MINUS;
                case '*' -> Token.Type.TIMES;
                case '/' -> Token.Type.DIVIDE;
                default -> null;
              };
          if (t != null) {
            tokens.add(new Token(t, String.valueOf(c), line, column));
            pos++;
            column++;
            continue;
          }

          // No valid token found
          throw new LexicalException(
              String.format("Invalid character '%c' at %d:%d", c, line, column));

        case STRING:
          // Collect until closing quote
          StringBuilder sb = new StringBuilder();
          while (pos < input.length()) {
            char ch = input.charAt(pos);
            if (ch == '\\' && pos + 1 < input.length()) {
              sb.append(ch).append(input.charAt(pos + 1));
              pos += 2;
              column += 2;
            } else if (ch == '"') {
              pos++;
              column++;
              tokens.add(new Token(Token.Type.STRING, sb.toString(), startLine, startCol));
              state = State.DEFAULT;
              break;
            } else {
              if (ch == '\n') {
                line++;
                column = 1;
              } else {
                column++;
              }
              sb.append(ch);
              pos++;
            }
          }
          if (state == State.STRING) {
            throw new LexicalException(
                String.format("Unterminated string literal at %d:%d", startLine, startCol));
          }
          continue;

        case LINE_COMMENT:
          // Skip until end-of-line
          while (pos < input.length() && input.charAt(pos) != '\n') {
            pos++;
            column++;
          }
          state = State.DEFAULT;
          continue;

        case BLOCK_COMMENT:
          // Skip until closing */
          boolean closed = false;
          while (pos < input.length()) {
            char ch = input.charAt(pos);
            if (ch == '\n') {
              line++;
              column = 1;
              pos++;
            } else if (ch == '*' && pos + 1 < input.length() && input.charAt(pos + 1) == '/') {
              pos += 2;
              column += 2;
              closed = true;
              break;
            } else {
              pos++;
              column++;
            }
          }
          if (!closed) {
            throw new LexicalException(
                String.format("Unterminated block comment starting at %d:%d", startLine, startCol));
          }
          state = State.DEFAULT;
          continue;
      }
    }

    // Append EOF token
    tokens.add(new Token(Token.Type.EOF, "", line, column));
    return tokens;
  }
}
