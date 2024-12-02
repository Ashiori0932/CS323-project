package SPL;

import java.util.HashMap;
import java.util.Map;
import SPL.splBaseListener;
import SPL.splParser;



public class SemanticAnalyzer extends splBaseListener {
    private Map<String, String> symbolTable = new HashMap<>();

@Override
public void exitVarDec(splParser.VarDecContext ctx) {
    // 如果是普通变量声明
    if (ctx.ID() != null) {
        String varName = ctx.ID().getText();
        // 检查变量是否已在符号表中
        if (symbolTable.containsKey(varName)) {
            System.err.println("Error: Variable " + varName + " is already declared.");
        } else {
            // System.out.println(varName+" variable");
            symbolTable.put(varName, "variable");
        }
    }
    // 如果是数组声明
    // else if (ctx.varDec() != null) {
    //     splParser.VarDecContext ctxTemp = ctx;
    //     String arraySizeString = "";
    //     while(ctxTemp.ID()==null){
    //         if (ctxTemp.LB() != null && ctxTemp.RB() != null) {
    //             String arraySize = ctxTemp.INT().getText();
    //             try {
    //                 int size = Integer.parseInt(arraySize);
    //                 if (size <= 0) {
    //                     System.err.println("Error: Array must have a positive size.");
    //                 } else {
    //                     arraySizeString = arraySizeString + "[" + size +"]";
    //                 }
    //             } catch (NumberFormatException e) {
    //                 System.err.println("Error: Array size is not a valid integer.");
    //             }
    //         }
    //         ctxTemp = ctxTemp.varDec();
    //     }
    //     String varName = ctxTemp.ID().getText();
    //     if (symbolTable.containsKey(varName)) {
    //         System.out.println(varName+" Array" + arraySizeString);
    //         System.err.println("Error: Variable " + varName + " is already declared.");
    //     } else {
    //         symbolTable.put(varName, "Array" + arraySizeString);
    //     }
    // }
}

}
