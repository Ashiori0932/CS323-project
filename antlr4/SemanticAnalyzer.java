package SPL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;



public class SemanticAnalyzer extends splBaseListener {
    // 对应symbol_id -> type,如果是结构体的定义按照这个结构symbol_id -> struct_name(symbolTable.put(varName_list, struct_name))
    private Map<String, String> symbolTable = new HashMap<>();
    private Map<String, String> FunctionTable = new HashMap<>();
    // 存储结构体名字和对应的符号表
    private Map<String, Map<String, String>> structSymbolTables = new HashMap<>();
    //store the type of each exp
    private Map<ParseTree, String> expTypesMap = new HashMap<>();
    //store args types of functions
    private Map<String, List<String>> funcArgsTypeMap = new HashMap<>();

    @Override
    public void exitParamDec(splParser.ParamDecContext ctx) {
        int line = ctx.getStart().getLine();
        // 如果是普通变量声明
        if (ctx.specifier().TYPE() != null) {
            String type = ctx.specifier().TYPE().getText();
            String varName = ctx.varDec().ID().getText();
            // 检查变量是否已在符号表中
            if (symbolTable.containsKey(varName)) {
                System.err.println("Error type 3 at Line " + line + ": Variable " + varName + " is already declared in the same parameter scope.");
            } else {
                //System.out.println("get varible" + varName + " in para dec "+ type);
                // 这里对应的应该写type和value
                symbolTable.put(varName, type);
            }
        }
        else{
            String varName = ctx.varDec().ID().getText();
            System.err.println("Error: Variable " + varName + "needs a type");
            if (varName == null){
                System.err.println("Error: Missing varible name");
            }
        }
    }

    @Override
    public void exitDef(splParser.DefContext ctx) {
        int line = ctx.getStart().getLine();
        // 如果是普通变量声明
        if (ctx.specifier().TYPE() != null) {
            String type = ctx.specifier().TYPE().getText();
            //System.out.println(type + "type is");
            // String varName = ctx.decList().dec().varDec().ID().getText();
            splParser.DecListContext temp = ctx.decList();
            // 如果能够继续往下寻找declist
            while (temp != null){
                splParser.VarDecContext temp_vardec = temp.dec().varDec();
                String arraySize = "";
                while(temp_vardec.ID() == null){
                    arraySize = "[" + temp_vardec.INT() + "]" + arraySize;
                    if(Integer.parseInt(temp_vardec.INT().getText())<=0){
                        System.err.println("Error type 12 at Line " + line + ": The dimension size must be greater than 0 when declaring an array.");
                    }
                    temp_vardec = temp_vardec.varDec();
                }
                String varName_list = temp_vardec.ID().getText();
                // System.out.println(varName_list + " 's type: " + type+arraySize);
                if (symbolTable.containsKey(varName_list)) {
                    System.err.println("Error type 3 at Line " + line + ": Variable " + varName_list + " is already declared in the same scope.");
                } else {
                    // System.out.println("get varible" + varName_list + " in def list"+ type);
                    // 这里对应的应该写type和value
                    symbolTable.put(varName_list, type + arraySize);

                    if (temp.dec().ASSIGN() != null) {
                        if (!(type + arraySize).equals(expTypesMap.get(temp.dec().exp()))) {
                            if (!expTypesMap.get(temp.dec().exp()).equals("wrong exp")) {
                                System.err.println("Error type 5 at Line " + line + ": unmatched type for on both sides of assignment for " + ctx.getText());
                            }
                        }
                    }
                }
                temp = temp.decList();
            }
            // // 检查变量是否已在符号表中
            // if (symbolTable.containsKey(varName)) {
            //     System.err.println("Error: Variable " + varName + " is already declared.");
            // } else {
            //     System.out.println("get varible" + varName + " in def list"+ type);
            //     // 这里对应的应该写type和value
            //     symbolTable.put(varName, type);
            // }
        }
        // 如果他是一个结构体的定义(实例化)
        else if(ctx.specifier() != null && ctx.specifier().structSpecifier() != null){
            String struct_name = ctx.specifier().structSpecifier().ID().getText();
            splParser.DecListContext temp = ctx.decList();
            // 如果能够继续往下寻找declist
            while (temp != null){
                splParser.VarDecContext temp_vardec = temp.dec().varDec();
                String arraySize = "";
                while(temp_vardec.ID() == null){
                    arraySize = "[" + temp_vardec.INT() + "]" + arraySize;
                    if(Integer.parseInt(temp_vardec.INT().getText())<=0){
                        System.err.println("Error type 12 at Line " + line + ": The dimension size must be greater than 0 when declaring an array.");
                    }
                    temp_vardec = temp_vardec.varDec();
                }
                String varName_list = temp_vardec.ID().getText();
                if (symbolTable.containsKey(varName_list)) {
                    System.err.println("Error type 3 at Line " + line + ": Variable " + varName_list + " is already declared in the same scope.");
                } else {
                    // System.out.println("get variable" + varName_list + " in def list"+ type);
                    // 这里对应的应该写type和value
                    symbolTable.put(varName_list, struct_name + arraySize);
                    //System.out.println("successfully put " + varName_list + " " + struct_name);
                }
                temp = temp.decList();
            }
        }
        else{
            String varName = ctx.decList().dec().varDec().ID().getText();
            System.err.println("Error: Variable " + varName + "needs a type");
            if (varName == null){
                System.err.println("Error: Missing varible name");
            }
        }
    }

