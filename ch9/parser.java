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



package ch9;
import static ch9.j0.yylex;
import static ch9.yyerror.yyerror;







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
public final static short NEW=286;
public final static short BOOLEAN=287;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    2,    2,    3,    3,    3,    4,    7,
    7,    7,    7,    7,    9,    9,   10,    8,    8,   11,
   11,   12,   12,    5,   13,   15,   16,   16,   17,   17,
   18,    6,   14,   19,   19,   20,   20,   21,   21,   22,
   24,   23,   23,   23,   23,   23,   23,   23,   23,   23,
   23,   25,   33,   33,   28,   29,   30,   30,   37,   37,
   38,   31,   32,   39,   39,   39,   40,   40,   41,   41,
   42,   42,   26,   26,   27,   43,   43,   43,   43,   43,
   44,   44,   44,   44,   44,   44,   44,   48,   47,   50,
   50,   45,   49,   49,   35,   35,   51,   51,   52,   52,
   52,   53,   53,   53,   53,   54,   54,   54,   55,   55,
   55,   55,   56,   56,   57,   57,   57,   58,   58,   59,
   59,   46,   36,   36,   34,   60,   60,   60,   61,   61,
   61,
};
final static short yylen[] = {                            2,
    4,    3,    2,    1,    2,    1,    1,    1,    3,    1,
    1,    1,    1,    1,    1,    1,    3,    1,    3,    1,
    3,    1,    1,    2,    4,    4,    1,    0,    1,    3,
    2,    2,    3,    1,    0,    1,    2,    1,    1,    2,
    2,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    2,    1,    1,    5,    7,    6,    8,    1,    2,
    2,    5,    9,    1,    1,    0,    1,    0,    1,    0,
    1,    3,    2,    3,    3,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    3,    1,    1,    5,    5,    1,
    3,    3,    1,    0,    4,    6,    1,    1,    2,    2,
    1,    1,    3,    3,    3,    1,    3,    3,    1,    1,
    1,    1,    1,    3,    1,    3,    3,    1,    3,    1,
    3,    4,    1,    1,    3,    1,    1,    1,    1,    1,
    1,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    1,   11,   10,    0,   13,
    0,    3,   12,    0,    4,    6,    7,    8,    0,    0,
   16,    0,    0,    0,    0,    2,    5,   20,    0,    0,
    0,    0,   24,   32,   15,    0,    0,    0,   29,   23,
   22,    0,    9,    0,    0,   17,    0,    0,    0,    0,
    0,   76,   77,   79,   78,   80,    0,   43,    0,    0,
    0,   42,    0,    0,   36,   38,   39,    0,   44,   45,
   46,   47,   48,   49,   50,   51,    0,   53,    0,   81,
    0,    0,    0,   86,   87,    0,    0,   26,    0,    0,
   25,    0,   21,    0,   73,    0,    0,    0,    0,    0,
  124,   83,   67,    0,    0,  101,  102,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   33,   37,   40,   52,    0,  130,  131,  129,    0,   30,
   74,   65,   71,    0,    0,    0,    0,   82,   84,   99,
  100,   75,    0,    0,    0,    0,    0,  109,  110,  111,
  112,    0,    0,    0,    0,    0,    0,    0,    0,   85,
    0,   90,    0,    0,    0,  125,    0,    0,    0,  103,
  104,  105,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  122,   95,    0,    0,    0,    0,   72,    0,
   62,   89,   88,   91,    0,    0,    0,    0,   59,   96,
    0,    0,    0,   56,   61,    0,   60,    0,    0,   58,
   63,    0,    0,   55,
};
final static short yydgoto[] = {                          2,
    6,   14,   15,   16,   17,   18,   60,   29,  100,   21,
   30,   42,   22,   62,   23,   37,   38,   39,   63,   64,
   65,   66,   67,   68,   69,   70,   71,   72,   73,   74,
   75,   76,   77,  101,  102,  162,  198,  199,  134,  104,
  201,  135,   80,  105,   82,   83,   84,   85,  163,  164,
  106,  107,  108,  109,  152,  110,  111,  112,  113,   86,
  129,
};
final static short yysindex[] = {                      -241,
 -196,    0, -206,  -46,  204,    0,    0,    0,   44,    0,
 -194,    0,    0,  304,    0,    0,    0,    0, -177,   52,
    0,   -2,   -2, -163, -208,    0,    0,    0,  -38,   35,
 -162,   48,    0,    0,    0, -177,   84,   85,    0,    0,
    0, -132,    0, -177,   42,    0,  -51,   96,   97,  -33,
  101,    0,    0,    0,    0,    0, -163,    0,  -33, -177,
  -36,    0,   20,   48,    0,    0,    0,   87,    0,    0,
    0,    0,    0,    0,    0,    0,   98,    0,    0,    0,
  109,    0,    0,    0,    0,  -47,   35,    0, -163,   44,
    0,   35,    0,   99,    0,   70,  -33,  -33,  -33,  -36,
    0,    0,    0,  100,  109,    0,    0,   55,   -4,  -57,
 -227, -124, -121,  -33,   89,  -27,  120,  118,  -33,  -33,
    0,    0,    0,    0,  -85,    0,    0,    0,  -33,    0,
    0,    0,    0,  128,  145,  149,  -36,    0,    0,    0,
    0,    0,  -33,  -33,  -33,  -33,  -33,    0,    0,    0,
    0,  -33,  -33,  -33,  -33,  -33,  154,  -33,  -33,    0,
  103,    0,  156,  158,  164,    0,  -33,  130,   -2,    0,
    0,    0,   55,   55,   -4,  -57,  -57, -227, -124,  104,
  105,  167,    0,    0,  -33,  -33,  146,  -36,    0,  -50,
    0,    0,    0,    0,  176,  130, -107,  -41,    0,    0,
  181,  145,  186,    0,    0, -107,    0,   -2,  -33,    0,
    0,  189,   -2,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,  -45,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  -34,
    0,    0,    0,  196,    0,    0,    0,    0,    0,  -19,
    0,  125,    0,    0,    0,    0,    0,  213,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  197,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  -59,    0,    0,  151,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   27,    0,
    0,  -13,   71,    0,    0,    0,   18,    0,    0,    0,
    0,  -17,    0,    0,    0,  212,    0,    0,    0,  132,
    0,    0,    0,    0,  141,    0,    0,  382,  240,   46,
   79,   26,  -24,    0,    0,  194,    0,  228,    0,  252,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  239,    0,  169,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  252,    0,
    0,    0,    0,  260,  106,    0,  197,    0,    0,    0,
    0,    0,  392,  412,  399,  253,  314,  419,   34,    0,
    0,    0,    0,    0,    0,  252,    0,   13,    0,  -14,
    0,    0,    0,    0,    0,  275,    0,   17,    0,    0,
    0,  277,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,    0,  231,    0,    0,    0,   39,  270,  551,    0,
  -21,    0,    0,  340,  289,    0,    0,  248,    0,    0,
  274,    0,  160,  272,    0,    0,    0, -188,    0,    0,
    0,    0,  -74,  334,  342,  445,    0,  173,    0,  205,
    0,  184,    0,  400,  473,  568,    0,    0, -148,    0,
    0,  -62,  -81,  229,    0,  -53,  236,  237,    0,    0,
    0,
};
final static int YYTABLESIZE=747;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         99,
   15,  126,  150,  120,  151,   44,   59,   95,  205,   31,
  182,   98,  159,  128,   87,   32,  123,  205,   31,  123,
   43,  133,   92,   82,   18,   55,   19,   82,   82,   82,
   82,   82,   82,   82,  123,  140,  141,  195,  147,   18,
  146,   19,    1,   19,   55,   82,   82,  127,   82,    7,
  153,  154,   19,    8,  119,   40,   57,   35,   31,    4,
   10,   31,   36,   41,  173,  174,  120,   54,  123,  120,
   54,    3,   83,  126,  121,   57,    5,  121,   13,   82,
  170,  171,  172,   24,  120,   54,  115,   59,   28,  115,
   25,  145,  121,  189,    7,  115,  143,   31,    8,  176,
  177,  144,   35,   46,  115,   10,   58,   84,   55,   59,
   55,   84,   84,   84,   84,   84,   84,   84,  120,  118,
   32,  133,  118,   13,   88,   45,  121,   36,   89,   84,
   84,  128,   84,   90,   93,   96,   97,  118,  115,   57,
  114,   57,   92,   59,  121,  123,   92,   92,   92,   92,
   92,   92,   92,  203,  125,  155,  124,  131,  142,  156,
  160,   44,   58,   84,   92,   92,   92,   92,   98,   59,
   32,  118,   98,   98,   98,   98,   98,   97,   98,  158,
  165,   97,   97,   97,   97,   97,  167,   97,  168,  169,
   98,   98,  126,   98,  180,  183,  184,  192,   92,   97,
   97,  185,   97,  186,  196,   98,   14,  193,  197,   98,
   98,   98,   98,   98,   94,   98,  200,  206,  148,  149,
   15,  208,  126,  126,   98,  209,   32,   98,   98,  213,
   98,   14,   35,   97,  126,  127,   28,   52,   53,   54,
   55,   56,   55,   55,   27,   55,   55,   55,   55,   35,
   55,   55,   57,   27,   55,   68,   55,   55,   55,   55,
   55,   98,   82,   82,   82,   82,   82,   82,  127,  127,
   66,   55,   55,   57,   57,   34,   57,   57,   57,   57,
  113,   57,   57,  113,   14,   57,   41,   57,   57,   57,
   57,   57,   94,  116,  126,  126,  116,   64,  113,  113,
   93,  113,   57,   57,   47,    7,  120,   48,   49,    8,
   50,  116,   51,   35,  121,   70,   10,   69,   52,   53,
   54,   55,   56,  115,  115,  115,  115,    7,   12,  118,
   91,    8,  113,   57,   13,   35,  130,  122,   10,  191,
   52,   53,   54,   55,   56,  116,   84,   84,   84,   84,
   84,   84,  128,  128,  117,   57,   13,  117,  118,  118,
   47,   33,   34,   48,   49,   78,   50,  132,   51,   35,
  207,  187,  117,   79,   52,   53,   54,   55,   56,  202,
  175,   92,   92,   92,   92,   92,   92,   92,   92,   57,
  178,    0,  179,    0,    0,   35,    0,   78,    0,    0,
   52,   53,   54,   55,   56,   79,  117,   98,   98,   98,
   98,   98,   98,  126,  126,   57,   97,   97,   97,   97,
   97,   97,  106,    0,  106,  106,  106,    0,   26,   78,
    0,   81,  108,    0,  108,  108,  108,   79,    0,  114,
  106,  106,  114,  106,   98,   98,   98,   98,   98,   98,
  108,  108,  107,  108,  107,  107,  107,  114,  114,  119,
  114,    7,  119,   81,    0,    8,    0,    0,    0,    9,
  107,  107,   10,  107,  106,    0,    0,  119,    0,    0,
    0,    0,    0,    0,  108,    0,    0,   11,    0,    0,
   13,  114,    0,    0,  103,   81,    0,    0,    0,    0,
    0,   78,    0,  117,  107,    0,    0,    0,  190,   79,
    0,  119,    0,   78,    0,  113,  113,  113,  113,  113,
  113,   79,    0,    0,    0,    0,    0,    0,    0,   78,
  116,  116,  116,  116,    0,    0,  204,   79,    0,    0,
    0,  136,    0,    0,    0,  210,    0,  211,    0,    0,
    0,    0,  214,    0,    0,   20,    0,    0,  157,    0,
    0,    7,    0,  161,   20,    8,    0,   81,    0,    9,
  138,  138,   10,  166,   20,   20,    0,    0,    0,   81,
    0,    0,   61,    0,    0,    0,    0,   11,    0,    0,
   13,  117,  117,  117,  117,   81,    0,    0,    0,    0,
    0,    0,  181,    0,    0,    0,    0,  116,    0,    0,
    0,  103,    0,    0,   61,  138,  138,  138,  138,  138,
    0,    0,    0,    0,  138,  138,  138,  138,  138,  194,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   20,
    0,    0,    0,    0,    0,    0,   61,    0,  137,  137,
    0,    0,    0,  212,    0,    0,    0,  106,  106,  106,
  106,  106,  106,    0,    0,  139,  139,  108,  108,  108,
  108,  108,  108,    0,  114,  114,  114,  114,  114,  114,
    0,    0,    0,    0,    0,    0,    0,  107,  107,  107,
  107,  107,  107,  137,  137,  137,  137,  137,  119,  119,
    0,    0,  137,  137,  137,  137,  137,    0,    0,    0,
  139,  139,  139,  139,  139,    0,    0,    0,  188,  139,
  139,  139,  139,  139,    0,    0,    0,    0,    0,    0,
  188,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  188,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         33,
   46,   61,   60,   40,   62,   44,   40,   59,  197,   46,
  159,   45,   40,   61,   36,  123,   41,  206,   46,   44,
   59,   96,   44,   37,   44,   40,   44,   41,   42,   43,
   44,   45,   46,   47,   59,   98,   99,  186,   43,   59,
   45,   59,  284,    5,   59,   59,   60,   61,   62,  258,
  278,  279,   14,  262,   91,  264,   40,  266,   41,  266,
  269,   44,   24,   25,  146,  147,   41,   41,   93,   44,
   44,  268,   46,   61,   41,   59,  123,   44,  287,   93,
  143,  144,  145,   40,   59,   59,   41,   40,  266,   44,
  285,   37,   59,  168,  258,   57,   42,   46,  262,  153,
  154,   47,  266,  266,   59,  269,   59,   37,  123,   40,
  125,   41,   42,   43,   44,   45,   46,   47,   93,   41,
  123,  196,   44,  287,   41,   91,   93,   89,   44,   59,
   60,   61,   62,  266,   93,   40,   40,   59,   93,  123,
   40,  125,   37,   40,  125,   59,   41,   42,   43,   44,
   45,   46,   47,  261,   46,  280,   59,   59,   59,  281,
   41,   44,   59,   93,   59,   60,   61,   62,   37,   40,
  123,   93,   41,   42,   43,   44,   45,   37,   47,   91,
  266,   41,   42,   43,   44,   45,   59,   47,   44,   41,
   59,   60,   61,   62,   41,   93,   41,   93,   93,   59,
   60,   44,   62,   40,   59,   37,  266,   41,  259,   41,
   42,   43,   44,   45,  266,   47,   41,  259,  276,  277,
  266,   41,  282,  283,   93,   40,  123,   59,   60,   41,
   62,  266,  266,   93,  282,  283,   41,  271,  272,  273,
  274,  275,  257,  258,   14,  260,  261,  262,  263,  125,
  265,  266,  286,   41,  269,   59,  271,  272,  273,  274,
  275,   93,  276,  277,  278,  279,  280,  281,  282,  283,
   59,  286,  287,  257,  258,  125,  260,  261,  262,  263,
   41,  265,  266,   44,   91,  269,   59,  271,  272,  273,
  274,  275,   41,   41,  282,  283,   44,   59,   59,   60,
   41,   62,  286,  287,  257,  258,  281,  260,  261,  262,
  263,   59,  265,  266,  281,   41,  269,   41,  271,  272,
  273,  274,  275,  278,  279,  280,  281,  258,  125,   60,
   42,  262,   93,  286,  287,  266,   89,   64,  269,  180,
  271,  272,  273,  274,  275,   93,  276,  277,  278,  279,
  280,  281,  282,  283,   41,  286,  287,   44,  280,  281,
  257,   22,   23,  260,  261,   32,  263,   96,  265,  266,
  198,  167,   59,   32,  271,  272,  273,  274,  275,  196,
  152,  276,  277,  278,  279,  280,  281,  282,  283,  286,
  155,   -1,  156,   -1,   -1,  266,   -1,   64,   -1,   -1,
  271,  272,  273,  274,  275,   64,   93,  276,  277,  278,
  279,  280,  281,  282,  283,  286,  276,  277,  278,  279,
  280,  281,   41,   -1,   43,   44,   45,   -1,  125,   96,
   -1,   32,   41,   -1,   43,   44,   45,   96,   -1,   41,
   59,   60,   44,   62,  276,  277,  278,  279,  280,  281,
   59,   60,   41,   62,   43,   44,   45,   59,   60,   41,
   62,  258,   44,   64,   -1,  262,   -1,   -1,   -1,  266,
   59,   60,  269,   62,   93,   -1,   -1,   59,   -1,   -1,
   -1,   -1,   -1,   -1,   93,   -1,   -1,  284,   -1,   -1,
  287,   93,   -1,   -1,   50,   96,   -1,   -1,   -1,   -1,
   -1,  168,   -1,   59,   93,   -1,   -1,   -1,  169,  168,
   -1,   93,   -1,  180,   -1,  276,  277,  278,  279,  280,
  281,  180,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  196,
  278,  279,  280,  281,   -1,   -1,  197,  196,   -1,   -1,
   -1,   97,   -1,   -1,   -1,  206,   -1,  208,   -1,   -1,
   -1,   -1,  213,   -1,   -1,    5,   -1,   -1,  114,   -1,
   -1,  258,   -1,  119,   14,  262,   -1,  168,   -1,  266,
   98,   99,  269,  129,   24,   25,   -1,   -1,   -1,  180,
   -1,   -1,   32,   -1,   -1,   -1,   -1,  284,   -1,   -1,
  287,  278,  279,  280,  281,  196,   -1,   -1,   -1,   -1,
   -1,   -1,  158,   -1,   -1,   -1,   -1,   57,   -1,   -1,
   -1,  167,   -1,   -1,   64,  143,  144,  145,  146,  147,
   -1,   -1,   -1,   -1,  152,  153,  154,  155,  156,  185,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   89,
   -1,   -1,   -1,   -1,   -1,   -1,   96,   -1,   98,   99,
   -1,   -1,   -1,  209,   -1,   -1,   -1,  276,  277,  278,
  279,  280,  281,   -1,   -1,   98,   99,  276,  277,  278,
  279,  280,  281,   -1,  276,  277,  278,  279,  280,  281,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  276,  277,  278,
  279,  280,  281,  143,  144,  145,  146,  147,  280,  281,
   -1,   -1,  152,  153,  154,  155,  156,   -1,   -1,   -1,
  143,  144,  145,  146,  147,   -1,   -1,   -1,  168,  152,
  153,  154,  155,  156,   -1,   -1,   -1,   -1,   -1,   -1,
  180,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  196,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=287;
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
"INCREMENT","DECREMENT","PUBLIC","STATIC","NEW","BOOLEAN",
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
"Type : BOOLEAN",
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
"Literal : INTLIT",
"Literal : DOUBLELIT",
"Literal : BOOLLIT",
"Literal : STRINGLIT",
"Literal : NULLVAL",
"Primary : Literal",
"Primary : FieldAccess",
"Primary : MethodCall",
"Primary : ArrayAccess",
"Primary : '(' Expr ')'",
"Primary : ArrayCreation",
"Primary : InstanceCreation",
"InstanceCreation : NEW Name '(' ArgListOpt ')'",
"ArrayCreation : NEW Type '[' Expr ']'",
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
"ArrayAccess : Name '[' Expr ']'",
"Expr : CondOrExpr",
"Expr : Assignment",
"Assignment : LeftHandSide AssignOp Expr",
"LeftHandSide : Name",
"LeftHandSide : FieldAccess",
"LeftHandSide : ArrayAccess",
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
  j0.semantic(yyval);
  j0.gencode(yyval);
 }
