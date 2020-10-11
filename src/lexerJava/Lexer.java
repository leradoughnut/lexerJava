package lexerJava;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private final ArrayList<Token> tokens;
    private boolean inComment = false;

    public Lexer(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        tokens = new ArrayList<>();
        while (scanner.hasNextLine()) {
            eatLine(scanner.nextLine());
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Token token : tokens) {
            builder.append(token.toString());
            builder.append("\n");
        }
        return builder.toString();
    }

    private void eatLine(String line) {
        int i = 0;
        int index;
        while (i < line.length()) {
            while (line.substring(i, i + 1).matches("\\s")) {
                if (i == line.length() - 1) {
                    break;
                }
                i++;
            }

            if (inComment) {
                if ((index = matchString("\\*/", line, i)) != -1) {
                    i = index;
                    inComment = false;
                } else
                    break;
            } else if (i < line.length() - 1 && line.substring(i, i + 2).matches("/\\*")) {
                if ((index = matchString("\\*/", line, i)) != -1) {
                    i = index - 1;
                } else {
                    inComment = true;
                    break;
                }

            } else if (i < line.length() - 1 && line.substring(i, i + 2).matches("//")) {
                break;

            } else if (line.substring(i, i + 1).matches("\"")) {
                if ((index = matchString("\"", line, i + 1)) != -1) {
                    tokens.add(new Token(line.substring(i, index), TokenType.STRING_CONSTANT));
                    i = index - 1;
                } else {
                    tokens.add(new Token(line.substring(i), TokenType.ERROR));
                    break;
                }
            } else if (line.substring(i, i + 1).matches("'")) {
                if ((index = matchString("'", line, i + 1)) != -1) {
                    if (line.substring(i, index).matches("'([^'\\\\\\n]|\\\\.)'")) {
                        tokens.add(new Token(line.substring(i, index), TokenType.CHAR_CONSTANT));
                        i = index - 1;
                    } else {
                        tokens.add(new Token(line.substring(i), TokenType.ERROR));
                        break;
                    }
                } else {
                    tokens.add(new Token(line.substring(i), TokenType.ERROR));
                    break;
                }
            } else if (line.substring(i, i + 1).matches(punctuator)) {
                tokens.add(new Token(line.substring(i, i + 1), TokenType.PUNCTUATOR));
            } else if (line.substring(i, i + 1).matches("@")) {
                if ((index = matchString(annotation, line, i)) != -1) {
                    tokens.add(new Token(line.substring(i, index), TokenType.ANNOTATION));
                    i += index - i;
                }
            } else if (line.substring(i, i + 1).matches(operator)) {
                String op = line.substring(i, i + 1);
                if ((i < line.length() - 1) && line.substring(i, i + 2).matches(operator)) {
                    op = line.substring(i, i + 2);
                    if ((i < line.length() - 2) && line.substring(i, i + 3).matches(operator)) {
                        op = line.substring(i, i + 3);
                    }
                }
                i += op.length() - 1;
                tokens.add(new Token(op, TokenType.OPERATOR));
            } else {
                index = i;
                if (line.substring(i, i + 1).matches("[0-9]")) {
                    while (i < line.length() && !line.substring(i, i + 1).matches("\\s") &&
                            (!line.substring(i, i + 1).matches(punctuator) || line.substring(i, i + 1).matches("\\."))
                            && !line.substring(i, i + 1).matches(operator)) {
                        i++;
                    }
                } else {
                    while (i < line.length() && !line.substring(i, i + 1).matches("\\s") &&
                            !line.substring(i, i + 1).matches(punctuator) && !line.substring(i, i + 1).matches(operator)) {
                        i++;
                    }
                }
                String current = line.substring(index, i);
                i--;

                if (current.matches(number))
                    tokens.add(new Token(current, TokenType.NUMBER));
                else if (current.matches(reserved))
                    tokens.add(new Token(current, TokenType.RESERVED_WORD));
                else if (current.matches(literals))
                    tokens.add(new Token(current, TokenType.LITERALS));
                else if (current.matches(identifier))
                    tokens.add(new Token(current, TokenType.IDENTIFIER));
                else
                    tokens.add(new Token(current, TokenType.ERROR));
            }
            i++;

        }
    }

    private int matchString(String pattern, String string, int index) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (m.find(index)) {
            return m.end();
        } else
            return -1;

    }


    private final static String identifier = "^([a-zA-Z_$])([a-zA-Z_$0-9])*$";
    private final static String number = "\\b\\d+|\\b\\d+.\\d+|\\b\\d+e\\d+|0[xX][0-9a-fA-F]+";
    private final static String punctuator = "(\\(|\\)|\\[|]|\\{|}|,|:|;|\\.)";
    private final static String operator = "(\\+|-|\\*|/|=|%|\\+\\+|--|==|!=|>|<|>=|<=|&|\\||^" +
            "|~|<<|>>|>>>|&&|\\|\\||!|\\+=|-=|\\*=|/=|%=)";
    private final static String reserved = "abstract|assert|boolean|break|byte|case|catch|char|class|" +
            "const|continue|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|" +
            "import|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|" +
            "static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while";
    private final static String annotation = "@Override|@Deprecated|@SuppressWarnings|@SafeVarargs" +
            "@Retention|@Documented|@Target|@Inherited";
    private final static String literals = "true|false|null";
}
