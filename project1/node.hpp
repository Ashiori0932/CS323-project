#ifndef NODE_H
#define NODE_H

#include <string>
#include <vector>

// 定义节点类型
enum class Node_TYPE {
    TYPE,
    ID,
    FLOAT,
    INT,
    CHAR,
    NON_TERMINAL,
    TERMINAL
};

// Node 类定义
class Node {
public:
    
    Node_TYPE type;                 // 节点类型
    std::string string_value;       // 字符串值
    int int_value;                  // 整型值
    float float_value;              // 浮动类型值
    char char_value;                // 字符值

    std::vector<Node*> nodes;       // 子节点

    Node(Node_TYPE type);           // 根据类型构造节点
    Node(Node_TYPE type, std::string yytext);          

    template <typename... Args>
    Node::Node(Node_TYPE type, std::string yytext, Args... args);

    ~Node();                        // 析构函数

    void print();          // 打印节点信息

};



#endif // NODE_H
