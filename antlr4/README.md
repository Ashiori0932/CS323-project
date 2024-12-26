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
antlr4 -visitor SPL/spl.g4 
```

## 编译ANTLR 生成的Java文件

```
javac SPL/*.java
```

## 编译并运行测试

```
javac spltest.java && java spltest OR javac main.java && java main
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


## Phase 3 使用方法
这一阶段已经不需要semantic analyzer了，所以已经把他移除了。

1. 
```
cd SPL
antlr4 -visitor -no-listener spl.g4
```

2. 
```
cd ..
javac SPL/*.java
```

3.
```
javac main.java && java main
```


## test_phase3 测试情况

| r1  | r2  | r3  | r4  | r5 | r6 | r7 | r8  | r9 | r10 |
|-----|-----|-----|-----|----|----|----|-----|----|-----|
| T   | T   | F   | T   | F  | F  | F  | F   | F  | T   |

一些约定: 
1. 在tutorial中没有数组以及struct的要求
2. 目前没有规定0寄存器, 任何用到的数字, 将会新定义成一个 t_i 输出t_i = x
3. t_i 标识新的变量temp, v_i表示从read读进来的变量, 所以说int a = read()的步骤应该是 READ v1; t1=v1
4. 另外注意答案与提供的不一定一样, 例如因为我们创建temp的规定是不一致的
5. WRITE, READ我们相当于一个函数在使用
6. int a 如果没有进行赋值我们不用进行emit

TODO:
1. WHILE Label的摆放 参见test_3_r03.spl
2. 其余label摆放的优化, 具体请见tutorial_3 page_14
3. 根据spl.g4添加其他漏掉的访问方式
4. 函数直接调用时候也需要CALL 参见test_3_r06.spl
