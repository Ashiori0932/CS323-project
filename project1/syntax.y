%{
    #include"lex.yy.c"
    #include "node.hpp"
    #include "error.hpp"
    void yyerror(const char*);
    Node* root_node;
    int has_error = 0;
%}

%union{
    Node* node;
}

%token <node> TYPE
%token <node> IF WHILE RETURN
%token <node> FLOAT INT CHAR
%token <node> ID

%right <node> ASSIGN NOT

%left <node> DOT
%left <node> OR AND
%left <node> LT LE GT GE NE EQ
%left <node> PLUS MINUS
%left <node> MUL DIV
%left <node> BITOP_NOT BITOP_AND BITOP_OR BITOP_XOR
%left <node> LP RP LB RB

%token <node> SEMI COMMA
%token <node> LC RC

%nonassoc <node> ILLEGAL_TOKEN
%nonassoc LOWER_ELSE
%nonassoc <node> ELSE

%type <node> Program ExtDefList ExtDef ExtDecList
%type <node> Specifier StructSpecifier 
%type <node> VarDec FunDec VarList ParamDec CompSt StmtList
%type <node> Stmt DefList Def DecList Dec Exp Args

%%

Program : ExtDefList {
    $$ = new Node(NONTERMINAL, "Program", $1); 
    if(!has_error) {
        $$->print_tree(0);
    }
}
;

ExtDefList : { $$ = new Node(NONE, ""); };
| ExtDef ExtDefList { $$ = new Node(NONTERMINAL, "ExtDefList", $1, $2); }
;

ExtDef : Specifier ExtDecList SEMI { $$ = new Node(NONTERMINAL, "ExtDef", $1, $2, $3); }
| Specifier SEMI { $$ = new Node(NONTERMINAL, "ExtDef", $1, $2); }
| Specifier FunDec CompSt { $$ = new Node(NONTERMINAL, "ExtDef", $1, $2, $3); }
| Specifier ExtDecList error { print_error(MISS_SEMI, @$.first_line); has_error = 1; }
| Specifier error { print_error(MISS_SEMI, @$.first_line); has_error = 1; }
;

ExtDecList: VarDec { $$ = new Node(NONTERMINAL, "ExtDecList", $1); };
| VarDec COMMA ExtDecList { $$ = new Node(NONTERMINAL, "ExtDecList", $1, $2, $3); }
;

/* specifier */
Specifier: TYPE { $$ = new Node(NONTERMINAL, "Specifier", $1); }
| StructSpecifier { $$ = new Node(NONTERMINAL, "Specifier", $1); }
;

StructSpecifier: STRUCT ID LC DefList RC { $$ = new Node(NONTERMINAL, "StructSpecifier", $1, $2, $3, $4, $5); }
| STRUCT ID { $$ = new Node(NONTERMINAL, "StructSpecifier", $1, $2); }
| STRUCT ID LC DefList error { print__error(MISS_RC, @$.first_line); has_error = 1; }
;

/* declarator */
VarDec: ID { $$ = new Node(NONTERMINAL, "VarDec", $1 ); }
| VarDec LB INT RB { $$ = new Node(NONTERMINAL, "VarDec", $1, $2, $3, $4); }
| VarDec LB INT error { print_error(MISS_RB, @$.first_line); has_error = 1; }
;

FunDec: ID LP VarList RP { $$ = new Node(NONTERMINAL, "FunDec", $1, $2, $3, $4); }
| ID LP RP { $$ = new Node(NONTERMINAL, "FunDec", $1, $2, $3); }
| ID LP VarList error { print_error(MISS_RP, @$.first_line); has_error = 1; }
| ID LP error { print_error(MISS_RP, @$.first_line); has_error = 1; }
;

VarList: ParamDec COMMA VarList { $$ = new Node(NONTERMINAL, "VarList", $1, $2, $3); }
| ParamDec { $$ = new Node(NONTERMINAL, "VarList", $1); }
;

ParamDec: Specifier VarDec { $$ = new Node(NONTERMINAL, "ParamDec", $1, $2); }
;

/* statement */
CompSt: LC DefList StmtList RC { $$ = new Node(NONTERMINAL, "CompSt", $1, $2, $3, $4); }
;

