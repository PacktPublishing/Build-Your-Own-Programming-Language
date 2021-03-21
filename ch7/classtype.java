package ch7;
public class classtype extends typeinfo {
   String name;
   symtab st;
   parameter [] methods;
   parameter [] fields;
   typeinfo [] constrs;
   public classtype(String s) { name = s; }
   public classtype(String s, symtab stab) { name = s; st = stab; }
}
