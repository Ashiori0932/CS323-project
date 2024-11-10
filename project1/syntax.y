%{
    #include"lex.yy.c"
    void yyerror(const char*);

    //JUST FOR TEST
    void print_node(Node* node) {
        if (node == NULL){
            print("Fail!")
            return;
        }
        printf("Node type: %s, ", node->type);
        if (strcmp(node->type, "number_INT") == 0) {
            printf("Value: %d\n", node->int_value);
        } else if (strcmp(node->type, "number_FLOAT") == 0) {
            printf("Value: %f\n", node->float_value);
        } else if (strcmp(node->type, "CHAR") == 0) {
        printf("Value: %s\n", node->string_value);
    }
}
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
%left <NODE> BITOP
%left <NODE> LP RP 
%left <NODE> LB RB
%token <NODE> SEMI COMMA
%token <NODE> LC RC

%%
TEST:
    OP
    ;
OP: 
    ADD {printf("GET ADD")}
    | SUB {printf("GET SUB")}
    | MUL {printf("GET MUL")}
    | DIV {printf("GET DIV")}
    ;


%%
void yyerror(const char *s){
    fprintf(stderr, "%s\n", s);
}
int main(){
    yyparse();
}