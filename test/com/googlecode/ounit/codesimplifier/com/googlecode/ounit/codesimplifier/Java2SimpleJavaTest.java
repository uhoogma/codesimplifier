package com.googlecode.ounit.codesimplifier;

import com.googlecode.ounit.codesimplifier.processing.FunctionListener;
import com.googlecode.ounit.codesimplifier.processing.PreSimplifier;
import com.googlecode.ounit.codesimplifier.processing.RemoveConditionals;
import com.googlecode.ounit.codesimplifier.processing.RemoveExpressionStatements;
import com.googlecode.ounit.codesimplifier.processing.RemoveLoops;
import com.googlecode.ounit.codesimplifier.processing.RemoveUserDefinedFunctions;
import com.googlecode.ounit.codesimplifier.testcode.ExpectedResults;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class Java2SimpleJavaTest {

    public Java2SimpleJavaTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void trg() {
        assertEquals(1L, 1L);
    }

    public static List<String> removeMainMethod(Set<String> methods) {
        List<String> methodNames = new ArrayList<>();
        methodNames.addAll(methods);
        methodNames.removeAll(new ArrayList<>(Arrays.asList("main")));
        return methodNames;
    }
    
    @Test(timeout = 120000)
    public void Java2SimpleJavaTest(){
        // preparing a file
        String inputFile = null;
        try {
            inputFile = new String(Files.readAllBytes(Paths.get(
                    "/home/urmas/NetBeansProjects/Antlr4/src/com/googlecode/ounit/codesimplifier/testcode/Input2.java")));
        } catch (IOException ex) {
            Logger.getLogger(Java2SimpleJavaTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        ANTLRInputStream input = new ANTLRInputStream(inputFile);
        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        RuleContext tree = parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        // composing a list from userdefined functions 
        FunctionListener collector = new FunctionListener();
        walker.walk(collector, tree);
        List<String> declaredMethods = removeMainMethod(collector.getFunctions());
        System.out.println("declaredMethods: " + declaredMethods.toString());
        /*
         PreSimplifier
         1. annotations - remove including "@"
         2. variable declarations - remove
         3. format string -remove
         4. string argument - remove
         6. return statement - remove
         */
        PreSimplifier preSimplifier = new PreSimplifier(tokens);
        walker.walk(preSimplifier, tree);
        String afterPresimplifier = preSimplifier.rewriter.getText();
        System.out.println(afterPresimplifier);
        assertEquals(ExpectedResults.AFTER_PRESIMPLIFIER, afterPresimplifier);
        /* 
         RemoveConditionals 
         8. conditionals -keep only control flow structures 
         */
        RemoveConditionals removeConditionals = new RemoveConditionals(tokens, preSimplifier.rewriter);
        walker.walk(removeConditionals, tree);
        /* 
         RemoveLoops 
         9. loops - remove conditions
         */
        RemoveLoops removeLoops = new RemoveLoops(tokens, removeConditionals.rewriter);
        walker.walk(removeLoops, tree);
        /* 
         RemoveUserDefinedFunctions 
         7a. user defined functions -remove all
         7b. system calls - keep intact 
         */
        RemoveUserDefinedFunctions removeUserDefinedFunctions = new RemoveUserDefinedFunctions(tokens, removeLoops.rewriter, declaredMethods);
        walker.walk(removeUserDefinedFunctions, tree);

        /* 
         RemoveExpressionStatements (InUserFunctionCalls)
         5a. expression statement in system call - keep
         5b. expression statement other - remove 
         */
        RemoveExpressionStatements removeExpressionStatements = new RemoveExpressionStatements(tokens, removeUserDefinedFunctions.rewriter, declaredMethods);
        walker.walk(removeExpressionStatements, tree);

        // finalresult
        // System.out.println(removeExpressionStatements.rewriter.getText());
    }

}
