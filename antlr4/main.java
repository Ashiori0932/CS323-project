
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.*;

import SPL.splLexer;
import SPL.splParser;

public class main {
    public static void main(String[] args) throws IOException {
        InputStream is = new FileInputStream("./test.spl"); // or System.in;
        ANTLRInputStream input = new ANTLRInputStream(is);
        splLexer lexer = new splLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        splParser parser = new splParser(tokens);
        ParseTree tree = parser.program(); // calc is the starting rule

        InterRep ir = new InterRep();
        // 开始遍历语法分析树
        ir.visit(tree);

        System.out.println("LISP:");
        System.out.println(tree.toStringTree(parser));
        System.out.println();

    }
}
