package com.googlecode.ounit.codesimplifier.processing;

import com.googlecode.ounit.codesimplifier.java2simplejava.Java8BaseListener;
import com.googlecode.ounit.codesimplifier.java2simplejava.Java8Parser;
import java.util.List;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;

public class RemoveExpressionStatements extends Java8BaseListener {
    /*
     5. Expression statement (exceptions in function calls) : Large part of 
     the statements in C programs are expression statements. Expression 
     statements perform all of the real work in the program. They have very
     important roles in both the source code and the execution of the 
     program. However, our abstractor eliminates all of the normal expression
     statements (except the ones in function call arguments). Expression 
     statements are also targets for source code obfuscation. Since each
     statement does a little work but a sequence of statements are composed
     to perform a significant work. For an individual statement, there may 
     be several alternative statements. For example, addition statement of a
     positive value can be replaced with subtraction statement of a negative
     value, without changing the functionality. Thus, individual statements 
     are rather unimportant to represent the characteristics of source code.
     Our abstraction completely eliminates expression statements from 
     regular statements, but the argument expressions of the function calls
     left in the abstract code remain as they are.*/

    public TokenStreamRewriter rewriter;

    private boolean insideMethodInvocation = false;

    public RemoveExpressionStatements(TokenStreamRewriter rewriter) {
        this.rewriter = rewriter;
    }

    @Override
    public void enterMethodInvocation(@NotNull Java8Parser.MethodInvocationContext ctx) {
        insideMethodInvocation = true;
    }

    @Override
    public void enterMethodInvocation_lf_primary(@NotNull Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        insideMethodInvocation = true;
    }

    @Override
    public void enterMethodInvocation_lfno_primary(@NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        insideMethodInvocation = true;
    }

    @Override
    public void enterArgumentList(@NotNull Java8Parser.ArgumentListContext ctx) {
        if (!insideMethodInvocation) {
            List<ParseTree> pt = ctx.children;
            if (pt != null) {
                for (int i = 0; i < pt.size(); i++) {
                    Interval interval = ctx.children.get(i).getSourceInterval();
                    Util.removeChildsTokens(interval, rewriter);
                }
            }
        }
    }

    @Override
    public void exitMethodInvocation(@NotNull Java8Parser.MethodInvocationContext ctx) {
        insideMethodInvocation = false;
    }

    @Override
    public void exitMethodInvocation_lf_primary(@NotNull Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        insideMethodInvocation = false;
    }

    @Override
    public void exitMethodInvocation_lfno_primary(@NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        insideMethodInvocation = false;
    }
}
