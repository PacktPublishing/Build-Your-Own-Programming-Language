package ch16;
import java.io.PrintStream;
public class x64 {
   String op;
   x64loc opnd1, opnd2;
   public x64(String o, Object src, Object dst) {
      op=o;
      if (src instanceof x64loc) opnd1 = (x64loc)src;
      else opnd1 = j0.loc(src);
      if (dst instanceof x64loc) opnd2 = (x64loc)dst;
      else opnd2 = j0.loc(dst);
   }
   public x64(String o, Object opnd) {
      op=o;
      if (opnd instanceof x64loc) opnd1 = (x64loc)opnd;
      else opnd1 = j0.loc(opnd);
   }
   public x64(String o) {
       op=o;
   }
   public void print(PrintStream f) {
       if (op.equals("lab")) {
	   if (opnd1 != null)
	       f.println(opnd1.str() + ":");
       }
       else {
	   f.print("\t" + op);
	   if (opnd1 != null)
	       f.print(" " +opnd1.str());
	   if (opnd2 != null)
	       f.print("," +opnd2.str());
	   f.println();
       }
   }
    public void print() { print(System.out); }
}
