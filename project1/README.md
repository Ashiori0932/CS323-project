# Project 1-词法分析与语法分析

!!! WARNING: This folder is already deprecated.
****

## test result
 
| r1  | r2  | r3  | r4  | r5  | r6  | r7  | r8  | r9  | r10 | r11 | r12 |
|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|
| T   | T   | T   | T   | T   | T   | T   | T   | T   | T   | T   | T   |

| s1  | s2  | s3  | s4  | s5  | s6  | s7  | s8  | s9  | s10 | s11 | s12 | s13 |
|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|
| F   | T   | F   | T   | F   | T   | F   | F   | F   | T?   | T   | F   | F   |

## test method
第一阶段测试基本指令：
生成可执行文件splc:
make 

测试parser:
echo "tested codes" | ./splc 
**or** ./splc < test/test02.spl
**or** ./splc < phase1/test_1_r01.spl
