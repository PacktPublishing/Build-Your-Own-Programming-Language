package ch5;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
class tree {
  int id;
  String sym;
  int rule;
  int nkids;
  token tok;
  tree kids[];

  public String escape(String s) {
      if (s.charAt(0) == '\"')
        return "\\"+s.substring(0, s.length()-1)+"\\\"";
      else return s;
  }

  public String pretty_print_name() {
    if (tok == null) return sym +"#"+(rule%10);
    else {
	return escape(tok.text)+":"+tok.cat;
	}
    }

  void print_graph(String filename){
    try {
      PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
      pw.printf("digraph {\n");
      j = 0;
      print_graph(pw);
      pw.printf("}\n");
      pw.close();
      }
    catch (java.io.IOException ioException) {
      System.err.println("printgraph exception");
      System.exit(1);
      }
    }

  int j;
  void print_graph(PrintWriter pw) {
  int i;
    if (tok != null) {
	print_leaf(pw);
        return;
    }
    // from here on out, we know we are not a leaf
    print_branch(pw);
    
    for(i=0; i<nkids; i++) {
        if (kids[i] != null) {
            pw.printf("N%d -> N%d;\n", id, kids[i].id);
	    kids[i].print_graph(pw);
        } else {
	    pw.printf("N%d -> N%d_%d;\n", id, id, j);
	    pw.printf("N%d%d [label=\"%s\"];\n", id, j, "Empty rule");
	    j++;
        }
    }
  }

  void print_leaf(PrintWriter pw) {
    String s = parser.yyname[tok.cat];
    print_branch(pw);
    pw.printf("N%d [shape=box style=dotted label=\" %s \\n ", id, s, tok.cat);
    pw.printf("text = %s \\l lineno = %d \\l\"];\n",
	      escape(tok.text), tok.lineno);
  }

  void print_branch(PrintWriter pw) {
    pw.printf("N%d ",id);
    pw.printf("[shape=box label=\"%s",pretty_print_name());
    if (tok != null)
	pw.printf("struct token* leaf %d", tok.id);
    pw.printf("\"];\n");
  }

  public void print(int level) {
    int i;
    for(i=0;i<level;i++) System.out.print(" ");
    if (tok != null) {
      System.out.println(id + "   " + tok.text +
                         " (" + tok.cat + "): "+tok.lineno);
    }
    else {
      System.out.println(id + "   " + sym +
			 " (" + rule + "): "+nkids);
    }
    for(i=0; i<nkids; i++)
      kids[i].print(level+1);
  }
    public void print() {
	print(0);
    }
    public tree(String s, int r, token t) {
	id = serial.getid();
        sym = s; rule = r; tok = t; }

    public tree(String s, int r, tree[] t) {
	id = serial.getid();
	//	System.out.println("id " + id + " goes to " + s + "(" +r+")");
	sym = s; rule = r; nkids = t.length;
	kids = t;
    }
}
