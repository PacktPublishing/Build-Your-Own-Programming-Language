//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package ch5;
import static ch5.j0.yylex;
import static ch5.yyerror.yyerror;







public class parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class parserVal is defined in parserVal.java


String   yytext;//user variable to return contextual strings
parserVal yyval; //used to return semantic vals from action routines
parserVal yylval;//the 'lval' (result) I got from yylex()
parserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new parserVal[YYSTACKSIZE];
  yyval=new parserVal();
  yylval=new parserVal();
  valptr=-1;
}
void val_push(parserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
parserVal val_pop()
{
  if (valptr<0)
    return new parserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
parserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new parserVal();
  return valstk[ptr];
}
final parserVal dup_yyval(parserVal val)
{
  parserVal dup = new parserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short BREAK=257;
public final static short DOUBLE=258;
public final static short ELSE=259;
public final static short FOR=260;
public final static short IF=261;
public final static short INT=262;
public final static short RETURN=263;
public final static short VOID=264;
public final static short WHILE=265;
public final static short IDENTIFIER=266;
public final static short CLASSNAME=267;
public final static short CLASS=268;
public final static short STRING=269;
public final static short BOOL=270;
public final static short INTLIT=271;
public final static short DOUBLELIT=272;
public final static short STRINGLIT=273;
public final static short BOOLLIT=274;
public final static short NULLVAL=275;
public final static short LESSTHANOREQUAL=276;
public final static short GREATERTHANOREQUAL=277;
public final static short ISEQUALTO=278;
public final static short NOTEQUALTO=279;
public final static short LOGICALAND=280;
public final static short LOGICALOR=281;
public final static short INCREMENT=282;
public final static short DECREMENT=283;
public final static short PUBLIC=284;
public final static short STATIC=285;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    2,    2,    3,    3,    3,    4,    7,
    7,    7,    7,    7,    9,    9,   10,    8,    8,   11,
   11,   12,   12,    5,   13,   15,   16,   16,   17,   17,
   18,    6,   14,   19,   19,   20,   20,   21,   21,   22,
   24,   23,   23,   23,   23,   23,   23,   23,   23,   23,
   23,   25,   33,   33,   28,   29,   30,   30,   37,   37,
   38,   31,   32,   39,   39,   39,   40,   40,   41,   41,
   42,   42,   26,   26,   27,   43,   43,   43,   43,   44,
   44,   44,   44,   44,   46,   46,   45,   47,   47,   35,
   35,   48,   48,   49,   49,   49,   50,   50,   50,   50,
   51,   51,   51,   52,   52,   52,   52,   53,   53,   54,
   54,   54,   55,   55,   56,   56,   36,   36,   34,   57,
   57,   58,   58,   58,
};
final static short yylen[] = {                            2,
    4,    3,    2,    1,    2,    1,    1,    1,    3,    1,
    1,    1,    1,    1,    1,    1,    3,    1,    3,    1,
    3,    1,    1,    2,    4,    4,    1,    0,    1,    3,
    2,    2,    3,    1,    0,    1,    2,    1,    1,    2,
    2,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    2,    1,    1,    5,    7,    6,    8,    1,    2,
    2,    5,    9,    1,    1,    0,    1,    0,    1,    0,
    1,    3,    2,    3,    3,    1,    1,    1,    3,    1,
    1,    1,    1,    1,    1,    3,    3,    1,    0,    4,
    6,    1,    1,    2,    2,    1,    1,    3,    3,    3,
    1,    3,    3,    1,    1,    1,    1,    1,    3,    1,
    3,    3,    1,    3,    1,    3,    1,    1,    3,    1,
    1,    1,    1,    1,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    1,   11,   10,    0,   13,
   12,    0,    3,    0,    4,    6,    7,    8,    0,    0,
   16,    0,    0,    0,    0,    2,    5,   20,    0,    0,
    0,    0,   24,   32,   15,    0,    0,    0,   29,   23,
   22,    0,    9,    0,    0,   17,    0,    0,    0,    0,
    0,   80,   81,   83,   82,   84,   43,    0,    0,    0,
   42,    0,    0,   36,   38,   39,    0,   44,   45,   46,
   47,   48,   49,   50,   51,    0,   53,    0,    0,   76,
    0,    0,    0,   26,    0,    0,   25,    0,   21,    0,
   73,    0,    0,    0,    0,    0,  118,   78,   67,    0,
    0,   96,   97,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   33,   37,   40,   52,    0,  123,  124,
  122,    0,   30,   74,   65,   71,    0,    0,    0,    0,
   77,   94,   95,   75,    0,    0,    0,    0,    0,  104,
  105,  106,  107,    0,    0,    0,    0,    0,    0,   79,
   85,    0,    0,    0,  119,    0,    0,    0,   98,   99,
  100,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   90,    0,    0,    0,   72,    0,   62,   86,    0,    0,
    0,    0,   59,   91,    0,    0,    0,   56,   61,    0,
   60,    0,    0,   58,   63,    0,    0,   55,
};
final static short yydgoto[] = {                          2,
    6,   14,   15,   16,   17,   18,   59,   29,  130,   21,
   30,   42,   22,   61,   23,   37,   38,   39,   62,   63,
   64,   65,   66,   67,   68,   69,   70,   71,   72,   73,
   74,   75,   76,   97,   98,   99,  182,  183,  127,  100,
  185,  128,  101,   80,   81,  152,  153,  102,  103,  104,
  105,  144,  106,  107,  108,  109,   82,  122,
};
final static short yysindex[] = {                      -218,
 -200,    0, -181,  -35,  -95,    0,    0,    0,   51,    0,
    0, -177,    0,  -93,    0,    0,    0,    0, -152,   79,
    0,   16,   16,  227,  217,    0,    0,    0,  -30,   65,
 -133,  169,    0,    0,    0, -152,  117,  116,    0,    0,
    0,  -96,    0, -152,   95,    0,  -46,  132,  150,   50,
  153,    0,    0,    0,    0,    0,    0,   50, -152,   52,
    0,   72,  169,    0,    0,    0,  141,    0,    0,    0,
    0,    0,    0,    0,    0,  143,    0,    0,  158,    0,
    0,  -24,   65,    0,  227,   51,    0,   65,    0,  148,
    0,  -39,   50,   50,   50,   52,    0,    0,    0,  151,
  158,    0,    0,  145,  109,   97, -100,  -69,  -68,   50,
  171,  170,   50,    0,    0,    0,    0,  -51,    0,    0,
    0,   50,    0,    0,    0,    0,  157,  173,  177,   52,
    0,    0,    0,    0,   50,   50,   50,   50,   50,    0,
    0,    0,    0,   50,   50,   50,   50,   50,  180,    0,
    0,  182,  188,  197,    0,   50,   -1,   16,    0,    0,
    0,  145,  145,  109,   97,   97, -100,  -69,  268,   50,
    0,   50,  179,   52,    0,  -11,    0,    0,  219,   -1,
 -111,    9,    0,    0,  222,  173,  224,    0,    0, -111,
    0,   16,   50,    0,    0,  234,   16,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,  -44,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   19,
    0,    0,    0,  235,    0,    0,    0,    0,    0,    6,
    0,  172,    0,    0,    0,    0,    0,  257,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  240,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  -58,
    0,    0,  175,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  107,    0,    0,
  -37,    0,  -13,    0,    0,    0,    0,   14,    0,    0,
    0,  246,    0,    0,    0,    1,    0,    0,    0,    0,
   10,    0,    0,   56,   76,   15,   90,   45,   43,    0,
    0,  247,  266,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  250,    0,   34,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  276,    0,  -26,    0,  240,    0,    0,    0,    0,
    0,   62,   68,   82,   23,   88,   96,  102,    0,    0,
    0,  266,    0,  -21,    0,  124,    0,    0,    0,  277,
    0,  144,    0,    0,    0,  278,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,    0,  314,    0,    0,    0,  181,  270,  445,    0,
    5,    0,    0,    4,  289,    0,    0,  245,    0,    0,
  287,    0,  195,  273,    0,    0,    0,  -71,    0,    0,
    0,    0,  -12,  288,  388,  378,    0,  190,    0,  223,
    0,  198,  390,    0,  441,    0,  203,    0,  329,   42,
  244,    0,   53,  253,  255,    0,    0,    0,
};
final static int YYTABLESIZE=638;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         77,
   58,   15,  120,   77,   77,   77,   77,   77,   77,   77,
   87,   32,   91,   44,   87,   87,   87,   87,   87,   87,
   87,   77,   77,  121,   77,   33,   34,   31,   43,   13,
   31,   26,   87,   87,   87,   87,  121,   93,   58,  120,
   83,   93,   93,   93,   93,   93,   92,   93,   88,   18,
   92,   92,   92,   92,   92,  110,   92,   19,  110,   93,
   93,  120,   93,  111,   18,    1,  111,    3,   92,   92,
   93,   92,   19,  110,   93,   93,   93,   93,   93,  126,
   93,  111,   95,  117,    4,  115,  117,    5,  115,   58,
   24,  113,   93,   93,   94,   93,  101,   31,  101,  101,
  101,  117,  103,  115,  103,  103,  103,   25,  102,  189,
  102,  102,  102,   28,  101,  101,  108,  101,  189,  108,
  103,  103,  109,  103,   31,  109,  102,  102,  112,  102,
  113,  112,   46,  113,  108,  108,  114,  108,   32,  114,
  109,  109,  116,  109,  175,  116,  112,   54,  113,  187,
   54,  139,   78,  138,  114,   45,  142,   84,  143,   85,
  116,  176,    7,   55,    7,   54,    8,  126,    8,   86,
    9,   92,    9,   10,   11,   10,   11,  145,  146,  162,
  163,  137,   55,   57,  188,   19,  135,   89,   12,   93,
   12,  136,  110,  194,   19,  195,  114,  165,  166,  116,
  198,  117,   57,  118,   36,   41,  124,   14,   58,  134,
  147,  150,  148,   44,  154,  156,  157,  158,    7,   90,
  169,   15,    8,  120,  120,  170,   35,   57,  171,   10,
   11,   52,   53,   54,   55,   56,  172,  180,   77,   77,
   77,   77,   77,   77,  121,  121,   55,  181,   55,   87,
   87,   87,   87,   87,   87,   87,   87,  119,  120,  184,
  120,  120,  192,  193,   35,   36,   57,  190,   57,   52,
   53,   54,   55,   56,  197,   28,   93,   93,   93,   93,
   93,   93,  120,  120,   14,   92,   92,   92,   92,   92,
   92,   32,  110,  110,  110,  110,   35,   27,   68,   34,
  111,  111,  111,  111,   66,   41,   89,   58,   64,   93,
   93,   93,   93,   93,   93,   35,   88,   70,   69,   77,
   52,   53,   54,   55,   56,  115,   57,   27,  112,  123,
   87,  101,  101,  101,  101,  101,  101,  103,  103,  103,
  103,  103,  103,  102,  102,  102,  102,  102,  102,  115,
   77,  108,  108,  108,  108,  108,  108,  109,  109,  109,
  109,  109,  109,  177,  125,  112,  112,  112,  112,  113,
  113,  191,  140,  141,  179,  114,  114,  186,  173,   77,
   55,   55,  116,   55,   55,   55,   55,  164,   55,   55,
   32,    0,   55,   55,   55,   55,   55,   55,   55,  167,
   57,   57,  168,   57,   57,   57,   57,    0,   57,   57,
    0,    0,   57,   57,   57,   57,   57,   57,   57,   78,
    0,   79,  132,  133,    0,   47,    7,    0,   48,   49,
    8,   50,    0,   51,   35,  111,    0,   10,   11,   52,
   53,   54,   55,   56,   77,    0,    0,    0,    0,   20,
   78,    0,   79,    0,    0,    0,   77,    0,   20,    0,
    0,    0,    0,  159,  160,  161,    0,   77,   20,   20,
  129,    0,    0,    0,    7,    0,   60,    0,    8,   78,
   40,   79,   35,    0,    7,   10,   11,  149,    8,    0,
  151,    0,   35,    0,   96,   10,   11,    0,    0,  155,
    0,    0,   96,    0,    0,    0,    0,   60,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   47,    0,    0,   48,   49,   20,
   50,    0,   51,   35,  131,  131,   60,   96,   52,   53,
   54,   55,   56,    0,   78,    0,   79,  178,    0,  151,
    0,    0,    0,    0,   96,    0,   78,   96,   79,    0,
    0,    0,    0,    0,    0,    0,   96,   78,    0,   79,
  196,    0,    0,    0,    0,  131,  131,  131,  131,  131,
    0,    0,    0,    0,  131,  131,  131,  131,  131,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   96,  174,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  174,   96,    0,   96,    0,    0,    0,
    0,    0,    0,    0,  174,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   96,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         37,
   40,   46,   61,   41,   42,   43,   44,   45,   46,   47,
   37,  123,   59,   44,   41,   42,   43,   44,   45,   46,
   47,   59,   60,   61,   62,   22,   23,   41,   59,  125,
   44,  125,   59,   60,   61,   62,   61,   37,   40,   61,
   36,   41,   42,   43,   44,   45,   37,   47,   44,   44,
   41,   42,   43,   44,   45,   41,   47,   44,   44,   59,
   60,   61,   62,   41,   59,  284,   44,  268,   59,   60,
   37,   62,   59,   59,   41,   42,   43,   44,   45,   92,
   47,   59,   33,   41,  266,   41,   44,  123,   44,   40,
   40,   40,   59,   60,   45,   62,   41,   46,   43,   44,
   45,   59,   41,   59,   43,   44,   45,  285,   41,  181,
   43,   44,   45,  266,   59,   60,   41,   62,  190,   44,
   59,   60,   41,   62,   46,   44,   59,   60,   41,   62,
   41,   44,  266,   44,   59,   60,   41,   62,  123,   44,
   59,   60,   41,   62,  157,   44,   59,   41,   59,  261,
   44,   43,   46,   45,   59,   91,   60,   41,   62,   44,
   59,  158,  258,   40,  258,   59,  262,  180,  262,  266,
  266,   40,  266,  269,  270,  269,  270,  278,  279,  138,
  139,   37,   59,   40,  181,    5,   42,   93,  284,   40,
  284,   47,   40,  190,   14,  192,  125,  145,  146,   59,
  197,   59,   59,   46,   24,   25,   59,  266,   40,   59,
  280,   41,  281,   44,  266,   59,   44,   41,  258,  266,
   41,  266,  262,  282,  283,   44,  266,   59,   41,  269,
  270,  271,  272,  273,  274,  275,   40,   59,  276,  277,
  278,  279,  280,  281,  282,  283,  123,  259,  125,  276,
  277,  278,  279,  280,  281,  282,  283,  282,  283,   41,
  282,  283,   41,   40,  266,   85,  123,  259,  125,  271,
  272,  273,  274,  275,   41,   41,  276,  277,  278,  279,
  280,  281,  282,  283,  266,  276,  277,  278,  279,  280,
  281,  123,  278,  279,  280,  281,  125,   41,   59,  125,
  278,  279,  280,  281,   59,   59,   41,   40,   59,  276,
  277,  278,  279,  280,  281,  266,   41,   41,   41,   32,
  271,  272,  273,  274,  275,  281,   59,   14,   59,   85,
   42,  276,  277,  278,  279,  280,  281,  276,  277,  278,
  279,  280,  281,  276,  277,  278,  279,  280,  281,   63,
   63,  276,  277,  278,  279,  280,  281,  276,  277,  278,
  279,  280,  281,  169,   92,  278,  279,  280,  281,  280,
  281,  182,  276,  277,  172,  280,  281,  180,  156,   92,
  257,  258,  281,  260,  261,  262,  263,  144,  265,  266,
  123,   -1,  269,  270,  271,  272,  273,  274,  275,  147,
  257,  258,  148,  260,  261,  262,  263,   -1,  265,  266,
   -1,   -1,  269,  270,  271,  272,  273,  274,  275,   32,
   -1,   32,   94,   95,   -1,  257,  258,   -1,  260,  261,
  262,  263,   -1,  265,  266,   58,   -1,  269,  270,  271,
  272,  273,  274,  275,  157,   -1,   -1,   -1,   -1,    5,
   63,   -1,   63,   -1,   -1,   -1,  169,   -1,   14,   -1,
   -1,   -1,   -1,  135,  136,  137,   -1,  180,   24,   25,
   93,   -1,   -1,   -1,  258,   -1,   32,   -1,  262,   92,
  264,   92,  266,   -1,  258,  269,  270,  110,  262,   -1,
  113,   -1,  266,   -1,   50,  269,  270,   -1,   -1,  122,
   -1,   -1,   58,   -1,   -1,   -1,   -1,   63,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  257,   -1,   -1,  260,  261,   85,
  263,   -1,  265,  266,   94,   95,   92,   93,  271,  272,
  273,  274,  275,   -1,  157,   -1,  157,  170,   -1,  172,
   -1,   -1,   -1,   -1,  110,   -1,  169,  113,  169,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  122,  180,   -1,  180,
  193,   -1,   -1,   -1,   -1,  135,  136,  137,  138,  139,
   -1,   -1,   -1,   -1,  144,  145,  146,  147,  148,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  156,  157,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  169,  170,   -1,  172,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  180,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  193,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=285;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"'!'",null,null,null,"'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,null,
"';'","'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'['",null,"']'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,"BREAK","DOUBLE","ELSE","FOR","IF",
"INT","RETURN","VOID","WHILE","IDENTIFIER","CLASSNAME","CLASS","STRING","BOOL",
"INTLIT","DOUBLELIT","STRINGLIT","BOOLLIT","NULLVAL","LESSTHANOREQUAL",
"GREATERTHANOREQUAL","ISEQUALTO","NOTEQUALTO","LOGICALAND","LOGICALOR",
"INCREMENT","DECREMENT","PUBLIC","STATIC",
};
final static String yyrule[] = {
"$accept : ClassDecl",
"ClassDecl : PUBLIC CLASS IDENTIFIER ClassBody",
"ClassBody : '{' ClassBodyDecls '}'",
"ClassBody : '{' '}'",
"ClassBodyDecls : ClassBodyDecl",
"ClassBodyDecls : ClassBodyDecls ClassBodyDecl",
"ClassBodyDecl : FieldDecl",
"ClassBodyDecl : MethodDecl",
"ClassBodyDecl : ConstructorDecl",
"FieldDecl : Type VarDecls ';'",
"Type : INT",
"Type : DOUBLE",
"Type : BOOL",
"Type : STRING",
"Type : Name",
"Name : IDENTIFIER",
"Name : QualifiedName",
"QualifiedName : Name '.' IDENTIFIER",
"VarDecls : VarDeclarator",
"VarDecls : VarDecls ',' VarDeclarator",
"VarDeclarator : IDENTIFIER",
"VarDeclarator : VarDeclarator '[' ']'",
"MethodReturnVal : Type",
"MethodReturnVal : VOID",
"MethodDecl : MethodHeader Block",
"MethodHeader : PUBLIC STATIC MethodReturnVal MethodDeclarator",
"MethodDeclarator : IDENTIFIER '(' FormalParmListOpt ')'",
"FormalParmListOpt : FormalParmList",
"FormalParmListOpt :",
"FormalParmList : FormalParm",
"FormalParmList : FormalParmList ',' FormalParm",
"FormalParm : Type VarDeclarator",
"ConstructorDecl : MethodDeclarator Block",
"Block : '{' BlockStmtsOpt '}'",
"BlockStmtsOpt : BlockStmts",
"BlockStmtsOpt :",
"BlockStmts : BlockStmt",
"BlockStmts : BlockStmts BlockStmt",
"BlockStmt : LocalVarDeclStmt",
"BlockStmt : Stmt",
"LocalVarDeclStmt : LocalVarDecl ';'",
"LocalVarDecl : Type VarDecls",
"Stmt : Block",
"Stmt : ';'",
"Stmt : ExprStmt",
"Stmt : BreakStmt",
"Stmt : ReturnStmt",
"Stmt : IfThenStmt",
"Stmt : IfThenElseStmt",
"Stmt : IfThenElseIfStmt",
"Stmt : WhileStmt",
"Stmt : ForStmt",
"ExprStmt : StmtExpr ';'",
"StmtExpr : Assignment",
"StmtExpr : MethodCall",
"IfThenStmt : IF '(' Expr ')' Block",
"IfThenElseStmt : IF '(' Expr ')' Block ELSE Block",
"IfThenElseIfStmt : IF '(' Expr ')' Block ElseIfSequence",
"IfThenElseIfStmt : IF '(' Expr ')' Block ElseIfSequence ELSE Block",
"ElseIfSequence : ElseIfStmt",
"ElseIfSequence : ElseIfSequence ElseIfStmt",
"ElseIfStmt : ELSE IfThenStmt",
"WhileStmt : WHILE '(' Expr ')' Stmt",
"ForStmt : FOR '(' ForInit ';' ExprOpt ';' ForUpdate ')' Block",
"ForInit : StmtExprList",
"ForInit : LocalVarDecl",
"ForInit :",
"ExprOpt : Expr",
"ExprOpt :",
"ForUpdate : StmtExprList",
"ForUpdate :",
"StmtExprList : StmtExpr",
"StmtExprList : StmtExprList ',' StmtExpr",
"BreakStmt : BREAK ';'",
"BreakStmt : BREAK IDENTIFIER ';'",
"ReturnStmt : RETURN ExprOpt ';'",
"Primary : Literal",
"Primary : FieldAccess",
"Primary : MethodCall",
"Primary : '(' Expr ')'",
"Literal : INTLIT",
"Literal : DOUBLELIT",
"Literal : BOOLLIT",
"Literal : STRINGLIT",
"Literal : NULLVAL",
"ArgList : Expr",
"ArgList : ArgList ',' Expr",
"FieldAccess : Primary '.' IDENTIFIER",
"ArgListOpt : ArgList",
"ArgListOpt :",
"MethodCall : Name '(' ArgListOpt ')'",
"MethodCall : Primary '.' IDENTIFIER '(' ArgListOpt ')'",
"PostFixExpr : Primary",
"PostFixExpr : Name",
"UnaryExpr : '-' UnaryExpr",
"UnaryExpr : '!' UnaryExpr",
"UnaryExpr : PostFixExpr",
"MulExpr : UnaryExpr",
"MulExpr : MulExpr '*' UnaryExpr",
"MulExpr : MulExpr '/' UnaryExpr",
"MulExpr : MulExpr '%' UnaryExpr",
"AddExpr : MulExpr",
"AddExpr : AddExpr '+' MulExpr",
"AddExpr : AddExpr '-' MulExpr",
"RelOp : LESSTHANOREQUAL",
"RelOp : GREATERTHANOREQUAL",
"RelOp : '<'",
"RelOp : '>'",
"RelExpr : AddExpr",
"RelExpr : RelExpr RelOp AddExpr",
"EqExpr : RelExpr",
"EqExpr : EqExpr ISEQUALTO RelExpr",
"EqExpr : EqExpr NOTEQUALTO RelExpr",
"CondAndExpr : EqExpr",
"CondAndExpr : CondAndExpr LOGICALAND EqExpr",
"CondOrExpr : CondAndExpr",
"CondOrExpr : CondOrExpr LOGICALOR CondAndExpr",
"Expr : CondOrExpr",
"Expr : Assignment",
"Assignment : LeftHandSide AssignOp Expr",
"LeftHandSide : Name",
"LeftHandSide : FieldAccess",
"AssignOp : '='",
"AssignOp : INCREMENT",
"AssignOp : DECREMENT",
};

