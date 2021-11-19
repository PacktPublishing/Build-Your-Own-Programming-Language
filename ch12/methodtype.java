package ch12;
public class methodtype extends typeinfo {
   typeinfo [] parameters;
   typeinfo return_type;
    public String str() {
	String s;
	s = "method " + ((return_type!=null)?return_type.str():"undef") + "(";
	for(typeinfo p : parameters)
	    s = s + p.str() + ",";
	s = s.substring(0,s.length()-2) + ")";
	return s;
    }
   methodtype(typeinfo [] p, typeinfo rt){
       parameters = p;
       if (rt !=null) return_type = rt;
       else return_type = new typeinfo("void");
       basetype="method";
   }
}
