package br.edu.fesa.Conditional_Command_Parser.utils;

import static org.junit.jupiter.api.Assertions.*;

import br.edu.fesa.Conditional_Command_Parser.exception.LexicalException;
import br.edu.fesa.Conditional_Command_Parser.model.Token;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for the Lexer component.
 */
class LexerTest {

  private final Lexer lexer = new Lexer();

  // -------------- Helpers --------------

  private void assertSingleToken(String input, Token.Type expectedType, String expectedValue)
      throws LexicalException {
    List<Token> tokens = lexer.tokenize(input);
    assertEquals(expectedType, tokens.get(0).getType());
    assertEquals(expectedValue, tokens.get(0).getValue());
    assertEquals(Token.Type.EOF, tokens.get(1).getType());
  }

  private void assertThrowsLex(String input, String expectedMessagePart) {
    LexicalException ex = assertThrows(
        LexicalException.class,
        () -> lexer.tokenize(input),
        "Expected LexicalException for input: " + input);
    assertTrue(ex.getMessage().contains(expectedMessagePart),
        () -> "Expected message to contain '" + expectedMessagePart + "', was: " + ex.getMessage());
  }

  // -------------- Integer & Float --------------

  @Nested
  @DisplayName("Numeric literals")
  class NumericLiteralsTest {

    @Test @DisplayName("Valid integer")
    void integerToken() throws LexicalException {
      assertSingleToken("12345", Token.Type.NUMBER, "12345");
    }

    @Test @DisplayName("Valid float")
    void floatToken() throws LexicalException {
      assertSingleToken("3.1415", Token.Type.FLOAT, "3.1415");
    }

    @Test @DisplayName("Invalid float (no leading digit)")
    void invalidFloat() {
      assertThrowsLex(".5", ".");
    }
  }

  // -------------- Character literals --------------

  @Nested
  @DisplayName("Character literals")
  class CharLiteralsTest {

    @Test @DisplayName("Simple char")
    void simpleChar() throws LexicalException {
      assertSingleToken("'a'", Token.Type.CHAR, "a");
    }

    @Test @DisplayName("Escaped char")
    void escapedChar() throws LexicalException {
      assertSingleToken("'\\n'", Token.Type.CHAR, "\\n");
    }

    @Test @DisplayName("Unterminated char literal")
    void unterminatedChar() {
      assertThrowsLex("'a", "Unterminated");
    }
  }

  // -------------- String literals --------------

  @Nested
  @DisplayName("String literals")
  class StringLiteralsTest {

    @Test @DisplayName("Simple string")
    void simpleString() throws LexicalException {
      assertSingleToken("\"hello\"", Token.Type.STRING, "hello");
    }

    @Test @DisplayName("String with escape")
    void stringWithEscape() throws LexicalException {
      assertSingleToken("\"line\\nbreak\"", Token.Type.STRING, "line\\nbreak");
    }

    @Test @DisplayName("Unterminated string")
    void unterminatedString() {
      assertThrowsLex("\"oops", "Unterminated string");
    }
  }

  // -------------- Identifiers & Keywords --------------

  @Nested
  @DisplayName("Identifiers vs Keywords")
  class IdKeywordTest {

    @ParameterizedTest(name = "keyword \"{0}\"")
    @ValueSource(strings = { "if", "else" })
    void recognizesKeywords(String kw) throws LexicalException {
      List<Token> tokens = lexer.tokenize(kw);
      assertEquals(Token.Type.valueOf(kw.toUpperCase()), tokens.get(0).getType());
      assertEquals(kw, tokens.get(0).getValue());
    }

    @Test @DisplayName("Non-keyword ID")
    void recognizesIdentifier() throws LexicalException {
      assertSingleToken("variable123", Token.Type.ID, "variable123");
    }
  }

  // -------------- Operators & Punctuation --------------

  @Test
  @DisplayName("Operators and punctuation")
  void operatorsAndPunctuation() throws LexicalException {
    String input = " ( ) = + - * / ";
    Token.Type[] expectedTypes = {
        Token.Type.LPAREN, Token.Type.RPAREN,
        Token.Type.EQUALS, Token.Type.PLUS,
        Token.Type.MINUS, Token.Type.TIMES,
        Token.Type.DIVIDE, Token.Type.EOF
    };

    List<Token> tokens = lexer.tokenize(input);
    for (int i = 0; i < expectedTypes.length; i++) {
      assertEquals(expectedTypes[i], tokens.get(i).getType(),
                   "Token at index " + i);
    }
  }

  // -------------- Comments --------------

  @Nested
  @DisplayName("Comments")
  class CommentsTest {

    @Test @DisplayName("Line comment is skipped")
    void lineComment() throws LexicalException {
      List<Token> tokens = lexer.tokenize("// this is a comment\n42");
      assertEquals(Token.Type.NUMBER, tokens.get(0).getType());
      assertEquals("42", tokens.get(0).getValue());
    }

    @Test @DisplayName("Block comment is skipped")
    void blockComment() throws LexicalException {
      List<Token> tokens = lexer.tokenize("/* hello\nworld */ 7");
      assertEquals(Token.Type.NUMBER, tokens.get(0).getType());
      assertEquals("7", tokens.get(0).getValue());
    }

    @Test @DisplayName("Unterminated block comment")
    void unterminatedBlock() {
      assertThrowsLex("/* not closed", "Unterminated block comment");
    }
  }

  // -------------- Whitespace & Position --------------

  @Test
  @DisplayName("Whitespace and position tracking")
  void whitespaceAndPosition() throws LexicalException {
    String input = "if\n  123";
    List<Token> tokens = lexer.tokenize(input);

    Token t1 = tokens.get(0);
    assertEquals(Token.Type.IF, t1.getType());
    assertEquals(1, t1.getLine());
    assertEquals(1, t1.getColumn());

    Token t2 = tokens.get(1);
    assertEquals(Token.Type.NUMBER, t2.getType());
    assertEquals("123", t2.getValue());
    assertEquals(2, t2.getLine());
    assertEquals(3, t2.getColumn());  // two spaces before '123'
  }

  // -------------- Error on invalid character --------------

  @Test
  @DisplayName("Invalid character throws exception")
  void invalidCharacter() {
    assertThrowsLex("abc$def", "$");
  }
}
