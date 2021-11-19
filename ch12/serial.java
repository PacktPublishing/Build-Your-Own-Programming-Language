package ch12;
class serial {
    static int serial;
    public static int getid(){ serial++; return serial; }
}
