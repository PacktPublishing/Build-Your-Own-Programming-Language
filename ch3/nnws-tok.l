%%
%int
%%
[a-zA-Z]+  { return simple2.scan(1); }
[0-9]+     { return simple2.scan(2); }
[ \t]+     { }
\r?\n      { simple2.increment_lineno(); }
.          { simple2.lexErr("unrecognized character"); }
