package main.java.com.googlecode.ounit.codesimplifier.processing;

import main.java.com.googlecode.ounit.codesimplifier.Java8BaseListener;
import main.java.com.googlecode.ounit.codesimplifier.Java8Parser;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.NotNull;

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
    boolean insideBefore = false;
    boolean insideArgumentList = false;

    @Override
    public void enterMethodInvocation(@NotNull Java8Parser.MethodInvocationContext ctx) {
        insideMethodInvocation = true;
        List<Token> methodname
                = tokens.getTokens(ctx.getStart().getTokenIndex(), ctx.getStop().getTokenIndex());
        if (ctx.Identifier() != null) { // methodName is at the first level
            // handle before
            insideBefore = true;
            // handle nameidentifier
            String methodName = ctx.Identifier().getText();
            //if (!declaredFunctions.contains(methodName)) {
            // remove from tokens
            int g = 0;

            for (int i = 0; i < methodname.size(); i++) {
                if (methodname.get(i).getText().equals(methodName)) {
                    g = i;
                }
            }
            tokensToRemove.add(methodname.get(g).getTokenIndex());
            System.out.println("methodinvocation deleting function name " + methodName);

            // }
            insideArgumentList = true;
            // handle argumentlist
        } else {// methodName is one level deeper
            // handle before
            insideBefore = true;
            // handle argumentlist
            insideArgumentList = true;
        }
    }

    @Override
    public void exitMethodInvocation(@NotNull Java8Parser.MethodInvocationContext ctx) {
        // kui on enda deklareeritud meetod kustuta kõik tokenid enne identifierit ja ka identifier
        List<Token> methodname
                = tokens.getTokens(ctx.getStart().getTokenIndex(), ctx.getStop().getTokenIndex());
        if (tokensToRemove.size() > 0) {
            for (Token token : methodname) {
                if (token.getTokenIndex() <= tokensToRemove.get(0)) {
                    // rewriter.delete(token);
                    System.out.println("deleting token" + token.getText());
                }
            }
            // tokensToRemove.remove(0);
        }
        System.out.println("tokens " + tokensToRemove.toString());

    }

    @Override
    public void enterMethodName(@NotNull Java8Parser.MethodNameContext ctx) {
        List<Token> methodname
                = tokens.getTokens(ctx.getStart().getTokenIndex(), ctx.getStop().getTokenIndex());
        if (insideMethodInvocation) {
            String methodName = ctx.Identifier().getText();
            int g = 0;
            for (int i = 0; i < methodname.size(); i++) {
                if (methodname.get(i).getText().equals(methodName)) {
                    g = i;
                }
            }
            tokensToRemove.add(methodname.get(g).getTokenIndex());
            System.out.println("methodinvocation deleting function name one level deeper " + methodName);
        }
    }
}
