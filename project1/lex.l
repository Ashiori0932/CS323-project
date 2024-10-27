%{
    #include <stdio.h>
    // #include "syntax.tab.h"
    
    int lines = 1;
    int comment_state = 0;
%}

letter [a-zA-Z]
letter_ {letter}|_
digit [0-9]
id ({letter}|_)({letter}|{digit}|_)*
number ([1-9]{digit}*)|0
hexdigit [0-9abcdefABCDEF]

op "+"|"-"|"*"|"/"
dot "."
semi ";"
comma ","
assign "="
lt "<"
gt ">"
bitop "!"|"^"|"&"|"\\|"
lp "\("
rp "\)"
lb "["
rb "]"
lc "{"
rc "}"

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

"//".* {
    // 单行注释
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
