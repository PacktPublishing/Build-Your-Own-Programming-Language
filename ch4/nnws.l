package ch4;
%%
%int
%%
[a-zA-Z]+   { return Parser.NAME; }
[0-9]+      { return Parser.NUMBER; }
[ \t]+      { }
.           { lexer.lexErr("unrecognized character"); }
