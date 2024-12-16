import SPL.splBaseVisitor;
import SPL.splParser;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class InterRep extends splBaseVisitor<String> {
    private List<String> code = new ArrayList<>(); // 存储生成的三地址码
    private Map<String, String> symbolTable = new HashMap<>(); // 符号表
    private int tempCount = 1;

    private String newTemp() {
        return "t" + (tempCount++);
    }

    // 添加三地址码指令
    private void emit(String instruction) {
        code.add(instruction);
    }

    @Override
    public String visitProgram(splParser.ProgramContext ctx) {
        visitExtDefList(ctx.extDefList());
        // 输出最终生成的三地址码
        for (String line : code) {
            System.out.println(line);
        }
        System.out.println("END");
        return null;
    }

    @Override
    public String visitExtDefList(splParser.ExtDefListContext ctx) {
        
        if (ctx.extDef() != null){
            visitExtDef(ctx.extDef());
        }
        return null;
    }

    @Override
    public String visitExtDef(splParser.ExtDefContext ctx) {
        
        if (ctx.compSt() != null) {
            visitCompSt(ctx.compSt());
        }
        return null;
    }


    @Override
    public String visitCompSt(splParser.CompStContext ctx) {
        
        if (ctx.defList() != null) {
            visitDefList(ctx.defList());
        }
        if (ctx.stmtList() != null) {
            visitStmtList(ctx.stmtList());
        }
        return null;
    }
    
    @Override
    public String visitDefList(splParser.DefListContext ctx) {
        
        // 处理变量声明
        if (ctx.def() != null) {
            visitDef(ctx.def());
        }
        if (ctx.defList() != null) {
            visitDefList(ctx.defList());
        }
        return null;
    }

    @Override
    public String visitDef(splParser.DefContext ctx) {
        
        // 处理单个变量声明
        if (ctx.decList() != null) {
            visitDecList(ctx.decList());
        }
        return null;
    }

    @Override
    public String visitDecList(splParser.DecListContext ctx){
        
        if (ctx.dec() != null) {
            visitDec(ctx.dec());
        }
        return null;
    }

    @Override
    public String visitDec(splParser.DecContext ctx) {
        
        if (ctx.ASSIGN() != null) {
            
            // 变量赋值声明，例如 int a = 5;
            
            String varName = ctx.varDec().getText();
            String value = visitExp(ctx.exp());
            symbolTable.put(varName, varName); // 存入符号表
            emit(varName + " := " + value);
        } else {
            // 单独的变量声明，例如 int a;
            String varName = ctx.varDec().getText();
            symbolTable.put(varName, varName);
            emit(varName + " := #0"); // 默认初始化为0
        }
        return null;
    }

    @Override
    public String visitStmt(splParser.StmtContext ctx) {
        if (ctx.exp() != null) {
            // 处理表达式语句，例如 a = b + c;
            visitExp(ctx.exp());
        } else if (ctx.RETURN() != null) {
            // 处理返回语句
            String returnValue = visitExp(ctx.exp());
            emit("RETURN " + returnValue);
        }
        return null;
    }

    @Override
    public String visitExp(splParser.ExpContext ctx) {
        if (ctx.getChildCount() == 3) {
            // 处理二元运算，例如 a + b, a = b
            String left = visitExp(ctx.exp(0));
            String right = visitExp(ctx.exp(1));
            String op = ctx.getChild(1).getText(); // 操作符 (+, -, *, /, = 等)
            if ("=".equals(op)) {
                emit(left + " := " + right);
                return left;
            } else {
                String temp = newTemp();
                emit(temp + " := " + left + " " + op + " " + right);
                return temp;
            }
        } else if (ctx.getChildCount() == 1) {
            // 处理单个变量或常量
            String text = ctx.getText();
            if (symbolTable.containsKey(text)) {
                return text; // 如果是变量，返回变量名
            } else {
                return "#" + text; // 如果是常量，直接返回值
            }
        }
        return null;
    }
}
