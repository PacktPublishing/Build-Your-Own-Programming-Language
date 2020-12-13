package ch6;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
class tree {
  int id, rule, nkids;
  String sym;
  token tok;
  tree kids[];
  Boolean isConst;
  symtab stab;

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

  void mkSymTables(symtab curr) {
   stab = curr;
   switch (sym) {
   case "ClassDecl": curr = new symtab("class", curr); break;
   case "MethodDecl": curr = new symtab("method", curr); break;
   }
   for (int i=0; i<nkids; i++) kids[i].mkSymTables(curr);
  }

  void populateSymTables() {
    switch(sym) {
    case "ClassDecl": {
       stab.insert(kids[0].tok.text, false, kids[0].stab);
       break;
    }
    case "FieldDecl": case "LocalVarDecl": {
       tree k = kids[1];
       while ((k != null) && k.sym.equals("VarDecls")) {
         insert_vardeclarator(k.kids[1]);
         k = k.kids[0];
         }
       insert_vardeclarator(k); return;
       }
    case "MethodDecl": {
       stab.insert(kids[0].kids[1].kids[0].tok.text, false, kids[0].stab); }
    case "FormalParm": {
      insert_vardeclarator(kids[1]); return; }
    }
   for(int i = 0; i < nkids; i++) {
      tree k = kids[i];
      k.populateSymTables();
   }
  }

  void insert_vardeclarator(tree vd) {
    if (vd.tok != null) stab.insert(vd.tok.text, false);
    else insert_vardeclarator(vd.kids[0]);
  }

  void calc_isConst() {
   switch(sym) {
   case "INTLIT": case "DOUBLELIT": case "STRINGLIT":
   case "BOOLFALSE": case "BOOLTRUE": isConst = true; break;
   case "UnaryExpr": isConst = kids[1].isConst; break;
   case "RelExpr":
      isConst = kids[0].isConst && kids[2].isConst; break;
   case "CondOrExpr": case "CondAndExpr":
   case "EqExpr": case "MULEXPR": case "ADDEXPR":
      isConst = kids[0].isConst && kids[1].isConst; break;
   default: isConst = false;
   }
   for(int i=0; i <nkids; i++)
      kids[i].calc_isConst();
  }

  void checkSymTables() { check_codeblocks(); }
  void check_codeblocks() {
  tree k;
   if (sym.equals("MethodDecl")) { kids[1].check_block(); }
   else {
      for(int i = 0; i<nkids; i++){
         k = kids[i];
         if (k.nkids>0) k.check_codeblocks();
      }
   }
  }
void check_block() {
   switch (sym) {
   case "IDENTIFIER": {
     if (stab.lookup(tok.text) == null)
        j0.semerror("undeclared variable " + tok.text);
     break;
     }
   case "FieldAccess": case "QualifiedName":
     kids[0].check_block();
     break;
  case "MethodCall": {
      kids[0].check_block();
      if (rule == 1290)
         kids[1].check_block();
      else kids[2].check_block();
      break;
     }
   case "LocalVarDecl": break;
   default:
      for(int i=0;i<nkids;i++)
            kids[i].check_block();
   }
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