    @Override
    public void exitFunDec(splParser.FunDecContext ctx) {
        if (ctx.varList() != null) {
            ArrayList<String> argsList = new ArrayList<>();
            splParser.VarListContext tempVarList = ctx.varList();
            while (tempVarList.varList() != null) {
                splParser.ParamDecContext paramDecContext = tempVarList.paramDec();
                if (paramDecContext.specifier() != null) {
                    if (paramDecContext.specifier().TYPE() != null) {
                        argsList.add(paramDecContext.specifier().TYPE().getText());
                    } else if (paramDecContext.specifier().structSpecifier() != null) {
                        argsList.add(paramDecContext.specifier().structSpecifier().ID().getText());
                    }
                }
                tempVarList = tempVarList.varList();
            }
            splParser.ParamDecContext paramDecContext = tempVarList.paramDec();
            if (paramDecContext.specifier() != null) {
                if (paramDecContext.specifier().TYPE() != null) {
                    argsList.add(paramDecContext.specifier().TYPE().getText());
                } else if (paramDecContext.specifier().structSpecifier() != null) {
                    argsList.add(paramDecContext.specifier().structSpecifier().ID().getText());
                }
            }
            funcArgsTypeMap.put(ctx.ID().getText(), argsList);
        } else {
            funcArgsTypeMap.put(ctx.ID().getText(), new ArrayList<>());
        }
    }

// // function declaration
// @Override
// public void exitFunDec(splParser.FunDecContext ctx) {
//     // 如果是普通变量声明
//     if (ctx.ID() != null) {
//         String varName = ctx.ID().getText();
//         // 检查变量是否已在符号表中
//         if (FunctionTable.containsKey(varName)) {
//             System.err.println("Error: Function " + varName + " is redefined.");
//         } else {
//             // System.out.println(varName+" variable");
//             FunctionTable.put(varName, "function");
//         }
//     }
// }

    // 外部声明
    @Override
    public void exitExtDef(splParser.ExtDefContext ctx) {
        // 如果是普通变量声明
        if (ctx.specifier().TYPE() != null) {
            String type = ctx.specifier().TYPE().getText();
            splParser.StmtListContext temp = null;
            if(ctx.compSt()!=null){
                temp = ctx.compSt().stmtList();
                // System.out.println("type is"+ type);
                // 对于多个stmtlist并列的情况判断return
                parse_mul_stmtlist(temp, type);
            }

            if (temp!=null && temp.stmt()!=null && temp.stmt().stmt()!=null){
                // 多个stmt 对return的判断
                List<splParser.StmtContext> temp_list = temp.stmt().stmt();
                if(!temp_list.isEmpty()){
                    //System.out.println("不是空的");
                    // System.out.println("length is " + temp_list.size());
                    //遍历每一个stmt
                    for(int i = 0;i<temp_list.size();i++){
                        // 如果一个stmt中有更多的stmt
                        if(temp_list.get(i).compSt()!=null){
                            splParser.StmtContext temmp = temp_list.get(i).compSt().stmtList().stmt();
                            if (temmp.exp().ID() != null && temmp.exp().LP() == null){
                                // System.out.println("IN return ID");
                                String stmtlist_name = temmp.exp().ID().getText();
                                String return_type = symbolTable.get(stmtlist_name);
                                // System.out.println("IN return" + stmtlist_name);
                                if(!return_type.equals(type)){
                                    int line = temmp.exp().ID().getSymbol().getLine();
                                    System.err.println("Error type 8 at Line " + line + ": " + stmtlist_name + " " + "return_type" + " is not match");
                                }
                            }
                            // 如果返回的就是一个函数
                            else if(temmp.exp().LP() != null && temmp.RETURN()!=null){
                                // System.out.println("IN return function");
                                String fun_Name = temmp.exp().ID().getText();
                                String fun_type = FunctionTable.get(fun_Name);
                                // System.out.println("IN return function" + fun_Name + " " + fun_type);
                                if(fun_type!=null && !fun_type.equals(type)){
                                    int line = temmp.exp().ID().getSymbol().getLine();
                                    System.err.println("Error type 8 at Line " + line + ": " + fun_Name + " return type " + fun_type + " is not match");
                                }
                            }
                            else if(temmp.RETURN()!=null){
                                // System.out.println("IN return");
                                //如果返回的是整数类型...
                                if(temmp.exp()!=null){
                                    int line = temmp.exp().getStart().getLine();
                                    String exp_type = expTypesMap.get(temmp.exp());
                                    //System.out.println(temmp.exp().getText());
                                    //System.out.println("exp_type is" + exp_type);
                                    // System.out.println("type is" + type);
                                    if(!type.equals(exp_type) && !exp_type.equals("wrong exp")){
                                        System.err.println("Error type 8 at Line " + line + ": " + "return type is not match");
                                    }
                                }

                                if(temmp.exp().INT() != null){
                                    if(!type.equals("int")){
                                        // System.out.println("这个函数的type是" + type);
                                        System.err.println("Error type 8: INT return type is not match");
                                    }
                                }
                                else if(temmp.exp().FLOAT() != null){
                                    if(!type.equals("float")){
                                        System.err.println("Error type 8: FLOAT return type is not match");
                                    }
                                }
                                else if(temmp.exp().CHAR() != null){
                                    if(!type.equals("char")){
                                        System.err.println("Error type 8: CHAR return type is not match");
                                    }
                                }
                            }
                        }
                        else if(temp_list.get(i).stmt()!=null){
                            // System.out.println(temp_list.size() + " " + i);
                            List<splParser.StmtContext> temp_mul_list = temp_list.get(i).stmt();
                            // 对于这里面每一个stmt
                            parse_temp_mul_list(temp_mul_list, type);
                        }
                    }
                }
            }

            if(ctx.funDec()!=null){

                String funName = ctx.funDec().ID().getText();
                // String varName = ctx.ID().getText();
                // 检查变量是否已在符号表中
                int line = ctx.funDec().ID().getSymbol().getLine();
                if (FunctionTable.containsKey(funName)) {
                    System.err.println("Error type 4 at Line " + line + ": Function " + funName + " is redefined.");
                } else {
                    // System.out.println(varName+" variable");
                    FunctionTable.put(funName, type);
                    // System.out.println("find!" + funName + " " + type);
                }
            }
            //System.out.println("find type!" + type);
        }
        symbolTable.clear();
    }

