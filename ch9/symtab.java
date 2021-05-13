package ch9;
import java.util.HashMap;
public class symtab {
   String scope;
   symtab parent;
   HashMap<String,symtab_entry> t;
   int count;
   symtab(String sc) {
      scope = sc;
      t = new HashMap<String,symtab_entry>();
   }
    symtab(String sc, symtab p) {
      scope = sc; parent = p;
      t = new HashMap<String,symtab_entry>();
   }
   symtab_entry lookup(String s) {
       symtab_entry rv;
       rv = t.get(s);
       if (rv != null) {
	   return rv;
       }
      if (parent != null) return parent.lookup(s);
      return null;
   }
   void insert(String s, Boolean iC, symtab sub, typeinfo typ) {
      if (t.containsKey(s)) {
         j0.semErr("redeclaration of " + s);
      } else {
         if (sub != null)
            sub.parent = this;
         t.put(s, new symtab_entry(s, this, iC, sub, typ,
				   new address(scope,count)));
	 count += 8;
      }
   }
   address genlocal() {
      String s = "__local$" + count;
      insert(s, false, null, new typeinfo("int"));
      return t.get(s).addr;
   }
   void insert(String s, Boolean iC) {
      if (t.containsKey(s)) {
         j0.semErr("redeclaration of " + s);
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
