package ch13;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
public class j0machine {
  public static byte[] code, stack;
  public static ByteBuffer codebuf, stackbuf;
  public static int ip, sp, bp, hp, op, opr, finstr;
  public static long opnd;

  public static boolean loadbytecode(String filename)
    throws IOException {
      code = Files.readAllBytes(Paths.get(filename));
      byte[] magstr = "Jzero!!\0".getBytes(
                          StandardCharsets.US_ASCII);
      int i = find(magstr, code);
      if (i>=0) {
        // need to check version, allow for self-execution script header
        codebuf = ByteBuffer.wrap(code);
        return true;
      }
      else return false;
  }

  public static int find(byte[]needle, byte[]haystack) {
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
  public static void init(String filename)
    throws IOException {
      ip = sp = 0;
      if (! loadbytecode(filename)) {
         System.err.println("cannot open program.j0");
         System.exit(1);
         }
      ip = 16;
      ip = finstr = (int) (8*getOpnd());
      stack = new byte[800000];
      stackbuf = ByteBuffer.wrap(stack);
    }
  public static void fetch() {
      op = code[ip];
      opr = code[ip+1];
      if (opr != 0) { opnd = getOpnd(); }
      ip += 8;
  }
  public static long getOpnd() {
   long i=0;
   if (codebuf.get(ip+7) < 0) i = -1;
   for(int j=7;j>1;j--) i = (i<<8) | codebuf.get(ip+j);
   return i;
  }
  public static void stop(String s) {
    System.err.println(s);
    System.exit(1);
    }
  public static void interp() {
    for(;;) {
      fetch();
      switch (op) {
        case Op.HALT: { stop("Execution complete."); break; }
        case Op.NOOP: { break; }
        case Op.ADD: {
          long val1 = stackbuf.getLong(sp--);
          long val2 = stackbuf.getLong(sp--);
	  stackbuf.putLong(sp++, val1 + val2);
          break;
          }
      case Op.PUSH: {
	  long val = deref(opr, opnd);
	  push(val);
	  break;
      }
      case Op.POP: {
	  long val = pop();
	  assign(opnd, val);
	  break;
      }
      case Op.GOTO: {
	  ip = (int)opnd;
	  break;
      }
      case Op.BIF: {
	  if (pop() != 0)
	      ip = (int)opnd;
	  break;
      }

      case Op.CALL: {
	  long f;
	  f = stackbuf.getLong(sp-16-(int)(8*opnd));
	  if (f >= 0) {
	      push( ip);
	      push( bp);
	      bp = sp;
	      ip = (int)f;
             }
	  else if (f == -1) do_println();
	  else { stop("no CALL defined for " + f); }
	  break;
           }
      case Op.RETURN: {
           sp = bp;
           bp = (int)pop();
           ip = (int)pop();
	   break;
           }

      case Op.LT: { stop("LT not implemented yet."); break; }
      case Op.LE: { stop("LE not implemented yet."); break; }
      default: { stop("Illegal opcode " + op); }
      }
    }
  }

    public static void do_println() {
	// execute a system.out.println on an argument on the stack
	long addr = stackbuf.getLong(sp-16);
	byte b = codebuf.get((int)addr++);
	while (b != 0) {
	   System.out.print((char)b);
           b = codebuf.get((int)addr++);
        }
	System.out.println();
    }

    public static long deref(int reg, long od) {
	switch(reg) {
	case Op.R_ABS: { return codebuf.getLong((int)od); }
	case Op.R_IMM: { return od; }
	case Op.R_STACK: { return stackbuf.getLong(bp+(int)od); }
        default: { stop("deref region " + reg); }
	}
	return 0;
    }

    public static void assign(long ad, long val) {
	switch(opr) {
	case Op.R_ABS: {  }
	case Op.R_IMM: {  }
	case Op.R_STACK: {  }
        default: {  } // stop("assign region " + opr); }
	}
    }

    public static void push(long val) {
      stackbuf.putLong(val);
      sp += 8;
    }

    public static long pop() {
      sp -= 8;
      long rv = stackbuf.getLong(sp);
      return rv;
    }
}
