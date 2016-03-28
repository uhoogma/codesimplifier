package main.java.com.googlecode.ounit.codesimplifier.processing;

import main.java.com.googlecode.ounit.codesimplifier.Java8BaseListener;
import main.java.com.googlecode.ounit.codesimplifier.Java8Parser;
import java.util.List;
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

    public TokenStreamRewriter rewriter;
    private final List<String> declaredFunctions;

    public RemoveUserDefinedFunctions(TokenStreamRewriter rewriter, List<String> declaredFunctions) {
        this.rewriter = rewriter;
        this.declaredFunctions = declaredFunctions;
    }

    private boolean insideMethodInvocation = false;
    private boolean deleteArgumentList = false;
    private String currentMethodName = null;

    @Override
    public void enterMethodInvocation(@NotNull Java8Parser.MethodInvocationContext ctx) {
        insideMethodInvocation = true;
        TerminalNode id = ctx.Identifier();
        List<ParseTree> pt = ctx.children;
        if (id != null && pt != null && declaredFunctions.contains(id.getText())) { // methodName is at the first level
            for (int i = 0; i < pt.size(); i++) {
                Interval interval = ctx.children.get(i).getSourceInterval();
                Util.removeChildsTokens(interval, rewriter);
            }
            insideMethodInvocation = false;
        }
    }

    @Override
    public void enterMethodInvocation_lfno_primary(@NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        insideMethodInvocation = true;
        TerminalNode id = ctx.Identifier();
        List<ParseTree> pt = ctx.children;
        if (id != null && pt != null && declaredFunctions.contains(id.getText())) { // methodName is at the first level
            for (int i = 0; i < pt.size(); i++) {
                Interval interval = ctx.children.get(i).getSourceInterval();
                Util.removeChildsTokens(interval, rewriter);
            }
            insideMethodInvocation = false;
        }
    }

    @Override
    public void enterMethodName(@NotNull Java8Parser.MethodNameContext ctx) {
        TerminalNode id = ctx.Identifier();
        if (insideMethodInvocation && id != null && declaredFunctions.contains(id.getText())) { // 2nd level
            currentMethodName = id.getText();
            deleteArgumentList = true;
        }
        insideMethodInvocation = false;
    }
    
    @Override
    public void exitMethodInvocation(@NotNull Java8Parser.MethodInvocationContext ctx) {
        if (deleteArgumentList && declaredFunctions.contains(currentMethodName)) {
            List<ParseTree> pt = ctx.children;
            for (int i = 0; i < pt.size(); i++) {
                Interval interval = ctx.children.get(i).getSourceInterval();
                Util.removeChildsTokens(interval, rewriter);
            }
        }
        deleteArgumentList = false;
        insideMethodInvocation = false;
    }

    @Override
    public void exitMethodInvocation_lfno_primary(@NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        if (deleteArgumentList && declaredFunctions.contains(currentMethodName)) {
            List<ParseTree> pt = ctx.children;
            for (int i = 0; i < pt.size(); i++) {
                Interval interval = ctx.children.get(i).getSourceInterval();
                Util.removeChildsTokens(interval, rewriter);
            }
        }
        deleteArgumentList = false;
        insideMethodInvocation = false;
    }

    @Override
    public void enterMethodInvocation_lf_primary(@NotNull Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        TerminalNode id = ctx.Identifier();
        List<ParseTree> pt = ctx.children;
        if (id != null && pt != null && declaredFunctions.contains(id.getText())) {
            for (int i = 0; i < pt.size(); i++) {
                Interval interval = ctx.children.get(i).getSourceInterval();
                Util.removeChildsTokens(interval, rewriter);
            }
        }
    }
}
