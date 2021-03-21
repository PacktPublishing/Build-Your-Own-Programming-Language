package ch7;
public class methodtype extends typeinfo {
   parameter [] parameters;
   typeinfo return_type;
   methodtype(parameter [] p, typeinfo rt){
      parameters = p; return_type = rt;
   }
}
