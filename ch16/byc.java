package ch16;
import java.util.HashMap;
import java.io.PrintStream;
public class byc {
   int op, opreg;
   long opnd;
   public byc(int o, address a) {
      op=o; addr(a);
   }
   public void print(PrintStream f) {
      f.println("\t" + nameof() + " " + addrof());
   }
   public void print() {
      print(System.out);
   }
  static HashMap<Short,String> ops;
  static { ops = new HashMap<>();
    ops.put(Op.HALT,"halt"); ops.put(Op.NOOP,"noop");
    ops.put(Op.ADD,"add"); ops.put(Op.SUB,"sub");
    ops.put(Op.MUL,"mul"); ops.put(Op.DIV, "div");
    ops.put(Op.MOD,"mod"); ops.put(Op.NEG, "neg");
    ops.put(Op.PUSH,"push"); ops.put(Op.POP, "pop");
    ops.put(Op.CALL, "call"); ops.put(Op.RETURN, "return");
    ops.put(Op.GOTO, "goto"); ops.put(Op.BIF, "bif");
    ops.put(Op.LT, "lt"); ops.put(Op.LE, "le");
    ops.put(Op.GT, "gt"); ops.put(Op.GE, "ge");
    ops.put(Op.EQ, "eq"); ops.put(Op.NEQ, "neq");
    ops.put(Op.LOCAL, "local"); ops.put(Op.LOAD, "load");
    ops.put(Op.STORE, "store"); ops.put(Op.LABEL, "LABEL");
    ops.put(Op.STRING, "STRING"); ops.put(Op.CODE, "CODE");
    ops.put(Op.PROC, "PROC"); ops.put(Op.GLOBAL, "GLOBAL");
    ops.put(Op.END, "END");
  }
  public String nameof() {
    return ops.get(op);
  }

  public String addrof() {
   switch (opreg) {
   case Op.R_NONE: return "";
   case Op.R_ABS: return "@"+ java.lang.Long.toHexString(opnd);
   case Op.R_IMM: return String.valueOf(opnd);
   case Op.R_STACK: return "stack:" + String.valueOf(opnd);
   case Op.R_HEAP: return "heap:" + String.valueOf(opnd);
   }
  return String.valueOf(opreg)+":"+String.valueOf(opnd);
  }

   public void printb(PrintStream f) {
   long x = opnd;
   f.print((char)op);
   f.print((char)opreg);
   for(int i = 0; i < 6; i++) {
      f.print((char)(x & 0xff));
      x = x>>8;
      }
   }
    public void printb() { printb(System.out); }

   public void addr(address a) {
   if (a == null) opreg = Op.R_NONE;
   else switch (a.region) {
     case "method": opreg = Op.R_STACK; opnd = a.offset; break;
     case "global": opreg = Op.R_ABS; opnd = a.offset; break;
     case "const": opreg = Op.R_ABS; opnd = a.offset; break;
     case "lab": opreg = Op.R_ABS; opnd = a.offset; break;
     case "obj": opreg = Op.R_HEAP; opnd = a.offset; break;
     case "imm": opreg = Op.R_IMM; opnd = a.offset; break;
     case "strings": opreg = Op.R_IMM; opnd = a.offset + 24; break;
     }
   }
}