//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 8 "j0gram.y"
{
  yyval=j0.node("ClassDecl",1000,val_peek(1),val_peek(0));
  j0.print(yyval);
 }
break;
case 2:
//#line 12 "j0gram.y"
{ yyval=j0.node("ClassBody",1010,val_peek(1)); }
break;
case 3:
//#line 13 "j0gram.y"
{ yyval=j0.node("ClassBody",1011); }
break;
case 5:
//#line 15 "j0gram.y"
{
  yyval=j0.node("ClassBodyDecls",1020,val_peek(1),val_peek(0)); }
break;
case 9:
//#line 18 "j0gram.y"
{
  yyval=j0.node("FieldDecl",1030,val_peek(2),val_peek(1)); }
break;
case 17:
//#line 23 "j0gram.y"
{
  yyval=j0.node("QualifiedName",1040,val_peek(2),val_peek(0));}
break;
case 19:
//#line 26 "j0gram.y"
{
  yyval=j0.node("VarDecls",1050,val_peek(2),val_peek(0)); }
break;
case 21:
//#line 28 "j0gram.y"
{
  yyval=j0.node("VarDeclarator",1060,val_peek(2)); }
break;
case 24:
//#line 32 "j0gram.y"
{
  yyval=j0.node("MethodDecl",1380,val_peek(1),val_peek(0));
 }
