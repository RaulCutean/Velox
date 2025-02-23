package org.lox;

import java.util.*;

import static org.lox.TokenType.*;
@SuppressWarnings({"unused"})
public class Scanner {
    ///////////////////////////////////////////////////////// FIELDS
    private final String source ;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0 ;
    private int current = 0 ;
    private int line = 1 ;
    private static final Map<String , TokenType> keywords;
    ///  STATIC BLOCK INITIALIZATION
    static {
        keywords = new HashMap<>();
        keywords.put("and", AND) ;
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }
    /////////////////////////////////////////////////////////END FIELDS

    ///////////////////////////////////////////////////////// CONSTRUCTOR
    Scanner(String source) {
        this.source = source ;
    }
    /////////////////////////////////////////////////////////END CONSTRUCTOR

    ///////////////////////////////////////////////////////// !!!!! MAIN FUNCTIONALITY
    List<Token> scanTokens() {

        while(!isAtEnd()) {
            start = current ;
            scanToken();
        }
        tokens.add(new Token(EOF , "" ,  NIL , line)) ;
        return tokens ;
    }
    private boolean isAtEnd() {
        return current >= source.length() ;
    }
    private void scanToken() {
        char c = advance() ;
        switch(c) {
            case '(': addToken(LEFT_PAREN); break ;
            case ')': addToken(RIGHT_PAREN); break ;
            case '{': addToken(LEFT_BRACE); break ;
            case '}': addToken(RIGHT_BRACE); break ;
            case ',': addToken(COMMA); break ;
            case '.': addToken(DOT); break ;
            case '-': addToken(MINUS); break ;
            case '+': addToken(PLUS); break ;
            case ';': addToken(SEMICOLON); break ;
            case '*': addToken(STAR); break ;
            case  '!':
                addToken(match('=') ? BANG_EQUAL : BANG) ;
                break ;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL) ;
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS) ;
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER) ;
                break;
            case '/':
                if(match('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                }
                }else if(match('*')) {
                    while(!isAtEnd()) {
                        if(peek() == '\n') {
                            line++ ;
                        }
                        if(peek() == '*' && peekNext() == '/') {
                            break ;
                        }
                        advance();
                    }
                    if(isAtEnd()) {
                        Lox.error(line, "Unterminated comment");
                    }
                    if(peek() == '*' && peekNext() == '/') {
                        advance();
                        advance();
                    }

                }
                else {
                    addToken(SLASH);
                }
                break ;
            case ' ':
            case '\r':
            case '\t':
                break ;
            case '\n':
                line++;
                break ;
            case '"':
                string() ;
                break ;
            default :
                if(isDigit(c)) {
                    number();
                }else if(isAlpha(c)) {
                    identifier() ;
                }
                else {
                    Lox.error(line , "Unexpected character " + c) ;
                }
                break ;
        }
    }
    ///////////////////////////////////////////////////////// !!!! END MAIN FUNCTIONALITY



    ///////////////////////////////////////////////////////// START HELPER FUNCTIONS
    /// ADVANCE CHARACTER IN STREAM
    private char advance() {
        current++ ;
        return source.charAt(current - 1);
    }
    /// END ADVANCE CHARACTER IN STREAM


    /// ADD CERTAIN TOKEN TYPE FOR SINGLE LENGTH LEXEMES
    private void addToken(TokenType type) {
        addToken(type , null);
    }
    /// END ADD CERTAIN TOKEN TYPE FOR SINGLE LENGTH LEXEMES


    ///////////////////////////////////////////////////////// START
    /// ADD CERTAIN TOKEN TYPE WITH LITERAL(STRING , NUMBER , IDENTIFIER)
    /// AND HELPER METHOD FOR SINGLE LENGTH OR DOUBLE LENGTH LEXEMES
    private void addToken(TokenType type , Object literal) {
        String text = source.substring(start , current) ;
        tokens.add(new Token(type , text , literal , line)) ;
    }
    ///////////////////////////////////////////////////////// END
    /// ADD CERTAIN TOKEN TYPE WITH LITERAL(STRING , NUMBER , IDENTIFIER)
    /// AND HELPER METHOD FOR SINGLE LENGTH OR DOUBLE LENGTH LEXEMES

    /// START SEE IF CHARACTER AFTER LEXEME MATCHES SOMETHING THAT MAKES IT DOUBLE LENGTH TOKEN
    private boolean match(char expected) {
        if(isAtEnd()) {
            return false;
        }
        if(source.charAt(current) != expected) {
            return false ;
        }
        current++ ;
        return true;
    }
    /// END SEE IF CHARACTER AFTER LEXEME MATCHES SOMETHING THAT MAKES IT DOUBLE LENGTH TOKEN

    /// START LOOKAHEAD PEEK FUNCTION
     private char peek() {
         if(isAtEnd()) {
             return '\0';
         }
         return source.charAt(current) ;
     }
    /// END LOOKAHEAD PEEK FUNCTION

    ///  !!!!START STRING HANDLER
    private void string() {
        while( (peek() != '"' && !isAtEnd())) {
            if(peek() == '\n') {
                line++ ;
            }
            advance() ;
        }
        if(isAtEnd()) {
            Lox.error(line , "Unterminated string") ;
            return ;
        }
        advance() ;
        String value = source.substring(start + 1 , current - 1 );
        addToken(STRING , value) ;
    }
    ///  END STRING HANDLER

    ///  START IsDigit HANDLER
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9' ;
    }
    ///  END IsDigit HANDLER

    ///  !!!! START NUMBER HANDLER
    private void number() {
        while(isDigit(peek()) ) {
            advance() ;
        }
        if(peek() == '.' && isDigit(peekNext())) {
            advance() ;
        }
        while(isDigit(peek())) {
            advance();
        }
        Double value = Double.parseDouble(source.substring(start , current));
        addToken(NUMBER , value) ;
    }
    ///  !!!! END NUMBER HANDLER
    /// START NUMBER HELPER PEEK TWO AHEAD
    private char peekNext() {
        if(current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }
    /// END NUMBER HELPER PEEK TWO AHEAD
    ///
    /// START isAlpha and isAlphaNumeric VERIFIER
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }
    private boolean isAlphaNumeric(char c) {
        return isDigit(c) || isAlpha(c);
    }
    /// END isAlpha and isAlphaNumeric VERIFIER

    /// !!!! START IDENTIFIER HANDLER
    private void identifier() {
        while(isAlphaNumeric(peek())) {
            advance() ;
        }
        String key = source.substring(start  , current) ;
        TokenType type = keywords.get(key);
        if(type == null) {
            type = IDENTIFIER;
        }
        addToken(type) ;
    }
    /// END IDENTIFIER HANDLER

}
