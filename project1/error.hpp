#ifndef ERROR_HPP
#define ERROR_HPP

#include <string>

typedef enum {
    MISS_SEMI,
    MISS_RC,
    MISS_RP,
    MISS_SPEC,
    MISS_RB,
    MISSING_OPERAND 
} ERROR_TYPE;

void print_error(ERROR_TYPE type, int line_num);

#endif 