package ch12;
public class yyerror {
    static int yyerror_isinitialized;
    static int yymaxstate = 1;
    static yyerror singleton;
    class errortable {
	// i == # of entries
	// if i == 1 then use msg else use p[j].msg where p[j].i == yychar
	public int i; public String msg; public errortable p[];
	errortable(int ii, String m) {
	    i = ii; msg = m;
	}
	errortable(int ii, String m, int n) {
	    i = ii; msg = m; p = new errortable[n];
	}
    }
    static errortable errtab[];
    static int __merr_errors;
    public yyerror() {
	System.out.println("yyerror called, yystate is " + j0.par.yystate);
	errtab = new errortable[1];
	errtab[0] = new errortable(1, "semi-colon expected");
    }
    public static void yyerror(String s) {
	//      if (singleton == null) singleton = new yyerror();
	//      if (s.indexOf("stack") != -1) {
         System.err.println(s);
         System.exit(1);
	 //      }
      if (__merr_errors++ > 10) {
	  System.err.println("too many errors, aborting");
	  System.exit(__merr_errors);
      }
      if (j0.yyfilename != null) {
      	  System.err.print(j0.yyfilename+":");
      }
      int state = j0.par.yystate;
      if (state > yymaxstate) {
	  int j;
	  errortable et[] = new errortable[state+1];
	  for (j = 0; j < yymaxstate; j++)
	      et[j] = errtab[j];
	  //	  for (; j <= state; j++) {
	  //	      et[j] = new errortable(1, "syntax error");
	  //	  }
	  errtab = et;
	  yymaxstate = state;
      }

      if ((s.equals("syntax error") || s.equals("parse error")) &&
      	  (state >= 0) && (state <= yymaxstate)) {
	  if (errtab[state].i == 1) {
      	      s = errtab[state].msg;
      	  }
      	  else {
	      int i;
      	      for(i=1;i<=errtab[state].i;i++)
      		  if (j0.par.yychar == errtab[state].p[i].i) {
             	      s=errtab[state].p[i].msg; break;
      		  }
	      if (i>errtab[state].i && errtab[state].i > 0)
		  s = errtab[state].p[0].msg;
	  }
      }
      if (s.equals("syntax error") || s.equals("parse error")) {
      	  s = s+" ("+state+";"+j0.par.yychar+")";
      }
      System.err.println(j0.yylineno+": # \\\"" + j0.yytext() + "\\\": "+s);
    }
}

