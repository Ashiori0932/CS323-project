#include "error.hpp"

using namespace std;

void print_error(ERROR_TYPE type, int line_num) {
    switch (type) {
        case MISS_SEMI: {
            printf("Error type B at Line %d: Missing semicolon \';\'\n", line_num);
            break;
        }
        case MISS_RC: {
            printf("Error type B at Line %d: Missing closing curly braces  \'}\'\n", line_num);
            break;
        }
        case MISS_RP: {
            printf("Error type B at Line %d: Missing closing parenthesis \')\'\n", line_num);
            break;
        }
        case MISS_SPEC: {
            printf("Error type B at Line %d: Missing specifier\n", line_num);
            break;
        }
        case MISS_RB: {
            printf("Error type B at Line %d: Missing closing bracket \']\'\n", line_num);
            break;
        }
        case MISSING_OPERAND: { // 缺少操作数
            printf("Error type B at Line %d: Missing operand for the operator\n", line_num);
            break;
        }
    }
}
   