    public void parse_mul_stmtlist(splParser.StmtListContext temp, String type){
        if (temp != null){
            // System.out.println("IN return1111");
            if(temp.stmt() != null && temp.stmt().RETURN() != null){
                // 如果返回的是一个ID
                //System.out.println("IN return1");
                if (temp.stmt().exp().ID() != null && temp.stmt().exp().LP() == null){
                    // System.out.println("IN return ID");
                    String stmtlist_name = temp.stmt().exp().ID().getText();
                    String return_type = symbolTable.get(stmtlist_name);
                    // System.out.println("IN return" + stmtlist_name);
                    if(!return_type.equals(type)){
                        int line = temp.stmt().exp().ID().getSymbol().getLine();
                        System.err.println("Error type 8 at Line " + line + ": " + "return type is not match");
                    }
                }
                // 如果返回的就是一个函数
                else if(temp.stmt().exp().LP() != null){
                    //System.out.println("IN return function");
                    String fun_Name = temp.stmt().exp().ID().getText();
                    String fun_type = FunctionTable.get(fun_Name);
                    // System.out.println("IN return function" + fun_Name + " " + fun_type);
                    if(!fun_type.equals(type)){
                        int line =  temp.stmt().exp().ID().getSymbol().getLine();
                        System.err.println("Error type 8 at Line " + line + ": " + "return type is not match");
                    }
                }
                else{
                    // System.out.println("IN return");
                    //如果返回的是一个exp
                    if(temp.stmt().exp()!=null){
                        String exp_type = expTypesMap.get(temp.stmt().exp());
                        // System.out.println("exp_type is" + exp_type);
                        if(!type.equals(exp_type) && !exp_type.equals("wrong exp")){
                            int line = temp.stmt().exp().getStart().getLine();
                            System.err.println("Error type 8 at Line " + line + ": return type is not match");
                        }
                    }
                    else if(temp.stmt().exp().INT() != null){
                        if(!type.equals("int")){
                            // System.out.println("这个函数的type是" + type);
                            int line = temp.stmt().exp().INT().getSymbol().getLine();
                            System.err.println("Error type 8 at Line " + line + ": return type is not match");
                        }
                    }
                    else if(temp.stmt().exp().FLOAT() != null){
                        if(!type.equals("float")){
                            int line = temp.stmt().exp().FLOAT().getSymbol().getLine();
                            System.err.println("Error type 8 at Line " + line + ": return type is not match");
                        }
                    }
                    else if(temp.stmt().exp().CHAR() != null){
                        if(!type.equals("char")){
                            int line = temp.stmt().exp().CHAR().getSymbol().getLine();
                            System.err.println("Error type 8 at Line " + line + ": return type is not match");
                        }
                    }
                }
            }
            temp = temp.stmtList();
            parse_mul_stmtlist(temp, type);
        }
    }

