package ch13;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
public class j0 {
   public static Yylex yylexer;
   public static ch13.parser par;
   public static ch13.symtab global_st, stringtab;
   public static HashMap<String,Integer> labeltable;
   public static boolean methodAddrPushed;
   public static RegUse [] regs;
   public static void main(String argv[]) throws Exception {
      j0init(argv);
      regs = new RegUse[]{new RegUse("%rdi", -8),
             new RegUse("%rsi", -16), new RegUse("%rdx", -24),
             new RegUse("%rcx", -32), new RegUse("%r8", -40),
             new RegUse("%r9",-48), new RegUse("%r10", -56),
             new RegUse("%r11", -64), new RegUse("%r12", -72),
             new RegUse("%r13", -80), new RegUse("%r14", -88) };

      par = new parser();
      //                  par.yydebug=true;
      yylineno = yycolno = 1;
      int i = par.yyparse();
      if (i == 0)
         System.out.println("no errors");
   }
   public static int yylineno, yycolno;
   public static String yyfilename;
    //   public static parserVal yylval;
   public static void j0init(String argv[]) throws Exception {
      int fnam_idx = 0;
      if (argv.length == 0) stop("usage: j0 [-x64] filename");
      if (argv[0].equals("-x64")) {
         isNative = true; fnam_idx++;
	 System.out.println("Compiling " + argv[1] + " to x64 .s format");
         }
      else {
        isNative = false;
	System.out.println("Compiling " + argv[0] + " to .j0 format");
      }
      if (fnam_idx >= argv.length) stop("usage: j0 [-x64] filename");
      yyfilename = argv[fnam_idx];
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
       ch13.j0.par.yylval =
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
    ch13.symtab out_st, System_st;
    global_st = new ch13.symtab("global");
    stringtab = new ch13.symtab("strings");
    System_st = new ch13.symtab("class");
    out_st = new ch13.symtab("class");
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
  public static boolean isNative;
  public static ArrayList<x64> xcode;
  public static void gencode(parserVal r) {
    tree root = (tree)(r.obj);
    root.genfirst();
    root.genfollow();
    root.gentargets();
    root.gencode();
    labeltable = new HashMap<>();
    methodAddrPushed = false;
    if (isNative) {
      xcode = x64code(root.icode);
      genx64code();
      }
    else {
      ArrayList<byc> bcode = bytecode(root.icode);
      if (bcode != null) {
        genbytecode(bcode);
        }
      }
  }

public static ArrayList<x64> l64(x64 x) {
   return new ArrayList<x64>(Arrays.asList(x)); }
public static ArrayList<x64> xgen(String o){
    return l64(new x64(o)); }
public static ArrayList<x64> xgen(String o,
                            address src, address dst) {
    return l64(new x64(o, loc(src), loc(dst))); }
public static ArrayList<x64> xgen(String o, address opnd) {
    return l64(new x64(o, loc(opnd))); }
public static ArrayList<x64> xgen(String o, address src, String dst) {
    return l64(new x64(o, loc(src), loc(dst))); }
public static ArrayList<x64> xgen(String o, String src, address dst) {
    return l64(new x64(o,loc(src),loc(dst))); }
public static ArrayList<x64> xgen(String o, String src, String dst) {
    return l64(new x64(o,loc(src),loc(dst))); }
public static ArrayList<x64> xgen(String o, String opnd) {
    return l64(new x64(o, loc(opnd))); }

public static ArrayList<x64> x64code(ArrayList<tac> icode)
{
    int parmCount = -1;
    xcode = new ArrayList<x64>();
    for(int i=0; i<icode.size(); i++) {
      tac instr = icode.get(i);
      switch(instr.op) {
      case "ADD": {
         xcode.addAll(xgen("movq", instr.op2, "%rax"));
         xcode.addAll(xgen("addq", instr.op3, "%rax"));
         xcode.addAll(xgen("movq", "%rax", instr.op1));
         break;
         }
      case "SUB": { } // append translation of SUB to rv
      case "NEG": {
         xcode.addAll(xgen("movq", instr.op2, "%rax"));
         xcode.addAll(xgen("negq", "%rax"));
         xcode.addAll(xgen("movq", "%rax", instr.op1));
         break;
         }
      case "ASN": {
         xcode.addAll(xgen("movq", instr.op2, "%rax"));
         xcode.addAll(xgen("movq", "%rax", instr.op1));
         break;
         }
      case "ADDR": {
         xcode.addAll(xgen("leaq", instr.op2, "%rax"));
         xcode.addAll(xgen("%rax", instr.op1));
         break;
         }
      case "LCON": {
         xcode.addAll(xgen("movq", instr.op2, "%rax"));
         xcode.addAll(xgen("movq", "(%rax)", "%rax"));
         xcode.addAll(xgen("movq", "%rax", instr.op1));
         break;
         }
      case "SCON": {
         xcode.addAll(xgen("movq", instr.op2, "%rbx"));
         xcode.addAll(xgen("movq", instr.op1, "%rax"));
         xcode.addAll(xgen("movq", "%rbx", "(%rax)"));
         break;
         }
      case "GOTO": {
         xcode.addAll(xgen("goto", instr.op1));
         break;
         }
      case "BLT": {
         xcode.addAll(xgen("movq", instr.op2, "%rax"));
         xcode.addAll(xgen("cmpq", instr.op3, "%rax"));
         xcode.addAll(xgen("jle", instr.op1));
         break;
         }
      case "PARM": {
         if (parmCount == -1) {
            parmCount = 0;
            for(int j = i+1; j<icode.size(); j++) {
	       tac callinstr = icode.get(j);
               if (callinstr.op.equals("CALL")) break;
               parmCount++;
               }
            }
         else parmCount--;
         genParm(parmCount, instr.op1);
         break;
         }
      case "CALL": {
         xcode.addAll(xgen("call", instr.op1));
         if (instr.op3 != null)
            xcode.addAll(xgen("movq", "%rax", instr.op2));
         methodAddrPushed = false;
	 parmCount = -1;
         break;
         }
      case "RET":
      case "RETURN": {
	  if (instr.op1 == null)
         xcode.addAll(xgen("movq", "$0", "%rax"));
	  else
         xcode.addAll(xgen("movq", instr.op1, "%rax"));
         xcode.addAll(xgen("leave"));
         xcode.addAll(xgen("ret", instr.op1));
         break;
         }
      case "proc": {
         int n = 0;
	 if (instr.op2 == null)
	     System.err.println("instr.op2 is null");
	 if (instr.op3 == null)
	     System.err.println("instr.op3 is null");
	 if (instr.op2 != null) n += instr.op2.intgr();
	 if (instr.op3 != null) n += instr.op3.intgr();
	 n *= 8;
	 //         int n = (instr.op1.offset + instr.op2.offset) * 8;
         xcode.addAll(xgen(".text"));
         xcode.addAll(xgen(".globl", instr.op1.region));
         xcode.addAll(xgen(".def\t" + instr.op1.region +
			   ";\t.scl\t2;\t.type\t32;\t.endef"));
	 xcode.addAll(xgen(".seh_proc\t"+instr.op1.region));
	 xcode.addAll(xgen("lab", instr.op1.region));
	 // Linux?
	 // xcode.addAll(xgen(".type", instr.op1, "@function"));
	 xcode.addAll(xgen("pushq", "%rbp"));
	 xcode.addAll(xgen(".seh_pushreg\t%rbp"));
	 xcode.addAll(xgen("movq", "%rsp", "%rbp"));
	 xcode.addAll(xgen(".seh_setframe\t%rbp, 0"));
	 xcode.addAll(xgen("subq", "$"+n, "%rsp"));
	 xcode.addAll(xgen(".seh_stackalloc\t"+n));
	 xcode.addAll(xgen(".seh_endprologue"));
	 if (instr.op1.region.equals("main"))
	     xcode.addAll(xgen("call","__main"));
	 int j = 0;
         if (instr.op2 != null)
	     for( ; j < instr.op2.offset; j++)
		 regs[j].loaded = regs[j].dirty = true;
	 for( ; j < 11; j++)
	     regs[j].loaded = regs[j].dirty = false;
	 break;
      }
      case "end": {
	  if (! xcode.get(xcode.size()-1).op.equals("ret")) {
	     xcode.addAll(xgen("leave"));
	     xcode.addAll(xgen("ret"));
             }
	  xcode.addAll(xgen(".seh_endproc"));
	 break;
             }
      case ".code": {
	  //	 xcode |||:= xgen(".text")
	 break;
	 }
      case ".global": {
	  //         xcode |||:= xgen(".global")
	 break;
          }
      case ".string": {
	  //           xcode |||:= xgen(".section\t.rdata,\"dr\"")
	 break;
          }
      case "LAB": {
         for(RegUse ru : regs) if (ru.dirty) ru.save();
	 if (! instr.op1.str().startsWith("strings:"))
	     xcode.addAll(xgen("lab", instr.op1));
         break;
         }

      }
   }
    return xcode;
}

public static void genParm(int n, address addr) {
   for (RegUse x : regs) x.save();
   if (n > 6) xcode.addAll(xgen("pushq", addr));
   else {
      String s = "error:" + String.valueOf(n);
      switch (n) {
      case 1: s = "%rdi"; break; case 2: s = "%rsi"; break;
      case 3: s ="%rdx"; break; case 4: s = "%rcx"; break;
      case 5: s = "%r8"; break; case 6: s = "%r9"; break;
      }
      String popcode;
      if ((addr instanceof address) && addr.region.equals("strings"))
	  popcode = "leaq"; else popcode = "movq";
      xcode.addAll(xgen(popcode, addr, s));
   }
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

    public static void genx64code() {
	int i = find(".java", yyfilename);
	String outfilename = yyfilename.substring(0,i) + ".s";
	PrintStream fout;
        fout = open(outfilename);
	fout.println("\t.file\t\"" + yyfilename + "\"");
	// write out our "runtime system" with every generated code
	fout.println("\t.text");
	fout.println("\t.globl\tPrintStream__println");
	fout.println("\t.def\tPrintStream__println;\t.scl\t2;\t.type\t32;\t.endef");
	fout.println("\t.seh_proc\tPrintStream__println");
	fout.println("PrintStream__println:");
	fout.println("\tpushq\t%rbp");
	fout.println("\t.seh_pushreg\t%rbp");
        fout.println("\tmovq\t%rsp, %rbp");
	fout.println("\t.seh_setframe\t%rbp, 0");
	fout.println("\tsubq\t$32, %rsp\n"+
		     "\t.seh_stackalloc\t32\n" +
		     "\t.seh_endprologue\n"+
		     "\tmovq\t%rsi, %rcx\n"+ // Windows conventions!
		     "\tcall\tputs\n"+
		     "\tnop\n"+
		     "\taddq\t$32, %rsp\n"+
		     "\tpopq\t%rbp\n"+
		     "\tret\n"+
		     "\t.seh_endproc\n"+
		     "\t.text\n"+
		     "\t.def\t__main;\t.scl\t2;\t.type\t32;\t.endef");
	write_x64strings(fout);
	x64print(fout);
	fout.println("\t.ident\t\"j0: (Unicon) 0.1.0\"");
	fout.println("\t.def\tputs;\t.scl\t2;\t.type\t32;\t.endef");
	fout.close();
    }

  public static void write_x64strings(PrintStream fout) {
    String s;
    fout.println("\t.section\t.rdata,\"dr\"");
    for(int i=0; i < stringtab.L.size(); i++) {
	s = stringtab.L.get(i);
	fout.println(".Lstr" + i + ":");
	s = s.substring(0, s.length()-1) + "\\0\"";
	fout.println("\t.ascii " + s);
     }
  }

  public static void x64print(PrintStream f) {
    for(x64 x : xcode) x.print(f);
  }
  public static void x64print() {
    for(x64 x : xcode) x.print(System.out);
  }

   public static x64loc loc(String s) { return new x64loc(s); }
   public static x64loc loc(Object o) {
      if (o instanceof String) return loc((String)o);
      if (o instanceof address) return loc((address)o);
      return null;
   }
   public static x64loc loc(address a) {
     if (a == null) return null;
     switch (a.region) {
     case "method":
     case "loc": { if (a.offset <= 88) return loadreg(a);
                   else return new x64loc("rbp", -a.offset); }
     case "glob": { return new x64loc("rip", a.offset); }
     case "const": { return new x64loc("imm", a.offset); }
     case "lab": { return new x64loc("lab", a.offset); }
     case "obj": { return new x64loc("r15", a.offset); }
     case "imm": { return new x64loc("imm", a.offset); }
     case "strings": { return new x64loc("%rip", ".Lstr"+a.offset); }
     default: {
	 // semErr("x64loc unknown region" + a.region);
	 return new x64loc("lab", a.region);
     }
     }
  }
  public static x64loc loadreg(address a) {
    int r = a.offset/8;
    if (!(regs[r].loaded)) {
      xcode.addAll(xgen("movq",
			String.valueOf(-a.offset)+"(%rbp)", regs[r].reg));
      regs[r].loaded = true;
      }
    return new x64loc(regs[a.offset/8+1].reg);
  }
}
