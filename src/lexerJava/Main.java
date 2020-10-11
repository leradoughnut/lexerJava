package lexerJava;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);


    public static void main(String[] args) {
        System.out.println("Enter the filename:");

        String filename = sc.nextLine();
        Path path = Paths.get(filename);
        File file = path.toFile();
        try{
            Lexer lexer = new Lexer(file);
            System.out.println(lexer.toString());
        }catch (FileNotFoundException e){
            System.out.println("File not found");
        }

    }
}
