package SPL;

import java.util.HashMap;
import java.util.Map;
import SPL.splBaseListener;
import SPL.splParser;
import java.util.List;



public class SemanticAnalyzer extends splBaseListener {
    // 对应symbol_id -> type,如果是结构体的定义按照这个结构symbol_id -> struct_name(symbolTable.put(varName_list, struct_name))
    private Map<String, String> symbolTable = new HashMap<>();
    private Map<String, String> FunctionTable = new HashMap<>();
    // 存储结构体名字和对应的符号表
    private Map<String, Map<String, String>> structSymbolTables = new HashMap<>();


@Override
public void exitParamDec(splParser.ParamDecContext ctx) {
    // 如果是普通变量声明
    if (ctx.specifier().TYPE() != null) {
        String type = ctx.specifier().TYPE().getText();
        String varName = ctx.varDec().ID().getText();
        // 检查变量是否已在符号表中
        if (symbolTable.containsKey(varName)) {
            System.err.println("Error type 3: Variable " + varName + " is already declared in the same parameter scope.");
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
                    System.err.println("Error type 12: The dimension size must be greater than 0 when declaring an array.");
                }
                temp_vardec = temp_vardec.varDec();
            }
            String varName_list = temp_vardec.ID().getText();
            // System.out.println(varName_list + " 's type: " + type+arraySize);
            if (symbolTable.containsKey(varName_list)) {
                System.err.println("Error type 3: Variable " + varName_list + " is already declared in the same scope.");
            } else {
                // System.out.println("get varible" + varName_list + " in def list"+ type);
                // 这里对应的应该写type和value
                symbolTable.put(varName_list, type + arraySize);    
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
                    System.err.println("Error type 12: The dimension size must be greater than 0 when declaring an array.");
                }
                temp_vardec = temp_vardec.varDec();
            }
            String varName_list = temp_vardec.ID().getText();
            if (symbolTable.containsKey(varName_list)) {
                System.err.println("Error type 3: Variable " + varName_list + " is already declared in the same scope.");
            } else {
                // System.out.println("get varible" + varName_list + " in def list"+ type);
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
        splParser.StmtListContext temp = ctx.compSt().stmtList();
        // System.out.println("type is"+ type);
        if (temp != null){
            // System.out.println("IN return1111");
            if(temp.stmt() != null && temp.stmt().RETURN() != null){
                // 如果返回的是一个ID
                // System.out.println("IN return1");
                if (temp.stmt().exp().ID() != null && temp.stmt().exp().LP() == null){
                    // System.out.println("IN return ID");
                    String stmtlist_name = temp.stmt().exp().ID().getText();
                    String return_type = symbolTable.get(stmtlist_name);
                    // System.out.println("IN return" + stmtlist_name);
                    if(!return_type.equals(type)){
                        System.err.println("Error type 8: " + stmtlist_name + " " + return_type + " is not match");
                    }
                }
                // 如果返回的就是一个函数
                else if(temp.stmt().exp().LP() != null){
                    //System.out.println("IN return function");
                    String fun_Name = temp.stmt().exp().ID().getText();
                    String fun_type = FunctionTable.get(fun_Name);
                    // System.out.println("IN return function" + fun_Name + " " + fun_type);
                    if(!fun_type.equals(type)){
                        System.err.println("Error type 8: " + fun_Name + " return type" + fun_type + " is not match");
                    }
                }
                else{
                    // System.out.println("IN return");
                    //如果返回的是整数类型...
                    if(temp.stmt().exp().INT() != null){
                        if(!type.equals("int")){
                            // System.out.println("这个函数的type是" + type);
                            System.err.println("Error type 8: return type is not match");
                        }
                    }
                    else if(temp.stmt().exp().FLOAT() != null){
                        if(!type.equals("float")){
                            System.err.println("Error type 8: return type is not match");
                        }
                    }
                    else{
                        if(!type.equals("chat")){
                            System.err.println("Error type 8: return type is not match");
                        }
                    }
                }
            }
        }

        // 多个stmt 对return的判断
        List<splParser.StmtContext> temp_list = temp.stmt().stmt();
        if(!temp_list.isEmpty()){
            //System.out.println("不是空的");
            // System.out.println("length is " + temp_list.size());
            //遍历每一个stmt
            for(int i = 1;i<temp_list.size();i++){
                splParser.StmtContext temmp = temp_list.get(i).compSt().stmtList().stmt();
                if (temmp.exp().ID() != null && temmp.exp().LP() == null){
                    // System.out.println("IN return ID");
                    String stmtlist_name = temmp.exp().ID().getText();
                    String return_type = symbolTable.get(stmtlist_name);
                    // System.out.println("IN return" + stmtlist_name);
                    if(!return_type.equals(type)){
                        System.err.println("Error type 8: " + stmtlist_name + " " + return_type + " is not match");
                    }
                }
                // 如果返回的就是一个函数
                else if(temmp.exp().LP() != null){
                    // System.out.println("IN return function");
                    String fun_Name = temmp.exp().ID().getText();
                    String fun_type = FunctionTable.get(fun_Name);
                    // System.out.println("IN return function" + fun_Name + " " + fun_type);
                    if(!fun_type.equals(type)){
                        System.err.println("Error type 8: " + fun_Name + " return type" + fun_type + " is not match");
                    }
                }
                else{
                    // System.out.println("IN return");
                    //如果返回的是整数类型...
                    if(temmp.exp().INT() != null){
                        if(!type.equals("int")){
                            // System.out.println("这个函数的type是" + type);
                            System.err.println("Error type 8: return type is not match");
                        }
                    }
                    else if(temmp.exp().FLOAT() != null){
                        if(!type.equals("float")){
                            System.err.println("Error type 8: return type is not match");
                        }
                    }
                    else{
                        if(!type.equals("chat")){
                            System.err.println("Error type 8: return type is not match");
                        }
                    }
                }
            }

        }

        String funName = ctx.funDec().ID().getText();
        // String varName = ctx.ID().getText();
        // 检查变量是否已在符号表中
        if (FunctionTable.containsKey(funName)) {
            System.err.println("Error type 4: Function " + funName + " is redefined.");
        } else {
            // System.out.println(varName+" variable");
            FunctionTable.put(funName, type);
            // System.out.println("find!" + funName + " " + type);
        }
        //System.out.println("find type!" + type);
    }
    symbolTable.clear();
}

