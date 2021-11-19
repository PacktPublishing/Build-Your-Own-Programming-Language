package ch12;
public class arraytype extends typeinfo {
   typeinfo element_type;
    public String str() {
	return "array of " + (element_type!=null?element_type.str():"undef");
    }
   public arraytype(typeinfo t) {
      basetype = "array"; element_type = t; }
}
