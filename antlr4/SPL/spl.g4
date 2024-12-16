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
                System.err.println("Error type B at Line " + $specifier.start.getLine() + ": Missing semicolon \';\'");
            }
            | specifier {
                System.err.println("Error type B at Line " + $specifier.start.getLine() + ": Missing semicolon \';\'");
            }
            |  extDecList SEMI{
              System.err.println("Error type B at Line " + $extDecList.start.getLine() + ": Missing specifier");
            }
            ;


extDecList  : varDec
            | varDec COMMA extDecList
            | varDec COMMA {
                System.err.println("Error type B at Line " + $COMMA.line + ": Missing variable name");
            }
            | varDec COMMA COMMA extDecList{  System.err.println("Error type B at Line " + $COMMA.line + ": Unexpected ','");}
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
                System.err.println("Error type B at Line " + $LB.line + ": Missing closing bracket \']\'");
            }
            | INVALID_ID
            ;

funDec      : ID LP varList RP
            | ID LP RP
            | ID LP varList {
                System.err.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis \')\'");
            }
            | ID LP {
                System.err.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis \')\'");
            }
            ;

varList     : paramDec COMMA varList
            | paramDec
            | paramDec COMMA {System.err.println("Error type B at Line " + $COMMA.line + ": Missing variable");}
            ;

paramDec    : specifier varDec
            |  varDec{
                System.err.println("Error type B at Line " + $varDec.start.getLine() + ": Missing specifier");
            }
            | specifier{ 
                System.err.println("Error type B at Line " + $specifier.start.getLine() + ": Missing variable name");
            }

            ;

// Statement
compSt      : LC defList stmtList RC? {
                if ($RC == null) {
                    System.err.println("Error type B at Line " + $LC.line + ": Missing closing curly bracket '}'");
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
                System.err.println("Error type B at Line " + $ELSE.line + ": no 'if' before 'else'");
            }
            | WHILE LP exp RP stmt
            | exp {
                System.err.println("Error type B at Line " + $exp.start.getLine() + ": Missing semicolon \';\'");
            }
            | RETURN exp {
                System.err.println("Error type B at Line " + $RETURN.line + ": Missing semicolon \';\'");
            }
            | IF LP exp stmt {
                System.err.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis \')\'");
            }
            | IF exp RP stmt {
                System.err.println("Error type B at Line " + $RP.line + ": Missing opening parenthesis \'(\'");
            }
            | WHILE LP exp stmt {
                System.err.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis \')\'");
            }
            | WHILE exp RP stmt {
                System.err.println("Error type B at Line " + $RP.line + ": Missing opening parenthesis \'(\'");
            }
            ;

// Local definition
defList     :  | /* empty */
                |def defList

            ;

def         : specifier decList SEMI
            | specifier decList {
                System.err.println("Error type B at Line " + $specifier.start.getLine() + ": Missing semicolon \';\'");
            }
            | decList SEMI  {
                System.err.println("Error type B at Line " + $decList.start.getLine() + ": Missing specifier");
            }
            |    IF LP RP {
                System.err.println("Error type B at Line " + $LP.line + ": Missing specifier");
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
                System.err.println("Error type B at Line " + $MUL.line + ": Missing Exp after *");
                }
            | exp DIV {
                System.err.println("Error type B at Line " + $DIV.line + ": Missing Exp after /");
                }
            | exp PLUS exp
            | exp MINUS exp
            | exp PLUS {
                System.err.println("Error type B at Line " + $PLUS.line + ": Missing Exp after +");
            }
            | exp MINUS {
                System.err.println("Error type B at Line " + $MINUS.line + ": Missing Exp after -");
            }
            | exp ASSIGN exp
            | exp ASSIGN {
                System.err.println("Error type B at Line " + $ASSIGN.line + ": Missing right value");
            }
            | exp NE exp
            | exp EQ exp
            | exp LT exp
            | exp LE exp
            | exp GT exp
            | exp GE exp
            | exp AND exp
            | exp OR exp
            | LP exp RP
            | MINUS exp
            | NOT exp
            | ID LP args RP?{
                if ($RP == null) {
                    System.err.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis ')'");
                }
            }
            | ID LP RP
            | exp LB exp RB
            | exp LB exp {
                System.err.println("Error type B at Line " + $LB.line + ": Missing closing bracket \']\'");
            }
            | exp LB {
                System.err.println("Error type B at Line " + $LB.line + ": Missing closing bracket \']\'");
            }                                       
            | exp DOT ID
            | exp DOT {  System.err.println("Error type B at Line " + $DOT.line + ": Missing field name");}
            | INT
            | FLOAT
            | CHAR
            | ID
            | LP exp {
                System.err.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis \')\'");
            }
            | ID LP args {
                System.err.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis \')\'");
            }
            | ID LP {
                System.err.println("Error type B at Line " + $LP.line + ": Missing closing parenthesis \')\'");
            }
            | INVALID_ID
            | INVALID_EXP
            | exp INVALID_EXP exp
            | INVALID_CHAR
            | ASSIGN exp { 
                System.err.println("Error type B at Line " + $ASSIGN.line + ": Unexpected statement");
            }
            ;

args        : exp COMMA args
            | exp COMMA {
                System.err.println("Error type B at Line " + $COMMA.line + ": Missing arguments");
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

INT: ('0'|[1-9][0-9]*) | ('0x'[1-9a-fA-F][0-9a-fA-F]+);

ID: [a-zA-Z_][a-zA-Z_0-9]*;

FLOAT: ('0'| [1-9][0-9]*) '.' [0-9]+ ;

CHAR: '\'' ( '\\x' CHATTOKEN CHATTOKEN | . ) '\'';
CHATTOKEN: [0-9a-fA-F];

INVALID_CHAR: '\'' .*? '\'' {
    System.err.println("Error type A at Line " + getLine() + ": unknown lexeme " + getText());
};

LINE_COMMENT: '//' ~[\r\n]* -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;

WS: [ \t\r\n]+ -> skip;

INVALID_ID: [0-9][a-zA-Z_0-9.]*{
    System.err.println("Error type A at Line " + getLine() + ": unknown lexeme " + getText());
};

INVALID_EXP: . {
    System.err.println("Error type A at Line " + getLine() + ": unknown lexeme " + getText());
};