break;
case 2:
//#line 13 "j0gram.y"
{ yyval=j0.node("ClassBody",1010,val_peek(1)); }
break;
case 3:
//#line 14 "j0gram.y"
{ yyval=j0.node("ClassBody",1011); }
break;
case 5:
//#line 16 "j0gram.y"
{
  yyval=j0.node("ClassBodyDecls",1020,val_peek(1),val_peek(0)); }
break;
case 9:
//#line 19 "j0gram.y"
{
  yyval=j0.node("FieldDecl",1030,val_peek(2),val_peek(1));
  j0.calctype(yyval);
  }
break;
case 17:
//#line 26 "j0gram.y"
{
  yyval=j0.node("QualifiedName",1040,val_peek(2),val_peek(0));}
break;
case 19:
//#line 29 "j0gram.y"
{
  yyval=j0.node("VarDecls",1050,val_peek(2),val_peek(0)); }
break;
case 21:
//#line 31 "j0gram.y"
{
  yyval=j0.node("VarDeclarator",1060,val_peek(2)); }
break;
case 24:
//#line 35 "j0gram.y"
{
  yyval=j0.node("MethodDecl",1380,val_peek(1),val_peek(0));
 }
break;
case 25:
//#line 38 "j0gram.y"
{
  yyval=j0.node("MethodHeader",1070,val_peek(1),val_peek(0));
  j0.calctype(yyval);
  }
