package main.java.com.googlecode.ounit.codesimplifier.processing;

import main.java.com.googlecode.ounit.codesimplifier.Java8BaseListener;
import main.java.com.googlecode.ounit.codesimplifier.Java8Parser;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class RemoveUserDefinedFunctions extends Java8BaseListener {
    /* 
     7. Function call : function calls are very important features in 
     similarity comparison for source code. Particularly, sequences of system
     calls and library calls are considered to be key characteristics in 
     similar source codes. Thus, we keep function names as they are, if they
     are system calls or library calls. As for arguments, format strings and
     string arguments are eliminated, but other expressions are kept the same
     as the original source code, which means expression statements in an 
     argument list are exceptions for elimination in the previous abstraction
     rule.
     */

    BufferedTokenStream tokens;
    public TokenStreamRewriter rewriter;
    List<String> declaredFunctions;
    List<Integer> tokensToRemove;

    public RemoveUserDefinedFunctions(BufferedTokenStream tokens, TokenStreamRewriter rewriter, List<String> declaredFunctions) {
        this.tokens = tokens;
        this.declaredFunctions = declaredFunctions;
        this.rewriter = rewriter;
        tokensToRemove = new ArrayList<>();
    }

    boolean insideMethodInvocation = false;

    @Override
    public void enterMethodInvocation(@NotNull Java8Parser.MethodInvocationContext ctx) {
        insideMethodInvocation = true;
        List<Token> methodname
                = tokens.getTokens(ctx.getStart().getTokenIndex(), ctx.getStop().getTokenIndex());
        TerminalNode id = ctx.Identifier();
        if (id != null && declaredFunctions.contains(id.getText())) { // methodName is at the first level
            String methodName = id.getText();
            tokensToRemove.add(0, methodname.get(0).getTokenIndex());
            System.out.println("methodinvocation deleting function name " + methodName);
            List<ParseTree> pt = ctx.children;
            for (int i = 0; i < pt.size(); i++) {
                Interval interval = ctx.children.get(i).getSourceInterval();
                Util.removeChildsTokens(interval, rewriter);
            }
        }
    }

    @Override
    public void exitMethodInvocation(@NotNull Java8Parser.MethodInvocationContext ctx) {
        // kui on enda deklareeritud meetod kustuta kõik childrenid
        TerminalNode id = ctx.Identifier();
        if (id != null && declaredFunctions.contains(id.getText())) {
            List<ParseTree> pt = ctx.children;
            for (int i = 0; i < pt.size(); i++) {
                Interval interval = ctx.children.get(i).getSourceInterval();
                Util.removeChildsTokens(interval, rewriter);
            }
        }
        insideMethodInvocation = false;
    }

    // 2nd level
    @Override
    public void enterMethodName(@NotNull Java8Parser.MethodNameContext ctx) {
        TerminalNode id = ctx.Identifier();
        if (insideMethodInvocation && id != null && declaredFunctions.contains(id.getText())) {
            List<ParseTree> pt = ctx.children;
            for (int i = 0; i < pt.size(); i++) {
                Interval interval = ctx.children.get(i).getSourceInterval();
                Util.removeChildsTokens(interval, rewriter);
            }
        }
        insideMethodInvocation = false;
    }

    @Override
    public void enterMethodInvocation_lf_primary(@NotNull Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        TerminalNode id = ctx.Identifier();
        if (insideMethodInvocation && id != null && declaredFunctions.contains(id.getText())) {
            List<ParseTree> pt = ctx.children;
            for (int i = 0; i < pt.size(); i++) {
                Interval interval = ctx.children.get(i).getSourceInterval();
                Util.removeChildsTokens(interval, rewriter);
            }
        }
        insideMethodInvocation = false;
    }

    @Override
    public void enterMethodInvocation_lfno_primary(@NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        insideMethodInvocation = true;
        List<Token> methodname
                = tokens.getTokens(ctx.getStart().getTokenIndex(), ctx.getStop().getTokenIndex());
        TerminalNode id = ctx.Identifier();
        if (id != null && declaredFunctions.contains(id.getText())) { // methodName is at the first level
            tokensToRemove.add(0, methodname.get(0).getTokenIndex());
            List<ParseTree> pt = ctx.children;
            for (int i = 0; i < pt.size(); i++) {
                Interval interval = ctx.children.get(i).getSourceInterval();
                Util.removeChildsTokens(interval, rewriter);
            }
        }
    }

    @Override
    public void exitMethodInvocation_lfno_primary(@NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        // kui on enda deklareeritud meetod kustuta kõik childrenid
        TerminalNode id = ctx.Identifier();
        if (id != null && declaredFunctions.contains(id.getText())) {
            List<ParseTree> pt = ctx.children;
            for (int i = 0; i < pt.size(); i++) {
                Interval interval = ctx.children.get(i).getSourceInterval();
                Util.removeChildsTokens(interval, rewriter);
            }
        }
    }
}
