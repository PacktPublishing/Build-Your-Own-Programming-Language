package ch13;
import java.io.PrintStream;
public class x64loc {
  public String reg;
  Object offset;
  public int mode;
    public String str() {
	String rv = "";
	switch (mode) {
	case 1: return reg;
	case 2: return String.valueOf(offset);
	case 3: case 4: {
	    if (reg.equals("lab")) return (String)offset;
	    return offset + "(" + reg + ")";
	}
	case 5: return "$" + String.valueOf(offset);
	case 6: return (offset instanceof Integer)? (".L"+offset) : (String)offset;
	default: System.err.println("x64loc unknown mode " + mode);
    }
	return "unknown";
    }

  public x64loc(String r) { reg = r; mode = 1; }
  public x64loc(int i) { offset=(Object)Integer.valueOf(i); mode=2; }
  public x64loc(String r, int off) {
    if (r.equals("imm")) {
      offset=(Object)Integer.valueOf(off); mode = 5; }
    else if (r.equals("lab")) {
      offset=(Object)Integer.valueOf(off); mode = 6; }
    else { reg = r; offset = (Object)Integer.valueOf(off); mode = 3; }
  }
  public x64loc(String r, String s) {
    reg = r; offset = (Object)s; mode=4;
  }
}
