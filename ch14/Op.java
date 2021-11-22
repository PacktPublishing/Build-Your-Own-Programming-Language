package ch14;
public class Op {
  public final static short HALT=1, NOOP=2, ADD=3, SUB=4,
    MUL=5, DIV=6, MOD=7, NEG=8, PUSH=9, POP=10, CALL=11,
    RETURN=12, GOTO=13, BIF=14, LT=15, LE=16, GT=17, GE=18,
      EQ=19, NEQ=20, LOCAL=21, LOAD=22, STORE=23, SADD=24;
    public final static short LABEL=101, STRING=102,
	CODE=103, PROC=104, GLOBAL=105, END=106;
  public final static short R_NONE=0, R_ABS=1, R_IMM=2, R_STACK=3, R_HEAP=4;
}
