package lexerJava;

public class Token {
    private String tokenAttribute;
    private TokenType tokenType;

    public Token(String tokenAttribute, TokenType tokenType){
        this.tokenAttribute = tokenAttribute;
        this.tokenType = tokenType;
    }

    @Override
    public String toString(){
        return tokenType + ": " + tokenAttribute;
    }
}
