package ch9;
public class tac {
   String op;
   address op1, op2, op3;
   public void print() {
      switch (op) {
      case "proc":
	  System.out.println(op + "\t" + op1.region + ",0,0");
			     // + op2.str() + "," + op3.str());
	  break;
      case "end":
      case ".code":
      case ".global":
      case ".string":
	  System.out.println(op);
	  break;
      case "LAB": System.out.println("L" + op1.offset + ":");
	  break;
      default:
	  System.out.print("\t" + op + "\t");
	  if (op1 != null)
	     System.out.print(op1.str());
	  if (op2 != null)
	      System.out.print("," + op2.str());
	  if (op3 != null)
	      System.out.print("," + op3.str());
	  System.out.println("");
      }
   }
   tac(String s) { op = s; }
   tac(String s, address o) { op = s; op1 = o; }
   tac(String s, address o1, address o2) {
      op = s; op1 = o1; op2 = o2; }
   tac(String s, address o1, address o2, address o3) {
      op = s; op1 = o1; op2 = o2; op3 = o3; }
}
