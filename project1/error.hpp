#ifndef ERROR_HPP
#define ERROR_HPP

#include <string>

typedef enum {
    MISS_SEMI,
    MISS_RC,
    MISS_RP,
    MISS_SPEC,
    MISS_RB,
    MISS_LC,
    MISS_LP,
    MISS_LB,
    MISS_COMMA,
    MISSING_OPERAND 
} ERROR_TYPE;

void print_error(ERROR_TYPE type, int line_num);

void print_error(ERROR_TYPE type, int line_num, std::string detail_operand);

#endif 