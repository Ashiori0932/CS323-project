#include "node.hpp"
#include <cstdio>
#include <cstdlib>


Node::Node(Node_TYPE type) :  type(type), string_value(""), int_value(0), float_value(0.0), char_value(0) {}
Node::Node(Node_TYPE type, std::string yytext) : 
    type(type),string_value(""), 
    int_value(0), 
    float_value(0.0), 
    char_value(0)
     {
    
    switch (type) {
        case Node_TYPE::TYPE: {
            // 这里根据yytext的值设置string_value
            string_value = yytext;
            break;
        }
        case Node_TYPE::ID: {
            // 存储标识符信息
            string_value = yytext;
            break;
        }
        case Node_TYPE::NUMBER_INT: {
            // 将yytext转为整数并存储
            int_value = std::stoi(yytext);
            break;
        }
        case Node_TYPE::NUMBER_FLOAT: {
            // 将yytext转为浮点数并存储
            float_value = std::stof(yytext);
            break;
        }
        case Node_TYPE::CHAR: {
            // 直接取字符的第一个字符并存储
            char_value = yytext[0];
            break;
        }
        default: {
            // 其他类型的情况，这里可以扩展
            break;
        }
    }
}


Node::~Node() {
    for (auto node : nodes) {
        delete node;
    }
}

void Node::print() { 
    // 根据节点类型输出不同信息
    switch (this->type) {
        case Node_TYPE::CHAR:
            printf("Node type: CHAR, Value: %s\n", this->string_value.c_str());
            break;
        case Node_TYPE::ID:
            printf("Node type: ID, Value: %s\n", this->string_value.c_str());
            break;
        case Node_TYPE::TYPE:
            printf("Node type: TYPE, Value: %s\n", this->string_value.c_str());
            break;
        case Node_TYPE::NUMBER_INT:
            printf("Node type: NUMBER_INT, Value: %d\n", this->int_value);
            break;
        case Node_TYPE::NUMBER_FLOAT:
            printf("Node type: NUMBER_FLOAT, Value: %f\n", this->float_value);
            break;
        case Node_TYPE::BITOP_NOT:
            printf("Node type: BITOP_NOT\n");
            break;
        case Node_TYPE::BITOP_XOR:
            printf("Node type: BITOP_XOR\n");
            break;
        case Node_TYPE::BITOP_AND:
            printf("Node type: BITOP_AND\n");
            break;
        case Node_TYPE::BITOP_OR:
            printf("Node type: BITOP_OR\n");
            break;
        case Node_TYPE::ASSIGN:
            printf("Node type: ASSIGN\n");
            break;
        case Node_TYPE::COMMA:
            printf("Node type: COMMA\n");
            break;
        case Node_TYPE::LT:
            printf("Node type: LT\n");
            break;
        case Node_TYPE::GT:
            printf("Node type: GT\n");
            break;
        case Node_TYPE::SEMI:
            printf("Node type: SEMI\n");
            break;
        case Node_TYPE::LP:
            printf("Node type: LP\n");
            break;
        case Node_TYPE::RP:
            printf("Node type: RP\n");
            break;
        case Node_TYPE::LB:
            printf("Node type: LB\n");
            break;
        case Node_TYPE::RB:
            printf("Node type: RB\n");
            break;
        case Node_TYPE::LC:
            printf("Node type: LC\n");
            break;
        case Node_TYPE::RC:
            printf("Node type: RC\n");
            break;
        case Node_TYPE::IF:
            printf("Node type: IF\n");
            break;
        case Node_TYPE::ELSE:
            printf("Node type: ELSE\n");
            break;
        case Node_TYPE::WHILE:
            printf("Node type: WHILE\n");
            break;
        case Node_TYPE::RETURN:
            printf("Node type: RETURN\n");
            break;
        default:
            printf("Node type: Unknown\n");
            break;
    }
}





