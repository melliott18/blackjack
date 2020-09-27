JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		Blackjack.java \
		Card.java \
		Computer.java \
		Deck.java \
		Hand.java \
		Player.java \
		Randomizer.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class
