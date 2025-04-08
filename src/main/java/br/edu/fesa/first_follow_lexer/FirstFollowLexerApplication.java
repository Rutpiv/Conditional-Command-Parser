package br.edu.fesa.first_follow_lexer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import br.edu.fesa.parser.Parser;

@SpringBootApplication
public class FirstFollowLexerApplication {

        public static void main(String[] args) {
            // Exemplo de uso
            String input = "if (x) y = z else w = 5"; // Altere para testar
            Parser parser = new Parser(input);
            boolean isValid = parser.parse();
            System.out.println("A entrada é válida? " + isValid);
    }
}
