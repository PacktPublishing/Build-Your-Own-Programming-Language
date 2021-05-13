package ch9;
public class typeinfo {
   String basetype;
   public typeinfo() { basetype = "unknown"; }
   public typeinfo(String s) { basetype = s; }
   public String str() { return basetype; }
}
