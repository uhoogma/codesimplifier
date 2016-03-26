package com.googlecode.ounit.codesimplifier.processing;

import com.googlecode.ounit.codesimplifier.Java8BaseListener;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

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

    BufferedTokenStream tokens;
    TokenStreamRewriter rewriter;
    List<String> declaredFunctions;
    List<Integer> tokensToRemove;

    public RemoveExpressionStatements(BufferedTokenStream tokens, TokenStreamRewriter rewriter, List<String> declaredFunctions) {
        this.tokens = tokens;
        this.declaredFunctions = declaredFunctions;
        this.rewriter = rewriter;
        tokensToRemove = new ArrayList<>();
    }
}
