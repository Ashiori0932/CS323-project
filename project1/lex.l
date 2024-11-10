%{
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
    /* library inclusions */
    #include "syntax.tab.h"
    int yycolno = 1;
    #define YY_USER_ACTION \
        yylloc.first_line = yylineno; \
        yylloc.first_column = yycolno; \
        yylloc.last_line = yylineno; \
        yylloc.last_column = yycolno + yyleng; \
    yycolno += yyleng;
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
BITOP "!"|"^"|"&"|"\\|"
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

{IF} {printf("line %d: LP %s\n", lines, yytext); yylval.NODE = new Node("LP"); return LP;}

{ELSE} {printf("line %d: ELSE %s\n", lines, yytext); yylval.NODE = new Node("ELSE"); return ELSE;}

{WHILE} {printf("line %d: WHILE %s\n", lines, yytext); yylval.NODE = new Node("WHILE"); return WHILE;}

{RETURN} {printf("line %d: LP %s\n", lines, yytext); yylval.NODE = new Node("RETURN"); return RETURN;}

{LP} {printf("line %d: LP %s\n", lines, yytext); yylval.NODE = new Node("LP"); return LP;}

{RP} {printf("line %d: RP %s\n", lines, yytext); yylval.NODE = new Node("RP"); return RP;}

{LB} {printf("line %d: LB %s\n", lines, yytext); yylval.NODE = new Node("LB"); return LB;}

{RB} {printf("line %d: RB %s\n", lines, yytext); yylval.NODE = new Node("RB"); return RB;}

{LC} {printf("line %d: LC %s\n", lines, yytext); yylval.NODE = new Node("LC"); return LC;}

{RC} {printf("line %d: RC %s\n", lines, yytext); yylval.NODE = new Node("RC"); return RC;}

{ASSIGN} {printf("line %d: ASSIGN %s\n", lines, yytext); yylval.NODE = new Node("ASSIGN"); return ASSIGN;}

{COMMA} {printf("line %d: COMMA %s\n", lines, yytext); yylval.NODE = new Node("COMMA"); return COMMA}

{LT} {printf("line %d: LT %s\n", lines, yytext); yylval.NODE = new Node("LT"); return LT;}

{GT} {printf("line %d: GT %s\n", lines, yytext); yylval.NODE = new Node("GT"); return GT;}

{SEMI} {printf("line %d: SEMI %s\n", lines, yytext); yylval.NODE = new Node("SEMI"); return SEMI;}


{TYPE} {
    printf("line %d: TYPE %s\n", lines, yytext); 
    yylval.NODE = new Node("TYPE", yytext)
    return TYPE;
}

{ID} {
    identifiers++;
    printf("line %d: Valid identifier %s\n", lines, yytext);
    yylval.NODE = new Node("ID", yytext)
    return ID;    // 以字母或下划线开头，视为有效
} 

{BITOP} {printf("line %d: number_INT %s\n", lines, yytext); yylval.NODE = new Node("BITOP", yytext ); return BITOP}

{number_INT} {printf("line %d: number_INT %s\n", lines, yytext); yylval.NODE = new Node("number_INT", atoi(yytext) ); return number_INT}

{number_FLOAT} {printf("line %d: number_float %s\n", lines, yytext); yyval.NODE = new Node("number_FLOAT", atof(yytext));  return number_FLOAT;}

{CHAR} {printf("line %d: CHAR %s\n", lines, yytext); yyval.NODE = new Node("CHAR", yytext); return CHAR;}

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
