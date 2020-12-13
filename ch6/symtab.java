package ch6;
import java.util.HashMap;
public class symtab {
   String scope;
   symtab parent;
   HashMap<String,symtab_entry> t;
   symtab(String sc) {
      scope = sc;
      t = new HashMap<String,symtab_entry>();
   }
    symtab(String sc, symtab p) {
      scope = sc; parent = p;
      t = new HashMap<String,symtab_entry>();
   }
   symtab_entry lookup(String s) {
      return t.get(s);
   }
   void insert(String s, Boolean iC, symtab sub) {
      if (t.containsKey(s)) {
         j0.semerror("redeclaration of " + s);
      } else {
         sub.parent = this;
         t.put(s, new symtab_entry(s, this, iC, sub));
      }
   }
   void insert(String s, Boolean iC) {
      if (t.containsKey(s)) {
         j0.semerror("redeclaration of " + s);
      } else {
         t.put(s, new symtab_entry(s, this, iC));
      }
   }
   void print() { print(0); }
   void print(int level) {
      for(int i=0; i<level; i++)
        System.out.print(" ");
      System.out.println(scope + " - " + t.size() + " symbols");
      for(symtab_entry se : t.values()) se.print(level+1);
   }
}
