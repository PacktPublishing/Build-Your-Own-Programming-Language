package ch13;
public class address {
    public String region;
    public int offset;
    public String regaddr() { return region.equals("method")?"loc":region; }
    public String str() {
        if (region.equals("lab")) return "L"+offset;
	return regaddr() + ":" + offset; }
    public int intgr() {
	if (region.equals("imm")) return offset;
	System.err.println("intgr() on region " + region);
	return 0;
    }
    public void print() { System.out.print(str()); }
    address(String s, int o) { region = s; offset = o; }
}
