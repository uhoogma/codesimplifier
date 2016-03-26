package main.java.com.googlecode.ounit.codesimplifier.processing;

import main.java.com.googlecode.ounit.codesimplifier.Java8BaseListener;
import main.java.com.googlecode.ounit.codesimplifier.Java8Parser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;

public class RemoveLoops extends Java8BaseListener {

    BufferedTokenStream tokens;
    public TokenStreamRewriter rewriter;
    List<Integer> tokensToRemove;

    public RemoveLoops(BufferedTokenStream tokens, TokenStreamRewriter rewriter) {
        this.tokens = tokens;
        this.rewriter = rewriter;
        tokensToRemove = new ArrayList<>();
    }

    @Override
    public void enterForStatement(Java8Parser.ForStatementContext ctx) {
        Token semi = ctx.getStart();
        int i = semi.getTokenIndex();
        List<Token> cmtChannel
                = tokens.getTokens(i, ctx.getStop().getTokenIndex());
        if (cmtChannel != null) {
            Token cmt = cmtChannel.get(2);
            if (cmt != null) {
                int type = cmt.getTokenIndex();
                String txt = (new Integer(type)).toString();
                System.out.println("for token: " + txt);
            }
        }
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
        int beg = 0;
        int end = 0;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).getText().equals("(")) {
                beg = i;
            }
            if (a.get(i).getText().equals(")")) {
                end = i;
            }
        }
        List<Integer> indices = new ArrayList<>();
        for (int i = beg + 1; i < end; i++) {
            Interval inter = a.get(i).getSourceInterval();
            indices.add(inter.a);
            indices.add(inter.b);
        }
        Collections.sort(indices);
        int semiColons = 0;
        for (int i = indices.get(0); i < indices.get(indices.size() - 1) + 1; i++) {
            Token t = tokens.get(i);
            if (semiColons < 2 && t.getText().equals(";")) { // need only 2 ;
                semiColons++;
            } else {
                rewriter.delete(t);
            }
        }
    }
}
