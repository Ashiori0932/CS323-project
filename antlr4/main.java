
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.*;

import SPL.splLexer;
import SPL.splParser;

public class main {
    public static void main(String[] args) throws IOException {
        InputStream is = new FileInputStream("./test/test_phase3/test_3_r10.spl"); // or System.in;
        ANTLRInputStream input = new ANTLRInputStream(is);
        splLexer lexer = new splLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        splParser parser = new splParser(tokens);
        ParseTree tree = parser.program(); // calc is the starting rule

        InterRep ir = new InterRep();
        // 开始遍历语法分析树
        ir.visit(tree);

        System.out.println("Parser Tree:");
        // System.out.println(tree.toStringTree(parser));
        // System.out.println();

        // 转换成缩进格式的树
        String formattedTree = formatParseTree(tree, parser, 0);
        System.out.println(formattedTree);


    }

    public static String formatParseTree(ParseTree tree, Parser parser, int level) {
        StringBuilder sb = new StringBuilder();
        // 缩进
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }

        // 获取当前节点的名称
        if (tree instanceof RuleNode) { // 规则节点
            int ruleIndex = ((RuleNode) tree).getRuleContext().getRuleIndex();
            String ruleName = parser.getRuleNames()[ruleIndex];
            sb.append(ruleName).append(" (").append(level + 1).append(")\n");
        } else if (tree instanceof TerminalNode) { // 终结符节点
            String text = tree.getText();
            sb.append(text).append("\n");
        }

        // 递归处理子节点
        for (int i = 0; i < tree.getChildCount(); i++) {
            sb.append(formatParseTree(tree.getChild(i), parser, level + 1));
        }

        return sb.toString();
    }

}