break;
case 26:
//#line 42 "j0gram.y"
{
  yyval=j0.node("MethodDeclarator",1080,val_peek(3),val_peek(1)); }
break;
case 30:
//#line 46 "j0gram.y"
{
  yyval=j0.node("FormalParmList",1090,val_peek(2),val_peek(0)); }
break;
case 31:
//#line 48 "j0gram.y"
{
  yyval=j0.node("FormalParm",1100,val_peek(1),val_peek(0));
  j0.calctype(yyval);
 }
break;
case 32:
//#line 53 "j0gram.y"
{
  yyval=j0.node("ConstructorDecl",1110,val_peek(1),val_peek(0)); }
break;
case 33:
//#line 56 "j0gram.y"
{yyval=j0.node("Block",1200,val_peek(1));}
break;
case 37:
//#line 58 "j0gram.y"
{
  yyval=j0.node("BlockStmts",1130,val_peek(1),val_peek(0)); }
break;
case 41:
//#line 63 "j0gram.y"
{
  yyval=j0.node("LocalVarDecl",1140,val_peek(1),val_peek(0));
  j0.calctype(yyval);
  }
break;
case 55:
//#line 76 "j0gram.y"
{
  yyval=j0.node("IfThenStmt",1150,val_peek(2),val_peek(0)); }