@Override
public void exitSpecifier(splParser.SpecifierContext ctx) {
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
            // 如果能够继续往下寻找declist
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
                            correspond_symbolTable.put(name, sub_struct_name );
                        }
                    }
                }
                temp = temp.defList();
            }
        }
        else{
            System.err.println("Error type 15: This struct "+ struct_name + " is redefined as same structure type " );
        }
    }
}


@Override
public void exitExp(splParser.ExpContext ctx) {
    if (ctx.ID() != null) {
        // 函数检查 (type 2)
        if(ctx.LP() != null) {
            String fun_Name = ctx.ID().getText();
            //System.out.println("in a function judge");
            if(!FunctionTable.containsKey(fun_Name)){
                System.err.println("Error type 2: "+fun_Name +" is invoked without a definition");
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
                        findInStruct(id_name, ctx);
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
                    System.err.println("Error type 1: " + var_Name + " is used before definition.");
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
        while(temp_exp.ID()==null){
            depth++;
            if(temp_exp.exp(0)==null){
                //实际上在语法检查后不会出现这种情况 只是写的时候为了防止可能的死循环
                System.err.println("Error type 10 : Applying indexing operator on non-array type variables");
                break;
            }
            // System.out.println(depth);
            temp_exp = temp_exp.exp(0);
        }
        if(temp_exp.ID()!=null){
            
            String varType = symbolTable.get(temp_exp.ID().getText()); // 从符号表中获取变量类型
            // System.out.println(temp_exp.ID().getText() + " " + varType);
            for (int i = 0; i < varType.length(); i++) {
                if (varType.charAt(i) == '[') {
                    depth--;
                }
            }
            // System.out.println(depth);
            if(depth>0){
                System.err.println("Error type 10 : Applying indexing operator on non-array type variables");
            }
        }

        splParser.ExpContext indexExp = ctx.exp(1); // 第二个子表达式是数组索引
        
        // 判断索引表达式的类型是否是整数
        if (indexExp != null && !isIntegerType(indexExp)) {
            System.err.println("Error type 12 : Array index is not an integer");
        }
    }
}

private String findInStruct(String id_name, splParser.ExpContext exp_ctx) {
    if(exp_ctx.exp(0)==null || exp_ctx.DOT() == null){
        System.err.println("Error type 13: accessing members of a non-structure variable.");
    }
    splParser.ExpContext father_exp = exp_ctx.exp(0);
    String father_name = father_exp.ID().getText();
    // System.out.println(father_name+" "+id_name);
    String struct_name = "";
    if(!symbolTable.containsKey(father_name)){
        struct_name = findInStruct(father_name, father_exp);
    }else{
        struct_name = symbolTable.get(father_name);
    }
    Map<String, String> correspond_symbolTable = structSymbolTables.get(struct_name);
    if(struct_name.equals("")){
        return "";
    }
    if(correspond_symbolTable == null){
        System.err.println("Error type 13: accessing members of a non-structure variable "+father_name+".");
        return "";
    }else if(!correspond_symbolTable.containsKey(id_name)){
        System.err.println("Error type 14: accessing an undefined structure member.");
        return "";
    }else{
        return correspond_symbolTable.get(id_name);
    }
}


// 判断给定表达式是否是整数类型
private boolean isIntegerType(splParser.ExpContext expCtx) {
    // 简单的类型判断，可以根据上下文分析扩展
    if (expCtx.INT() != null) {// 索引是一个整数字面值
        return true; 
    }else if (expCtx.ID() != null && expCtx.LP() == null && expCtx.RP() == null) {// 索引是一个变量
        String varName = expCtx.ID().getText();
        String varType = symbolTable.get(varName); // 从符号表中获取变量类型
        return "int".equals(varType); 
    }else if (expCtx.LB() != null && expCtx.RB() != null) {// 索引是一个数组
        splParser.ExpContext idExp = expCtx.exp(0); // 索引部分
        splParser.ExpContext temp_exp = idExp;
        while(temp_exp.ID()==null){
            if(temp_exp.exp(0)==null){
                //实际上在语法检查后不会出现这种情况 只是写的时候为了防止可能的死循环
                System.err.println("Error type 10 : Applying indexing operator on non-array type variables");
                break;
            }
            temp_exp = temp_exp.exp(0);
        }
        if(temp_exp.ID()!=null){
            String varType = symbolTable.get(temp_exp.ID().getText()); // 从符号表中获取变量类型
            if(varType.startsWith("int")){
                return true;
            }else{
                return false;
            }
        }
    }else if (expCtx.ID() != null && expCtx.LP() != null && expCtx.RP() != null) { // 索引是一个调用
        String funcName = expCtx.ID().getText();
        // System.out.println("?");
        if (FunctionTable.containsKey(funcName)) {
            String returnType = FunctionTable.get(funcName); // 从函数表中获取返回值类型
            // System.out.println(returnType);
            return "int".equals(returnType); // 如果返回值是 "int"，则为整数类型
        }
    }else if (expCtx.exp().size() > 0) { // 索引是一个数学表达式
        boolean isAllInteger = true;
        for (splParser.ExpContext subExp : expCtx.exp()) {
            if (!isIntegerType(subExp)) {
                isAllInteger = false;
                break;
            }
        }
        // 如果所有子表达式都是整数类型，数学表达式也是整数类型
        return isAllInteger;
    }
    return false; // 其他情况默认为非整数类型
}



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
