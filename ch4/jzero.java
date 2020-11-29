package ch4;
public class jzero {
   public static ch4.parser par;
   public static void main(String argv[]) throws Exception {
      ch4.j0.init(argv[0]);
      par = new ch4.parser();
      yylineno = 1;
      int i = par.yyparse();
      if (i == 0)
         System.out.println("no errors");
   }
}
