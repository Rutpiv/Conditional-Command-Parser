package br.edu.fesa.Conditional_Command_Parser.utils;

import static org.junit.jupiter.api.Assertions.*;

import br.edu.fesa.Conditional_Command_Parser.model.Token;
import java.util.List;
import org.junit.jupiter.api.Test;

/* Unit tests for the Lexer component. */
class LexerTest {

  private final Lexer lexer = new Lexer();

  // ====================== Number Token Test ======================

  /*
   * Verifies that when receiving a string composed only of digits,
   * the Lexer returns a NUMBER token with the correct value.
   */
  @Test
  void tokenize_Number_ShouldReturnNumberToken() throws SyntaxException {
    String input = "12345";
    List<Token> tokens = lexer.tokenize(input);
    // First token should be NUMBER with value "12345"
    Token token = tokens.get(0);
    assertEquals(Token.Type.NUMBER, token.getType(), "Expected token: NUMBER");
    assertEquals("12345", token.getValue(), "Incorrect token value");
    
    // Last token should be EOF
    Token eof = tokens.get(tokens.size() - 1);
    assertEquals(Token.Type.EOF, eof.getType(), "Expected token: EOF");
  }

  // ====================== Identifier Token Test ======================

  /*
   * Verifies that when receiving a string representing an identifier (non-keyword),
   * the Lexer returns an ID token with the correct value.
   */
  @Test
  void tokenize_Identifier_ShouldReturnIdentifierToken() throws SyntaxException {
    String input = "variavel";
    List<Token> tokens = lexer.tokenize(input);
    Token token = tokens.get(0);
    assertEquals(Token.Type.ID, token.getType(), "Expected token: ID");
    assertEquals("variavel", token.getValue(), "Incorrect token value");
    
    Token eof = tokens.get(tokens.size() - 1);
    assertEquals(Token.Type.EOF, eof.getType(), "Expected token: EOF");
  }

  // ====================== Keyword Recognition Test ======================

  /*
   * Verifies that the Lexer recognizes defined keywords ("if" and "else")
   * and returns them with appropriate token types.
   */
  @Test
  void tokenize_Keywords_ShouldReturnCorrectKeywordTokens() throws SyntaxException {
    String input = "if else";
    List<Token> tokens = lexer.tokenize(input);
    
    Token tokenIf = tokens.get(0);
    assertEquals(Token.Type.IF, tokenIf.getType(), "Expected token: IF");
    assertEquals("if", tokenIf.getValue(), "Incorrect token value");
    
    Token tokenElse = tokens.get(1);
    assertEquals(Token.Type.ELSE, tokenElse.getType(), "Expected token: ELSE");
    assertEquals("else", tokenElse.getValue(), "Incorrect token value");
    
    Token eof = tokens.get(tokens.size() - 1);
    assertEquals(Token.Type.EOF, eof.getType(), "Expected token: EOF");
  }

  // ====================== Operators and Punctuation Test ======================

  /*
   * Verifies that the Lexer recognizes tokens for simple operators and punctuation: 
   * '(', ')', '=', '+', '-', '*', '/'.
   */
  @Test
  void tokenize_OperatorsAndPunctuation_ShouldReturnCorrectTokens() throws SyntaxException {
    String input = "( )=+-*/";
    List<Token> tokens = lexer.tokenize(input);
    
    // Expected order: LPAREN, RPAREN, EQUALS, PLUS, MINUS, TIMES, DIVIDE, EOF
    assertEquals(Token.Type.LPAREN, tokens.get(0).getType(), "Expected token: LPAREN");
    assertEquals("(", tokens.get(0).getValue());
    
    assertEquals(Token.Type.RPAREN, tokens.get(1).getType(), "Expected token: RPAREN");
    assertEquals(")", tokens.get(1).getValue());
    
    assertEquals(Token.Type.EQUALS, tokens.get(2).getType(), "Expected token: EQUALS");
    assertEquals("=", tokens.get(2).getValue());
    
    assertEquals(Token.Type.PLUS, tokens.get(3).getType(), "Expected token: PLUS");
    assertEquals("+", tokens.get(3).getValue());
    
    assertEquals(Token.Type.MINUS, tokens.get(4).getType(), "Expected token: MINUS");
    assertEquals("-", tokens.get(4).getValue());
    
    assertEquals(Token.Type.TIMES, tokens.get(5).getType(), "Expected token: TIMES");
    assertEquals("*", tokens.get(5).getValue());
    
    assertEquals(Token.Type.DIVIDE, tokens.get(6).getType(), "Expected token: DIVIDE");
    assertEquals("/", tokens.get(6).getValue());
    
    Token eof = tokens.get(tokens.size() - 1);
    assertEquals(Token.Type.EOF, eof.getType(), "Expected token: EOF");
  }

  // ====================== Whitespace Handling and Position Tracking Test ======================

  /*
   * Verifies that the Lexer correctly ignores whitespace and updates line/column information.
   * In the example, keyword "if" is on line 1 and number "123" on line 2.
   */
  @Test
  void tokenize_WithWhitespace_ShouldTrackLineAndColumn() throws SyntaxException {
    String input = "if\n123";
    List<Token> tokens = lexer.tokenize(input);
    
    // Token "if" should be at line 1, column 1
    Token tokenIf = tokens.get(0);
    assertEquals(Token.Type.IF, tokenIf.getType(), "Expected token: IF");
    assertEquals(1, tokenIf.getLine(), "Incorrect line for 'if' token");
    assertEquals(1, tokenIf.getColumn(), "Incorrect column for 'if' token");
    
    // Token "123" should be at line 2, column 1
    Token tokenNumber = tokens.get(1);
    assertEquals(Token.Type.NUMBER, tokenNumber.getType(), "Expected token: NUMBER");
    assertEquals("123", tokenNumber.getValue(), "Incorrect token value");
    assertEquals(2, tokenNumber.getLine(), "Incorrect line for '123' token");
    assertEquals(1, tokenNumber.getColumn(), "Incorrect column for '123' token");
  }

  // ====================== Invalid Character Test ======================

  /*
   * Verifies that when encountering an unrecognized character (e.g., '$'),
   * a SyntaxException is thrown with a message indicating the problem and character position.
   */
  @Test
  void tokenize_InvalidCharacter_ShouldThrowSyntaxException() {
    String input = "abc$def";
    Exception exception = assertThrows(SyntaxException.class, () -> {
      lexer.tokenize(input);
    });
    // Verify exception message contains invalid character '$'
    assertTrue(exception.getMessage().contains("$"), "Exception message should mention '$' character");
  }
}
