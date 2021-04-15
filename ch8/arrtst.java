public class arrtst {
   public static void main(String argv[]) {
      int x[];
      x = new int[3];
      x[1] = 0;
      x[0] = x[1];
      x[2] = argv[1];
      System.out.println("hello, world");
   }
}
