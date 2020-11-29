package ch4;
public class trivial {
   static ch4.Parser par;
   public static void main(String argv[]) throws Exception {
      ch4.lexer.init(argv[0]);
      par = new ch4.Parser();
      int i = par.yyparse();
      if (i == 0)
         System.out.println("no errors");
   }
}
