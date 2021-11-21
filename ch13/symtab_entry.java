package ch13;
public class symtab_entry {
   String sym;
   symtab parent_st, st;
   boolean isConst;
   typeinfo typ;
   address addr;
   void print(int level) {
      for(int i=0; i<level; i++) System.out.print(" ");
      System.out.print(sym);
      if (isConst) System.out.print(" (const)");
      System.out.println("");
      if (st != null) st.print(level+1);
   }
   symtab_entry(String s, symtab p, boolean iC) {
       sym = s; parent_st = p; isConst = iC; }
   symtab_entry(String s, symtab p, boolean iC,
		symtab t, typeinfo ti, address a) {
      sym = s; parent_st = p; isConst = iC;
      st = t; typ = ti; addr = a;
 }
}
