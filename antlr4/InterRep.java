import SPL.splBaseVisitor;
import SPL.splParser;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// define v_i -> READ
// define t_i -> other definition

public class InterRep extends splBaseVisitor<String> {
    private List<String> code = new ArrayList<>(); // 存储生成的三地址码
    private Map<String, String> symbolTable = new HashMap<>(); // 符号表
    private Map<String, String> functionTable = new HashMap<>(); // 函数表
    private int tempCount = 0;
    private int readCount = 0;
    private int labelCount = 0;

    private String newTemp() {
        return "t" + (tempCount++);
    }
    private String newRead() {
        return "v" + (readCount++);
    }
    private String newLabel(){
        return "label" + (labelCount++);
    }

    private String gotoLabel(String label){
        return "GOTO " + label;
    }

    private String getLabel(String label){
        return "LABEL " + label + " :";
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
//        System.out.println("END");
        return null;
    }

    @Override
    public String visitExtDefList(splParser.ExtDefListContext ctx) {

        if (ctx.extDef() != null){
            visitExtDef(ctx.extDef());
        }
        if(ctx.extDefList()!=null){
            visitExtDefList(ctx.extDefList());
        }
        return null;
    }

    @Override
    public String visitExtDef(splParser.ExtDefContext ctx) {
        if (ctx.funDec() != null){
            visitFunDec(ctx.funDec());
        }
        if (ctx.compSt() != null) {
            visitCompSt(ctx.compSt());
        }
        else if(ctx.extDecList()!=null){
            visitExtDecList(ctx.extDecList());
        }
        return null;
    }

    @Override
    public String visitExtDecList(splParser.ExtDecListContext ctx){
        if(ctx.varDec()!=null){
            visitVarDec(ctx.varDec());
        }
        if(ctx.extDecList()!=null){
            visitExtDecList(ctx.extDecList());
        }
        return null;
    }

