package com.googlecode.ounit.codesimplifier.processing;

import com.googlecode.ounit.codesimplifier.java2simplejava.Java8BaseListener;
import com.googlecode.ounit.codesimplifier.java2simplejava.Java8Parser;
import java.util.Set;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.OrderedHashSet;

public class UserDefinedNamesListener extends Java8BaseListener {

    private Set<String> functions = new OrderedHashSet<>();
    private Set<String> variables = new OrderedHashSet<>();

    private boolean insideVariableDeclarator = false;

    public Set<String> getFunctions() {
        return functions;
    }

    public void setFunctions(Set<String> aFunctions) {
        functions = aFunctions;
    }

    public Set<String> getVariables() {
        return variables;
    }

    public void setVariables(Set<String> variables) {
        this.variables = variables;
    }

    @Override
    public void enterMethodDeclarator(@NotNull Java8Parser.MethodDeclaratorContext ctx) {
        String currentFunctionName = ctx.Identifier().getText();
        functions.add(currentFunctionName);
    }

    @Override
    public void enterVariableDeclarator(@NotNull Java8Parser.VariableDeclaratorContext ctx) {
        insideVariableDeclarator = true;
    }

    @Override
    public void enterVariableDeclaratorId(@NotNull Java8Parser.VariableDeclaratorIdContext ctx) {
        if (insideVariableDeclarator) {
            variables.add(ctx.Identifier().getText());
            insideVariableDeclarator = false;
        }
    }

}
