import java.io.FileReader;
public class j0 {
   static Yylex lex;
   public static int yylineno;
   public static token yylval;
   public static void main(String argv[]) throws Exception {
      lex = new Yylex(new FileReader(argv[0]));
      yylineno = 1;
      int i;
      while ((i=lex.yylex()) != Yylex.YYEOF) {
         System.out.println("token " + i + ": " + yytext());
      }
   }
   public static String yytext() {
      return lex.yytext();
   }
   public static void lexErr(String s) {
      System.err.println(s + ": line " + yylineno +
                             ": " + yytext());
      System.exit(1);
   }
   public static int tokenize(int cat) {
      yylval = new token(cat, yytext(), yylineno);
      return cat;
   }
   public static void increment_lineno() {
      yylineno++;
   }
}

}
