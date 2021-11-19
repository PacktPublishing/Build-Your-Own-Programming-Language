package ch12;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
public class j0 {
   public static Yylex yylexer;
   public static ch12.parser par;
   public static ch12.symtab global_st, stringtab;
   public static HashMap<String,Integer> labeltable;
   public static boolean methodAddrPushed;
   public static void main(String argv[]) throws Exception {
      init(argv[0]);
      par = new parser();
      //                  par.yydebug=true;
      yylineno = yycolno = 1;
      //      System.out.println("Compiling "+ yyfilename + " to .j0 format");
      int i = par.yyparse();
      if (i == 0)
         System.out.println("no errors");
   }
    public static int yylineno, yycolno;
   public static String yyfilename;
    //   public static parserVal yylval;
   public static void init(String s) throws Exception {
      yyfilename = s;
      if (! yyfilename.endsWith(".java"))
	  yyfilename = yyfilename + ".java";
      yylexer = new Yylex(new FileReader(yyfilename));
   }
   public static int YYEOF() { return Yylex.YYEOF; }
   public static int yylex() {
      int rv = 0;
      try {
        rv = yylexer.yylex();
      } catch(java.io.IOException ioException) {
        rv = -1;
      }
      return rv;
   }
   public static String yytext() {
      return yylexer.yytext();
   }
   public static void lexErr(String s) {
       stop(s);
   }
   public static int scan(int cat) {
       ch12.j0.par.yylval =
	   new parserVal(new tree("token",cat,
		new token(cat, yytext(), yylineno)));
      return cat;
   }
   public static void newline() {
      yylineno++;
   }
   public static void whitespace() {
   }
   public static void comment() {
   }
   public static short ord(String s) { return (short)(s.charAt(0)); }
   public static void print(parserVal root) {
       ((tree)root.obj).print_graph(yyfilename + ".dot");
   }
  public static tree unwrap(Object obj) {
    if (obj instanceof token)
      return new tree("token",0,(token)obj);
    else return (tree)obj;
  }
  public static parserVal node(String s,int r,parserVal...p) {
     tree[] t = new tree[p.length];
     for(int i = 0; i < t.length; i++)
	 t[i] = (tree)(p[i].obj);
     return new parserVal((Object)new tree(s,r,t));
  }
  public static void semantic(parserVal r) {
    tree root = (tree)(r.obj);
    ch12.symtab out_st, System_st;
    global_st = new ch12.symtab("global");
    stringtab = new ch12.symtab("strings");
    System_st = new ch12.symtab("class");
    out_st = new ch12.symtab("class");
    out_st.insert("println", false, null,
		  new methodtype(new typeinfo[]{new classtype("String")},
				 new typeinfo("void")));
    System_st.insert("out", false, out_st,
       new classtype("PrintStream",out_st));
    global_st.insert("System", false, System_st,
       new classtype("System",System_st));
   root.mkSymTables(global_st);
   root.populateSymTables();
   root.checkSymTables();
   root.mkcls();
   //   global_st.print();
   root.checktype(false);
  }
  public static void semErr(String s) {
   stop("semantic error: " + s);
  }
  public static void calctype(parserVal pv){
    tree t = (tree)pv.obj;
    t.kids[0].calctype();
    t.kids[1].assigntype(t.kids[0].typ);
  }
  public static void gencode(parserVal r) {
    tree root = (tree)(r.obj);
    root.genfirst();
    root.genfollow();
    root.gentargets();
    root.gencode();
    labeltable = new HashMap<>();
    methodAddrPushed = false;
    ArrayList<byc> bcode = bytecode(root.icode);
    genbytecode(bcode);
  }

public static ArrayList<byc> bgen(int o, address a) {
    ArrayList<byc> L = new ArrayList<byc>();
   byc b = new byc(o, a);
   L.add(b);
   return L;
}

  public static ArrayList<byc> bytecode(ArrayList<tac> icode) {
    ArrayList<byc> rv = new ArrayList<byc>();
    for(int i=0; i<icode.size(); i++) {
      tac instr = icode.get(i);
      switch(instr.op) {
      case "ADD": {
        rv.addAll(j0.bgen(Op.PUSH, instr.op2));
        rv.addAll(j0.bgen(Op.PUSH, instr.op3));
        rv.addAll(j0.bgen(Op.ADD, null));
        rv.addAll(j0.bgen(Op.POP, instr.op1));
        break;
      }
      case "SUB": {  }
      case "NEG": {
        rv.addAll(j0.bgen(Op.PUSH, instr.op2));
        rv.addAll(j0.bgen(Op.NEG, null));
        rv.addAll(j0.bgen(Op.POP, instr.op1));
        break;
      }
      case "ASN": {
        rv.addAll(j0.bgen(Op.PUSH, instr.op2));
        rv.addAll(j0.bgen(Op.POP, instr.op1));
        break;
      }
      case "ADDR": {
        rv.addAll(j0.bgen(Op.LOAD, instr.op1));
        break;
      }
      case "LCON": {
        rv.addAll(j0.bgen(Op.LOAD, instr.op2));
        rv.addAll(j0.bgen(Op.POP, instr.op1));
        break;
      }
      case "SCON": {
        rv.addAll(j0.bgen(Op.STORE, instr.op2));
        rv.addAll(j0.bgen(Op.POP, instr.op1));
        break;
      }
      case "GOTO": {
        rv.addAll(j0.bgen(Op.GOTO, instr.op1));
        break;
      }
      case "BLT": {
        rv.addAll(j0.bgen(Op.PUSH, instr.op2));
        rv.addAll(j0.bgen(Op.PUSH, instr.op3));
        rv.addAll(j0.bgen(Op.LT, null));
	rv.addAll(j0.bgen(Op.BIF, instr.op1));
	break;
      }
      case "PARM": {
        if (methodAddrPushed == false) {
	    for(int j = i+1; j < icode.size(); j++) {
            tac callinstr = icode.get(j);
	    if (callinstr.op.equals("CALL")) {
		if (callinstr.op1.str().equals("PrintStream__println:0")) {
                  rv.addAll(j0.bgen(Op.PUSH, new address("imm", -1)));
               } else {
                  rv.addAll(j0.bgen(Op.PUSH, callinstr.op1));
	       }
               break;
	    }
	    methodAddrPushed = true;
          }
        }
        rv.addAll(j0.bgen(Op.PUSH, instr.op1));
        break;
      }
      case "CALL": {
         rv.addAll(j0.bgen(Op.CALL, instr.op2));
	 methodAddrPushed = false;
         break;
      }
      case "RET": case "RETURN": {
        rv.addAll(j0.bgen(Op.RETURN, instr.op1));
        break;
      }
      case "proc": {
	// record address
	  labeltable.put(instr.op1.region, rv.size() * 8);
	break;
        }
      case "end": { break; }
      case ".code": { break; }
      case ".global": { break; }
      case ".string": {break; }
      case "LAB": {
	  labeltable.put("L"+String.valueOf(instr.op1.offset), rv.size() * 8);
         break;
      }
      case "string": {break; }
      case "global": { break; }
      case "FIELD": {
	  rv.addAll(j0.bgen(Op.PUSH, instr.op2));
	  rv.addAll(j0.bgen(Op.POP, instr.op1));
	  break;
	 }

      default: { stop("What is " + instr.op); }
      }
    }
    return rv;
  }

