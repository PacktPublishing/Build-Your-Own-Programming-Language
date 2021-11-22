package ch16;
import java.util.ArrayList;
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
  ArrayList<tac> icode;
  address addr, first, follow, onTrue, onFalse;

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
    case "ClassDecl":
	stab.insert(kids[0].tok.text, false, kids[0].stab, null);
	//		    new classtype(kids[0].tok.text, kids[0].stab));
       break;
    case "FieldDecl": case "LocalVarDecl":
       tree k = kids[1];
       while ((k != null) && k.sym.equals("VarDecls")) {
         insert_vardeclarator(k.kids[1]);
         k = k.kids[0];
         }
       insert_vardeclarator(k); return;
    case "MethodDecl":
	String s = kids[0].kids[1].kids[0].tok.text;
	stab.insert(s, false, kids[0].stab, kids[0].kids[1].typ);
	kids[0].stab.insert("return", false, null, kids[0].kids[0].typ);
	break;
    case "FormalParm":
      insert_vardeclarator(kids[1]); return;
    }
    if (kids!=null)
	for(tree k : kids)
	   if (k!=null)
	      k.populateSymTables();
  }

  void insert_vardeclarator(tree vd) {
    if (vd.tok != null) {
      stab.insert(vd.tok.text, false, null, vd.typ);
      }
    else insert_vardeclarator(vd.kids[0]);
  }

  void calc_isConst() {
   for(int i=0; i <nkids; i++)
      kids[i].calc_isConst();
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
  }

  void mkcls() {
    symtab_entry rv;
    if (sym.equals("ClassDecl")) {
      int ms=0, fs=0;
      rv = stab.lookup(kids[0].tok.text);
      for(String k : rv.st.t.keySet()) {
	  symtab_entry ste = rv.st.t.get(k);
	  if ((ste.typ.str()).startsWith("method ")) ms++;
	  else fs++;
      }
      parameter flds[] = new parameter[fs];
      parameter methds[] = new parameter[ms];
      fs=0; ms=0;
      for(String k : rv.st.t.keySet()) {
	  symtab_entry ste = rv.st.t.get(k);
	  if ((ste.typ.str()).startsWith("method "))
	      methds[ms++] = new parameter(k, ste.typ);
	  else flds[fs++] = new parameter(k, ste.typ);
      }
      rv.typ = new classtype(kids[0].tok.text, rv.st,
			       flds, methds, new typeinfo[0]);
    }
    else for(int i = 0; i<nkids; i++)
      if (kids[i].nkids>0) kids[i].mkcls();
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
   case "IDENTIFIER":
     if (stab.lookup(tok.text) == null)
        j0.semErr("undeclared variable " + tok.text);
     break;
   case "FieldAccess": case "QualifiedName":
     kids[0].check_block();
     break;
  case "MethodCall":
      kids[0].check_block();
      if (rule == 1290)
         kids[1].check_block();
      else kids[2].check_block();
      break;
   case "LocalVarDecl": break;
   default:
      for(int i=0;i<nkids;i++)
         kids[i].check_block();
   }
  }

  void calctype() {
    for(int i=0; i<nkids; i++) kids[i].calctype();
    switch (sym) {
    case "FieldDecl": typ = kids[0].typ; return;
    case "token":
	if ((typ = tok.typ) != null) return;
	switch (tok.cat) {
	case parser.IDENTIFIER:
	    if (stab != null) {
		symtab_entry rv = stab.lookup(tok.text);
		if (rv != null)
		    if ((typ = rv.typ) != null) return;
	    }
	    typ = new classtype(tok.text);
	    return;
	default:
	  j0.semErr("can't grok the type of " + tok.text + " cat " + tok.cat);
	}
	break;
    default: j0.semErr("don't know how to calctype " + sym);
    }
  }

  void cksig(methodtype sig) {
    int i = sig.parameters.length, nactual = 1;
    tree t = kids[1];
      if (t == null) {
        if (i != 0) j0.semErr("0 params, expected " + i);
      }
      else {
        while (t.sym.equals("ArgList")) { nactual++; t=t.kids[0]; }
	if (nactual != i)
	    j0.semErr(nactual + " parameters, expected "+ i);
	t = kids[1];
	i--;
	while (t.sym.equals("ArgList")) {
	    check_types(t.kids[1].typ, sig.parameters[i]);
	    t = t.kids[0];
	    i--;
	}
	check_types(t.typ, sig.parameters[0]);
      }
      typ = sig.return_type;
    }

  typeinfo [] mksig() {
  switch (sym) {
    case "FormalParm": return new typeinfo[]{kids[0].typ};
    case "FormalParmList":
      typeinfo ta1[] = kids[0].mksig();
      typeinfo ta2[] = kids[1].mksig();
      typeinfo ta[] = new typeinfo[ta1.length + ta2.length];
      for(int i=0; i<ta1.length; i++) ta[i]=ta1[i];
      for(int j=0; j<ta2.length; j++)
	ta[ta1.length+j]=ta1[j];
      return ta;
      }
    return null;
  }

  void assigntype(typeinfo t) {
    typ = t;
    switch (sym) {
    case "VarDeclarator":
      kids[0].assigntype(new arraytype(t));
      return;
    case "MethodDeclarator": // pass a return type into a method
	typeinfo parmList[];
	if (kids[1] != null) parmList = kids[1].mksig();
	else parmList = new typeinfo [0];
	kids[0].typ = typ = new methodtype(parmList , t);
	return;
    case "token":
      switch (tok.cat) {
        case parser.IDENTIFIER: return;
        default: j0.semErr("eh? " + tok.cat);
      }
      break;
      default: j0.semErr("don't know how to assigntype " + sym);
    }
    if(kids!=null) for(tree k : kids) k.assigntype(t);
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
    
  public typeinfo dequalify() {
      typeinfo rv = null;
      symtab_entry ste;
      if (kids[0].sym.equals("QualifiedName"))
	  rv = kids[0].dequalify();
      else if (kids[0].sym.equals("token") &
	       (kids[0].tok.cat==parser.IDENTIFIER)) {
	  if ((ste = stab.lookup(kids[0].tok.text)) == null)
	      j0.semErr("unknown symbol " + kids[0].tok.text);
	  rv = ste.typ;
      }
      else j0.semErr("can't dequalify " + sym);
      if (!rv.basetype.equals("class"))
	  j0.semErr("can't dequalify " + rv.basetype);
      ste = ((classtype)rv).st.lookup(kids[1].tok.text);
      if (ste != null) return ste.typ;
      j0.semErr("couldn't lookup " + kids[1].tok.text +
		  " in " + rv.str());
      return null;
  }

  void checktype(boolean in_codeblock) {
    if (checkkids(in_codeblock)) return;
    if (! in_codeblock) return;
    switch (sym) {
    case "Assignment": typ = check_types(kids[0].typ, kids[2].typ); break;
    case "AddExpr": typ = check_types(kids[0].typ, kids[1].typ); break;
    case "RelExpr": typ = check_types(kids[0].typ, kids[2].typ); break;
    case "ArgList": case "Block": case "BlockStmts": typ = null; break;
    case "MethodCall":
      if (rule == 1290) {
        symtab_entry rve;
        methodtype rv;
        if (kids[0].sym.equals("QualifiedName")) {
          rv = (methodtype)(kids[0].dequalify());
          cksig(rv);
          }
        else {
          if (!kids[0].sym.equals("token"))
            j0.semErr("can't check type of " + kids[0].sym);
          if (kids[0].tok.cat == parser.IDENTIFIER) {
	      // System.out.println("checking the type of a call to " +
	      //                    kids[0].tok.text);
            if ((rve = stab.lookup(kids[0].tok.text)) != null) {
  	    if (! (rve.typ instanceof methodtype))
              j0.semErr("method expected, got " + rve.typ.str());
	    rv = (methodtype)rve.typ;
            cksig(rv);
            }
          }
          else j0.semErr("can't typecheck token " + kids[0].tok.cat);
          }
        }
      else j0.semErr("Jzero does not handle complex calls");
      break;
    case "QualifiedName":
	if (kids[0].typ instanceof classtype) {
	  classtype ct = (classtype)(kids[0].typ);
          typ = (ct.st.lookup(kids[1].tok.text)).typ;
	  }
        else if (kids[0].typ instanceof arraytype) {
          typ = new typeinfo("int");
          }
        else j0.semErr("illegal . on  " + kids[0].typ.str());
	break;
    case "InstanceCreation": {
      symtab_entry rv;
      if ((rv = stab.lookup(kids[0].tok.text))==null)
        j0.semErr("unknown type " + kids[0].tok.text);
      if ((typ = rv.typ) == null)
        j0.semErr(kids[0].tok.text + " has unknown type");
      break;
    }
    case "ArrayCreation":
	typ = new arraytype(kids[0].typ); break;
    case "ArrayAccess":
	if (kids[0].typ.str().startsWith("array ")) {
	    if (kids[1].typ.str().equals("int"))
		typ = ((arraytype)(kids[0].typ)).element_type;
	    else j0.semErr("subscripting array with "+kids[1].typ.str());
          }
	else j0.semErr("illegal subscript on type "+ kids[0].typ.str());
	break;
    case "ReturnStmt":
      symtab_entry ste;
      if ((ste=stab.lookup("return")) == null)
         j0.semErr("stab did not find a returntype");
      typeinfo rt = ste.typ;
      if (kids[0].typ != null)
          typ = check_types(rt, kids[0].typ);
      else { // return; check that return type is void
          if (!rt.str().equals("void"))
	      j0.semErr("void return from non-void method");
	  typ = rt;		    
          }
      break;
    case "token": typ = tok.type(stab); break;
    case "IfThenStmt": case "WhileStmt": { // no checks for j0 statements
    }
    default: j0.semErr("cannot check type of " + sym);
    }
  }

   public String get_op() {
     switch (sym) {
     case "ReturnStmt" : return "return";
     case "MethodCall" : return "param";
     case "Assignment" : return "=";
     case "AddExpr": if (rule==1320) return "+"; else return "-";
     case "RelExpr": {
        if (kids[1].sym.equals("token")) return kids[1].tok.text;
        }
     default: return null;
     }
   }

   public typeinfo check_types(typeinfo op1, typeinfo op2) {
     String operator = get_op();
     switch (operator) {
     case "param": case "return": case "=": case "+": case"-": {
       tree tk;
       //       if ((tk = findatoken())!=null)
       //         System.out.print("line " + tk.tok.lineno + ": ");
       if (op1.str().equals(op2.str()) &&
	   (op1.str().equals("int") ||
	    op1.str().equals("double") ||
	    op1.str().equals("String"))) {
	   //  System.out.println("typecheck "+operator+" on a "+ op1.str()+
	   //		    " and a "+ op2.str()+ " -> OK");
	 return op1;
	 }
       else if (op1.basetype.equals("array") &&
                op2.basetype.equals("array") &&
                operator.equals("=") &&
                (check_types(((arraytype)op1).element_type,
			     ((arraytype)op2).element_type) != null)) {
                return op1;
               }
       else if (op1.str().equals(op2.str()) & operator.equals("=")) {
	  return op1;
               }
       else j0.semErr("typecheck "+operator+" on a "+ op2.str()+
			" and a "+ op1.str()+ " -> FAIL");
       break;
       }
       case "<": case ">": {
	   if (op1.str().equals(op2.str()) &&
	       (op1.str().equals("int") ||
		op1.str().equals("double"))) {
		    // write("typecheck ",operator," on a ", op1.str(),
		    //     " and a ", op2.str(), " -> OK")
		    return new typeinfo("bool");
               }
        }
     default: j0.semErr("don't know how to check " + operator);
     }
   return null;
   }
   public tree findatoken() {
     tree rv;
     if (sym=="token") return this;
     if (kids != null)
     for (tree t : kids) if ((rv=t.findatoken()) != null) return rv;
     return null;
   }
  address genlabel() {
    return new address("lab", serial.getid());
  }
  address genlocal() {
	return stab.genlocal();
  }
void genfirst() {
  if (kids != null) for(tree k : kids) k.genfirst();
  switch (sym) {
  case "UnaryExpr": {
      if (kids[1].first != null) first = kids[1].first;
      else first = genlabel();
      break;
  }
  case "AddExpr": case "MulExpr": case "RelExpr": {
      if (kids[0].first != null) first = kids[0].first;
      else if (kids[1].first != null) first = kids[1].first;
      else first = genlabel();
      break;
      }
  case "Block": case "WhileStmt": {
      if (kids[0].first != null) first = kids[0].first;
      else first = genlabel();
      break;
      }
  case "BlockStmts": {
    if (kids[1].first == null) kids[1].first = genlabel();
    if (kids[0].first != null) first = kids[0].first;
    else first = kids[1].first;
    break;
  }
    // ...
  default: {
      if (kids != null)
	  for(tree k : kids)
	      if (k.first != null) { first = k.first; break; }
      }
   }
}

void genfollow() {
  switch (sym) {
   case "MethodDecl": {
     kids[1].follow = follow = genlabel();
     break;
     }
   case "BlockStmts": {
      kids[0].follow = kids[1].first;
      kids[1].follow = follow;
      break;
      }
   case "Block": {
      kids[0].follow = follow;
      break;
      }
   // ...
   }
  if (kids != null) for(tree k : kids) k.genfollow();
}

void gencode() {
  if (kids != null) for(tree k : kids) k.gencode();
  switch (sym) {
  case "ClassDecl": { genClassDecl(); break; }
  case "AddExpr": { genAddExpr(); break; }
  case "MulExpr": { genMulExpr(); break; }
  case "RelExpr": { genRelExpr(); break; }
  case "WhileStmt": { genWhileStmt(); break; }
  case "IfThenStmt": { genIfThenStmt(); break; }
  case "Assignment": { genAssignment(); break; }
  case "MethodCall": { genMethodCall(); break; }
  case "MethodDecl": { genMethodDecl(); break; }
  case "QualifiedName": { genQualifiedName(); break; }
  // ...
  case "token": { gentoken(); break; }
  default: {
    icode = new ArrayList<tac>();
    if (kids != null) for(tree k : kids) icode.addAll(k.icode);
    }
  }
}

void genClassDecl() {
  int first_global;
  first_global = 0;
  icode = new ArrayList<tac>();
  // emit string constants
  if (j0.stringtab.t.size()>0) {
     icode.addAll( gen(".string") );
     for(String k : j0.stringtab.t.keySet()) {
	 symtab_entry ste = j0.stringtab.t.get(k);
	 if (ste.addr == null) {
	     System.err.println("null label in stringtab");
	     System.exit(1);
	 }
         icode.addAll( gen("LAB", ste.addr));
         icode.addAll( gen("string", new address(k, 0)) );
        }
      }

  // emit the globals 
  for(String k : j0.global_st.t.keySet()) {
      symtab_entry ste = j0.global_st.t.get(k);
      // if type is not a proc...or class or something
      if (first_global == 0) {
	  icode.addAll( gen(".global") ); first_global = 1; }
          icode.addAll( gen("global", ste.addr, new address(k,0)) );
        }
  icode.addAll( gen(".code") );
  if (kids != null) for(tree k: kids) icode.addAll(k.icode);
}

void genAssignment() {
  addr = kids[0].addr;
  icode = new ArrayList<tac>();
  icode.addAll(kids[0].icode); icode.addAll(kids[2].icode);
  icode.addAll(gen("ASN", addr, kids[2].addr));
}

void genAddExpr() {
  addr = genlocal();
  icode = new ArrayList<tac>();
  icode.addAll(kids[0].icode); icode.addAll(kids[1].icode);
  if (typ.str().equals("String")) {
      if (rule != 1320)
	  j0.semErr("subtraction on strings is not defined");
      icode.addAll(gen("SADD", addr, kids[0].addr, kids[1].addr));
  }
  else
      icode.addAll(gen(((rule==1320)?"ADD":"SUB"), addr,
                   kids[0].addr, kids[1].addr));
}

void genMulExpr() {
  addr = genlocal();
  icode = new ArrayList<tac>();
  icode.addAll(kids[0].icode); icode.addAll(kids[1].icode);
  if (rule==1310)
    icode.addAll(gen("MUL", addr, kids[0].addr, kids[1].addr));
  else if (rule==1311)
    icode.addAll(gen("DIV", addr, kids[0].addr, kids[1].addr));
  else
    icode.addAll(gen("MOD", addr, kids[0].addr, kids[1].addr));
}

void genMethodDecl() {
    icode = new ArrayList<tac>();
    String methodname = kids[0].kids[1].kids[0].tok.text;
    int nparams = 0;
    int nlocals = 0;
    symtab_entry m = j0.global_st.lookup("hello").st.t.get(methodname);
    if (m == null) System.err.println("null m");
    nparams = ((methodtype)(m.typ)).parameters.length;
    nlocals = m.st.count/8;
    icode.addAll(gen("proc", new address(methodname, 0), new address("imm", nparams), new address("imm", nlocals)));
    for(tree k : kids) icode.addAll(k.icode);
    icode.addAll(gen("LAB", follow));
    icode.addAll(gen("RET"));
    icode.addAll(gen("end"));
}

void gentoken() {
  icode = new ArrayList<tac>();
  switch (tok.cat) {
    case parser.IDENTIFIER: {
      symtab_entry ste;
      ste = stab.lookup(tok.text);
      if (ste == null) {
      }
      else {
	  addr = ste.addr;
      }
      break;
      }
    case parser.INTLIT: {
      addr = new address("imm", Integer.parseInt(tok.text)); break;
    }
    case parser.STRINGLIT: {
      j0.stringtab.insert(tok.text, true, null, new typeinfo("string"));
      addr = j0.stringtab.lookup(tok.text).addr;
      break;
    }
    // ...
    }
}

void gentargets() {
   switch (sym) {
   case "IfThenStmt": {
      kids[0].onTrue = kids[1].first;
      kids[0].onFalse = follow;
      }
   case "CondAndExpr": {
      kids[0].onTrue = kids[1].first;
      kids[0].onFalse = onFalse;
      kids[1].onTrue = onTrue;
      kids[1].onFalse = onFalse;
      }    
   // ...
   }
   if (kids!=null) for (tree k:kids) k.gentargets();
}

void genRelExpr() {
  String op = "ERROR";
  switch (kids[1].tok.cat) {
    case '<': op="BLT"; break; case ';': op="BGT"; break;
    case parser.LESSTHANOREQUAL: op="BLE"; break;
    case parser.GREATERTHANOREQUAL: op="BGT";
    }
  icode = new ArrayList<tac>();
  icode.addAll(kids[0].icode); icode.addAll(kids[2].icode);
  icode.addAll(gen(op, onTrue, kids[0].addr, kids[2].addr));
  icode.addAll(gen("GOTO", onFalse));
}

void genIfThenStmt() {
  icode = new ArrayList<tac>();
  icode.addAll(kids[0].icode);
  icode.addAll(gen("LAB", kids[0].onTrue));
  icode.addAll(kids[1].icode);
}

void genWhileStmt() {
  icode = new ArrayList<tac>();
  icode.addAll(gen("LAB", kids[0].first));
  icode.addAll(kids[0].icode);
  icode.addAll(gen("LAB", kids[0].onTrue));
  icode.addAll(kids[1].icode);
  icode.addAll(gen("GOTO", kids[0].first));
}

void genQualifiedName() {
  symtab_entry ste;
  classtype ct;

  icode = new ArrayList<tac>();
  icode.addAll( kids[0].icode );
  if (typ != null && typ instanceof methodtype) {// no icode, compile-time method resolution
      addr = new address(kids[0].typ.str() + "__" + kids[1].tok.text, 0);
      }
   else {
      addr = genlocal();
      if (kids[0].typ instanceof arraytype) { // array.length
	  icode.addAll( gen("ASIZE", addr, kids[0].addr) );
      }
      else if ((kids[0].typ != null) && (kids[0].typ instanceof classtype) &&
	       ((ct = (classtype)(kids[0].typ)) != null) &&
	       ((ste=ct.st.lookup(kids[1].tok.text)) != null)) {
	  // lookup address within class
	  if (kids[1].addr == null) kids[1].addr = ste.addr;
	  icode.addAll( gen("FIELD", addr, kids[0].addr, kids[1].addr));
      }
   }
}

void genMethodCall() {
  int nparms = 0;
  if (kids[1] != null) {
    tree k = kids[1];
    icode = k.icode;
    while (k.sym.equals("ArgList")) {
      icode.addAll(gen("PARM", k.kids[1].addr));
      k = k.kids[0]; nparms++;
      }
    icode.addAll(gen("PARM", k.addr)); nparms++;
    }
  else
    icode = new ArrayList<tac>();
  if (kids[0].sym.equals("QualifiedName")) {
    icode.addAll(kids[0].icode);
    icode.addAll(gen("PARM", kids[0].kids[0].addr));
  }
  else icode.addAll(gen("PARM", new address("self",0)));
  icode.addAll(gen("CALL", kids[0].addr, new address("imm",nparms)));
}


ArrayList<tac> gen(String o, address ... a) {
  ArrayList<tac> L = new ArrayList<tac>();
  tac t = null;
  switch(a.length) {
    case 3: t = new tac(o, a[0], a[1], a[2]); break;
    case 2: t = new tac(o, a[0], a[1]); break;
    case 1: t = new tac(o, a[0]); break;
    case 0: t = new tac(o); break;
    default: j0.semErr("gen(): wrong # of arguments");
  }
  L.add(t);
  return L;
}

public void poolStrings() {
  if (kids != null)
    for (int i = 0; i < kids.length; i++)
  if ((kids[i] != null) && kids[i] instanceof tree) {
    if (kids[i].nkids>0) kids[i].poolStrings();
    else kids[i] = kids[i].internalize();
  }
}

public tree internalize() {
  tree t1, t2, t3, t4;
  if (!sym.equals("STRINGLIT")) return this;
  t4 = new tree("token",parser.IDENTIFIER,
            new token(parser.IDENTIFIER,"pool", tok.lineno, tok.colno));
  t3 = new tree("token",parser.IDENTIFIER,
            new token(parser.IDENTIFIER,"String", tok.lineno, tok.colno));
  t2 = node("QualifiedName", 1040, t3, t4);
  t1 = node("MethodCall",1290,t2,this);
  return t1;
}

   public tree(String s, int r, token t) {
	id = serial.getid();
        sym = s; rule = r; tok = t; if (tok!=null) typ = tok.typ; }

    public tree(String s, int r, tree[] t) {
	id = serial.getid();
	//	System.out.println("id " + id + " goes to " + s + "(" +r+")");
	sym = s; rule = r; nkids = t.length;
	kids = t;
    }
    public static tree node(String s,int r,tree...p) {
	tree[] t = new tree[p.length];
	for(int i = 0; i < t.length; i++) t[i] = p[i];
	return new tree(s,r,t);
  }
}
