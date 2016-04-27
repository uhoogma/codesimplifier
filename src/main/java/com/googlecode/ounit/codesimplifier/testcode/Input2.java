package com.googlecode.ounit.codesimplifier.testcode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author javadoc comment
 */
public class Input2 {

    static List<String> stringList = new ArrayList<>();
    /* multiline 
     comment */

    // one line comment
    @SuppressWarnings(value = "false")
    public static void main(String[] args) {
        foo();
        bar(1 + 2); // expression statement in userdefined function
        Test1.foo();
        int other, allTogether;
        int number = 10, ptr;
        System.out.printf("this \nmust'n \ngo", 1 + 2);// expression statement in library function
        System.out.println("this must go");
        int[] y = new int[number];
        if (number > 0) {
            for (other = 0; other < number; other++) {
                System.out.printf("this must go", y[other]);
                System.out.printf("this must go");
            }
            if (false) {
                foo();
            }
            ptr = y[0];
            for (other = 0, allTogether = 0; other < number; other++) {
                allTogether += (ptr + other);
                y[0] = (int) Math.PI;
            }
            System.out.printf("%d", allTogether / number);
        } else {
            System.out.printf("");
        }
    }

    public static int foo() {
        return 42;
    }

    public static int bar(int input) {
        return input;
    }

    public static void loops() {
        for (int i = 0; i < 10; i++) {
            System.err.println("content"); // keep
            int f; // discard
        }
        // basicForStatementNoShortIf
        for (int i = 0; i < (int) Math.PI + 7; i++) {
            for (int j = 0; j < 10; j++) {
                System.err.println("content"); // keep
            }
        }         // enhancedForStatement
        for (String str : stringList) {
            System.out.printf("%w", str); // keep
        }
        // enhancedForStatementNoShortIf
        for (String str : stringList) {
            System.out.printf("%w", str); // keep
        }
        int h1 = 0;
        // whileStatement
        while (h1 < 0) {
            h1++;
        }
        // whileStatementNoShortIf
        while (h1 < 0) {
            h1--;
        }
        // doStatement
        do {
            foo();
        } while (h1 < 0);
    }

    public static int conditionals() {
        int h1 = 0;
        int a = 9;
        // ifThenStatement
        if (h1 < 0) {
            a = -1;
        }
        // ifThenElseStatement
        if (h1 < 0) {
            if (h1 < 0) {
                a = -1;
            }
        } else if(h1 < (0+0)) {
            a = -1;
        }
        // ifThenElseStatementNoShortIf
        if (h1 < 0) {
            if (h1 < 0) {
                a = -1;
            }
        } else {
            if (h1 < 0) {
                a = -1;
            }
        }
        // switchStatement
        switch (h1) {
            case 0:
                foo();
            case 1 - 2:
                bar(h1);
            default:
        }
        return a;
    }
}
