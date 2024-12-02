import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;

import SPL.splLexer;
import SPL.splParser;
import SPL.SemanticAnalyzer;

public class spltest {
    public static void main(String[] args) throws IOException {
        InputStream is = new FileInputStream("./test/test_t3.spl"); // 输入文件
        ANTLRInputStream input = new ANTLRInputStream(is);
        splLexer lexer = new splLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        splParser parser = new splParser(tokens);
        ParseTree tree = parser.program(); // program 是起始规则

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(); // 自定义 Listener
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(semanticAnalyzer, tree);

        // 转换成缩进格式的树
        String formattedTree = formatParseTree(tree, parser, 0);
        System.out.println(formattedTree);

    }

    /**
     * 将 ParseTree 转换成缩进格式的树
     * @param tree 当前节点
     * @param parser 解析器，用于获取规则名称
     * @param level 当前深度，用于计算缩进
     * @return 缩进格式的树字符串
     */
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
