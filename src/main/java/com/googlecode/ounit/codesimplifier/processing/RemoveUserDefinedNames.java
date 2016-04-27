package com.googlecode.ounit.codesimplifier.processing;

import com.googlecode.ounit.codesimplifier.java2simplejava.Java8BaseListener;
import com.googlecode.ounit.codesimplifier.java2simplejava.Java8Parser;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class RemoveUserDefinedNames extends Java8BaseListener {
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
    private final Set<String> declaredVariables;

    public RemoveUserDefinedNames(TokenStreamRewriter rewriter, List<String> declaredFunctions, Set<String> declaredVariables) {
        this.rewriter = rewriter;
        this.declaredFunctions = declaredFunctions;
        this.declaredVariables = declaredVariables;
    }

    private boolean insideMethodInvocation = false;
    private boolean deleteArgumentList = false;
    private String currentMethodName = null;

    private boolean waitingForVariableDeclaration = true;

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
            if (pt != null) {
                for (int i = 0; i < pt.size(); i++) {
                    Interval interval = ctx.children.get(i).getSourceInterval();
                    Util.removeChildsTokens(interval, rewriter);
                }
            }
        }
        deleteArgumentList = false;
        insideMethodInvocation = false;
    }

    @Override
    public void exitMethodInvocation_lfno_primary(@NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        if (deleteArgumentList && declaredFunctions.contains(currentMethodName)) {
            List<ParseTree> pt = ctx.children;
            if (pt != null) {
                for (int i = 0; i < pt.size(); i++) {
                    Interval interval = ctx.children.get(i).getSourceInterval();
                    Util.removeChildsTokens(interval, rewriter);
                }
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

    @Override
    public void enterVariableDeclarator(@NotNull Java8Parser.VariableDeclaratorContext ctx) {
        waitingForVariableDeclaration = true;
    }

    @Override
    public void enterFormalParameter(@NotNull Java8Parser.FormalParameterContext ctx) {
        waitingForVariableDeclaration = true;
    }

    @Override
    public void enterLastFormalParameter(@NotNull Java8Parser.LastFormalParameterContext ctx) {
        waitingForVariableDeclaration = true;
    }

    @Override
    public void enterCatchFormalParameter(@NotNull Java8Parser.CatchFormalParameterContext ctx) {
        waitingForVariableDeclaration = true;
    }

    @Override
    public void enterResource(@NotNull Java8Parser.ResourceContext ctx) {
        waitingForVariableDeclaration = true;
    }

    @Override
    public void exitVariableDeclaratorId(@NotNull Java8Parser.VariableDeclaratorIdContext ctx) {
        TerminalNode variableId = ctx.Identifier();
        List<ParseTree> pt = ctx.children;
        if (waitingForVariableDeclaration && safeToProceed(variableId, pt)) {
            Util.removeChild(pt.get(0), rewriter);
            waitingForVariableDeclaration = false;
        }
    }

    private boolean safeToProceed(TerminalNode variableId, List<ParseTree> pt) {
        return variableId != null && pt != null && !pt.isEmpty() && declaredVariables.contains(variableId.getText());
    }
    
    @Override
    public void exitExpressionName(@NotNull Java8Parser.ExpressionNameContext ctx) {
        TerminalNode variableId = ctx.Identifier();
        List<ParseTree> pt = ctx.children;
        if (safeToProceed(variableId, pt)) {
            Util.removeChild(pt.get(pt.size() - 1), rewriter);
        }
    }

    @Override
    public void exitAmbiguousName(@NotNull Java8Parser.AmbiguousNameContext ctx) {
        TerminalNode variableId = ctx.Identifier();
        List<ParseTree> pt = ctx.children;
        if (safeToProceed(variableId, pt)) {
            Util.removeChild(pt.get(pt.size() - 1), rewriter);
        }
    }

    @Override
    public void exitFieldAccess(@NotNull Java8Parser.FieldAccessContext ctx) {
        TerminalNode variableId = ctx.Identifier();
        List<ParseTree> pt = ctx.children;
        if (safeToProceed(variableId, pt)) {
            Util.removeChild(pt.get(pt.size() - 1), rewriter);
        }
    }

    @Override
    public void exitFieldAccess_lf_primary(@NotNull Java8Parser.FieldAccess_lf_primaryContext ctx) {
        TerminalNode variableId = ctx.Identifier();
        List<ParseTree> pt = ctx.children;
        if (safeToProceed(variableId, pt)) {
            Util.removeChild(pt.get(pt.size() - 1), rewriter);
        }
    }

    @Override
    public void exitFieldAccess_lfno_primary(@NotNull Java8Parser.FieldAccess_lfno_primaryContext ctx) {
        TerminalNode variableId = ctx.Identifier();
        List<ParseTree> pt = ctx.children;
        if (safeToProceed(variableId, pt)) {
            Util.removeChild(pt.get(pt.size() - 1), rewriter);
        }
    }
}
