grammar spl;

@header {
package SPL;
}

program     : extDefList ;

extDefList  : extDef extDefList
            | /* empty */ ;

extDef      : specifier extDecList SEMI?{
                if ($SEMI == null) {
                    System.out.println("Error type B at Line " + $specifier.start.getLine() + ": Missing semicolon \';\'");
                }
            }
            | specifier SEMI? {
                if ($SEMI == null) {
                    System.out.println("Error type B at Line " + $specifier.start.getLine() + ": Missing semicolon \';\'");
                }
            }
            | specifier funDec compSt
            | extDecList SEMI{
                System.out.println("Error type B at Line " + $extDecList.start.getLine() + ": Missing specifier");
            }
            ;


extDecList  : varDec
            | varDec COMMA extDecList
            | varDec COMMA {
                System.out.println("Error type B at Line " + $COMMA.line + ": Missing variable name");
            }
            | varDec COMMA COMMA extDecList{  
                System.out.println("Error type B at Line " + $COMMA.line + ": Unexpected ','");
            }
            ;

// Specifier
specifier   : TYPE
            | structSpecifier
            ;

structSpecifier : STRUCT ID LC defList RC? {
                    if ($RC == null) {
                        System.out.println("Error type B at Line " + $LC.line + ": Missing closing curly brace");
                    }
               }
               | STRUCT LC defList RC {
                    System.out.println("Error type B at Line " + $STRUCT.line + ": Missing struct name");
               }
               | STRUCT ID
               ;

// Declarator
varDec      : ID
            | varDec LB INT RB? {
                if ($RB == null) {
                    System.out.println("Error type B at Line " + $LB.line + ": Missing closing bracket \']\'");
                }
            }
            | INVALID_ID
            ;

funDec      : ID LP varList RP? {
                if ($RP == null) {
                    System.out.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis \')\'");
                }
            }
            | ID LP RP? {
                if ($RP == null) {
                    System.out.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis \')\'");
                }
            }
            ;

varList     : paramDec COMMA varList
            | paramDec
            | paramDec COMMA {
                System.out.println("Error type B at Line " + $COMMA.line + ": Missing variable");
            }
            ;

paramDec    : specifier varDec
            | varDec {
                System.out.println("Error type B at Line " + $varDec.start.getLine() + ": Missing specifier");
            }
            | specifier{
                System.out.println("Error type B at Line " + $specifier.start.getLine() + ": Missing variable name");
            }

            ;

// Statement
compSt      : LC defList stmtList RC? {
                if ($RC == null) {
                    System.out.println("Error type B at Line " + $LC.line + ": Missing closing curly bracket '}'");
                }
            }

            ;

stmtList    : stmt stmtList
            | /* empty */
            ;

stmt        : exp SEMI
            | compSt
            | RETURN exp SEMI
            | IF LP exp RP stmt ELSE stmt
            | IF LP exp RP stmt
            | ELSE stmt {
                System.out.println("Error type B at Line " + $ELSE.line + ": no 'if' before 'else'");
            }
            | WHILE LP exp RP stmt
            | exp {
                System.out.println("Error type B at Line " + $exp.start.getLine() + ": Missing semicolon \';\'");
            }
            | RETURN exp {
                System.out.println("Error type B at Line " + $RETURN.line + ": Missing semicolon \';\'");
            }
            | IF LP exp stmt {
                System.out.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis \')\'");
            }
            | IF exp RP stmt {
                System.out.println("Error type B at Line " + $RP.line + ": Missing opening parenthesis \'(\'");
            }
            | WHILE LP exp stmt {
                System.out.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis \')\'");
            }
            | WHILE exp RP stmt {
                System.out.println("Error type B at Line " + $RP.line + ": Missing opening parenthesis \'(\'");
            }
            ;

// Local definition
defList     : /* empty */
            | def defList
            ;

