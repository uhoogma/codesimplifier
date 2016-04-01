package main.java.com.googlecode.ounit.codesimplifier.processing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.com.googlecode.ounit.codesimplifier.Java8Lexer;
import main.java.com.googlecode.ounit.codesimplifier.Java8Parser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Java2SimpleJava {

    static final String FILE_PATH = "/home/urmas/NetBeansProjects/Antlr4/src/main/java/com/googlecode/ounit/codesimplifier/testcode/";

    public static void main(String[] args) {
        // processFile(FILE_PATH, "Input2.java");
        processFile(FILE_PATH, "Quaternion.java");
    }

    public static String processFile(String filePath, String fileName) {
        String inputFile = null;
        try {
            inputFile = new String(Files.readAllBytes(Paths.get(
                    filePath + fileName)));
        } catch (IOException ex) {
            Logger.getLogger(Java2SimpleJava.class.getName()).log(Level.SEVERE, null, ex);
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
        System.out.println("declaredMethods:\n" + declaredMethods.toString());
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
        System.out.println("afterPresimplifier:\n" + afterPresimplifier);

        /* 
         RemoveConditionals 
         8. conditionals -keep only control flow structures 
         */
        RemoveConditionals removeConditionals = new RemoveConditionals(tokens, preSimplifier.rewriter);
        walker.walk(removeConditionals, tree);
        String afterRemoveConditionals = removeConditionals.rewriter.getText();
        System.out.println("afterRemoveConditionals:\n" + afterRemoveConditionals);
        /* 
         RemoveLoops 
         9. loops - remove conditions
         */
        RemoveLoops removeLoops = new RemoveLoops(tokens, removeConditionals.rewriter);
        walker.walk(removeLoops, tree);
        String afterRemoveLoops = removeLoops.rewriter.getText();
        System.out.println("afterRemoveLoops:\n" + afterRemoveLoops);
        /* 
         RemoveUserDefinedFunctions 
         7a. user defined functions -remove all
         7b. system calls - keep intact 
         */
        RemoveUserDefinedFunctions removeUserDefinedFunctions = new RemoveUserDefinedFunctions(removeLoops.rewriter, declaredMethods);
        walker.walk(removeUserDefinedFunctions, tree);
        String afterRemoveUserDefinedFunctions = removeUserDefinedFunctions.rewriter.getText();
        System.out.println("afterRemoveUserDefinedFunctions:\n" + afterRemoveUserDefinedFunctions);
        /* 
         RemoveExpressionStatements (InUserFunctionCalls)
         5a. expression statement in system call - keep
         5b. expression statement other - remove 
         */
        RemoveExpressionStatements removeExpressionStatements = new RemoveExpressionStatements(removeUserDefinedFunctions.rewriter);
        walker.walk(removeExpressionStatements, tree);
        String afterRemoveExpressionStatements = removeExpressionStatements.rewriter.getText();
        System.out.println("afterRemoveExpressionStatements:\n" + afterRemoveExpressionStatements);

        // we also need abstract user-defined methods keeping only return type
        AbstractUsersMethods abstractUsersMethods = new AbstractUsersMethods(removeExpressionStatements.rewriter, declaredMethods);
        walker.walk(abstractUsersMethods, tree);
        String afterAbstractUsersMethods = abstractUsersMethods.rewriter.getText();
        System.out.println("afterAbstractUsersMethods:\n" + afterAbstractUsersMethods);

        //we also need to remove packagedeclaration
        RemovePackageDeclaration removePackageDeclaration = new RemovePackageDeclaration(removeExpressionStatements.rewriter);
        walker.walk(removePackageDeclaration, tree);
        String afterRemovePackageDeclaration = removePackageDeclaration.rewriter.getText();
        System.out.println("afterRemovePackageDeclaration:\n" + afterRemovePackageDeclaration);

        // final result
        return afterRemovePackageDeclaration;
    }

    public static List<String> removeMainMethod(Set<String> methods) {
        List<String> methodNames = new ArrayList<>();
        methodNames.addAll(methods);
        methodNames.removeAll(new ArrayList<>(Arrays.asList("main")));
        return methodNames;
    }
}
