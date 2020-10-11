package lexerJava;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        String filename = "test.java";
        Path path = Paths.get(filename);
        File file = path.toFile();
        try{
            Lexer lexer = new Lexer(file);
            System.out.println(lexer.toString());
        }catch (FileNotFoundException e){
            System.out.println("problem");
        }

    }
}