break;
case 56:
//#line 78 "j0gram.y"
{
  yyval=j0.node("IfThenElseStmt",1160,val_peek(4),val_peek(2),val_peek(0)); }
break;
case 57:
//#line 80 "j0gram.y"
{
  yyval=j0.node("IfThenElseIfStmt",1170,val_peek(3),val_peek(1),val_peek(0)); }
break;
case 58:
//#line 82 "j0gram.y"
{
  yyval=j0.node("IfThenElseIfStmt",1171,val_peek(5),val_peek(3),val_peek(2),val_peek(0)); }
break;
case 60:
//#line 85 "j0gram.y"
{
  yyval=j0.node("ElseIfSequence",1180,val_peek(1),val_peek(0)); }
break;
case 61:
//#line 87 "j0gram.y"
{
  yyval=j0.node("ElseIfStmt",1190,val_peek(0)); }
break;
case 62:
//#line 89 "j0gram.y"
{
  yyval=j0.node("WhileStmt",1210,val_peek(2),val_peek(0)); }
break;
case 63:
//#line 92 "j0gram.y"
{
  yyval=j0.node("ForStmt",1220,val_peek(6),val_peek(4),val_peek(2),val_peek(0)); }
break;
case 72:
//#line 98 "j0gram.y"
{
  yyval=j0.node("StmtExprList",1230,val_peek(2),val_peek(0)); }
