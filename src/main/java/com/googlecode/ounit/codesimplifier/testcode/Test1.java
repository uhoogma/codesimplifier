package main.java.com.googlecode.ounit.codesimplifier.testcode;
/**
 * @author Urmas Hoogma
 */
import antlr4.*;
import java.util.ArrayList;
import java.util.ArrayList.*;

public class Test1 {

    // simple comment
    @SuppressWarnings(value = "false")
    public static void main(String[] args) {
        foo();
        bar(1);
        Test1.foo();
        int other, allTogether;
        int number = 10, ptr;
        System.out.printf("this \nmust \ngo", number);
        System.out.println("this must go");
        int[] y = new int[number];
        if (number > 0) {
            for (other = 0; other < number; other++) {
                System.out.printf("this must go", y[other]);
                System.out.printf("this must go");
            }
            ptr = y[0];
            for (other = 0, allTogether = 0; other < number; other++) {
                allTogether += (ptr + other);
            }
            System.out.printf("", allTogether / number);
        } else {
            System.out.printf("");
        }
    }
    
    public static int foo(){
        return 42;
    }
    
    public static int bar(int input){
        return input;
    }
}
