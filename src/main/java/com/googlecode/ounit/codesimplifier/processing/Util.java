package main.java.com.googlecode.ounit.codesimplifier.processing;

import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 *
 * @author urmas
 */
public class Util {

    public static void removeChildsTokens(Interval interval, TokenStreamRewriter rewriter) {
        for (int i = interval.a; i < interval.b + 1; i++) {
            rewriter.delete(i);
        }
    }

    public static void removeChild(ParseTree child, TokenStreamRewriter rewriter) {
        Interval interval = child.getSourceInterval();
        removeChildsTokens(interval, rewriter);
    }
}
