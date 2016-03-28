package main.java.com.googlecode.ounit.codesimplifier.processing;

import main.java.com.googlecode.ounit.codesimplifier.Java8BaseListener;
import main.java.com.googlecode.ounit.codesimplifier.Java8Parser;
import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.NotNull;

public class RemoveConditionals extends Java8BaseListener {
    /*
     8. Conditional branch : an if statement contains a boolean expression 
     inside to decide the execution path conditionally. Since the structures
     of control flows can be important features in source code, we keep the
     structures, but eliminate boolean expressions. There are many 
     obfuscation methods that disguise the actual boolean expressions to 
     fail the text/token based similarity detection tools. In order to lessen
     the effect of such obfuscations, we abstract away the expression in the
     if statement. With similar view points, if-else statements and switch 
     statements are abstracted to have only the control flow structures, but
     no detailed expressions.*/

    BufferedTokenStream tokens;
    public TokenStreamRewriter rewriter;

    public RemoveConditionals(BufferedTokenStream tokens, TokenStreamRewriter rewriter) {
        this.tokens = tokens;
        this.rewriter = rewriter;
    }

    @Override
    public void enterIfThenStatement(@NotNull Java8Parser.IfThenStatementContext ctx) {
        Interval interval = ctx.children.get(2).getSourceInterval();
        Util.removeChildsTokens(interval, rewriter);
    }

    @Override
    public void enterIfThenElseStatement(@NotNull Java8Parser.IfThenElseStatementContext ctx) {
        Interval interval = ctx.children.get(2).getSourceInterval();
        Util.removeChildsTokens(interval, rewriter);
    }

    @Override
    public void enterIfThenElseStatementNoShortIf(@NotNull Java8Parser.IfThenElseStatementNoShortIfContext ctx) {
        Interval interval = ctx.children.get(2).getSourceInterval();
        Util.removeChildsTokens(interval, rewriter);
    }

    @Override
    public void enterSwitchStatement(@NotNull Java8Parser.SwitchStatementContext ctx) {
        Interval interval = ctx.children.get(2).getSourceInterval();
        Util.removeChildsTokens(interval, rewriter);
    }

    @Override
    public void enterSwitchLabel(@NotNull Java8Parser.SwitchLabelContext ctx) {
        List<ParseTree> pt = ctx.children;
        if (pt.size() == 3) {
            Interval interval = ctx.children.get(1).getSourceInterval();
            Util.removeChildsTokens(interval, rewriter);
        }
    }
}
