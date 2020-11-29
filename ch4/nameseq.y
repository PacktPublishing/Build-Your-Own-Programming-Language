%{
import static ch4.lexer.yylex;
%}
%token NAME NUMBER
%%
sequence : pair sequence | ;
pair : NAME NUMBER ;
%%
