%token BREAK DOUBLE ELSE FOR IF INT RETURN VOID WHILE
%token IDENTIFIER CLASSNAME CLASS STRING BOOL
%token INTLIT DOUBLELIT STRINGLIT BOOLLIT NULLVAL
%token LESSTHANOREQUAL GREATERTHANOREQUAL
%token ISEQUALTO NOTEQUALTO LOGICALAND LOGICALOR
%token INCREMENT DECREMENT PUBLIC STATIC
%%
ClassDecl: PUBLIC CLASS IDENTIFIER ClassBody {
  $$=j0.node("ClassDecl",1000,$3,$4);
  j0.print($$);
 } ;
ClassBody: '{' ClassBodyDecls '}' { $$=j0.node("ClassBody",1010,$2); }
         | '{' '}' { $$=j0.node("ClassBody",1011); };
ClassBodyDecls: ClassBodyDecl
| ClassBodyDecls ClassBodyDecl {
  $$=j0.node("ClassBodyDecls",1020,$1,$2); };
ClassBodyDecl: FieldDecl | MethodDecl | ConstructorDecl ;
FieldDecl: Type VarDecls ';' {
  $$=j0.node("FieldDecl",1030,$1,$2); };
Type: INT | DOUBLE | BOOL | STRING | Name ;

Name: IDENTIFIER | QualifiedName ;
QualifiedName: Name '.' IDENTIFIER {
  $$=j0.node("QualifiedName",1040,$1,$3);};

VarDecls: VarDeclarator | VarDecls ',' VarDeclarator {
  $$=j0.node("VarDecls",1050,$1,$3); };
VarDeclarator: IDENTIFIER | VarDeclarator '[' ']' {
  $$=j0.node("VarDeclarator",1060,$1); };

MethodReturnVal : Type | VOID ;
MethodDecl: MethodHeader Block {
  $$=j0.node("MethodDecl",1380,$1,$2);
 };
MethodHeader: PUBLIC STATIC MethodReturnVal MethodDeclarator {
  $$=j0.node("MethodHeader",1070,$3,$4); };
MethodDeclarator: IDENTIFIER '(' FormalParmListOpt ')' {
  $$=j0.node("MethodDeclarator",1080,$1,$3); };

FormalParmListOpt: FormalParmList | ;
FormalParmList: FormalParm | FormalParmList ',' FormalParm {
  $$=j0.node("FormalParmList",1090,$1,$3); };
FormalParm: Type VarDeclarator {
  $$=j0.node("FormalParm",1100,$1,$2);
 };

ConstructorDecl: MethodDeclarator Block {
  $$=j0.node("ConstructorDecl",1110,$1,$2); };

Block: '{' BlockStmtsOpt '}' {$$=j0.node("Block",1200,$2);};
BlockStmtsOpt: BlockStmts | ;
BlockStmts:  BlockStmt | BlockStmts BlockStmt {
  $$=j0.node("BlockStmts",1130,$1,$2); };
BlockStmt:   LocalVarDeclStmt | Stmt ;

LocalVarDeclStmt: LocalVarDecl ';' ;
LocalVarDecl: Type VarDecls {
  $$=j0.node("LocalVarDecl",1140,$1,$2); };

Stmt: Block | ';' | ExprStmt | BreakStmt | ReturnStmt
      | IfThenStmt | IfThenElseStmt | IfThenElseIfStmt
      | WhileStmt | ForStmt ;

ExprStmt: StmtExpr ';' ;

StmtExpr: Assignment | MethodCall ;

IfThenStmt: IF '(' Expr ')' Block {
  $$=j0.node("IfThenStmt",1150,$3,$5); };
IfThenElseStmt: IF '(' Expr ')' Block ELSE Block {
  $$=j0.node("IfThenElseStmt",1160,$3,$5,$7); };
IfThenElseIfStmt: IF '(' Expr ')' Block ElseIfSequence {
  $$=j0.node("IfThenElseIfStmt",1170,$3,$5,$6); }
|  IF '(' Expr ')' Block ElseIfSequence ELSE Block {
  $$=j0.node("IfThenElseIfStmt",1171,$3,$5,$6,$8); };

