package ch12;
public class address {
    public String region;
    public int offset;
    public String regaddr() { return region.equals("method")?"loc":region; }
    public String str() {
        if (region.equals("lab")) return "L"+offset;
	return regaddr() + ":" + offset; }
    public void print() { System.out.print(str()); }
    address(String s, int o) { region = s; offset = o; }
}