def         : specifier decList SEMI? {
                if ($SEMI == null) {
                    System.out.println("Error type B at Line " + $specifier.start.getLine() + ": Missing semicolon \';\'");
                }
            }
            | decList SEMI  {
                System.out.println("Error type B at Line " + $decList.start.getLine() + ": Missing specifier");
            }
            | IF LP RP {
                System.out.println("Error type B at Line " + $LP.line + ": Missing specifier");
            }
            ;


decList     : dec
            | dec COMMA decList
            ;

dec         : varDec ASSIGN exp
            | varDec
            ;

// Expression
exp         :
             exp MUL exp
            | exp DIV exp
            | exp MUL {
                System.out.println("Error type B at Line " + $MUL.line + ": Missing Exp after *");
            }
            | exp DIV {
                System.out.println("Error type B at Line " + $DIV.line + ": Missing Exp after /");
            }
            | exp PLUS exp
            | exp MINUS exp
            | exp PLUS {
                System.out.println("Error type B at Line " + $PLUS.line + ": Missing Exp after +");
            }
            | exp MINUS {
                System.out.println("Error type B at Line " + $MINUS.line + ": Missing Exp after -");
            }
            | MINUS exp
            | PLUS exp
            | exp ASSIGN exp
            | exp ASSIGN {
                System.out.println("Error type B at Line " + $ASSIGN.line + ": Missing right value");
            }
            | exp NE exp
            | exp EQ exp
            | exp LT exp
            | exp LE exp
            | exp GT exp
            | exp GE exp
            | exp AND exp
            | exp OR exp
            | LP exp RP? {
                if ($RP == null) {
                    System.out.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis ')'");
                }
            }
            | NOT exp
            | ID LP args RP? {
                if ($RP == null) {
                    System.out.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis ')'");
                }
            }
            | ID LP RP? {
                if ($RP == null) {
                    System.out.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis ')'");
                }
            }
            | exp LB exp RB? {
                if ($RB == null) {
                    System.out.println("Error type B at Line " + $LB.line + ": Missing closing bracket \']\'");
                }
            }
            | exp LB {
                System.out.println("Error type B at Line " + $LB.line + ": Missing closing bracket \']\'");
            }
            | exp DOT ID?{
                if ($ID == null) {
                    System.out.println("Error type B at Line " + $DOT.line + ": Missing field name");
                }
            }
            | INT
            | FLOAT
            | CHAR
            | ID
            | INVALID_ID
            | INVALID_EXP
            | exp INVALID_EXP exp
            | INVALID_CHAR
            | ASSIGN exp {
                System.out.println("Error type B at Line " + $ASSIGN.line + ": Unexpected statement");
            }
            ;

args        : exp COMMA args
            | exp COMMA {
                System.out.println("Error type B at Line " + $COMMA.line + ": Missing arguments");
            }
            | exp
            ;

TYPE: 'int' | 'float' | 'char';

STRUCT: 'struct';

IF: 'if';
ELSE: 'else';
WHILE: 'while';
RETURN: 'return';

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

INT: ('0'|[1-9][0-9]*) | ('0x0'|'0x'[1-9a-fA-F][0-9a-fA-F]+);

ID: [a-zA-Z_][a-zA-Z_0-9]*;

FLOAT: ('0'| [1-9][0-9]*) '.' [0-9]+ ;

CHAR: '\'' ( '\\x' [0-9a-fA-F] [0-9a-fA-F] | . ) '\'';

INVALID_CHAR: '\'' .*? '\'' {
    System.out.println("Error type A at Line " + getLine() + ": unknown lexeme " + getText());
};

SL_COMMENT: '//' .*? '\n' -> skip;
ML_COMMENT: '/*' .*? '*/' -> skip;

WS: [ \t\r\n]+ -> skip;

INVALID_ID: [0-9][a-zA-Z_0-9.]*{
    System.out.println("Error type A at Line " + getLine() + ": unknown lexeme " + getText());
};

INVALID_EXP: . {
    System.out.println("Error type A at Line " + getLine() + ": unknown lexeme " + getText());
};