package ch9;
public class address {
    public String region;
    public int offset;
    public String regaddr() { return region.equals("method")?"loc":region; }
    public String str() {
	switch (region) {
	case "lab": return "L" + offset;
	case "loc": case "imm": case "method":
	case "global": case "class": case "strings":
	    return regaddr() + ":" + offset;
	}
	return region;
    }
    public void print() { System.out.print(str()); }
    address(String s, int o) { region = s; offset = o; }
}
