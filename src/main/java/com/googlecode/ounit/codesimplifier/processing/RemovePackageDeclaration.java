package main.java.com.googlecode.ounit.codesimplifier.processing;

import java.util.List;
import main.java.com.googlecode.ounit.codesimplifier.Java8BaseListener;
import main.java.com.googlecode.ounit.codesimplifier.Java8Parser;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;

class RemovePackageDeclaration extends Java8BaseListener {

    public TokenStreamRewriter rewriter;

    public RemovePackageDeclaration(TokenStreamRewriter rewriter) {
        this.rewriter = rewriter;
    }

    @Override
    public void enterPackageDeclaration(@NotNull Java8Parser.PackageDeclarationContext ctx) {
        List<ParseTree> pt = ctx.children;
        if (pt != null) {
            pt.stream().forEach((pt1) -> {
                Util.removeChild(pt1, rewriter);
            });
        }
    }
}