break;
case 74:
//#line 101 "j0gram.y"
{
  yyval=j0.node("BreakStmt",1240,val_peek(1)); }
break;
case 75:
//#line 103 "j0gram.y"
{
  yyval=j0.node("ReturnStmt",1250,val_peek(1)); }
break;
case 85:
//#line 108 "j0gram.y"
{ yyval=val_peek(1);}
break;
case 88:
//#line 109 "j0gram.y"
{
  yyval=j0.node("InstanceCreation", 1261, val_peek(3), val_peek(1)); }
break;
case 89:
//#line 111 "j0gram.y"
{
  yyval=j0.node("ArrayCreation", 1260, val_peek(3), val_peek(1)); }
break;
case 91:
//#line 114 "j0gram.y"
{
  yyval=j0.node("ArgList",1270,val_peek(2),val_peek(0)); }
break;
case 92:
//#line 116 "j0gram.y"
{
  yyval=j0.node("FieldAccess",1280,val_peek(2),val_peek(0)); }
break;
case 95:
//#line 120 "j0gram.y"
{
  yyval=j0.node("MethodCall",1290,val_peek(3),val_peek(1)); }
break;
case 96:
//#line 122 "j0gram.y"
{
    yyval=j0.node("MethodCall",1291,val_peek(5),val_peek(3),val_peek(1)); }
