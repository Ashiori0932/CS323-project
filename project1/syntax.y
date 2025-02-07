%{
    #include"lex.yy.c"
    #include "node.hpp"
    #include "error.hpp"
    void yyerror(const char*);
    int has_error = 0;

    extern int lines;
%}

%union{
    Node* node;
}

%nonassoc <node> ILLEGAL_TOKEN
%nonassoc LOWER_ELSE
%nonassoc <node> ELSE

%token <node> TYPE STRUCT
%token <node> IF WHILE RETURN
%token <node> FLOAT INT CHAR
%token <node> ID

%right <node> ASSIGN

%left <node> DOT
%left <node> OR AND
%left <node> LT LE GT GE NE EQ
%left <node> PLUS MINUS
%left <node> MUL DIV
%right <node> NOT
%left <node> BITOP_AND BITOP_OR BITOP_XOR
%left <node> LP RP LB RB

%token <node> SEMI COMMA
%token <node> LC RC

%type <node> Program ExtDefList ExtDef ExtDecList
%type <node> Specifier StructSpecifier 
%type <node> VarDec FunDec VarList ParamDec CompSt StmtList
%type <node> Stmt DefList Def DecList Dec Exp Args

%%

Program : ExtDefList {
    $$ = new Node(Node_TYPE::NONTERMINAL, "Program", $1); 
    if(!has_error) {
        $$->print_tree(0);
        printf("\n");
    }
}
;

ExtDefList : { $$ = new Node(Node_TYPE::NONE, ""); };
| ExtDef ExtDefList { $$ = new Node(Node_TYPE::NONTERMINAL, "ExtDefList", $1, $2); }
;

ExtDef : Specifier ExtDecList SEMI { $$ = new Node(Node_TYPE::NONTERMINAL, "ExtDef", $1, $2, $3); }
| Specifier SEMI { $$ = new Node(Node_TYPE::NONTERMINAL, "ExtDef", $1, $2); }
| Specifier FunDec CompSt { $$ = new Node(Node_TYPE::NONTERMINAL, "ExtDef", $1, $2, $3); }
| Specifier ExtDecList error { print_error(MISS_SEMI, @$.first_line); has_error = 1; } //
| Specifier error { print_error(MISS_SEMI, @$.first_line); has_error = 1; } //
;

ExtDecList: VarDec { $$ = new Node(Node_TYPE::NONTERMINAL, "ExtDecList", $1);};
| VarDec COMMA ExtDecList { $$ = new Node(Node_TYPE::NONTERMINAL, "ExtDecList", $1, $2, $3); }
| VarDec ExtDecList error { print_error(MISS_COMMA, @$.first_line); has_error = 1; }
;

/* specifier */
Specifier: TYPE { $$ = new Node(Node_TYPE::NONTERMINAL, "Specifier", $1);}
| StructSpecifier { $$ = new Node(Node_TYPE::NONTERMINAL, "Specifier", $1); }
;

StructSpecifier: STRUCT ID LC DefList RC { 
    $$ = new Node(Node_TYPE::NONTERMINAL, "StructSpecifier", $1, $2, $3, $4, $5); 
}
| STRUCT ID { $$ = new Node(Node_TYPE::NONTERMINAL, "StructSpecifier", $1, $2); }
//| STRUCT ID LC DefList error { print_error(MISS_RC, @$.first_line); has_error = 1; } //
;

/* declarator */
VarDec: ID  { $$ = new Node(Node_TYPE::NONTERMINAL, "VarDec", $1 ); }
| VarDec LB INT RB { $$ = new Node(Node_TYPE::NONTERMINAL, "VarDec", $1, $2, $3, $4); }
| VarDec LB INT error { print_error(MISS_RB, @$.first_line); has_error = 1; } //
;

FunDec: ID LP VarList RP { 
    $$ = new Node(Node_TYPE::NONTERMINAL, "FunDec", $1, $2, $3, $4); 
}
| ID LP RP { $$ = new Node(Node_TYPE::NONTERMINAL, "FunDec", $1, $2, $3); }
| ID LP VarList error { print_error(MISS_RP, @$.first_line); has_error = 1; } //
| ID LP error { print_error(MISS_RP, @$.first_line); has_error = 1; } //
;

VarList: ParamDec COMMA VarList { $$ = new Node(Node_TYPE::NONTERMINAL, "VarList", $1, $2, $3); }
| ParamDec { $$ = new Node(Node_TYPE::NONTERMINAL, "VarList", $1); }
| ParamDec VarList error {print_error(MISS_COMMA, @$.first_line); has_error = 1; }
;

ParamDec: Specifier VarDec { $$ = new Node(Node_TYPE::NONTERMINAL, "ParamDec", $1, $2); }
;

/* statement */
CompSt: LC DefList StmtList RC { $$ = new Node(Node_TYPE::NONTERMINAL, "CompSt", $1, $2, $3, $4); }
;

StmtList: { $$ = new Node(Node_TYPE::NONE, ""); }
| Stmt StmtList { $$ = new Node(Node_TYPE::NONTERMINAL, "StmtList", $1, $2); }
;