    public void parse_temp_mul_list(List<splParser.StmtContext> temp_mul_list, String type){
        for(int j=0;j<temp_mul_list.size();j++){
            if(temp_mul_list.get(j).compSt() != null){
                splParser.StmtContext temmp_mul = temp_mul_list.get(j).compSt().stmtList().stmt();
                if (temmp_mul.exp().ID() != null && temmp_mul.exp().LP() == null){
                    // System.out.println("IN return ID");
                    String stmtlist_name = temmp_mul.exp().ID().getText();
                    String return_type = symbolTable.get(stmtlist_name);
                    // System.out.println("IN return" + stmtlist_name);
                    if(!return_type.equals(type)){
                        int line = temmp_mul.exp().ID().getSymbol().getLine();
                        System.err.println("Error type 8 at Line " + line + ": " + "return type is not match");
                    }
                }
                // 如果返回的就是一个函数
                else if(temmp_mul.exp().LP() != null){
                    // System.out.println("IN return function");
                    String fun_Name = temmp_mul.exp().ID().getText();
                    String fun_type = FunctionTable.get(fun_Name);
                    // System.out.println("IN return function" + fun_Name + " " + fun_type);
                    if(fun_type!=null && !fun_type.equals(type)){
                        int line = temmp_mul.exp().ID().getSymbol().getLine();
                        System.err.println("Error type 8 at Line " + line + ": " + " return type is not match");
                    }
                }
                else if(temmp_mul.RETURN()!=null){
                    // System.out.println("IN return");
                    //如果返回的是整数类型...
                    if(temmp_mul.exp()!=null){
                        String exp_type = expTypesMap.get(temmp_mul.exp());
                        // System.out.println("exp_type is" + exp_type);
                        if(!type.equals(exp_type) && !exp_type.equals("wrong exp")){
                            int line = temmp_mul.exp().getStart().getLine();
                            System.err.println("Error type 8 at Line " + line + ": return type is not match");
                        }
                    }

                    else if(temmp_mul.exp().INT() != null){
                        if(!type.equals("int")){
                            // System.out.println("这个函数的type是" + type);
                            int line =  temmp_mul.exp().INT().getSymbol().getLine();
                            System.err.println("Error type 8 at Line " + line + ": return type is not match");
                        }
                    }
                    else if(temmp_mul.exp().FLOAT() != null){
                        if(!type.equals("float")){
                            int line = temmp_mul.exp().FLOAT().getSymbol().getLine();
                            System.err.println("Error type 8 at Line " + line + ": return type is not match");
                        }
                    }
                    else if(temmp_mul.exp().CHAR() != null){
                        if(!type.equals("char")){
                            int line = temmp_mul.exp().CHAR().getSymbol().getLine();
                            System.err.println("Error type 8 at Line " + line + ": return type is not match");
                        }
                    }
                }
            }
            else if(temp_mul_list.get(j).stmt()!=null){
                System.out.println(temp_mul_list.size() + " " + j);
                temp_mul_list = temp_mul_list.get(j).stmt();
                // 对于这里面每一个stmt
                parse_temp_mul_list(temp_mul_list, type);
            }
        }
    }


    @Override
    public void exitSpecifier(splParser.SpecifierContext ctx) {
        int line = ctx.getStart().getLine();
        //如果他是一个结构体 而且是处在定义阶段
        if(ctx.structSpecifier() != null && ctx.structSpecifier().LC()!=null){
            // 如果没有定义这个结构体就把他的id加入到结构体名字的map中
            String struct_name = ctx.structSpecifier().ID().getText();
            //System.out.println("into the struct creation" + struct_name);
            if(!structSymbolTables.containsKey(struct_name)){
                structSymbolTables.put(struct_name, new HashMap<>());
                // System.out.println("This struct " + struct_name +" is successfully added");
                splParser.DefListContext temp = ctx.structSpecifier().defList();
                Map<String, String> correspond_symbolTable = structSymbolTables.get(struct_name);
                // 如果能够继续往下寻找decList
                while (temp != null){
                    splParser.DefContext temp_def = temp.def();
                    if(temp_def != null){
                        // 如果是普通的定义
                        if(temp_def.specifier().TYPE()!=null){
                            String type = temp_def.specifier().TYPE().getText();
                            splParser.DecListContext temp_declist = temp_def.decList();
                            String arraySize = "";
                            while(temp_declist != null){
                                splParser.VarDecContext temp_vardec = temp_declist.dec().varDec();
                                while(temp_vardec.ID() == null){
                                    arraySize = "["+ temp_vardec.INT() +"]"+arraySize;
                                    // 声明是数组维度大小必须大于0，在def时已经报错
                                    temp_vardec = temp_vardec.varDec();
                                }
                                String var_Name = temp_vardec.ID().getText();
                                // System.out.println(var_Name + " 's type: " + type+arraySize);
                                // 如果不在结构体这个变量表里面，那么就把它加进去。
                                if(!correspond_symbolTable.containsKey(var_Name)){
                                    //System.out.println(var_Name + "is successfully added to map " + struct_name);
                                    //数组存的type格式如：int[10][100][10000]
                                    correspond_symbolTable.put(var_Name, type+arraySize);
                                }
                                else{
                                    //这里不需要报错，因为在def时候已经报错了
                                    // System.err.println("Error type 3: varible "+var_Name+ " is already defined in the same scope!");
                                }
                                temp_declist = temp_declist.decList();
                            }
                        }
                        // 如果结构体里面还有结构体
                        else{
                            String sub_struct_name = temp_def.specifier().structSpecifier().ID().getText();
                            //System.out.println("in the struct" + sub_struct_name);
                            // 如果结构体表里面有这个结构体,那么把这个结构体按照既定的格式加到大结构体的符号表中
                            String name = temp_def.decList().dec().varDec().ID().getText();
                            //System.out.println("in the struct" + name);
                            if(structSymbolTables.containsKey(sub_struct_name)){
                                correspond_symbolTable.put(name, sub_struct_name);
                            }
                        }
                    }
                    temp = temp.defList();
                }
            }
            else{
                System.err.println("Error type 15 at Line " + line + ": This struct "+ struct_name + " is redefined as same structure type " );
            }
        }
    }

