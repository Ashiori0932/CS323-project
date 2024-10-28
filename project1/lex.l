%{
    #include <stdio.h>
    #include <ctype.h>
    // #include "syntax.tab.h"
    
    int lines = 1;
    int comment_state = 0;
    int identifiers = 0;
    int has_error = 0;
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


OP "+"|"-"|"*"|"/"
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

{LP} {printf("line %d: LP %s\n", lines, yytext); }

{RP} {printf("line %d: RP %s\n", lines, yytext); }

{LB} {printf("line %d: LB %s\n", lines, yytext); }

{RB} {printf("line %d: RB %s\n", lines, yytext); }

{LC} {printf("line %d: LC %s\n", lines, yytext); }

{RC} {printf("line %d: RC %s\n", lines, yytext); }

{ASSIGN} {printf("line %d: ASSIGN %s\n", lines, yytext); }

{COMMA} {printf("line %d: COMMA %s\n", lines, yytext); }

{LT} {printf("line %d: LT %s\n", lines, yytext); }

{GT} {printf("line %d: GT %s\n", lines, yytext); }

{SEMI} {printf("line %d: SEMI %s\n", lines, yytext); }


{TYPE} {
    printf("line %d: TYPE %s\n", lines, yytext); 
}

{ID} {
    identifiers++;
    printf("line %d: Valid identifier %s\n", lines, yytext);    // 以字母或下划线开头，视为有效
} 

{number_INT} {printf("line %d: number_INT %s\n", lines, yytext);}

{number_FLOAT} {printf("line %d: number_float %s\n", lines, yytext);}

{CHAR} {printf("line %d: CHAR %s\n", lines, yytext);}

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
