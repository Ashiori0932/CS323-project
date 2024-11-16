#include "node.hpp"
#include <cstdio>
#include <cstdlib>


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
        case Node_TYPE::NONTERMINAL: {
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

void Node::print() const { 
    // 根据节点类型输出不同信息
    switch (this->type) {
        case Node_TYPE::TYPE:
            printf("TYPE: %s\n", this->string_value.c_str());
            break;
        case Node_TYPE::ID:
            printf("ID: %s\n", this->string_value.c_str());
            break;
        case Node_TYPE::INT:
            printf("INT: %d\n", this->int_value);
            break;
        case Node_TYPE::FLOAT:
            printf("FLOAT: %f\n", this->float_value);
            break;
        case Node_TYPE::CHAR:
            printf("CHAR: %s\n", this->string_value.c_str());
            break;
        case Node_TYPE::NONTERMINAL:
            printf("s\n", Node::string_value);
            break;
        case Node_TYPE::TERMINAL:
            printf("s\n", Node::string_value);
            break;
        case Node_TYPE::NONE:
            break;
        default:
            printf("Unknown\n");
            break;
    }
}

void Node::print_tree(int space) const {
    for(int i = 0; i < space; i++) {
        printf(" ");
    }
    this->print();
    if(this->type == Node_TYPE::NONTERMINAL) {
        space += 2;
        for (const auto& child : nodes) {
            child->print_tree(space);
        }
    }
}



