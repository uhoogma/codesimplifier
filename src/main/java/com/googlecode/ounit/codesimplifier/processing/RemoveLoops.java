package main.java.com.googlecode.ounit.codesimplifier.processing;

import main.java.com.googlecode.ounit.codesimplifier.Java8BaseListener;
import main.java.com.googlecode.ounit.codesimplifier.Java8Parser;
import java.util.List;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;

public class RemoveLoops extends Java8BaseListener {

    BufferedTokenStream tokens;
    public TokenStreamRewriter rewriter;

    public RemoveLoops(BufferedTokenStream tokens, TokenStreamRewriter rewriter) {
        this.tokens = tokens;
        this.rewriter = rewriter;
    }

    /*
     9. Loop expression : for statement has three basic expressions
     — initialization expression, loop condition expression,
     and increment expression. These expressions
     are quite easily obfuscated by malicious theft. For example,
     changing variable names in for expressions can
     not affect actual execution of program. Also changing
     expressions is available to equal to the number of times
     the execution of the loop. Thus, these for expressions
     are eliminated by generating abstracted source code.
     So that only loop constructs can remain keeping their
     loop structures, like form for ( ; ; ) . Similarly, while
     and do-while statements are abstracted by eliminating
     the enclosed condition expressions and only the control
     structures remain in the abstracted source code.*/
    /* 
     basicForStatement
     basicForStatementNoShortIf
     ;;
     enhancedForStatement
     enhancedForStatementNoShortIf : 
    
     whileStatement
     whileStatementNoShortIf

     doStatement
     */
    @Override
    public void enterBasicForStatement(@NotNull Java8Parser.BasicForStatementContext ctx) {
        List<ParseTree> a = ctx.children;
        removeForLoopsConditions(a, ";");
    }

    @Override
    public void enterBasicForStatementNoShortIf(@NotNull Java8Parser.BasicForStatementNoShortIfContext ctx) {
        List<ParseTree> a = ctx.children;
        removeForLoopsConditions(a, ";");
    }

    private void removeForLoopsConditions(List<ParseTree> a, String conditionSeparator) {
        if (a != null) {
            int count = a.size();
            for (int i = 2; i < count - 2; i++) {
                if (a.get(i).getText().equals(conditionSeparator)) {
                    Interval interval = a.get(i).getSourceInterval();
                    rewriter.replace(tokens.get(interval.a), conditionSeparator);
                } else {
                    Util.removeChild(a.get(i), rewriter);
                }
            }
        }
    }

    @Override
    public void enterEnhancedForStatement(@NotNull Java8Parser.EnhancedForStatementContext ctx) {
        List<ParseTree> a = ctx.children;
        removeForLoopsConditions(a, ":");
    }

    @Override
    public void enterEnhancedForStatementNoShortIf(@NotNull Java8Parser.EnhancedForStatementNoShortIfContext ctx) {
        List<ParseTree> a = ctx.children;
        removeForLoopsConditions(a, ":");
    }

    @Override
    public void enterWhileStatement(@NotNull Java8Parser.WhileStatementContext ctx) {
        Interval interval = ctx.children.get(2).getSourceInterval();
        Util.removeChildsTokens(interval, rewriter);
    }

    @Override
    public void enterWhileStatementNoShortIf(@NotNull Java8Parser.WhileStatementNoShortIfContext ctx) {
        Interval interval = ctx.children.get(2).getSourceInterval();
        Util.removeChildsTokens(interval, rewriter);
    }

    @Override
    public void enterDoStatement(@NotNull Java8Parser.DoStatementContext ctx) {
        List<ParseTree> a = ctx.children;
        Util.removeChild(a.get(4), rewriter);
    }
}