    @Override
    public String visitFunDec(splParser.FunDecContext ctx){
        if(ctx.ID()!=null){
            String fun_name = ctx.ID().getText();
            // 将函数名
            functionTable.put(fun_name, fun_name);
            emit("FUNCTION " +fun_name+ " :");
        }
        if(ctx.varList()!=null){
            visitVarList(ctx.varList());
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
        if(ctx.decList()!=null){
            visitDecList(ctx.decList());
        }
        return null;
    }

    @Override
    public String visitVarList(splParser.VarListContext ctx){
        if(ctx.paramDec()!=null){
            visitParamDec(ctx.paramDec());
        }
        if(ctx.varList()!=null){
            visitVarList(ctx.varList());
        }
        return null;
    }

    @Override
    public String visitVarDec(splParser.VarDecContext ctx){
        if(ctx.ID()!=null){
            // todo
            String temp = newTemp();
            symbolTable.put(ctx.ID().getText(), temp); // 存入符号表
            return temp;
        }
        // not used because no arrays
//        else if(ctx.varDec()!=null){
//            visitVarDec(ctx.varDec());
//        }
        return null;
    }

    @Override
    public String visitParamDec(splParser.ParamDecContext ctx){
        if(ctx.varDec()!=null){
            // 这里已经转化为t_i形式了
            String var_name = visitVarDec(ctx.varDec());
            // String tempp = symbolTable.get(var_name);
            emit("PARAM " + var_name);
        }
        return null;
    }

    @Override
    public String visitDec(splParser.DecContext ctx) {
        if (ctx.ASSIGN() != null) {

            // 变量赋值声明，例如 int a = 5;

            String temp = visitVarDec(ctx.varDec());

            // String varName = ctx.varDec().getText();
            String value = visitExp(ctx.exp());
            // String temp = newTemp();
            // symbolTable.put(varName, temp); // 存入符号表
            emit(temp + " := " + value);
        } else {
            // 单独的变量声明，例如 int a; 如果只是单个定义不需要打印输出
            // String varName = visitVarDec(ctx.varDec());
            // System.out.println("var name is " + varName);
            // String varName = ctx.varDec().getText();
            // String temp = newTemp();
            // symbolTable.put(varName, temp);
            // emit(temp + " := #0"); // 默认初始化为0
        }
        return null;
    }

    @Override
    public String visitStmtList(splParser.StmtListContext ctx){
        if(ctx.stmt()!=null){
            visitStmt(ctx.stmt());
        }
        if(ctx.stmtList()!=null){
            visitStmtList(ctx.stmtList());
        }
        return null;
    }

    @Override
    public String visitStmt(splParser.StmtContext ctx) {
        ArrayList<String> end_label = new ArrayList<>();
        if(ctx.compSt()!=null){
            visitCompSt(ctx.compSt());
        }
        else if (ctx.RETURN() != null) {
            // 处理返回语句
            String returnValue = visitExp(ctx.exp());
            emit("RETURN " + returnValue);
            return null;
        }
        else if(ctx.IF()!=null){
            //IF
            if(ctx.exp()!=null){
                String new_label = newLabel();
                emit("IF " + visitExp(ctx.exp()) + " " + gotoLabel(new_label));
                String new_label2 = newLabel();

                String new_gotoLable = gotoLabel(new_label2);
                emit(new_gotoLable);

                emit(getLabel(new_label));
                if(ctx.stmt()!=null){
                    List<splParser.StmtContext> stmt_list = ctx.stmt();
                    // 如果是包含else的
                    if(stmt_list.size() == 2){
                        visitStmt(stmt_list.get(0));
                        // 遍历完每个stmt创建一个跳转的label
                        String temp_label = newLabel();
                        emit(gotoLabel(temp_label));
                        // 将需要在结束时候输入的东西现在全部加入一个end_label数组
                        end_label.add(getLabel(temp_label));
                        emit(getLabel(new_label2));
                        visitStmt(stmt_list.get(1));
                    }
                    else{
                        visitStmt(stmt_list.get(0));
                        emit(getLabel(new_label2));
                    }
                }
                // 添加一个入口
                // emit("LABEL label" + (labelCount - 2));
            }
            for (String label : end_label) {
                // System.out.println("Label 有 " + label);
                emit(label);
            }
        }
        else if (ctx.WHILE()!=null){
            // 循环开始标签、循环结束标签、循环继续标签
            String startLabel = newLabel();
            String endLabel = newLabel();
            String continueLabel = newLabel();

            emit(getLabel(startLabel));

            String condition = visitExp(ctx.exp()); // 循环条件

            emit("IF " + condition + " " + gotoLabel(continueLabel)); // 如果条件符合，前往循环继续标签
            emit(gotoLabel(endLabel)); // 如果条件不符合，前往循环结束标签
            emit(getLabel(continueLabel));

            // 循环体
            if (ctx.stmt() != null) {
                visitStmt(ctx.stmt().get(0));
            }

            // 循环体结束后前往循环开始标签
            emit(gotoLabel(startLabel));
            emit(getLabel(endLabel));
        }
        else if (ctx.exp() != null) {
            // 处理表达式语句，例如 a = b + c;
            visitExp(ctx.exp());
        }
        return null;
    }

    @Override
    public String visitExp(splParser.ExpContext ctx) {

        // deal with READ
        if(ctx != null && ctx.ID()!=null && ctx.ID().getText().equals("read")) {

            String readtemp = newRead();
            emit("READ " + readtemp);
//            return "READ" + readtemp;
            return readtemp;
        }
        // write LP ARGS RP
//        else if(ctx != null && ctx.ID()!=null && (ctx.ID().getText().equals("write")||functionTable.containsKey(ctx.ID().getText()))){
//            // System.out.println("Find write");
//            if(ctx.ID().getText().equals("write") && ctx.args()!=null){
//                emit("WRITE " + visitArgs(ctx.args()));
//                return null;
//            }
//            else if(ctx.args()!=null){
//                visitArgs(ctx.args());
//            }
//            return "CALL " + ctx.ID().getText();
//        }
//         ID LP ARGS RP
        else if (ctx != null && ctx.getChildCount() == 4) {
//            System.out.println("Find function" + ctx.ID().getText());

            if(ctx.args()!=null) {
                if (ctx.ID().getText().equals("write")) {
                    emit("WRITE " + visitArgs(ctx.args()));
                    return null;
                }

                visitArgs(ctx.args());
                String new_temp = newTemp();
                emit(new_temp + " := " + "CALL " + ctx.ID().getText());
                return new_temp;
            }
        }
        else if (ctx != null && ctx.getChildCount() == 3) {
            // 处理二元运算，例如 a + b, a = b
            String right = visitExp(ctx.exp(1));
            if(right == null){
                return visitExp(ctx.exp(0));
            }
            String left = visitExp(ctx.exp(0));
            // System.out.println("left is " + left + " right is " + right);
            //如果是read的exp三元组，就将之前read传过来的变量名进行预处理
//            if(right.contains("READ")){
//                // System.out.println("left is " + left);
//                // System.out.println("right is " + right);
//                if (ctx.ASSIGN() != null) {
//                    // if it's assignment, e.g. n = read();
//                    emit(left + " := " + right.replaceFirst("READ", ""));
//                    return null;
//                } else {
//                    // not assignment
//                    right = right.replaceFirst("READ", "");
//                }
////                emit(left + " := " + right.replaceFirst("READ", ""));
////                // symbolTable.put(left, right.replaceFirst("READ", ""));
////                return null;
//            }
            String op = ctx.getChild(1).getText(); // 操作符 (+, -, *, /, = 等)
            // System.out.println("op is " + op);
            if (left !=null && "=".equals(op)) {
                emit(left + " := " + right);
                return left;
            } else if (left !=null && "<".equals(op)) {
                // if the parent is still exp
                if (ctx.getParent().getClass().equals(splParser.ExpContext.class)) {
                    String temp = newTemp();
                    emit(temp + " := " + left + " " + op + " " + right);
                    return temp;
                }
                return left + " < " + right;
            } else if (left != null && ">".equals(op)) {
                // if the parent is still exp
                if (ctx.getParent().getClass().equals(splParser.ExpContext.class)) {
                    String temp = newTemp();
                    emit(temp + " := " + left + " " + op + " " + right);
                    return temp;
                }
                return left + " > " + right;
            } else if (left != null && "==".equals(op)) {
                // if the parent is still exp
                if (ctx.getParent().getClass().equals(splParser.ExpContext.class)) {
                    String temp = newTemp();
                    emit(temp + " := " + left + " " + op + " " + right);
                    return temp;
                }
                return left + " == " + right;
            } else if (left != null && ">=".equals(op)) {
                // if the parent is still exp
                if (ctx.getParent().getClass().equals(splParser.ExpContext.class)) {
                    String temp = newTemp();
                    emit(temp + " := " + left + " " + op + " " + right);
                    return temp;
                }
                return left + " >= " + right;
            } else if (left != null && "<=".equals(op)) {
                // if the parent is still exp
                if (ctx.getParent().getClass().equals(splParser.ExpContext.class)) {
                    String temp = newTemp();
                    emit(temp + " := " + left + " " + op + " " + right);
                    return temp;
                }
                return left + " <= " + right;
            } else if (left != null && "!=".equals(op)) {
                // if the parent is still exp
                if (ctx.getParent().getClass().equals(splParser.ExpContext.class)) {
                    String temp = newTemp();
                    emit(temp + " := " + left + " " + op + " " + right);
                    return temp;
                }
                return left + " != " + right;
            } else if (left != null) {
                // if the ctxExp is derived from: parentExp -> exp ASSIGN ctxExp
                if (ctx.getParent().getClass().equals(splParser.ExpContext.class)) {
                    splParser.ExpContext parentExp = (splParser.ExpContext) ctx.getParent();
                    if (parentExp.ASSIGN() != null && parentExp.exp(1).equals(ctx)) {
                        return left + " " + op + " " + right;
                    }
                }
                String temp = newTemp();
                emit(temp + " := " + left + " " + op + " " + right);
                return temp;
            }
        }
        else if (ctx != null && ctx.getChildCount() == 2) {
            // 处理单个变量或常量
            // -a
            if(ctx.MINUS()!=null){
                String expp = visitExp(ctx.exp(0));
                // if the ctxExp is derived from: parentExp -> exp ASSIGN ctxExp
                if (ctx.getParent().getClass().equals(splParser.ExpContext.class)) {
                    splParser.ExpContext parentExp = (splParser.ExpContext) ctx.getParent();
                    if (parentExp.ASSIGN() != null && parentExp.exp(1).equals(ctx)) {
                        return "#0" + " - " + expp;
                    }
                }
                String expp_temp = newTemp();
                emit(expp_temp + " := " + "#0" + " - " + expp);
                return expp_temp;
            }
            else if (ctx.NOT()!=null){
                String expp = visitExp(ctx.exp(0));
                // if the ctxExp is derived from: parentExp -> exp ASSIGN ctxExp
                if (ctx.getParent().getClass().equals(splParser.ExpContext.class)) {
                    splParser.ExpContext parentExp = (splParser.ExpContext) ctx.getParent();
                    if (parentExp.ASSIGN() != null && parentExp.exp(1).equals(ctx)) {
                        return "NOT " + expp;
                    }
                }
                String expp_temp = newTemp();
                emit(expp_temp + " := " + "NOT " + expp);
                return expp_temp;
            }
        }
        //TODO: 参考spl.g4扩展到更多
        else if (ctx != null && ctx.getChildCount() == 1) {
            // 处理单个变量或常量
            String text = ctx.getText();
            if (symbolTable.containsKey(text)) {
                // System.out.println("get key " + symbolTable.get(text));
                return symbolTable.get(text); // 如果是变量，返回corresponding name e.x.t1
            } else {
                // System.out.println("!!!");
                boolean is_digit = true;
                for (char c : text.toCharArray()) {
                    if (!Character.isDigit(c)) {
                        is_digit = false; // 如果遇到非数字字符，返回 false
                    }
                }
                String temp = newTemp();
                if (is_digit) {
                    // System.out.println("TEXT is " + text);
//                    emit(temp + " := " + "#" + text);
                    return "#" + text;
                }
                else{
                    symbolTable.put(text, temp);
                }
                return temp;
            }
        }
        return null;
    }

    @Override
    public String visitArgs(splParser.ArgsContext ctx){
        if(ctx.args()!=null){
            visitArgs(ctx.args());
        }
        if(ctx.exp()!=null){
            String tempp = visitExp(ctx.exp());
            emit("ARG " + tempp);
            return tempp;
        }
        return null;
    }
}