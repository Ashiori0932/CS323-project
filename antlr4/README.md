# ANTLR4使用

## ANTLR4安装

```
cd /usr/local/lib
curl -O http://www.antlr.org/download/antlr-4.5-complete.jar

vim ~/.zshrc # or vim ~/.bashrc
<!-- 
export CLASSPATH=".:/usr/local/lib/antlr-4.5-complete.jar:$CLASSPATH"
alias antlr4='java -Xmx500M -cp "/usr/local/lib/antlr-4.5-complete.jar:$CLASSPATH" org.antlr.v4.Tool'
alias grun='java org.antlr.v4.runtime.misc.TestRig' 
-->
. ~/.zshrc # or restart the terminal
```

## 生成Lexer和Parser

```
antlr4 SPL/spl.g4
```

## 编译ANTLR 生成的Java文件

```
javac SPL/*.java
```

## 编译并运行测试

```
javac spltest.java && java spltest
```
## 测试命令
```
grun SPL.spl program -tree ./test/test03.spl
```

## phase2 测试情况

 
| r1  | r2  | r3  | r4  | r5 | r6 | r7 | r8  | r9 | r10 | r11 | r12 |
|-----|-----|-----|-----|----|----|----|-----|----|-----|-----|-----|
| T   | T   | T   | T   | T  | T  | T  | T   | T  | T   | T   | T   |

| r13  | r14 | r15  |
|------|-----|------|
| T    | T   | F    | 


## Test2 测试情况

| r1  | r2  | r3  | r4  | r5 | r6 | r7 | r8  | r9 | r10 | r11 | r12 |
|-----|-----|-----|-----|----|----|----|-----|----|-----|-----|-----|
| T   | T   | T   | T   | T  | T  | T  | T   | T  | T   | T   | T   |

| r13  | r14 | r15  |
|------|-----|------|
| T    | T   | T    | 