StmtList: { $$ = new Node(NONE, ""); }
| Stmt StmtList { $$ = new Node(NONTERMINAL, "StmtList", $1, $2); }
;

Stmt: Exp SEMI { $$ = new Node(NONTERMINAL, "Stmt", $1, $2); }
| CompSt { $$ = new Node(NONTERMINAL, "Stmt", $1); }
| RETURN Exp SEMI { $$ = new Node(NONTERMINAL, "Stmt", $1, $2, $3); }
| IF LP Exp RP Stmt %prec LOWER_ELSE { $$ = new Node(NONTERMINAL, "Stmt", $1, $2, $3, $4, $5); }
| IF LP Exp RP Stmt ELSE Stmt { $$ = new Node(NONTERMINAL, "Stmt", $1, $2, $3, $4, $5, $6, $7); }
| WHILE LP Exp RP Stmt { $$ = new Node(NONTERMINAL, "Stmt", $1, $2, $3, $4, $5); }
| Exp error { print_error(MISS_SEMI, @$.first_line); has_error = 1; }
| RETURN Exp error { print_error(MISS_SEMI, @$.first_line); has_error = 1; }
| IF LP Exp error Stmt { print_error(MISS_RP, @$.first_line); has_error = 1; }
| WHILE LP Exp error Stmt { print_error(MISS_RP, @$.first_line); has_error = 1; }
;

/* local definition */ 
DefList: { $$ = new Node(NONTERMINAL, "DefList"); };
| Def DefList { $$ = new Node(NONTERMINAL, "DefList", $1, $2); }
; 

Def: Specifier DecList SEMI { $$ = new Node(NONTERMINAL, "Def", $1, $2, $3); }
| Specifier DecList error{ print_error(MISS_SEMI, @$.first_line); has_error = 1; }
| error DecList SEMI { print_error(MISS_SPEC, @$.first_line +1); has_error = 1; }
;

DecList: Dec { $$ = new Node(NONTERMINAL, "DecList", $1); }
| Dec COMMA DecList { $$ = new Node(NONTERMINAL, "DecList", $1, $2, $3); }
;

Dec: VarDec { $$ = new Node(NONTERMINAL, "Dec", $1); }
| VarDec ASSIGN Exp { $$ = new Node(NONTERMINAL, "Dec", $1, $2, $3); }
;

/* Expression */
Exp: Exp ASSIGN Exp { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| Exp AND Exp { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| Exp OR Exp { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| Exp LT Exp { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| Exp LE Exp { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| Exp GT Exp { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| Exp GE Exp { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| Exp NE Exp { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| Exp EQ Exp { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| Exp PLUS Exp { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| Exp MINUS Exp { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| Exp MUL Exp { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| Exp DIV Exp { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| LP Exp RP { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| MINUS Exp { $$ = new Node(NONTERMINAL, "Exp", $1, $2); }
| NOT Exp { $$ = new Node(NONTERMINAL, "Exp", $1, $2); }
| ID LP Args RP { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3, $4); }
| ID LP RP  { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| Exp LB Exp RB { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3, $4); }
| Exp DOT ID { $$ = new Node(NONTERMINAL, "Exp", $1, $2, $3); }
| ID { $$ = new Node(NONTERMINAL, "Exp", $1); }
| INT { $$ = new Node(NONTERMINAL, "Exp", $1); }
| FLOAT { $$ = new Node(NONTERMINAL, "Exp", $1); }
| CHAR { $$ = new Node(NONTERMINAL, "Exp", $1); }
| LP Exp error { print_error(MISS_RP, @$.first_line); has_error = 1; }
| ID LP Args error { print_error(MISS_RP, @$.first_line); has_error = 1; }
| ID LP error { print_error(MISS_RP, @$.first_line); has_error = 1; }
| Exp LB Exp error { print_error(MISS_RB, @$.first_line); has_error = 1; }
;

Args: Exp COMMA Args { $$ = new Node(NONTERMINAL, "Args", $1, $2, $3); }
| Exp  { $$ = new Node(NONTERMINAL, "Args", $1); }
;


%%
void yyerror(const char *s){
    fprintf(stderr, "%s\n", s);
}

int main(){
    yyparse();
}