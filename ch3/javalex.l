%%
%int
id=([a-zA-Z_][a-zA-Z0-9_]*)
%%
"/*"([^*]|"*"+[^/*])*"*"+"/" { j0.comment(); }
"//".*\r?\n                  { j0.comment(); }
[ \t\r\f]+                   { j0.whitespace(); }
\n             { j0.newline(); }
"break"                { return j0.scan(parser.BREAK); }
"double"               { return j0.scan(parser.DOUBLE); } 
"else"                 { return j0.scan(parser.ELSE); }
"false"                { return j0.scan(parser.BOOLLIT); }
"for"                  { return j0.scan(parser.FOR); }
"if"                   { return j0.scan(parser.IF); }
"int"                  { return j0.scan(parser.INT); }
"null"                 { return j0.scan(parser.NULLVAL); }
"public"               { return j0.scan(parser.PUBLIC); }
"return"               { return j0.scan(parser.RETURN); }
"static"               { return j0.scan(parser.STATIC); }
"string"               { return j0.scan(parser.STRING); }
"true"                 { return j0.scan(parser.BOOLLIT); }
"bool"                 { return j0.scan(parser.BOOL); }
"void"                 { return j0.scan(parser.VOID); }
"while"                { return j0.scan(parser.WHILE); }
"class"                { return j0.scan(parser.CLASS); }
"("                    { return j0.scan(j0.ord("("));}
")"                    { return j0.scan(j0.ord(")"));}
"["                    { return j0.scan(j0.ord("["));}
"]"                    { return j0.scan(j0.ord("]"));}
"{"                    { return j0.scan(j0.ord("{"));}
"}"                    { return j0.scan(j0.ord("}"));}
";"                    { return j0.scan(j0.ord(";"));}
":"                    { return j0.scan(j0.ord(":"));}
"!"                    { return j0.scan(j0.ord("!"));}
"*"                    { return j0.scan(j0.ord("*"));}
"/"                    { return j0.scan(j0.ord("/"));}
"%"                    { return j0.scan(j0.ord("%"));}
"+"                    { return j0.scan(j0.ord("+"));}
"-"                    { return j0.scan(j0.ord("-"));}
"<"                    { return j0.scan(j0.ord("<"));}
"<="                   { return j0.scan(parser.LESSTHANOREQUAL);}
">"                    { return j0.scan(j0.ord(">"));}
">="                   { return j0.scan(parser.GREATERTHANOREQUAL);}
"=="                   { return j0.scan(parser.ISEQUALTO);}
"!="                   { return j0.scan(parser.NOTEQUALTO);}
"&&"                   { return j0.scan(parser.LOGICALAND);}
"||"                   { return j0.scan(parser.LOGICALOR);}
"="                    { return j0.scan(j0.ord("=")); }
"+="                   { return j0.scan(parser.INCREMENT); }
"-="                   { return j0.scan(parser.DECREMENT); }
","                    { return j0.scan(j0.ord(",")); }
"."                    { return j0.scan(j0.ord(".")); }
{id}                   { return j0.scan(parser.IDENTIFIER); }
[0-9]+                 { return j0.scan(parser.INTLIT); }
[0-9]*"."[0-9]*([eE][+-]?[0-9]+)? { return j0.scan(parser.DOUBLELIT); }
([0-9]+)([eE][+-]?([0-9]+))  { return j0.scan(parser.DOUBLELIT); }
\"[^\"]*\"     { return j0.scan(parser.STRINGLIT); }
.              { j0.lexErr("unrecognized character"); }
