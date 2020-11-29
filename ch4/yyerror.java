package ch4;
public class yyerror {
    public static void yyerror(String s) {
      System.err.println(s);
      System.exit(1);
    }
}