break;
case 25:
//#line 35 "j0gram.y"
{
  yyval=j0.node("MethodHeader",1070,val_peek(1),val_peek(0)); }
break;
case 26:
//#line 37 "j0gram.y"
{
  yyval=j0.node("MethodDeclarator",1080,val_peek(3),val_peek(1)); }
break;
case 30:
//#line 41 "j0gram.y"
{
  yyval=j0.node("FormalParmList",1090,val_peek(2),val_peek(0)); }
break;
case 31:
//#line 43 "j0gram.y"
{
  yyval=j0.node("FormalParm",1100,val_peek(1),val_peek(0));
 }
break;
case 32:
//#line 47 "j0gram.y"
{
  yyval=j0.node("ConstructorDecl",1110,val_peek(1),val_peek(0)); }
break;
case 33:
//#line 50 "j0gram.y"
{yyval=j0.node("Block",1200,val_peek(1));}
break;
case 37:
//#line 52 "j0gram.y"
{
  yyval=j0.node("BlockStmts",1130,val_peek(1),val_peek(0)); }
break;
case 41:
//#line 57 "j0gram.y"
{
  yyval=j0.node("LocalVarDecl",1140,val_peek(1),val_peek(0)); }
break;
case 55:
//#line 68 "j0gram.y"
{
  yyval=j0.node("IfThenStmt",1150,val_peek(2),val_peek(0)); }