    @Override
    public void exitExp(splParser.ExpContext ctx) {
        int line = ctx.getStart().getLine();
        if (ctx.INT() != null) {
            expTypesMap.put(ctx, "int");
            return;
        } else if (ctx.FLOAT() != null) {
            expTypesMap.put(ctx, "float");
            return;
        } else if (ctx.CHAR() != null) {
            expTypesMap.put(ctx, "char");
            return;
        }
        // check if the types of child exps are matched
        if (ctx.exp() != null) {
            if (ctx.ASSIGN() != null) {
                if (!expTypesMap.get(ctx.exp(0)).equals(expTypesMap.get(ctx.exp(1)))) {
                    if (!expTypesMap.get(ctx.exp(0)).equals("wrong exp") &&
                            !expTypesMap.get(ctx.exp(1)).equals("wrong exp")) {
                        
                    }
                    System.err.println("Error type 5 at Line " + line + ": unmatched type for on both sides of assignment for " + ctx.getText());
                    expTypesMap.put(ctx, "wrong exp");
                }
                else if (expTypesMap.get(ctx.exp(0)).equals("wrong exp")) {
                    // System.err.println("Error type 5 at Line " + line + ": unmatched type for on both sides of assignment for " + ctx.getText());
                    expTypesMap.put(ctx, "wrong exp");
                }
                else {
                    expTypesMap.put(ctx, expTypesMap.get(ctx.exp(0)));
                }
                if ((ctx.exp(0).ID() == null && ctx.exp(0).LB() == null) ||
                        (ctx.exp(0).ID() != null && ctx.exp(0).LP() != null)) {
                    System.err.println("Error type 6 at Line " + line + ": rvalue appears on the left-side of assignment");
                    expTypesMap.put(ctx, "wrong exp");
                }
            } else if (ctx.exp(1) != null) {
                // +, -, *, /, !=, ==, >, >=, <, <=, &&, ||, exp[exp]
                if (ctx.LB() == null) {
                    // exp[exp] is handled below
                    if (!expTypesMap.get(ctx.exp(0)).equals(expTypesMap.get(ctx.exp(1)))) {
                        if (!expTypesMap.get(ctx.exp(0)).equals("wrong exp") &&
                                !expTypesMap.get(ctx.exp(1)).equals("wrong exp")) {
                            System.err.println("Error type 7 at Line " + line + ": binary operation on unmatched variables in " + ctx.getText());
                        }
                        expTypesMap.put(ctx, "wrong exp");
                    }
                    else if (expTypesMap.get(ctx.exp(0)).equals("wrong exp")) {
//                        System.err.println("Error type 7 at Line " + line + ": binary operation on unmatched variables in " + ctx.getText());
                        expTypesMap.put(ctx, "wrong exp");
                    }
                    else if (!expTypesMap.get(ctx.exp(0)).equals("int") && !expTypesMap.get(ctx.exp(0)).equals("float")) {
                        System.err.println("Error type 7 at Line " + line + ": " + expTypesMap.get(ctx.exp(0)) + " cannot be calculated");
                        expTypesMap.put(ctx, "wrong exp");
                    } else {
                        // &&, ||
                        if (ctx.AND() != null || ctx.OR() != null) {
                            if (!expTypesMap.get(ctx.exp(0)).equals("int")) {
                                System.err.println("Error type 7 at Line " + line + ": float cannot do boolean operations");
                                expTypesMap.put(ctx, "wrong exp");
                            } else {
                                // correct ctx
                                expTypesMap.put(ctx, expTypesMap.get(ctx.exp(0)));
                            }
                        } else {
                            // correct ctx
                            expTypesMap.put(ctx, expTypesMap.get(ctx.exp(0)));
                        }
                    }
                }
            } else {
                // (exp), !exp, -exp
                if (ctx.NOT() != null) {
                    if (!expTypesMap.get(ctx.exp(0)).equals("int")) {
                        System.err.println("Error type 7 at Line " + line + ": float cannot do boolean operations");
                        expTypesMap.put(ctx, "wrong exp");
                    } else {
                        expTypesMap.put(ctx, expTypesMap.get(ctx.exp(0)));
                    }
                } else if (ctx.MINUS() != null) {
                    if (!expTypesMap.get(ctx.exp(0)).equals("int") && !expTypesMap.get(ctx.exp(0)).equals("float")) {
                        System.err.println("Error type 7 at Line " + line + ": " + expTypesMap.get(ctx.exp(0)) + " cannot be calculated");
                        expTypesMap.put(ctx, "wrong exp");
                    }
                    else {
                        expTypesMap.put(ctx, expTypesMap.get(ctx.exp(0)));
                    }
                } else {
                    // correct ctx
                    expTypesMap.put(ctx, expTypesMap.get(ctx.exp(0)));
                }
            }
        }
        if (ctx.ID() != null) {
            // 函数检查 (type 2)
            if(ctx.LP() != null) {
                String fun_Name = ctx.ID().getText();
                //System.out.println("in a function judge");
                if(!FunctionTable.containsKey(fun_Name)){
                    if(symbolTable.containsKey(fun_Name)){
                        System.err.println("Error type 11 at Line " + line + ": Applying function invocation operator on non-function name " + fun_Name + ".");
                        expTypesMap.put(ctx, "wrong exp");
                    }else{
                        System.err.println("Error type 2 at Line " + line + ": "+fun_Name +" is invoked without a definition");
                        expTypesMap.put(ctx, "wrong exp");
                    }
                } else {
                    // check whether the args of function are correct
                    List<String> argsTypes = funcArgsTypeMap.get(fun_Name);
                    if (ctx.args() == null) {
                        if (!argsTypes.isEmpty()) {
                            System.err.println("Error type 9 at Line " + line + ": function \"" + fun_Name + "\"’s arguments expect arguments, but got none");
                        }
                    } else {
                        splParser.ArgsContext tempArgs = ctx.args();
                        int cnt = 0;
                        boolean correctMatched = true;
                        for (int i = 0; i < argsTypes.size(); i++) {
                            if (expTypesMap.get(tempArgs.exp())!=null &&
                                    (expTypesMap.get(tempArgs.exp()).equals(argsTypes.get(i)) ||
                                            expTypesMap.get(tempArgs.exp()).equals("wrong exp"))) {
                                cnt++;
                                if (tempArgs.args() != null && i != argsTypes.size()-1) {
                                    tempArgs = tempArgs.args();
                                } else {
                                    break;
                                }
                            } else {
                                System.err.println("Error type 9 at Line " + line + ": function \"" + fun_Name + "\"’s arguments mismatch the declared parameters 2");
                                correctMatched = false;
                                break;
                            }
                        }
                        if (correctMatched && !(cnt == argsTypes.size() && tempArgs.args() == null)) {
                            System.err.println("Error type 9 at Line " + line + ": function \"" + fun_Name + "\"’s arguments mismatch the declared parameters");
                        }
                    }
                    // value is the return type of the function
                    expTypesMap.put(ctx, FunctionTable.get(fun_Name));
                }
            }
            // 普通检查（type 1）
            else{
                String var_Name = ctx.ID().getText();
                // 检查结构体变量是否已经定义
                if(ctx.DOT() != null){
                    // System.out.println(var_Name + "111");
                    // splParser.ExpContext firstExp = ctx.exp().get(0);
                    if(ctx.ID() != null) {
                        String id_name = ctx.ID().getText();
                        // System.out.println("id name is " + id_name);
                        splParser.ExpContext temp = ctx;
                        String temp_name = id_name;
                        if(!symbolTable.containsKey(id_name)){
                            String symbol_type = findInStruct(id_name, ctx, line);
                            if (!symbol_type.isEmpty()) {
                                expTypesMap.put(ctx, symbol_type);
                            } else {
                                expTypesMap.put(ctx, "wrong exp");
                            }
                        }

                        // while(!symbolTable.containsKey(temp_name)){
                        //     if(temp.exp(0)==null || temp.DOT() == null){
                        //         System.err.println("Error type 13: accessing members of a non-structure variable.");
                        //         break;
                        //     }
                        //     temp = temp.exp(0);
                        //     String father_name = temp.ID().getText();
                        //     System.out.println(father_name+" "+temp_name);
                        //     Map<String, String> correspond_symbolTable = structSymbolTables.get(symbolTable.get(father_name));
                        //     if(!correspond_symbolTable.containsKey(temp_name)){
                        //         System.err.println("Error type 13: accessing members of a non-structure variable.");
                        //         break;
                        //     }
                        //     temp_name = father_name;
                        // }
                        // if(!symbolTable.containsKey(struct_name)){
                        //     //System.err.println("Error type 14:" + struct_name + " is not defined yet in the stucture.");
                        // }
                        // else{
                        //     // 找到这个结构体的真实type
                        //     String struct_type = symbolTable.get(struct_name);
                        //     // 找到这个结构体对应的符号表
                        //     Map<String, String> correspond_symbolTable = structSymbolTables.get(struct_type);
                        //     if(!correspond_symbolTable.containsKey(var_Name)){
                        //         System.err.println("Error type 14:" + var_Name + " is not defined yet in the struct " + struct_name + " (accessing an undefined structure member)");
                        //     }
                        // }
                    }
                }
                // 普通检查变量是否已经定义
                else{
                    if (!symbolTable.containsKey(var_Name)) {
                        System.err.println("Error type 1 at Line " + line + ": " + var_Name + " is used before definition.");
                        expTypesMap.put(ctx, "wrong exp");
                    }
                    else {
                        // value is the data type of identifier
                        expTypesMap.put(ctx, symbolTable.get(var_Name));
                    }
                }
            }
        }

        if (ctx.LB() != null && ctx.RB() != null) {
            // 获取数组索引的表达式
            int depth = 1;
            splParser.ExpContext idExp = ctx.exp(0); // 第一个子表达式是数组标识符
            splParser.ExpContext temp_exp = idExp;
            // if(idExp.ID()!=null){
            //     String varType = symbolTable.get(idExp.ID()); // 从符号表中获取变量类型
            // }
            while (temp_exp.ID() == null) {
                depth++;
                if (temp_exp.LB() == null) {
                    // e.g. (arr + 3)[2], (arr[a] + 2)[1]

                    System.err.println("Error type 10 at Line " + line + " : Applying indexing operator on non-array type variables");
                    expTypesMap.put(ctx, "wrong exp");
                    break;
                }
                if (expTypesMap.get(temp_exp) != null && expTypesMap.get(temp_exp).equals("wrong exp")) {
                    // temp_exp already has error, that is, exp1 of exp1[exp2] is already illegal
                    expTypesMap.put(ctx, "wrong exp");
                    break;
                }
                if (temp_exp.exp(0) == null) {
                    // this is when temp_exp cannot produce exp[exp] (or even (exp + exp), -exp). e.g. temp_exp is 3

                    System.err.println("Error type 10 at Line " + line + " : Applying indexing operator on non-array type variables");
                    expTypesMap.put(ctx, "wrong exp");
                    break;
                }
                // System.out.println(depth);
                temp_exp = temp_exp.exp(0);
            }
            if (temp_exp.ID() != null) {
                String varType = symbolTable.get(temp_exp.ID().getText()); // 从符号表中获取变量类型
                // System.out.println(temp_exp.ID().getText() + " " + varType);
                if(varType==null){
                    varType = findInStruct(temp_exp.ID().getText(), temp_exp, line);
                    if (!varType.isEmpty()) {
                        expTypesMap.put(temp_exp, varType);
                    } else {
                        expTypesMap.put(temp_exp, "wrong exp");
                    }
                }
                int originDimension = 0;
                for (int i = 0; i < varType.length(); i++) {
                    if (varType.charAt(i) == '[') {
                        originDimension++;
                    }
                }
                // System.out.println(depth);
                if (depth > originDimension) {

                    System.err.println("Error type 10 at Line " + line + " : Applying indexing operator on non-array type variables");
                    expTypesMap.put(ctx, "wrong exp");
                } else {
                    int cnt = 0;
                    int head;
                    int tail;
                    for (head = 0; head < varType.length(); head++) {
                        if (varType.charAt(head) == '[') {
                            break;
                        }
                    }
                    for (tail = 0; tail < varType.length(); tail++) {
                        if (varType.charAt(tail) == ']') {
                            cnt++;
                        }
                        if (cnt == depth) {
                            break;
                        }
                    }
                    String expType = varType.substring(0, head) + varType.substring(tail + 1);
                    expTypesMap.put(ctx, expType);
                }
            }

            splParser.ExpContext indexExp = ctx.exp(1); // 第二个子表达式是数组索引

            // 判断索引表达式的类型是否是整数
            if (indexExp != null && !expTypesMap.get(indexExp).equals("int")) {

                System.err.println("Error type 12 at Line " + line + " : Array index is not an integer");
                expTypesMap.put(ctx, "wrong exp");
            }
        }
    }

