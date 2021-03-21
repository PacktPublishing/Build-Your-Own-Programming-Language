package ch7;
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
  typeinfo typ;

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
	stab.insert(kids[0].tok.text, false, kids[0].stab,
		    new classtype(kids[0].tok.text, kids[0].stab));
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
	String s = kids[0].kids[1].kids[0].tok.text;
	stab.insert(s, false, kids[0].stab,
		    new methodtype(null, null));
	break;
    }
    case "FormalParm": {
      insert_vardeclarator(kids[1]); return;
      }
    }
    if (kids!=null) {
	for(tree k : kids) {
	   if (k!=null) {
	      k.populateSymTables();
	   }
	}
    }
  }

  void insert_vardeclarator(tree vd) {
    if (vd.tok != null) {
      stab.insert(vd.tok.text, false, null, vd.typ);
      }
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

  typeinfo calctype() {
    for(int i=0; i<nkids; i++) kids[i].calctype();
    switch (sym) {
      case "FieldDecl": return typ = kids[0].typ;
      case "token": {
	  switch (tok.cat) {
	  case parser.IDENTIFIER:{ return typ=new classtype(tok.text); }
	  case parser.INT: { return typ=new typeinfo(tok.text); }
          default: j0.semerror("can't grok the type of " + tok.text);
	  }
      }
      default: j0.semerror("don't know how to calctype " + sym);
    }
    return null;
  }

  void assigntype(typeinfo t) {
    typ = t;
    switch (sym) {
    case "VarDeclarator": {
      kids[0].assigntype(new arraytype(t));
      return;
    }
    case "token": {
      switch (tok.cat) {
        case parser.IDENTIFIER:{ return; }
        default: j0.semerror("eh? " + tok.cat);
	  }
        }
      default: j0.semerror("don't know how to assigntype " + sym);
    }
    for(tree k : kids) k.assigntype(t);
  }

  public boolean checkkids(boolean in_codeblock) {
    switch (sym) {
    case "MethodDecl": { kids[1].checktype(true); return true; }
    case "LocalVarDecl": { kids[1].checktype(false); return true; }
    case "FieldAccess": { kids[0].checktype(in_codeblock);
                          return true; }
    case "QualifiedName":
       kids[0].checktype(in_codeblock); break;
    default: if (kids != null) for (tree k : kids)
	       k.checktype(in_codeblock);
    }
    return false;
  }
    
  void checktype(boolean in_codeblock) {
    if (checkkids(in_codeblock)) return;
    if (! in_codeblock) return;
    switch (sym) {
    case "Assignment": {
	if (kids[0].typ == null) System.out.println("kids[0]");
	if (kids[2].typ == null) System.out.println("kids[2]");
	typ = check_types(kids[0].typ, kids[2].typ); break;
    }
    case "AddExpr": typ = check_types(kids[0].typ, kids[1].typ); break;
    case "Block": case "BlockStmts": typ = null; break;
    case "MethodCall": break;
    case "QualifiedName": {
	if (kids[0].typ instanceof classtype) {
	  classtype ct = (classtype)(kids[0].typ);
          typ = (ct.st.lookup(kids[1].tok.text)).typ;
	} else j0.semerror("illegal . on  " + kids[0].typ.str());
	break;
    }
    case "token": typ = tok.type(stab); break;
    default: j0.semerror("cannot check type of " + sym);
    }
  }

   public String get_op() {
     switch (sym) {
     case "Assignment" : return "=";
     case "AddExpr": if (rule==1320) return "+"; else return "-";
     }
     return sym;
   }
   public typeinfo check_types(typeinfo op1, typeinfo op2) {
     String operator = get_op();
     switch (operator) {
     case "=": case "+": case"-": {
       tree tk;
       if ((tk = findatoken())!=null)
         System.out.print("line " + tk.tok.lineno + ": ");
       if ((op1.basetype.equals(op2.basetype)) &&
	   (op1.basetype.equals("int"))) {
         System.out.println("typecheck "+operator+" on a "+ op2.str()+
			    " and a "+ op1.str()+ " -> OK");
	 return op1;
	 }
       else j0.semerror("typecheck "+operator+" on a "+ op2.str()+
			" and a "+ op1.str()+ " -> FAIL");
       }
     default: j0.semerror("don't know how to check " + operator);
     }
   return null;
   }
   public tree findatoken() {
     tree rv;
     if (sym=="token") return this;
     for (tree t : kids) if ((rv=t.findatoken()) != null) return rv;
     return null;
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
