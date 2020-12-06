package ch5;
public class token {
   public int id;
   public int cat;
   public String text;
   public int lineno;
   public token(int c, String s, int l) {
      cat = c; text = s; lineno = l;
      id = serial.getid();
   }
}
