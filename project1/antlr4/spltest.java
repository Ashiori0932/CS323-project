import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;

import SPL.splLexer;
import SPL.splParser;

public class spltest {
    public static void main(String[] args) throws IOException {
        InputStream is = new FileInputStream("./test/test02.spl"); // or System.in;
        ANTLRInputStream input = new ANTLRInputStream(is);
        splLexer lexer = new splLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        splParser parser = new splParser(tokens);
        ParseTree tree = parser.program(); // program is the starting rule

        System.out.println(tree.toStringTree(parser));
        System.out.println();
    }
}