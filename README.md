# SPL Compiler

During this project, we aim to build a compiler for SPL(Sustech Programming Language).

## Tools
We use antlr4 as the front-end tool for our compiler.

### ANTLR4安装

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

## How to use
Here is the instruction you may use for this repository

### Generate AST and do semantic analysis

1. generate lexer and parser

```
antlr4 SPL/spl.g4 
```

2. compile

```
javac SPL/*.java
```

3. compile and run

```
javac spltest.java && java spltest OR javac main.java && java main
```

4. for test
```
grun SPL.spl program -tree ./test/test03.spl
```


### Generate TAC(Three-address code)


1. generate lexer and parser
```
cd antlr4
cd SPL
antlr4 -visitor -no-listener spl.g4
```

3. compile
```
cd ..
javac SPL/*.java
```

4. compile and run
```
javac Main.java && java Main
```
