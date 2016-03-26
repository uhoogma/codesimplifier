package main.java.com.googlecode.ounit.codesimplifier.processing;

import main.java.com.googlecode.ounit.codesimplifier.Java8BaseListener;
import main.java.com.googlecode.ounit.codesimplifier.Java8Parser;
import java.util.List;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.NotNull;

/**
 *
 * @author urmas
 */
public class PreSimplifier extends Java8BaseListener {

    BufferedTokenStream tokens;
    public TokenStreamRewriter rewriter;

    public PreSimplifier(BufferedTokenStream tokens) {
        this.tokens = tokens;
        /* first and only initialization of a TokenStreamRewriter. 
         From now on this rewriter is passed on from class to class */
        rewriter = new TokenStreamRewriter(tokens);
    }

    /*  
     1. Annotation : annotations have no role in actual program execution
     logic, they only increase the number of lines of the source code, which
     may skew the similarity measures based on the number lines matched. 
     Annotations are only the information useful and necessary for the 
     programmers or people who need to look into the source code for 
     understanding. Thus, we believe annotation should not intervene the 
     similarity measure for the source code programs. Deleting annotations 
     in source codes makes no effects on the execution of the program. Thus,
     these annotations are eliminated by our abstractor.
     */
    @Override
    public void enterAnnotation(Java8Parser.AnnotationContext ctx) {
        List<Token> annotationTokens
                = tokens.getTokens(ctx.getStart().getTokenIndex(), ctx.getStop().getTokenIndex());
        if (annotationTokens != null) {
            for (Token annotationToken : annotationTokens) {
                if (annotationToken != null) {
                    rewriter.delete(annotationToken.getTokenIndex());
                }
            }
        }
    }

    /*
     2. Variable declaration : variable declarations define the names of the
     variables and their types. Variables are necessary items in source code
     to store the status of program execution or the temporary values of 
     calculations. However, they are very easy targets to be obfuscated by 
     malicious duplicators, when software theft occurs. For example, 
     changing variable names in source code does not change the overall 
     logic and algorithm of the execution of the program. At binary level, 
     the modified programs will perform almost the same functions, even if 
     the names and types of variables are changed in the source code. Not 
     only variable names but also locations of variable declarations in the 
     source code can disturb the text/token-based similarity methods for 
     source code, but these changes may rarely affect the functionalities 
     of original programs. Therefore, our abstractor eliminates all of 
     variable declarations, as they are less important features in source 
     code similarity.
     */
    @Override
    public void enterLocalVariableDeclarationStatement(Java8Parser.LocalVariableDeclarationStatementContext ctx) {
        List<Token> variableDeclarationTokens
                = tokens.getTokens(ctx.getStart().getTokenIndex(), ctx.getStop().getTokenIndex());
        if (variableDeclarationTokens != null) {
            for (Token variableDeclarationToken : variableDeclarationTokens) {
                if (variableDeclarationToken != null) {
                    rewriter.delete(variableDeclarationToken.getTokenIndex());
                }
            }
        }
    }

    /*
     3. Format string : format strings are used to specify the types and 
     formats of the arguments supplied in I/O functions of the standard C 
     library. The format symbols (e.g., %d, %f, %s) used in the format 
     strings such as printf() and scanf()) to control data formats for I/O.
     If the types of input (or output) variables change, these symbols 
     should also be changed to appropriate format symbols. Thus, format 
     strings are very dependent on the actual variables used for I/O. Since 
     our abstractor eliminates variable declarations, there are no relevant
     data types for variables. In this line of logic, format strings are 
     rather unnecessary for our similarity comparison with abstraction. Our 
     abstractor transforms format stings into abstracted forms of empty 
     strings ("").
     4. String argument : a string is nothing more than a character array 
     and one of the popularly used data types. String arguments are 
     frequently used to display some sequence characters, but they are too 
     easy to be obfuscated in malicious software theft. By changing the 
     words from the string, obfuscation can make the same software a lot 
     different. The users can easily think that the obfuscated software is 
     different from the original, as the interaction messages from the 
     software are totally different. Our abstractor takes away the actual 
     contents of string arguments, leaving them in an abstracted form such 
     as ("").
     */
    @Override
    public void enterLiteral(@NotNull Java8Parser.LiteralContext ctx) {
        List<Token> literalTokens
                = tokens.getTokens(ctx.getStart().getTokenIndex(), ctx.getStop().getTokenIndex());
        if (literalTokens != null) {
            for (Token literalToken : literalTokens) {
                // matching (optional) String with following escapeSequence
                if (literalToken != null && literalToken.getText().matches("\"(?s).*\"")) {
                    System.out.println("literaltoken" + literalToken);
                    rewriter.replace(literalToken.getTokenIndex(), "\"\"");
                }
            }
        }
    }

    /*
     6. return statement : return statement terminates the execution of a 
     function and returns control to the calling function. A return statement
     can also return a value to the calling function. This return value is 
     dependent on the calculation result with multiple variables. Since we
     remove most of calculations and keep only the control flow structures, 
     keeping return statement is pointless. Thus, we eliminate all of the 
     return statements in the source code.
     */
    @Override
    public void enterReturnStatement(@NotNull Java8Parser.ReturnStatementContext ctx) {
        List<Token> returnStatementTokens
                = tokens.getTokens(ctx.getStart().getTokenIndex(), ctx.getStop().getTokenIndex());
        if (returnStatementTokens != null) {
            for (Token returnStatementToken : returnStatementTokens) {
                if (returnStatementToken != null) {
                    rewriter.delete(returnStatementToken.getTokenIndex());
                }
            }
        }
    }
}
