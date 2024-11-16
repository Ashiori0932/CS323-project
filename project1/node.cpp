#include "node.hpp"
#include <cstdio>
#include <cstdlib>


Node::Node(Node_TYPE type) : type(type), string_value(""), int_value(0), float_value(0.0), char_value(0) {}

Node::Node(Node_TYPE type, std::string yytext) : 
    type(type), string_value(""), 
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
        case Node_TYPE::INT: {
            // 将yytext转为整数并存储
            int_value = std::stoi(yytext);
            break;
        }
        case Node_TYPE::FLOAT: {
            // 将yytext转为浮点数并存储
            float_value = std::stof(yytext);
            break;
        }
        case Node_TYPE::CHAR: {
            // 直接取字符的第一个字符并存储
            char_value = yytext[0];
            break;
        }
        case Node_TYPE::NON_TERMINAL: {
            string_value = yytext;
            break;
        }
        case Node_TYPE::TERMINAL: {
            string_value = yytext;
            break;
        }
        default: {
            // 其他类型的情况，这里可以扩展
            break;
        }
    }
}

template <typename... Args>
Node::Node(Node_TYPE type, std::string yytext, Args... args) : 
    type(type), string_value(""), int_value(0), 
    float_value(0.0), char_value(0) {
        nodes = {args...};
    }

Node::~Node() {
    for (auto node : nodes) {
        delete node;
    }
}

void Node::print() { 
    // 根据节点类型输出不同信息
    switch (this->type) {
        case Node_TYPE::TYPE:
            printf("Node type: TYPE, Value: %s\n", this->string_value.c_str());
            break;
        case Node_TYPE::ID:
            printf("Node type: ID, Value: %s\n", this->string_value.c_str());
            break;
        case Node_TYPE::INT:
            printf("Node type: INT, Value: %d\n", this->int_value);
            break;
        case Node_TYPE::FLOAT:
            printf("Node type: FLOAT, Value: %f\n", this->float_value);
            break;
        case Node_TYPE::CHAR:
            printf("Node type: CHAR, Value: %s\n", this->string_value.c_str());
            break;
        case Node_TYPE::NON_TERMINAL:
            printf("Node type: s\n", Node::string_value);
            break;
        case Node_TYPE::TERMINAL:
            printf("Node type: s\n", Node::string_value);
            break;
        default:
            printf("Node type: Unknown\n");
            break;
    }
}





