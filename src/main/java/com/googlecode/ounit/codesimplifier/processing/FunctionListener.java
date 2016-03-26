package main.java.com.googlecode.ounit.codesimplifier.processing;

import main.java.com.googlecode.ounit.codesimplifier.Java8BaseListener;
import main.java.com.googlecode.ounit.codesimplifier.Java8Parser;
import java.util.Set;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.OrderedHashSet;

public class FunctionListener extends Java8BaseListener {

    private Set<String> functions= new OrderedHashSet<>();

    public Set<String> getFunctions() {
        return functions;
    }

    public void setFunctions(Set<String> aFunctions) {
        functions = aFunctions;
    }

    @Override
    public void enterMethodDeclarator(@NotNull Java8Parser.MethodDeclaratorContext ctx) {
        String currentFunctionName = ctx.Identifier().getText();
        System.out.println("enterMethodDeclarator: " + currentFunctionName);
        functions.add(currentFunctionName);
    }
}
