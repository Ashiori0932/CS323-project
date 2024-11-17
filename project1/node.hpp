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
    NONTERMINAL,
    TERMINAL,
    NONE
};

// Node 类定义
class Node {
public:
    
    Node_TYPE type;                 // 节点类型
    std::string string_value;       // 字符串值
    int int_value;                  // 整型值
    float float_value;              // 浮动类型值
    std::string char_value;                // 字符值

    std::vector<Node*> nodes;       // 子节点

    Node(Node_TYPE type, std::string yytext);

    Node(int val);       

    //constructor for nodes of nonterminals
    template <typename... Args>
    Node(Node_TYPE type, std::string yytext, Args... args) : 
    type(type), string_value(yytext), int_value(0), 
    float_value(0.0), char_value(yytext) {
        nodes = {args...};
    }

    ~Node();                        // 析构函数

    void print() const;          // 打印节点信息
    void print_tree(int space) const;
};



#endif // NODE_H
