import java.io.FileReader;
public class simple {
   static Yylex yylexer;
   public static String yytext() {
      return yylexer.yytext();
   }
   public static void lexErr(String s) {
      System.err.println(s + ": " + yytext());
      System.exit(1);
   }
   public static void main(String argv[]) throws Exception {
      if (argv.length == 0) {
         System.out.println("usage: java simple [file]");
      }
      yylexer = new Yylex(new FileReader(argv[0]));
      int i;
      while ((i=yylexer.yylex()) != Yylex.YYEOF) {
         System.out.println("token " + i + ": " + yytext());
      }
   }
}
