package main.java.com.googlecode.ounit.codesimplifier.processing;

import java.util.List;
import main.java.com.googlecode.ounit.codesimplifier.Java8BaseListener;
import main.java.com.googlecode.ounit.codesimplifier.Java8Parser;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.NotNull;

public class AbstractUsersMethods extends Java8BaseListener {

    public TokenStreamRewriter rewriter;
    private final List<String> declaredFunctions;

    public AbstractUsersMethods(TokenStreamRewriter rewriter, List<String> declaredFunctions) {
        this.rewriter = rewriter;
        this.declaredFunctions = declaredFunctions;
    }

    boolean insideMethodDeclaration = false;
    boolean deleteDeclaration = false;

    Interval resultInterval;
    Interval headerInterval;
    Interval modifiersInterval;

    @Override
    public void enterMethodDeclaration(@NotNull Java8Parser.MethodDeclarationContext ctx) {
        insideMethodDeclaration = true;
    }

    @Override
    public void exitMethodDeclaration(@NotNull Java8Parser.MethodDeclarationContext ctx) {
        if (deleteDeclaration) {
            if (modifiersInterval != null) {
                for (int i = modifiersInterval.a - 1; i <= modifiersInterval.b; i++) {
                    rewriter.delete(i);
                }
            }
            if (headerInterval != null && resultInterval != null) {
                for (int i = headerInterval.a - 1; i <= headerInterval.b; i++) {
                    if ((i >= headerInterval.a && i <= headerInterval.b) && (i < resultInterval.a || i > resultInterval.b)) {
                        rewriter.delete(i);
                    }
                }
            }
        }
        insideMethodDeclaration = false;
        deleteDeclaration = false;
        resultInterval = null;
        headerInterval = null;
        modifiersInterval = null;
    }

    @Override
    public void enterMethodDeclarator(@NotNull Java8Parser.MethodDeclaratorContext ctx) {
        if (declaredFunctions.contains(ctx.Identifier().getText())) {
            deleteDeclaration = true;
        }
    }

    @Override
    public void enterMethodHeader(@NotNull Java8Parser.MethodHeaderContext ctx) {
        headerInterval = ctx.getSourceInterval();
    }

    @Override
    public void enterMethodModifier(@NotNull Java8Parser.MethodModifierContext ctx) {
        modifiersInterval = ctx.getSourceInterval();
    }

    @Override
    public void enterResult(@NotNull Java8Parser.ResultContext ctx) {
        resultInterval = ctx.getSourceInterval();
    }
}
