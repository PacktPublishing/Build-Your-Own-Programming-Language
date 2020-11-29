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



package ch4;
import static ch4.j0.yylex;
import static ch4.yyerror.yyerror;







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
   18,    6,   19,   20,   20,   14,   22,   22,   23,   23,
   24,   24,   25,   27,   26,   26,   26,   26,   26,   26,
   26,   26,   26,   26,   26,   36,   28,   37,   37,   37,
   31,   32,   33,   33,   42,   42,   43,   34,   35,   44,
   44,   44,   45,   45,   46,   46,   47,   47,   29,   29,
   30,   48,   48,   48,   48,   49,   49,   49,   49,   49,
   40,   21,   21,   50,   39,   39,   39,   39,   51,   51,
   52,   52,   52,   53,   53,   53,   53,   54,   54,   54,
   55,   55,   55,   55,   56,   56,   57,   57,   57,   58,
   58,   59,   59,   41,   41,   38,   60,   60,   61,   61,
   61,
};
final static short yylen[] = {                            2,
    4,    3,    2,    1,    2,    1,    1,    1,    3,    1,
    1,    1,    1,    1,    1,    1,    3,    1,    3,    1,
    3,    1,    1,    2,    4,    4,    1,    0,    1,    3,
    2,    2,    4,    1,    0,    3,    1,    0,    1,    2,
    1,    1,    2,    2,    1,    1,    1,    1,    1,    0,
    1,    1,    1,    1,    1,    0,    2,    1,    1,    1,
    5,    7,    6,    8,    1,    2,    2,    5,    9,    1,
    1,    0,    1,    0,    1,    0,    1,    3,    2,    3,
    3,    1,    3,    1,    1,    1,    1,    1,    1,    1,
    4,    1,    3,    3,    4,    4,    6,    6,    1,    1,
    2,    2,    1,    1,    3,    3,    3,    1,    3,    3,
    1,    1,    1,    1,    1,    3,    1,    3,    3,    1,
    3,    1,    3,    1,    1,    3,    1,    1,    1,    1,
    1,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    1,   11,   10,    0,   13,
   12,    0,    3,    0,    4,    6,    7,    8,    0,    0,
   16,    0,    0,    0,    0,    2,    5,   20,    0,    0,
    0,    0,   24,   32,   15,    0,    0,    0,   29,   23,
   22,    0,    9,    0,    0,   17,    0,    0,    0,    0,
    0,   86,   87,   89,   88,   90,   46,    0,    0,    0,
   45,    0,    0,   39,   41,   42,    0,   47,   48,   49,
   51,   52,   53,   54,   55,    0,   58,    0,   60,    0,
   82,    0,    0,    0,   33,    0,    0,   25,    0,   21,
    0,   79,    0,    0,    0,    0,    0,  125,   85,   73,
    0,    0,  103,  104,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   36,   40,   43,   57,    0,
  130,  131,  129,    0,   30,    0,   80,   71,   77,    0,
    0,    0,    0,   84,  101,  102,    0,   81,    0,    0,
    0,    0,    0,  111,  112,  113,  114,    0,    0,    0,
    0,    0,    0,   83,    0,    0,   92,    0,    0,  126,
    0,    0,    0,    0,    0,  105,  106,  107,    0,    0,
    0,    0,    0,    0,    0,    0,   96,    0,    0,    0,
    0,   26,    0,    0,   78,    0,   95,   68,   93,    0,
    0,    0,    0,    0,   65,   98,   97,    0,    0,    0,
   62,   67,    0,   66,    0,    0,   64,   69,    0,    0,
   61,
};
final static short yydgoto[] = {                          2,
    6,   14,   15,   16,   17,   18,   36,   29,   97,   21,
   30,   42,   22,   61,   88,   37,   38,   39,   23,  155,
  156,   62,   63,   64,   65,   66,   67,   68,   69,   70,
   71,   72,   73,   74,   75,    0,   76,   98,   99,   79,
  157,  194,  195,  130,  101,  198,  131,  102,   81,   82,
  103,  104,  105,  106,  148,  107,  108,  109,  110,   83,
  124,
};
final static short yysindex[] = {                      -272,
 -229,    0, -216,  -57,  -95,    0,    0,    0,   28,    0,
    0, -194,    0,  243,    0,    0,    0,    0, -155,   74,
    0,   35,   35,  346,  193,    0,    0,    0,   14,   81,
 -106,  207,    0,    0,    0, -155,  142,  147,    0,    0,
    0,  -69,    0, -155,  107,    0,  -56,  172,  177,   50,
  178,    0,    0,    0,    0,    0,    0,   50, -155,  -14,
    0,   95,  207,    0,    0,    0,  166,    0,    0,    0,
    0,    0,    0,    0,    0,  167,    0,    0,    0,  181,
    0,    0,  -34,   81,    0,  346,  189,    0,   81,    0,
  173,    0,  225,   50,   50,   50,  115,    0,    0,    0,
  199,  181,    0,    0,  156,  170,   43, -145,  -49,  -44,
   50,  195,  216,   50,   50,    0,    0,    0,    0,   -2,
    0,    0,    0,   50,    0,  346,    0,    0,    0,  208,
  229,  234,  115,    0,    0,    0,   50,    0,   50,   50,
   50,   50,   50,    0,    0,    0,    0,   50,   50,   50,
   50,   50,  235,    0,  149,  241,    0,  251,    9,    0,
  259,   50,   -3,   35,  267,    0,    0,    0,  156,  156,
  170,   43,   43, -145,  -49,  258,    0,   50,    0,   50,
   50,    0,  250,  -14,    0,   59,    0,    0,    0,  201,
  290,   -3, -109,   73,    0,    0,    0,  310,  229,  312,
    0,    0, -109,    0,   35,   50,    0,    0,  324,   35,
    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,  -45,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  100,
    0,    0,    0,  326,    0,    0,    0,    0,    0,   21,
    0,  244,    0,    0,    0,    0,    0,  338,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  323,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  -59,
    0,    0,  262,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  146,    0,    0,
    0,  -37,    0,   45,    0,    0,    0,    0,   71,    0,
    0,    0,  333,    0,    0,    0,    1,    0,    0,    0,
    0,   10,    0,    0,   57,   77,   15,  103,  109,  137,
    0,    0,  341,  264,  362,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  326,    0,    0,    0,    0,
  345,    0,   34,    0,    0,    0,  362,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  -28,    0,    0,  -26,    0,
    0,  323,    0,    0,    0,    0,    0,    0,   63,   69,
   83,   23,   97,  105,  110,  -85,    0,    0,  160,  264,
  362,    0,    0,   24,    0,  136,    0,    0,    0,    0,
    0,  371,    0,  174,    0,    0,    0,    0,  372,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,
};
final static short yygindex[] = {                         0,
    0,    0,  400,    0,    0,    0,  520,  356,  479,    0,
   48,    0,    0,    6,    0,  291,    0,  332,    0,  190,
    0,    0,    0,  353,    0,  245,  327,    0,    0,    0,
 -162,    0,    0,    0,    0,    0,   -6,  363,  395,    0,
  330,    0,  228,    0,  257,    0,  231,  397,    0,  451,
    0,  233,   37,  277,    0,   16,  279,  276,    0,    0,
    0,
};
final static int YYTABLESIZE=671;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         84,
   15,  127,   92,   84,   84,   84,   84,   84,   84,   84,
   94,    1,   34,   32,   94,   94,   94,   94,   94,   94,
   94,   84,   84,  128,   84,  115,  123,   33,   34,   13,
  202,   31,   94,   94,   94,   94,   58,  100,    3,   50,
  202,  100,  100,  100,  100,  100,   99,  100,  181,    4,
   99,   99,   99,   99,   99,  117,   99,   44,  117,  100,
  100,  127,  100,  118,   18,    5,  118,   24,   99,   99,
  100,   99,   43,  117,  100,  100,  100,  100,  100,   18,
  100,  118,   96,   84,  127,   31,  129,   84,   31,   58,
   25,   89,  100,  100,   95,  100,   34,  108,   94,  108,
  108,  108,  146,  110,  147,  110,  110,  110,  114,  109,
   28,  109,  109,  109,   19,  108,  108,  115,  108,   31,
  115,  110,  110,  116,  110,  100,  116,  109,  109,   19,
  109,  180,  149,  150,   99,  115,  115,  119,  115,  117,
  119,  116,  116,  120,  116,  121,  120,  118,  121,  122,
  123,  200,  122,  123,  137,  119,  185,   32,  100,   46,
   31,  120,    7,  121,  172,  173,    8,  122,  123,  186,
    9,   45,   50,   10,   11,   61,   50,  124,  169,  170,
  124,  108,   85,   50,   50,  129,   59,  110,   12,   59,
   86,   85,  141,  109,   61,  124,   87,  139,  201,   90,
   91,  115,  140,   91,   59,   95,   14,  116,  207,   91,
  208,   93,  143,   63,  142,  211,   94,  111,   91,  116,
   15,  119,  127,  127,  118,  119,  120,  120,  126,  121,
  151,  127,   63,  122,  123,  154,  152,  114,   84,   84,
   84,   84,   84,   84,  128,  128,   58,  121,  122,   94,
   94,   94,   94,   94,   94,   94,   94,  138,   61,   44,
   61,  124,   35,  159,   58,   57,  162,   52,   53,   54,
   55,   56,  163,  177,  164,  176,  100,  100,  100,  100,
  100,  100,  127,  127,  178,   99,   99,   99,   99,   99,
   99,  179,  117,  117,  117,  117,   63,   58,   63,  182,
  118,  118,  118,  118,  158,  127,  127,  187,  192,  100,
  100,  100,  100,  100,  100,   35,   57,  193,  144,  145,
   52,   53,   54,   55,   56,  196,  165,  135,  136,   32,
  197,  203,  108,  108,  108,  108,  108,  108,  110,  110,
  110,  110,  110,  110,  109,  109,  109,  109,  109,  109,
  205,  206,  115,  115,  115,  115,  115,  115,  116,  116,
  116,  116,  116,  116,  210,   14,   28,   26,   38,  190,
  191,  166,  167,  168,  119,  119,  119,  119,   27,  100,
   32,   74,  120,  120,  121,  121,   37,  112,   35,  122,
  123,   72,   61,   61,   77,   61,   61,   61,   61,   44,
   61,   61,   35,   70,   61,   61,   61,   61,   61,   61,
   61,   76,   75,   27,  113,  117,  161,  125,  183,  128,
  188,  204,  199,  132,  171,   77,   78,  175,   80,  174,
   63,   63,    0,   63,   63,   63,   63,    0,   63,   63,
  153,    0,   63,   63,   63,   63,   63,   63,   63,    0,
    7,    0,    0,  160,    8,   77,   40,   78,   35,   80,
    0,   10,   11,   47,    7,    0,   48,   49,    8,   50,
    0,   51,   35,    0,    0,   10,   11,   52,   53,   54,
   55,   56,    7,   20,    0,    0,    8,   78,    0,   80,
   35,  100,   20,   10,   11,   52,   53,   54,   55,   56,
    7,    0,   20,   20,    8,    0,    0,  189,    9,    0,
   60,   10,   11,    0,   47,    0,    0,   48,   49,    0,
   50,    0,   51,   35,   19,   77,   12,    0,   52,   53,
   54,   55,   56,   19,    0,  209,    0,    0,   77,    0,
    0,   60,    0,    0,   41,  134,  134,    0,    0,    0,
    0,   59,    0,    0,   77,    0,    0,   78,    0,   80,
    0,    0,    0,    0,   20,    0,    0,    0,    0,    0,
   78,   60,   80,  133,  133,    0,    0,    0,    0,    0,
    0,    0,   59,    0,    0,    0,   78,    0,   80,  134,
  134,  134,  134,  134,    0,    0,    0,    0,  134,  134,
  134,  134,  134,    7,   20,    0,    0,    8,    0,    0,
    0,   35,   59,    0,   10,   11,    0,  133,  133,  133,
  133,  133,    0,    0,    0,    0,  133,  133,  133,  133,
  133,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  184,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  184,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  184,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         37,
   46,   61,   59,   41,   42,   43,   44,   45,   46,   47,
   37,  284,   41,  123,   41,   42,   43,   44,   45,   46,
   47,   59,   60,   61,   62,   40,   61,   22,   23,  125,
  193,   46,   59,   60,   61,   62,   40,   37,  268,  125,
  203,   41,   42,   43,   44,   45,   37,   47,   40,  266,
   41,   42,   43,   44,   45,   41,   47,   44,   44,   59,
   60,   61,   62,   41,   44,  123,   44,   40,   59,   60,
   37,   62,   59,   59,   41,   42,   43,   44,   45,   59,
   47,   59,   33,   36,   61,   41,   93,  125,   44,   40,
  285,   44,   59,   60,   45,   62,  125,   41,  125,   43,
   44,   45,   60,   41,   62,   43,   44,   45,  123,   41,
  266,   43,   44,   45,   44,   59,   60,   41,   62,   46,
   44,   59,   60,   41,   62,  125,   44,   59,   60,   59,
   62,  123,  278,  279,  125,   59,   60,   41,   62,  125,
   44,   59,   60,   41,   62,   41,   44,  125,   44,   41,
   41,  261,   44,   44,   40,   59,  163,  123,  125,  266,
   46,   59,  258,   59,  149,  150,  262,   59,   59,  164,
  266,   91,  258,  269,  270,   40,  262,   41,  142,  143,
   44,  125,   41,  269,  270,  192,   41,  125,  284,   44,
   44,   46,   37,  125,   59,   59,  266,   42,  193,   93,
   41,  125,   47,   44,   59,   46,  266,  125,  203,  266,
  205,   40,   43,   40,   45,  210,   40,   40,   59,  125,
  266,  125,  282,  283,   59,   59,   46,  125,   40,  125,
  280,   59,   59,  125,  125,   41,  281,  123,  276,  277,
  278,  279,  280,  281,  282,  283,   40,  282,  283,  276,
  277,  278,  279,  280,  281,  282,  283,   59,  123,   44,
  125,  125,  266,  266,   40,   59,   59,  271,  272,  273,
  274,  275,   44,  125,   41,   41,  276,  277,  278,  279,
  280,  281,  282,  283,   44,  276,  277,  278,  279,  280,
  281,   41,  278,  279,  280,  281,  123,   40,  125,   41,
  278,  279,  280,  281,  115,  282,  283,   41,   59,  276,
  277,  278,  279,  280,  281,  266,   59,  259,  276,  277,
  271,  272,  273,  274,  275,  125,  137,   95,   96,  123,
   41,  259,  276,  277,  278,  279,  280,  281,  276,  277,
  278,  279,  280,  281,  276,  277,  278,  279,  280,  281,
   41,   40,  276,  277,  278,  279,  280,  281,  276,  277,
  278,  279,  280,  281,   41,  266,   41,  125,  125,  180,
  181,  139,  140,  141,  278,  279,  280,  281,   41,   50,
  123,   59,  280,  281,  280,  281,  125,   58,  125,  281,
  281,   59,  257,  258,   32,  260,  261,  262,  263,   59,
  265,  266,   41,   59,  269,  270,  271,  272,  273,  274,
  275,   41,   41,   14,   59,   63,  126,   86,  162,   93,
  176,  194,  192,   94,  148,   63,   32,  152,   32,  151,
  257,  258,   -1,  260,  261,  262,  263,   -1,  265,  266,
  111,   -1,  269,  270,  271,  272,  273,  274,  275,   -1,
  258,   -1,   -1,  124,  262,   93,  264,   63,  266,   63,
   -1,  269,  270,  257,  258,   -1,  260,  261,  262,  263,
   -1,  265,  266,   -1,   -1,  269,  270,  271,  272,  273,
  274,  275,  258,    5,   -1,   -1,  262,   93,   -1,   93,
  266,  162,   14,  269,  270,  271,  272,  273,  274,  275,
  258,   -1,   24,   25,  262,   -1,   -1,  178,  266,   -1,
   32,  269,  270,   -1,  257,   -1,   -1,  260,  261,   -1,
  263,   -1,  265,  266,    5,  163,  284,   -1,  271,  272,
  273,  274,  275,   14,   -1,  206,   -1,   -1,  176,   -1,
   -1,   63,   -1,   -1,   25,   95,   96,   -1,   -1,   -1,
   -1,   32,   -1,   -1,  192,   -1,   -1,  163,   -1,  163,
   -1,   -1,   -1,   -1,   86,   -1,   -1,   -1,   -1,   -1,
  176,   93,  176,   95,   96,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   63,   -1,   -1,   -1,  192,   -1,  192,  139,
  140,  141,  142,  143,   -1,   -1,   -1,   -1,  148,  149,
  150,  151,  152,  258,  126,   -1,   -1,  262,   -1,   -1,
   -1,  266,   93,   -1,  269,  270,   -1,  139,  140,  141,
  142,  143,   -1,   -1,   -1,   -1,  148,  149,  150,  151,
  152,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  163,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  176,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  192,
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
"ConstructorDecl : ConstructorDeclarator Block",
"ConstructorDeclarator : IDENTIFIER '(' FormalParmListOpt ')'",
"ArgListOpt : ArgList",
"ArgListOpt :",
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
"Stmt :",
"Stmt : IfThenStmt",
"Stmt : IfThenElseStmt",
"Stmt : IfThenElseIfStmt",
"Stmt : WhileStmt",
"Stmt : ForStmt",
"StmtWithoutTrailingSubstatement :",
"ExprStmt : StmtExpr ';'",
"StmtExpr : Assignment",
"StmtExpr : MethodCall",
"StmtExpr : InstantiationExpr",
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
"Primary : '(' Expr ')'",
"Primary : FieldAccess",
"Primary : MethodCall",
"Literal : INTLIT",
"Literal : DOUBLELIT",
"Literal : BOOLLIT",
"Literal : STRINGLIT",
"Literal : NULLVAL",
"InstantiationExpr : Name '(' ArgListOpt ')'",
"ArgList : Expr",
"ArgList : ArgList ',' Expr",
"FieldAccess : Primary '.' IDENTIFIER",
"MethodCall : Name '(' ArgListOpt ')'",
"MethodCall : Name '{' ArgListOpt '}'",
"MethodCall : Primary '.' IDENTIFIER '(' ArgListOpt ')'",
"MethodCall : Primary '.' IDENTIFIER '{' ArgListOpt '}'",
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
