all: java unicon

LYU=javalex.u j0gram.u j0gram_tab.u
U=j0.u token.u tree.u serial.u yyerror.u $(LYU)
unicon: j0
%.u : %.icn
	unicon -c $<
j0: $(U)
	unicon $(U)
javalex.icn: javalex.l
	uflex javalex.l
j0gram.icn j0gram_tab.icn: j0gram.y
	iyacc -dd j0gram.y


# A typical run might use
# set CLASSPATH=".;c:\users\username\byopl"
# in order to run from c:\users\username\byopl\ch5
BYSRC=parser.java parserVal.java
JSRC=j0.java Yylex.java token.java yyerror.java tree.java serial.java $(BYSRC)
BYJOPTS= -Jclass=parser -Jpackage=ch5
BYJIMPS= -Jyylex=ch5.j0.yylex -Jyyerror=ch5.yyerror.yyerror
java: j0.class

j: java
	java ch5.j0 hello.java
	dot -Tpng hello.java.dot >hello.png

j0.class: $(JSRC)
	javac $(JSRC)
parser.java parserVal.java: j0gram.y
	yacc $(BYJOPTS) $(BYJIMPS) j0gram.y
Yylex.java: javalex.l
	jflex javalex.l

