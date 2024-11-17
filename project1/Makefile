CC=gcc
GPP=g++
FLEX=flex
BISON=bison
CXX_VERSION = -std=c++17

splc:
	$(BISON) -d syntax.y
	$(FLEX) lex.l
	$(GPP) -o splc syntax.tab.c node.cpp error.cpp -lfl $(CXX_VERSION)

clean:	
	@rm -rf lex.yy.c syntax.tab.c syntax.tab.h *.out output.txt
.PHONY: splc