break;
case 99:
//#line 127 "j0gram.y"
{
  yyval=j0.node("UnaryExpr",1300,val_peek(1),val_peek(0)); }
break;
case 100:
//#line 129 "j0gram.y"
{
  yyval=j0.node("UnaryExpr",1301,val_peek(1),val_peek(0)); }
break;
case 103:
//#line 133 "j0gram.y"
{
      yyval=j0.node("MulExpr",1310,val_peek(2),val_peek(0)); }
break;
case 104:
//#line 135 "j0gram.y"
{
      yyval=j0.node("MulExpr",1311,val_peek(2),val_peek(0)); }
break;
case 105:
//#line 137 "j0gram.y"
{
      yyval=j0.node("MulExpr",1312,val_peek(2),val_peek(0)); }
break;
case 107:
//#line 140 "j0gram.y"
{
      yyval=j0.node("AddExpr",1320,val_peek(2),val_peek(0)); }
break;
case 108:
//#line 142 "j0gram.y"
{
      yyval=j0.node("AddExpr",1321,val_peek(2),val_peek(0)); }
break;
case 114:
//#line 145 "j0gram.y"
{
  yyval=j0.node("RelExpr",1330,val_peek(2),val_peek(1),val_peek(0)); }
break;
case 116:
//#line 149 "j0gram.y"
{
  yyval=j0.node("EqExpr",1340,val_peek(2),val_peek(0)); }
break;
case 117:
//#line 151 "j0gram.y"
{
  yyval=j0.node("EqExpr",1341,val_peek(2),val_peek(0)); }
break;
case 119:
//#line 153 "j0gram.y"
{
  yyval=j0.node("CondAndExpr", 1350, val_peek(2), val_peek(0)); }
break;
case 121:
//#line 155 "j0gram.y"
{
  yyval=j0.node("CondOrExpr", 1360, val_peek(2), val_peek(0)); }
break;
case 122:
//#line 158 "j0gram.y"
{ yyval=j0.node("ArrayAccess",1390,val_peek(3),val_peek(1)); }
break;
case 125:
//#line 161 "j0gram.y"
{
  yyval=j0.node("Assignment",1370, val_peek(2), val_peek(1), val_peek(0)); }
break;
//#line 962 "parser.java"
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
