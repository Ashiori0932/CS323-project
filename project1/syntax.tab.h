/* A Bison parser, made by GNU Bison 3.8.2.  */

/* Bison interface for Yacc-like parsers in C

   Copyright (C) 1984, 1989-1990, 2000-2015, 2018-2021 Free Software Foundation,
   Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <https://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* DO NOT RELY ON FEATURES THAT ARE NOT DOCUMENTED in the manual,
   especially those whose name start with YY_ or yy_.  They are
   private implementation details that can be changed or removed.  */

#ifndef YY_YY_SYNTAX_TAB_H_INCLUDED
# define YY_YY_SYNTAX_TAB_H_INCLUDED
/* Debug traces.  */
#ifndef YYDEBUG
# define YYDEBUG 0
#endif
#if YYDEBUG
extern int yydebug;
#endif

/* Token kinds.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
  enum yytokentype
  {
    YYEMPTY = -2,
    YYEOF = 0,                     /* "end of file"  */
    YYerror = 256,                 /* error  */
    YYUNDEF = 257,                 /* "invalid token"  */
    TYPE = 258,                    /* TYPE  */
    STRUCT = 259,                  /* STRUCT  */
    IF = 260,                      /* IF  */
    WHILE = 261,                   /* WHILE  */
    RETURN = 262,                  /* RETURN  */
    FLOAT = 263,                   /* FLOAT  */
    INT = 264,                     /* INT  */
    CHAR = 265,                    /* CHAR  */
    ID = 266,                      /* ID  */
    ASSIGN = 267,                  /* ASSIGN  */
    NOT = 268,                     /* NOT  */
    DOT = 269,                     /* DOT  */
    OR = 270,                      /* OR  */
    AND = 271,                     /* AND  */
    LT = 272,                      /* LT  */
    LE = 273,                      /* LE  */
    GT = 274,                      /* GT  */
    GE = 275,                      /* GE  */
    NE = 276,                      /* NE  */
    EQ = 277,                      /* EQ  */
    PLUS = 278,                    /* PLUS  */
    MINUS = 279,                   /* MINUS  */
    MUL = 280,                     /* MUL  */
    DIV = 281,                     /* DIV  */
    BITOP_NOT = 282,               /* BITOP_NOT  */
    BITOP_AND = 283,               /* BITOP_AND  */
    BITOP_OR = 284,                /* BITOP_OR  */
    BITOP_XOR = 285,               /* BITOP_XOR  */
    LP = 286,                      /* LP  */
    RP = 287,                      /* RP  */
    LB = 288,                      /* LB  */
    RB = 289,                      /* RB  */
    SEMI = 290,                    /* SEMI  */
    COMMA = 291,                   /* COMMA  */
    LC = 292,                      /* LC  */
    RC = 293,                      /* RC  */
    ILLEGAL_TOKEN = 294,           /* ILLEGAL_TOKEN  */
    LOWER_ELSE = 295,              /* LOWER_ELSE  */
    ELSE = 296                     /* ELSE  */
  };
  typedef enum yytokentype yytoken_kind_t;
#endif

/* Value type.  */
#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
union YYSTYPE
{
#line 9 "syntax.y"

    Node* node;

#line 109 "syntax.tab.h"

};
typedef union YYSTYPE YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define YYSTYPE_IS_DECLARED 1
#endif

/* Location type.  */
#if ! defined YYLTYPE && ! defined YYLTYPE_IS_DECLARED
typedef struct YYLTYPE YYLTYPE;
struct YYLTYPE
{
  int first_line;
  int first_column;
  int last_line;
  int last_column;
};
# define YYLTYPE_IS_DECLARED 1
# define YYLTYPE_IS_TRIVIAL 1
#endif


extern YYSTYPE yylval;
extern YYLTYPE yylloc;

int yyparse (void);


#endif /* !YY_YY_SYNTAX_TAB_H_INCLUDED  */