ElseIfSequence: ElseIfStmt | ElseIfSequence ElseIfStmt {
  $$=j0.node("ElseIfSequence",1180,$1,$2); };
ElseIfStmt: ELSE IfThenStmt {
  $$=j0.node("ElseIfStmt",1190,$2); };
WhileStmt: WHILE '(' Expr ')' Stmt {
  $$=j0.node("WhileStmt",1210,$3,$5); };

ForStmt: FOR '(' ForInit ';' ExprOpt ';' ForUpdate ')' Block {
  $$=j0.node("ForStmt",1220,$3,$5,$7,$9); };
ForInit: StmtExprList | LocalVarDecl | ;
ExprOpt: Expr |  ;
ForUpdate: StmtExprList | ;

StmtExprList: StmtExpr | StmtExprList ',' StmtExpr {
  $$=j0.node("StmtExprList",1230,$1,$3); };

BreakStmt: BREAK ';' | BREAK IDENTIFIER ';' {
  $$=j0.node("BreakStmt",1240,$2); };
ReturnStmt: RETURN ExprOpt ';' {
  $$=j0.node("ReturnStmt",1250,$2); };

Primary:  Literal | FieldAccess | MethodCall | '(' Expr ')' {
  $$=$2;};
Literal: INTLIT	| DOUBLELIT | BOOLLIT | STRINGLIT | NULLVAL ;

ArgList: Expr | ArgList ',' Expr {
  $$=j0.node("ArgList",1270,$1,$3); };
FieldAccess: Primary '.' IDENTIFIER {
  $$=j0.node("FieldAccess",1280,$1,$3); };

ArgListOpt:  ArgList | ;
MethodCall: Name '(' ArgListOpt ')' {
  $$=j0.node("MethodCall",1290,$1,$3); }
  | Primary '.' IDENTIFIER '(' ArgListOpt ')' {
    $$=j0.node("MethodCall",1291,$1,$3,$5); }
	;

PostFixExpr: Primary | Name ;
UnaryExpr:  '-' UnaryExpr {
  $$=j0.node("UnaryExpr",1300,$1,$2); }
    | '!' UnaryExpr {
  $$=j0.node("UnaryExpr",1301,$1,$2); }
    | PostFixExpr ;
MulExpr: UnaryExpr
    | MulExpr '*' UnaryExpr {
      $$=j0.node("MulExpr",1310,$1,$3); }
    | MulExpr '/' UnaryExpr {
      $$=j0.node("MulExpr",1311,$1,$3); }
    | MulExpr '%' UnaryExpr {
      $$=j0.node("MulExpr",1312,$1,$3); };
AddExpr: MulExpr
    | AddExpr '+' MulExpr {
      $$=j0.node("AddExpr",1320,$1,$3); }
    | AddExpr '-' MulExpr {
      $$=j0.node("AddExpr",1321,$1,$3); };
RelOp: LESSTHANOREQUAL | GREATERTHANOREQUAL | '<' | '>' ;
RelExpr: AddExpr | RelExpr RelOp AddExpr {
  $$=j0.node("RelExpr",1330,$1,$2,$3); };

EqExpr: RelExpr
    | EqExpr ISEQUALTO RelExpr {
  $$=j0.node("EqExpr",1340,$1,$3); }
| EqExpr NOTEQUALTO RelExpr {
  $$=j0.node("EqExpr",1341,$1,$3); };
CondAndExpr: EqExpr | CondAndExpr LOGICALAND EqExpr {
  $$=j0.node("CondAndExpr", 1350, $1, $3); };
CondOrExpr: CondAndExpr | CondOrExpr LOGICALOR CondAndExpr {
  $$=j0.node("CondOrExpr", 1360, $1, $3); };

Expr: CondOrExpr | Assignment ;
Assignment: LeftHandSide AssignOp Expr {
$$=j0.node("Assignment",1370, $1, $2, $3); };
LeftHandSide: Name | FieldAccess ;
AssignOp: '=' | INCREMENT | DECREMENT ;