    public static int calculate_fio() {
	return 3+ stringtab.count/8 + global_st.count/8;
    }

    public static void write_stringarea(PrintStream fout) {
	String s;
	for(int i=0; i<stringtab.L.size(); i++) {
	    s = stringtab.L.get(i);
	    // should fully-binarize (de-escape) string here
	    // for now, just strip double quotes, replace with NULs
	    s = s.substring(1,s.length()-1) + "\0\0" ;

	    int len = s.length();
	    while (len > 0) {
		if (len < 9) {
		   fout.print(s);
		   if (len < 8) {
		       for(int j = 0; j<8-len; j++)
			   fout.print( "\0");
		   }
		}
		else { fout.print(s.substring(0,8)); s = s.substring(8); }
		len -= 8;
         }
     }
    }

    public static void write_globalarea(PrintStream fout){
	for(int i=0; i<global_st.count; i++)
	    fout.print("\0");
    }

    public static void write_instructions(ArrayList<byc> bc,
					  PrintStream fout) {
	for(int i=0; i<bc.size(); i++) {
	    byc b = bc.get(i);
	    switch( b.op ) {
	    case Op.CODE: { }
	    case Op.GLOBAL: { }
	    case Op.LABEL: { }
	    case Op.PROC: {  }
	    case Op.STRING: {  }
	    case Op.END: {break; }
	    default: {
		bc.get(i).printb(fout);
	    }
	    }
	}
    }

    public static void stop(String s) {
	System.err.println(s);
	System.exit(1);
    }

    public static String rawstring(int i, int len) {
	String s = "";
	for(int j=0; j<len; j++) {
	    s = Character.toString((char)(i & 0xFF)) + s;
	    i = i >> 8;
	}
	return s;
    }
    public static String reverse(String s) {
	return (new StringBuffer(s)).reverse().toString();
    }
    public static int where;
    public static PrintStream open(String s){
	PrintStream p = null;
	where = 0;
	try {
          p = new PrintStream(s);
	} catch (java.io.FileNotFoundException e) {
          stop("couldn't open output file "+ s + " for writing");
	}
	return p;
    }

  public static int find(byte[] needle, byte[]haystack) {
    int i=0;
    for( ; i < haystack.length - needle.length+1; ++i) {
        boolean found = true;
        for(int j = 0; j < needle.length; ++j) {
           if (haystack[i+j] != needle[j]) {
               found = false;
               break;
           }
        }
        if (found) return i;
    }
    return -1;
  }
  public static int find(String needle, String haystack) {
      return find(needle.getBytes(), haystack.getBytes());
  }

    public static void genbytecode(ArrayList<byc> bc) {
    int i = find(".java", yyfilename), fio;
    int entrypt;
    String outfilename = yyfilename.substring(0,i) + ".j0";
    PrintStream fout;
    fout = open(outfilename);
      fout.print("Jzero!!\0");               // word 0, magic word
      fout.print("1.0\0\0\0\0\0");           // word 1, version #
      // write first instruction offset. convert Java binary to bcode binary
      fio = calculate_fio();
      fout.print("\0\0" + reverse(rawstring(fio, 6)));
      write_stringarea(fout);
      write_globalarea(fout);

      // bootstrap instructions: push addr of main, push dummy self, call 0, and halt
      entrypt = fio*8+32;
      if (! labeltable.containsKey("main")) stop("main() not found");
      entrypt += labeltable.get("main");
      fout.print("\11\2"+reverse(rawstring(entrypt, 6))); // PUSH IMM (func entry pt) fio*8+24
      fout.print("\11\2\0\0\0\0\0\0");            // PUSH 0 (null self)
      fout.print("\13\2\0\0\0\0\0\0");            // call 0
      fout.print("\1\0\0\0\0\0\0\0");             // halt

      write_instructions(bc, fout);
      fout.close();
  }
}
