%{
    #include "node.hpp"
    #include <stdio.h>
    #include <ctype.h>
    #include <stdlib.h>
    #include "syntax.tab.h"
    
    
    int lines = 1;
    int comment_state = 0;
    int identifiers = 0;
    int has_error = 0;
%}

%{
    /* library inclusions 
    #include "syntax.tab.h"
    int yycolno = 1;
    #define YY_USER_ACTION \
        yylloc.first_line = yylineno; \
        yylloc.first_column = yycolno; \
        yylloc.last_line = yylineno; \
        yylloc.last_column = yycolno + yyleng; \
    yycolno += yyleng;*/
%}


TYPE "int"|"char"|"float"
letter [a-zA-Z]
letter_ {letter}|_
digit [0-9]
pos_digit [1-9]
ID ({letter_})+({letter}|{digit}|_)*

hexdigit [0-9abcdefABCDEF]
hexadecimal [+-]?(0x)({hexdigit}+)
decimal [+-]?(0|{pos_digit}{digit}*)

number_INT {decimal}|{hexadecimal}
number_FLOAT (([1-9]{digit}*)|0).(([1-9]{digit}*)|0)
CHAR \'(\\x{hexdigit}{2}|.)\'
WRONG_ID ((number_INT|{number_FLOAT}){ID})

IF "if"
ELSE "else"
WHILE "while"
RETURN "return"
EQ "=="
NE "!="
AND "&&"
OR "||"


ADD "+" 
SUB "-" 
MUL "*" 
DIV "/" 
SEMI ";"
COMMA ","
ASSIGN "="
LT "<"
GT ">"
BITOP_NOT "!"      
BITOP_XOR "^"       
BITOP_AND "&"       
BITOP_OR "\\|"     
LP "("
RP ")"
LB "["
RB "]"
LC "{"
RC "}"

%x comment

%%


"/*" {
    // 进入注释状态
    comment_state = 1;
    printf("%d Entering comment state\n", comment_state);
    BEGIN(comment);
}


<comment>[^*\n]* {
    // 处理不含 '*' 的内容 
    printf("Comment content (not '*'): %s\n", yytext);
}

<comment>"*"+[^/*\n]* {
    // 处理含 '*' 但不跟随 '/' 的情况 
    printf("Comment content (multiple '*'): %s\n", yytext);
}

<comment>\n {
    // 注释内的行数统计
    ++lines; 
    printf("New line in comment, total lines: %d\n", lines);
}

<comment>"*"+"/" {
    // 退出注释状态
    comment_state = 0;
    printf("%d Exiting comment state\n", comment_state);
    BEGIN(INITIAL);
}

{WRONG_ID} {
    // handle identifier mistake
    has_error = 1;
    printf("line %d: ID ERROR occur: %s\n", lines, yytext);
}

{IF} {printf("line %d: LP %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::LP); return LP;}

{ELSE} {printf("line %d: ELSE %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::ELSE); return ELSE;}

{WHILE} {printf("line %d: WHILE %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::WHILE); return WHILE;}

{RETURN} {printf("line %d: RETURN %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::RETURN); return RETURN;}

{LP} {printf("line %d: LP %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::LP); return LP;}

{RP} {printf("line %d: RP %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::RP); return RP;}

{LB} {printf("line %d: LB %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::LB); return LB;}

{RB} {printf("line %d: RB %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::RB); return RB;}

{LC} {printf("line %d: LC %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::LC); return LC;}

{RC} {printf("line %d: RC %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::RC); return RC;}

{ASSIGN} {printf("line %d: ASSIGN %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::ASSIGN); return ASSIGN;}

{COMMA} {printf("line %d: COMMA %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::COMMA); return COMMA;}

{LT} {printf("line %d: LT %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::LT); return LT;}

{GT} {printf("line %d: GT %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::GT); return GT;}

{SEMI} {printf("line %d: SEMI %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::SEMI); return SEMI;}


{BITOP_NOT} {
    printf("line %d: BITOP %s\n", lines, yytext);
    yylval.NODE = new Node(Node_TYPE::BITOP_NOT);
    return BITOP_NOT;
}

{BITOP_XOR} {
    printf("line %d: BITOP %s\n", lines, yytext);
    yylval.NODE = new Node(Node_TYPE::BITOP_XOR);
    return BITOP_XOR;
}

{BITOP_AND} {
    printf("line %d: BITOP %s\n", lines, yytext);
    yylval.NODE = new Node(Node_TYPE::BITOP_AND);
    return BITOP_AND;
}

{BITOP_OR} {
    printf("line %d: BITOP %s\n", lines, yytext);
    yylval.NODE = new Node(Node_TYPE::BITOP_OR);
    return BITOP_OR;
}

{TYPE} {
    printf("line %d: TYPE %s\n", lines, yytext); 
    yylval.NODE = new Node(Node_TYPE::TYPE, yytext);
    return TYPE;
}

{ID} {
    identifiers++;
    printf("line %d: Valid identifier %s\n", lines, yytext);
    yylval.NODE = new Node(Node_TYPE::ID, yytext);
    return ID;    // 以字母或下划线开头，视为有效
}

{number_INT} {printf("line %d: number_INT %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::NUMBER_INT, yytext); return number_INT;}

{number_FLOAT} {printf("line %d: number_float %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::NUMBER_FLOAT, yytext); return number_FLOAT;}

{CHAR} {printf("line %d: CHAR %s\n", lines, yytext); yylval.NODE = new Node(Node_TYPE::CHAR, yytext); return CHAR;}


"//".* {
    // 单行注释
    comment_state = 1;
    printf("Single line comment: %s\n", yytext);
    printf("%d Single line comment is out of comment state\n", comment_state);
}

\n {
    // 行数统计
    lines++;
    printf("New line, total lines: %d\n", lines);
}

[ \t\r]+ {
    // 忽略空白字符
}

%%
