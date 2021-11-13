
all: java unicon

U=j0x.u j0machine.u op.u
unicon:j0x
%.u : %.icn
	unicon -c $<

j0x: $(U)
	unicon $(U)

JSRC=j0x.java j0machine.java Op.java
java: j0x.class

j0x.class: $(JSRC)
	javac $(JSRC)
