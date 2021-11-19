package ch12;
public class classtype extends typeinfo {
   String name;
   symtab st;
   parameter [] methods;
   parameter [] fields;
   typeinfo [] constrs;
    public String str() {
	return name;
    }
    public classtype(String s) { name = s; basetype="class";}
    public classtype(String s, symtab stab) { name = s; st = stab; basetype="class";}
   public classtype(String s, symtab stab,
		    parameter []ms, parameter[]fs, typeinfo[]cs) {
       name = s; st = stab; methods=ms; fields=fs; constrs=cs; basetype="class";}
}
