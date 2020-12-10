package ch5;
import java.io.FileReader;
public class j0 {
   public static Yylex yylexer;
   public static ch5.parser par;
   public static void main(String argv[]) throws Exception {
      init(argv[0]);
      par = new ch5.parser();
      //                  par.yydebug=true;
      yylineno = 1;
      int i = par.yyparse();
      if (i == 0)
         System.out.println("no errors");
   }
   public static int yylineno;
   public static String yyfilename;
    //   public static parserVal yylval;
   public static void init(String s) throws Exception {
      yyfilename = s;
      yylexer = new Yylex(new FileReader(s));
   }
   public static int YYEOF() { return Yylex.YYEOF; }
   public static int yylex() {
      int rv = 0;
      try {
        rv = yylexer.yylex();
      } catch(java.io.IOException ioException) {
        rv = -1;
      }
      return rv;
   }
   public static String yytext() {
      return yylexer.yytext();
   }
   public static void lexErr(String s) {
      System.err.println(s);
      System.exit(1);
   }
   public static int scan(int cat) {
       ch5.j0.par.yylval =
	   new parserVal(new tree("token",0,
		new token(cat, yytext(), yylineno)));
      return cat;
   }
   public static void newline() {
      yylineno++;
   }
   public static void whitespace() {
   }
   public static void comment() {
   }
   public static short ord(String s) { return (short)(s.charAt(0)); }
   public static void print(parserVal root) {
       ((tree)root.obj).print_graph(yyfilename + ".dot");
   }
  public static tree unwrap(Object obj) {
    if (obj instanceof token)
      return new tree("token",0,(token)obj);
    else return (tree)obj;
  }
  public static parserVal node(String s,int r,parserVal...p) {
     tree[] t = new tree[p.length];
     for(int i = 0; i < t.length; i++)
	 t[i] = (tree)(p[i].obj);
     return new parserVal((Object)new tree(s,r,t));
  }
}
