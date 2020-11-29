package ch4;
public class token {
   public int cat;
   public String text;
   public int lineno;
   public token(int c, String s, int l) {
      cat = c; text = s; lineno = l;
   }
}
