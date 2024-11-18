%{
    #include "node.hpp"
    #include <stdio.h>
    #include <ctype.h>
    #include <cstdio>
    #include <cstdlib>
    #include <stdlib.h>
    #include "syntax.tab.h"
    #include "string.h"

    extern "C" int yylex();
    extern int has_error;
    
    int lines = 1;
    int comment_state = 0;
    int identifiers = 0;
%}

%{
    /* library inclusions */
    int yycolno = 1;
    #define YY_USER_ACTION \
        yylloc.first_line = lines; \
        yylloc.first_column = yycolno; \
        yylloc.last_line = yylineno; \
        yylloc.last_column = yycolno + yyleng; \
    yycolno += yyleng;
%}


TYPE "int"|"char"|"float"
STRUCT "struct"
letter [a-zA-Z]
letter_ {letter}|_
digit [0-9]
pos_digit [1-9]
ID ({letter_})+({letter}|{digit}|_)*
hex [0-9a-fA-F]

INT_16 0[xX](0|[1-9a-fA-F]{hex}*)

hexadecimal [+-]?(0x)({hex}+)
decimal [+-]?(0|{pos_digit}{digit}*)

INT {decimal}|{hexadecimal}
FLOAT (([1-9]{digit}*)|0).(([1-9]{digit}*)|0)
CHAR '.'
CHAR_16 \'\\(x|X){hex}+\'
WRONG_ID ({INT}){ID}

CHAR_16_illgeal_char '\\x([g-zG-Z]){hex}*'

IF "if"
ELSE "else"
WHILE "while"
RETURN "return"
EQ "=="
NE "!="
AND "&&"
OR "||"


PLUS "+" 
MINUS "-" 
MUL "*" 
DIV "/" 
DOT "."
SEMI ";"
COMMA ","
ASSIGN "="
LT "<"
LE "<="
GT ">"
GE ">="
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
    //printf("%d Entering comment state\n", comment_state);
    BEGIN(comment);
}


<comment>[^*\n]* {
    // 处理不含 '*' 的内容 
    //printf("Comment content (not '*'): %s\n", yytext);
}

