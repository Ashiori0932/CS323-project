import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;

import SPL.splLexer;
import SPL.splParser;

public class spltest {
    public static void main(String[] args) throws IOException {
        String inputFilePath = "./test/test02.spl"; 

        String inputFileName = new File(inputFilePath).getName();
        String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + "_parse_tree.txt";

        File outputDir = new File("./output");
        if (!outputDir.exists()) {
            boolean created = outputDir.mkdirs();
        }

        InputStream is = new FileInputStream(inputFilePath);
        ANTLRInputStream input = new ANTLRInputStream(is);
        splLexer lexer = new splLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        splParser parser = new splParser(tokens);
        ParseTree tree = parser.program();

        String formattedTree = formatTree(tree, 0);

        System.out.println(formattedTree);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./output/" + outputFileName))) {
            writer.write(formattedTree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String formatTree(ParseTree tree, int level) {
        StringBuilder sb = new StringBuilder();
        String indent = "  ".repeat(level); 

        sb.append(indent).append("("); 
        sb.append(tree.getClass().getSimpleName());

        if (tree instanceof TerminalNode) {
            TerminalNode terminalNode = (TerminalNode) tree;
            sb.append(" ").append(terminalNode.getText()); 
        }

        if (tree.getChildCount() > 0) {
            sb.append("\n"); 
            for (int i = 0; i < tree.getChildCount(); i++) {
                sb.append(formatTree(tree.getChild(i), level + 1)); 
            }
            sb.append(indent).append(")").append("\n"); 
        } else {
            sb.append(")").append("\n"); 
        }

        return sb.toString();
    }
}