break;
case 56:
//#line 70 "j0gram.y"
{
  yyval=j0.node("IfThenElseStmt",1160,val_peek(4),val_peek(2),val_peek(0)); }
break;
case 57:
//#line 72 "j0gram.y"
{
  yyval=j0.node("IfThenElseIfStmt",1170,val_peek(3),val_peek(1),val_peek(0)); }
break;
case 58:
//#line 74 "j0gram.y"
{
  yyval=j0.node("IfThenElseIfStmt",1171,val_peek(5),val_peek(3),val_peek(2),val_peek(0)); }
break;
case 60:
//#line 77 "j0gram.y"
{
  yyval=j0.node("ElseIfSequence",1180,val_peek(1),val_peek(0)); }
break;
case 61:
//#line 79 "j0gram.y"
{
  yyval=j0.node("ElseIfStmt",1190,val_peek(0)); }
break;
case 62:
//#line 81 "j0gram.y"
{
  yyval=j0.node("WhileStmt",1210,val_peek(2),val_peek(0)); }
break;
case 63:
//#line 84 "j0gram.y"
{
  yyval=j0.node("ForStmt",1220,val_peek(6),val_peek(4),val_peek(2),val_peek(0)); }
break;
case 72:
//#line 90 "j0gram.y"
{
  yyval=j0.node("StmtExprList",1230,val_peek(2),val_peek(0)); }