Stmt: Exp SEMI { $$ = new Node(Node_TYPE::NONTERMINAL, "Stmt", $1, $2); }
| CompSt { $$ = new Node(Node_TYPE::NONTERMINAL, "Stmt", $1); }
| RETURN Exp SEMI { $$ = new Node(Node_TYPE::NONTERMINAL, "Stmt", $1, $2, $3); }
| 
IF LP Exp RP Stmt %prec LOWER_ELSE { 
    $$ = new Node(Node_TYPE::NONTERMINAL, "Stmt", $1, $2, $3, $4, $5); 
}
| 
IF LP Exp RP Stmt ELSE Stmt { 
    $$ = new Node(Node_TYPE::NONTERMINAL, "Stmt", $1, $2, $3, $4, $5, $6, $7); 
}
| 
WHILE LP Exp RP Stmt { 
    $$ = new Node(Node_TYPE::NONTERMINAL, "Stmt", $1, $2, $3, $4, $5); 
}
| Exp error { print_error(MISS_SEMI, @$.first_line); has_error = 1; }
| RETURN Exp error { print_error(MISS_SEMI, @$.first_line); has_error = 1; }
| IF LP Exp error Stmt { print_error(MISS_RP, @$.first_line); has_error = 1; }
| IF error Exp RP Stmt { print_error(MISS_LP, @$.first_line); has_error = 1; }
| WHILE LP Exp error Stmt { print_error(MISS_RP, @$.first_line); has_error = 1; }
| WHILE error Exp RP Stmt { print_error(MISS_LP, @$.first_line); has_error = 1; }
;

/* local definition */ 
DefList: { $$ = new Node(Node_TYPE::NONE, ""); };
| Def DefList { $$ = new Node(Node_TYPE::NONTERMINAL, "DefList", $1, $2);}
; 

Def: Specifier DecList SEMI { $$ = new Node(Node_TYPE::NONTERMINAL, "Def", $1, $2, $3); }
| Specifier DecList error{ print_error(MISS_SEMI, @$.first_line);  has_error = 1; } //
| error DecList SEMI { print_error(MISS_SPEC, @$.first_line); has_error = 1; } //
;

DecList: Dec { $$ = new Node(Node_TYPE::NONTERMINAL, "DecList", $1); }
| Dec COMMA DecList { $$ = new Node(Node_TYPE::NONTERMINAL, "DecList", $1, $2, $3); }
| Dec DecList error {print_error(MISS_COMMA, @$.first_line); has_error = 1;}
;

Dec: VarDec { $$ = new Node(Node_TYPE::NONTERMINAL, "Dec", $1); }
| VarDec ASSIGN Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Dec", $1, $2, $3);}
;

/* Expression */
Exp: Exp ASSIGN Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3);}
| Exp AND Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| Exp OR Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| Exp LT Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| Exp LE Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| Exp GT Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| Exp GE Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| Exp NE Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| Exp EQ Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| Exp PLUS Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| Exp MINUS Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| Exp MUL Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| Exp DIV Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| Exp BITOP_AND Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| Exp BITOP_OR Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| Exp BITOP_XOR Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| LP Exp RP { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| MINUS Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2); }
| NOT Exp { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2); }
| ID LP Args RP { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3, $4); }
| ID LP RP  { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| Exp LB Exp RB { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3, $4); }
| Exp DOT ID { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1, $2, $3); }
| ID { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1); }
| INT { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1);}
| FLOAT { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1); }
| CHAR { $$ = new Node(Node_TYPE::NONTERMINAL, "Exp", $1); }
| LP Exp error { print_error(MISS_RP, @$.first_line); has_error = 1; } // 
| ID LP Args error { print_error(MISS_RP, @$.first_line); has_error = 1; } //
| ID LP error { print_error(MISS_RP, @$.first_line); has_error = 1; } //
| Exp LB Exp error { print_error(MISS_RB, @$.first_line); has_error = 1; } //
| Exp PLUS error { print_error(MISSING_OPERAND, @$.first_line, "+"); has_error = 1; } // test1 error
| Exp MINUS error { print_error(MISSING_OPERAND, @$.first_line, "-"); has_error = 1; }
| Exp MUL error { print_error(MISSING_OPERAND, @$.first_line, "*"); has_error = 1; }
| Exp DIV error { print_error(MISSING_OPERAND, @$.first_line, "/"); has_error = 1; }
| Exp ILLEGAL_TOKEN Exp {printf("ILLEGAL_TOKEN between Exp");}
| ILLEGAL_TOKEN {}
;

Args: Exp COMMA Args { $$ = new Node(Node_TYPE::NONTERMINAL, "Args", $1, $2, $3); }
| Exp  { $$ = new Node(Node_TYPE::NONTERMINAL, "Args", $1); }
;


%%
void yyerror(const char *s){
    // fprintf(stderr, "%s\n", s);
}

int main(){
    //printf("Total lines: %d\n", lines);
    yyparse();
}