<comment>"*"+[^/*\n]* {
    // 处理含 '*' 但不跟随 '/' 的情况 
    //printf("Comment content (multiple '*'): %s\n", yytext);
}

<comment>\n {
    // 注释内的行数统计
    ++lines; 
    //printf("New line in comment, total lines: %d\n", lines);
}

<comment>"*"+"/" {
    // 退出注释状态
    comment_state = 0;
    //printf("%d Exiting comment state\n", comment_state);
    BEGIN(INITIAL);
}
{IF} {
    //printf("line %d: IF %s\n", lines, yytext); 
    yylval.node = new Node(Node_TYPE::TERMINAL, "IF"); 
    return IF;
}

{ELSE} {
    //printf("line %d: ELSE %s\n", lines, yytext); 
    yylval.node = new Node(Node_TYPE::TERMINAL, "ELSE"); 
    return ELSE;
}

{WHILE} { yylval.node = new Node(Node_TYPE::TERMINAL, "WHILE"); return WHILE; }

{RETURN} { yylval.node = new Node(Node_TYPE::TERMINAL, "RETURN"); return RETURN;}

{LP} { yylval.node = new Node(Node_TYPE::TERMINAL, "LP"); return LP;}

{RP} { yylval.node = new Node(Node_TYPE::TERMINAL, "RP"); return RP;}

{LB} { yylval.node = new Node(Node_TYPE::TERMINAL, "LB"); return LB;}

{RB} { yylval.node = new Node(Node_TYPE::TERMINAL, "RB"); return RB;}

{LC} { yylval.node = new Node(Node_TYPE::TERMINAL, "LC"); return LC;}

{RC} { yylval.node = new Node(Node_TYPE::TERMINAL, "RC"); return RC;}

{NE} {yylval.node = new Node(Node_TYPE::TERMINAL, "NE"); return NE;}

{ASSIGN} { yylval.node = new Node(Node_TYPE::TERMINAL, "ASSIGN"); return ASSIGN;}

{COMMA} { yylval.node = new Node(Node_TYPE::TERMINAL, "COMMA"); return COMMA;}

{PLUS} {
    //printf("line %d: PLUS %s\n", lines, yytext); 
    yylval.node = new Node(Node_TYPE::TERMINAL, "PLUS"); 
    return PLUS;
}

{MINUS} {
    //printf("line %d: MINUS %s\n", lines, yytext); 
    yylval.node = new Node(Node_TYPE::TERMINAL, "MINUS"); 
    return MINUS;
}

{MUL} {
    //printf("line %d: MUL %s\n", lines, yytext); 
    yylval.node = new Node(Node_TYPE::TERMINAL, "MUL"); 
    return MUL;
}

{DIV} {
    //printf("line %d: DIV %s\n", lines, yytext); 
    yylval.node = new Node(Node_TYPE::TERMINAL, "DIV"); 
    return DIV;
}

{EQ} { yylval.node = new Node(Node_TYPE::TERMINAL, "EQ"); return EQ;}

{LT} { yylval.node = new Node(Node_TYPE::TERMINAL, "LT"); return LT;}

{LE} { yylval.node = new Node(Node_TYPE::TERMINAL, "LE"); return LE;}

{GT} { yylval.node = new Node(Node_TYPE::TERMINAL, "GT"); return GT;}

{GE} { yylval.node = new Node(Node_TYPE::TERMINAL, "GE"); return GE;}

{DOT} { yylval.node = new Node(Node_TYPE::TERMINAL, "DOT"); return DOT;}

{SEMI} { yylval.node = new Node(Node_TYPE::TERMINAL, "SEMI"); return SEMI;}

{AND} {
    //printf("line %d: AND %s\n", lines, yytext); 
    yylval.node = new Node(Node_TYPE::TERMINAL, "AND"); 
    return AND;
}

{OR} {
    //printf("line %d: OR %s\n", lines, yytext); 
    yylval.node = new Node(Node_TYPE::TERMINAL, "OR"); 
    return OR;
}

{BITOP_NOT} {
    //printf("line %d: BITOP %s\n", lines, yytext);
    yylval.node = new Node(Node_TYPE::TERMINAL, "BITOP_NOT");
    return BITOP_NOT;
}

{BITOP_XOR} {
    //printf("line %d: BITOP %s\n", lines, yytext);
    yylval.node = new Node(Node_TYPE::TERMINAL, "BITOP_XOR");
    return BITOP_XOR;
}

{BITOP_AND} {
    //printf("line %d: BITOP %s\n", lines, yytext);
    yylval.node = new Node(Node_TYPE::TERMINAL, "BITOP_AND");
    return BITOP_AND;
}

{BITOP_OR} {
    //printf("line %d: BITOP %s\n", lines, yytext);
    yylval.node = new Node(Node_TYPE::TERMINAL, "BITOP_OR");
    return BITOP_OR;
}

{TYPE} {
    //printf("line %d: TYPE %s\n", lines, yytext); 
    yylval.node = new Node(Node_TYPE::TYPE, yytext);
    return TYPE;
}

{STRUCT} {
    //printf("line %d: STRUCT %s\n", lines, yytext); 
    yylval.node = new Node(Node_TYPE::TERMINAL, "STRUCT"); 
    return STRUCT;
}

{ID} {
    identifiers++;
    //printf("line %d: Valid identifier %s\n", lines, yytext);
    yylval.node = new Node(Node_TYPE::ID, yytext);
    return ID;    // 以字母或下划线开头，视为有效
}

{INT} { yylval.node = new Node(std::atoi(yytext)); return INT;}

{FLOAT} { yylval.node = new Node(Node_TYPE::FLOAT, yytext); return FLOAT;}

{CHAR} { yylval.node = new Node(Node_TYPE::CHAR, yytext); return CHAR;}

{CHAR_16} { yylval.node = new Node(Node_TYPE::CHAR, yytext); return CHAR;}

{INT_16} {
    yylval.node = new Node(static_cast<int>(strtol(yytext,nullptr,0)));
    return INT;
}

{CHAR_16_illgeal_char} {
    has_error=1;
    fprintf(stdout,"Error type A at Line %d: unknown lexeme %s\n",lines, yytext);
    return ILLEGAL_TOKEN;
}

{WRONG_ID} {
    // handle identifier mistake
    printf("line %d: ID ERROR occur: %s\n", lines, yytext);
    has_error = 1;
}


"//".* {
    // 单行注释
    comment_state = 1;
    //printf("Single line comment: %s\n", yytext);
    //printf("%d Single line comment is out of comment state\n", comment_state);
}

\n {
    // 行数统计
    lines++;
    //printf("New line, total lines: %d\n", lines);
}

[ \t\r]+ {
    // 忽略空白字符
}

[^0-9a-zA-Z_'\\\(\)\[\]\{\}\;\,\+\-\*\/] {
    has_error=1;
    fprintf(stdout,"Error type A at Line %d: unknown lexeme %s\n",lines, yytext);
    return ILLEGAL_TOKEN;
}

%%
