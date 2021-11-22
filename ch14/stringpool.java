public class stringpool {
static HashMap<String,Long> si;
static HashMap<Long,String> is;
static long serial;
static { si = new HashMap<>(); is = new HashMap<>(); }
public static long put(String s) {
    if (si.containsKey(s)) return si.get(s);
    serial++;
    si.put(s, serial);
    is.put(serial, s);
    return serial;
}
public static String get(long L) {
    return is.get(L);
}
}