break;
case 74:
//#line 93 "j0gram.y"
{
  yyval=j0.node("BreakStmt",1240,val_peek(1)); }
break;
case 75:
//#line 95 "j0gram.y"
{
  yyval=j0.node("ReturnStmt",1250,val_peek(1)); }
break;
case 79:
//#line 98 "j0gram.y"
{
  yyval=val_peek(1);}
break;
case 86:
//#line 102 "j0gram.y"
{
  yyval=j0.node("ArgList",1270,val_peek(2),val_peek(0)); }
break;
case 87:
//#line 104 "j0gram.y"
{
  yyval=j0.node("FieldAccess",1280,val_peek(2),val_peek(0)); }
break;
case 90:
//#line 108 "j0gram.y"
{
  yyval=j0.node("MethodCall",1290,val_peek(3),val_peek(1)); }
break;
case 91:
//#line 110 "j0gram.y"
{
    yyval=j0.node("MethodCall",1291,val_peek(5),val_peek(3),val_peek(1)); }
break;
case 94:
//#line 115 "j0gram.y"
{
  yyval=j0.node("UnaryExpr",1300,val_peek(1),val_peek(0)); }
break;
case 95:
//#line 117 "j0gram.y"
{
  yyval=j0.node("UnaryExpr",1301,val_peek(1),val_peek(0)); }
