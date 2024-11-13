%{
    #include"lex.yy.c"
    #include "node.hpp"
    void yyerror(const char*);
    Node* root_node;

%}

%union{
    Node* NODE;
}

%token <NODE> TYPE
%token <NODE> IF ELSE WHILE RETURN
%token <NODE> number_FLOAT number_INT CHAR
%token <NODE> ID
%right <NODE> ASSIGN
%left <NODE> OR AND
%left <NODE> LT LE GT GE NE EQ
%left <NODE> ADD SUB
%left <NODE> MUL DIV
%left <NODE> BITOP_NOT BITOP_AND BITOP_OR BITOP_XOR
%left <NODE> LP RP 
%left <NODE> LB RB
%token <NODE> SEMI COMMA
%token <NODE> LC RC


%%
TEST:
    OP
    ;
OP: 
    ADD {printf("GET ADD");}
    | SUB {printf("GET SUB");}
    | MUL {printf("GET MUL");}
    | DIV {printf("GET DIV");}
    ;


%%
void yyerror(const char *s){
    fprintf(stderr, "%s\n", s);
}
int main(){
    yyparse();
}