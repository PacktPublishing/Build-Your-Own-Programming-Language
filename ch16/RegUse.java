package ch16;
import java.util.ArrayList;
public class RegUse {
   public String reg;
   int offset;
   public boolean loaded, dirty;
    public RegUse(String s, int i) { reg = s; offset=i; loaded=dirty=false; }
   public ArrayList<x64> load() {
      if (loaded) return null;
      loaded = true;
      return j0.xgen("movq", offset+"(%rbp)", reg);
   }
   public ArrayList<x64> save() {
      if (!dirty) return null;
      dirty = false;
      return j0.xgen("movq", reg, offset+"(%rbp)");
   }
}
