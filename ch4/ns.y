%token NAME NUMBER
%%
sequence : pair sequence | ;
pair : NAME NUMBER ;
%%