    private String findInStruct(String id_name, splParser.ExpContext exp_ctx, int line) {
        String struct_name = "";
        splParser.ExpContext father_exp;
        String father_name;
        if(exp_ctx.exp(0)==null || exp_ctx.DOT() == null){
            // splParser.ExpContext idExp = exp_Ctx.exp(0); // id部分
            // splParser.ExpContext temp_exp = idExp;
            // while(temp_exp.ID()==null){
            //     if(temp_exp.exp(0)==null){
            //         //实际上在语法检查后不会出现这种情况 只是写的时候为了防止可能的死循环
            //         System.err.println("Error type 10 : Applying indexing operator on non-array type variables");
            //         break;
            //     }
            //     temp_exp = temp_exp.exp(0);
            // }
            // if(temp_exp.ID()!=null){
            //     String varType = symbolTable.get(temp_exp.ID().getText()); // 从符号表中获取变量类型
            //     struct_name = input.replaceAll("[^a-zA-Z]", "");
            // }
            System.err.println("Error type 13 at Line " + line + ": accessing members of a non-structure variable.");
            return "";
        }else{
            father_exp = exp_ctx.exp(0);
            while(father_exp.ID()==null && father_exp.RB()!= null){
                father_exp = father_exp.exp(0);
            }
            father_name = father_exp.ID().getText();
        }

        // System.out.println(father_name+" "+id_name);

        if(!symbolTable.containsKey(father_name)){
            struct_name = findInStruct(father_name, father_exp, line);
        }else{
            struct_name = symbolTable.get(father_name);
        }
        struct_name = struct_name.replaceAll("[^a-zA-Z]", "");
        Map<String, String> correspond_symbolTable = structSymbolTables.get(struct_name);
        if(struct_name.equals("")){
            return "";
        }
        if(correspond_symbolTable == null){
            System.err.println("Error type 13 at Line " + line + ": accessing members of a non-structure variable "+father_name+".");
            return "";
        }else if(!correspond_symbolTable.containsKey(id_name)){
            System.err.println("Error type 14 at Line " + line + ": accessing an undefined structure member.");
            return "";
        }else{
            return correspond_symbolTable.get(id_name);
        }
    }


// 判断给定表达式是否是整数类型
//private boolean isIntegerType(splParser.ExpContext expCtx) {
//    // 简单的类型判断，可以根据上下文分析扩展
//    if (expCtx.INT() != null) {// 索引是一个整数字面值
//        return true;
//    }else if (expCtx.ID() != null && expCtx.LP() == null && expCtx.RP() == null) {// 索引是一个变量
//        String varName = expCtx.ID().getText();
//        String varType = symbolTable.get(varName); // 从符号表中获取变量类型
//        return "int".equals(varType);
//    }else if (expCtx.LB() != null && expCtx.RB() != null) {// 索引是一个数组
//        splParser.ExpContext idExp = expCtx.exp(0); // id部分
//        splParser.ExpContext temp_exp = idExp;
//        while(temp_exp.ID()==null){
//            if(temp_exp.exp(0)==null){
//                //实际上在语法检查后不会出现这种情况 只是写的时候为了防止可能的死循环
//                System.err.println("Error type 10 : Applying indexing operator on non-array type variables");
//                break;
//            }
//            temp_exp = temp_exp.exp(0);
//        }
//        if(temp_exp.ID()!=null){
//            String varType = symbolTable.get(temp_exp.ID().getText()); // 从符号表中获取变量类型
//            if(varType.startsWith("int")){
//                return true;
//            }else{
//                return false;
//            }
//        }
//    }else if (expCtx.ID() != null && expCtx.LP() != null && expCtx.RP() != null) { // 索引是一个调用
//        String funcName = expCtx.ID().getText();
//        // System.out.println("?");
//        if (FunctionTable.containsKey(funcName)) {
//            String returnType = FunctionTable.get(funcName); // 从函数表中获取返回值类型
//            // System.out.println(returnType);
//            return "int".equals(returnType); // 如果返回值是 "int"，则为整数类型
//        }
//    }else if (expCtx.exp().size() > 0) { // 索引是一个数学表达式
//        boolean isAllInteger = true;
//        for (splParser.ExpContext subExp : expCtx.exp()) {
//            if (!isIntegerType(subExp)) {
//                isAllInteger = false;
//                break;
//            }
//        }
//        // 如果所有子表达式都是整数类型，数学表达式也是整数类型
//        return isAllInteger;
//    }
//    return false; // 其他情况默认为非整数类型
//}



// @Override
// public void exitVarDec(splParser.VarDecContext ctx) {
//     splParser.VarDecContext temp = ctx;
//     String arraySize = "";
//     while (temp.ID() == null) {
//         arraySize = arraySize + "["+ temp.INT() +"]"
//         temp = temp.varDec();
//         // System.out.println("exitVarDec");
//         // String varName = ctx.ID().getText();
//         // // 检查变量是否已经定义
//         // if (!symbolTable.containsKey(varName) && !FunctionTable.containsKey(varName)) {
//         //     System.err.println("Error: Variable " + varName + " is used before definition.");
//         // }
//     }
//     String varName = temp.ID().getText();
//     if (!symbolTable.containsKey(varName) && !FunctionTable.containsKey(varName)) {
//         System.err.println("Error: Variable " + varName + " is used before definition.");
//     }
// }


// @Override
// public void exitDec(splParser.DecContext ctx) {
//     if (ctx.ID() != null) {
//         String varName = ctx.ID().getText();
//         // 检查变量是否已经定义
//         if (!symbolTable.containsKey(varName) && !FunctionTable.containsKey(varName)) {
//             System.err.println("Error: Variable " + varName + " is used before definition.");
//         }
//     }
// }

}
