public class j0x {
  public static void main(String[] argv) {
    if (argv.length < 1) {
      System.err.println("usage: j0x file[.j0]" + argv.length);
      System.exit(1);
      }
    String filename = argv[0];
    if (! filename.endsWith(".j0"))
      filename = filename + ".j0";
    try {
      j0machine.init(filename);
    } catch(Exception ex) {
	System.err.println("Can't initialize. Exiting.");
	System.exit(1);
    }
    j0machine.interp();
  }
}