break;
case 98:
//#line 121 "j0gram.y"
{
      yyval=j0.node("MulExpr",1310,val_peek(2),val_peek(0)); }
break;
case 99:
//#line 123 "j0gram.y"
{
      yyval=j0.node("MulExpr",1311,val_peek(2),val_peek(0)); }
break;
case 100:
//#line 125 "j0gram.y"
{
      yyval=j0.node("MulExpr",1312,val_peek(2),val_peek(0)); }
break;
case 102:
//#line 128 "j0gram.y"
{
      yyval=j0.node("AddExpr",1320,val_peek(2),val_peek(0)); }
break;
case 103:
//#line 130 "j0gram.y"
{
      yyval=j0.node("AddExpr",1321,val_peek(2),val_peek(0)); }
break;
case 109:
//#line 133 "j0gram.y"
{
  yyval=j0.node("RelExpr",1330,val_peek(2),val_peek(1),val_peek(0)); }
break;
case 111:
//#line 137 "j0gram.y"
{
  yyval=j0.node("EqExpr",1340,val_peek(2),val_peek(0)); }
break;
case 112:
//#line 139 "j0gram.y"
{
  yyval=j0.node("EqExpr",1341,val_peek(2),val_peek(0)); }
break;
case 114:
//#line 141 "j0gram.y"
{
  yyval=j0.node("CondAndExpr", 1350, val_peek(2), val_peek(0)); }
break;
case 116:
//#line 143 "j0gram.y"
{
  yyval=j0.node("CondOrExpr", 1360, val_peek(2), val_peek(0)); }
break;
case 119:
//#line 147 "j0gram.y"
{
yyval=j0.node("Assignment",1370, val_peek(2), val_peek(1), val_peek(0)); }
break;
//#line 900 "parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
