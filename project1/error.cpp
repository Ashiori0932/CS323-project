#include "error.hpp"

using namespace std;

void print_error(ERROR_TYPE type, int line_num)
{
    switch (type)
    {
    case MISS_SEMI:
    {
        printf("Error type B at Line %d: Missing semicolon \';\'\n", line_num);
        break;
    }
    case MISS_RC:
    {
        printf("Error type B at Line %d: Missing closing curly braces  \'}\'\n", line_num);
        break;
    }
    case MISS_RP:
    {
        printf("Error type B at Line %d: Missing closing parenthesis \')\'\n", line_num);
        break;
    }
    case MISS_SPEC:
    {
        printf("Error type B at Line %d: Missing specifier\n", line_num);
        break;
    }
    case MISS_RB:
    {
        printf("Error type B at Line %d: Missing closing bracket \']\'\n", line_num);
        break;
    }
    case MISS_LC:
    {
        printf("Error type B at Line %d: Missing opening curly braces \'{\'\n", line_num);
        break;
    }
    case MISS_LB:
    {
        printf("Error type B at Line %d: Missing opening bracket \'[\'\n", line_num);
        break;
    }
    case MISS_LP:
    {
        printf("Error type B at Line %d: Missing opening parenthesis \'(\'\n", line_num);
        break;
    }
    case MISS_COMMA:
    {
        printf("Error type B at Line %d: Missing comma", line_num);
        break;
    }
    }
}

void print_error(ERROR_TYPE type, int line_num, std::string detail_operand)
{
    switch (type)
    {
    case MISSING_OPERAND:
        printf("Error type B at Line %d: Missing operand for the operator %s \n", line_num, detail_operand.c_str());
        break;

    default:
        break;
    }
}