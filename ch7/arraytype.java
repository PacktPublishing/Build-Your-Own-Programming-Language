package ch7;
public class arraytype extends typeinfo {
   typeinfo element_type;
   public arraytype(typeinfo t) {
      basetype = "array"; element_type = t; }
}
