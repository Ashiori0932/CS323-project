grammar spl;

@header {
package SPL;
}

program     : extDefList ;

extDefList  : extDef extDefList
            | /* empty */ ;

extDef      : specifier extDecList SEMI
            | specifier SEMI
            | specifier funDec compSt 
            | specifier extDecList {
                System.err.println("Error type B at Line " + $specifier.start.getLine() + ": Missing semicolon");
            }
            | specifier {
                System.err.println("Error type B at Line " + $specifier.start.getLine() + ": Missing semicolon");
            }
            ;


extDecList  : varDec
            | varDec COMMA extDecList 
            ;

// Specifier
specifier   : TYPE
            | structSpecifier 
            ;

structSpecifier : STRUCT ID LC defList RC
               | STRUCT ID 
               | STRUCT ID LC defList {
                   System.err.println("Error type B at Line " + $STRUCT.line + ": Missing closing curly brace");
               }
               ;

// Declarator
varDec      : ID
            | varDec LB INT RB 
            | varDec LB INT {
                System.err.println("Error type B at Line " + $LB.line + ": Missing closing bracket");
            }
            ;

funDec      : ID LP varList RP
            | ID LP RP 
            | ID LP varList {
                System.err.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis");
            }
            | ID LP {
                System.err.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis");
            }
            ;

varList     : paramDec COMMA varList
            | paramDec 
            ;

paramDec    : specifier varDec 
;

// Statement
compSt      : LC defList stmtList RC 
;

stmtList    : stmt stmtList
            | /* empty */ 
            ;

stmt        : exp SEMI
            | compSt
            | RETURN exp SEMI
            | IF LP exp RP stmt
            | IF LP exp RP stmt ELSE stmt
            | WHILE LP exp RP stmt 
            | exp {
                System.err.println("Error type B at Line " + $exp.start.getLine() + ": Missing semicolon");
            }
            | RETURN exp {
                System.err.println("Error type B at Line " + $RETURN.line + ": Missing semicolon");
            }
            | IF LP exp stmt {
                System.err.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis");
            }
            | WHILE LP exp stmt {
                System.err.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis");
            }
            ;

// Local definition
defList     : def defList
            | /* empty */ 
            ;

def         : specifier decList SEMI 
            | specifier decList {
                System.err.println("Error type B at Line " + $specifier.start.getLine() + ": Missing semicolon");
            }
            ;

decList     : dec
            | dec COMMA decList 
            ;

dec         : varDec
            | varDec ASSIGN exp 
            ;

// Expression
exp         : exp ASSIGN exp
            | exp AND exp
            | exp OR exp
            | exp LT exp
            | exp LE exp
            | exp GT exp
            | exp GE exp
            | exp NE exp
            | exp EQ exp
            | exp PLUS exp
            | exp MINUS exp
            | exp MUL exp
            | exp DIV exp
            | LP exp RP
            | MINUS exp
            | NOT exp
            | ID LP args RP
            | ID LP RP
            | exp LB exp RB
            | exp DOT ID
            | ID
            | INT
            | FLOAT
            | CHAR 
            | LP exp {
                System.err.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis");
            }
            | ID LP args {
                System.err.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis");
            }
            | ID LP {
                System.err.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis");
            }
            | exp LB exp {
                System.err.println("Error type B at Line " + $LB.line + ": Missing closing bracket");
            }
            ;

args        : exp COMMA args
            | exp 
            ;

// 类型关键词
TYPE: 'int' | 'float' | 'char';

// 结构体关键词
STRUCT: 'struct';

// 关键字
IF: 'if';
ELSE: 'else';
WHILE: 'while';
RETURN: 'return';

// 运算符和符号
DOT: '.';
SEMI: ';';
COMMA: ',';
ASSIGN: '=';
LT: '<';
LE: '<=';
GT: '>';
GE: '>=';
NE: '!=';
EQ: '==';
PLUS: '+';
MINUS: '-';
MUL: '*';
DIV: '/';
AND: '&&';
OR: '||';
NOT: '!';
LP: '(';
RP: ')';
LB: '[';
RB: ']';
LC: '{';
RC: '}';

// 整数
INT: ('+'|'-')? ( '0' | [1-9][0-9]* ) ;

// 十六进制整数
HEX_INT: '-'? '0' ('x' | 'X') [0-9a-fA-F]+ ;

// 浮点数
FLOAT: '-'? [0-9]+ '.' [0-9]+ ;

// 字符
CHAR: '\'' (ESC_SEQ | ~['\\]) '\'' ;

// 转义序列
fragment ESC_SEQ: '\\' [btnr"'\\] | '\\' 'x' [0-9a-fA-F];

// 标识符
ID: [a-zA-Z_] [a-zA-Z_0-9]* ;

// 忽略空格、换行、制表符
WS: [ \t\r\n]+ -> skip ;

INVALID_EXP: . {
System.err.println("Error type A at Line " + getLine() + ": invalid expression " + getText());
};