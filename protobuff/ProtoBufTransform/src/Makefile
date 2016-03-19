JAVAC=javac
JAVAC_FLAGS=-sourcepath .  -cp ./lib -cp ./lib/java-cup-11b.jar -cp ./lib/java-cup-11b-runtime.jar
JAVA=java
JAVA_FLAGS=
CUP=java_cup.Main
CUPFLAGS=-nopositions -expect 100
JLEX=JLex.Main
all: test

test: absyn ProtobufExp/Yylex.class ProtobufExp/PrettyPrinter.class ProtobufExp/Test.class ProtobufExp/ComposVisitor.class ProtobufExp/AbstractVisitor.class ProtobufExp/FoldVisitor.class ProtobufExp/AllVisitor.class ProtobufExp/parser.class ProtobufExp/sym.class ProtobufExp/Test.class

.PHONY: absyn

%.class: %.java
	${JAVAC} ${JAVAC_FLAGS} $^

absyn: ProtobufExp/Absyn/Exp.java ProtobufExp/Absyn/EMsg.java ProtobufExp/Absyn/Field.java ProtobufExp/Absyn/GeneralField.java ProtobufExp/Absyn/FieldModifier.java ProtobufExp/Absyn/FieldModifier_required.java ProtobufExp/Absyn/FieldModifier_optional.java ProtobufExp/Absyn/FieldModifier_repeated.java ProtobufExp/Absyn/FieldType.java ProtobufExp/Absyn/FieldType_int32.java ProtobufExp/Absyn/FieldType_int64.java ProtobufExp/Absyn/FieldType_string.java ProtobufExp/Absyn/FieldType_bool.java ProtobufExp/Absyn/FieldType_bytes.java ProtobufExp/Absyn/FieldTypeIdent.java ProtobufExp/Absyn/FieldList.java ProtobufExp/Absyn/OneFieldList.java ProtobufExp/Absyn/ConsFieldList.java
	${JAVAC} ${JAVAC_FLAGS} $^

ProtobufExp/Yylex.java: ProtobufExp/Yylex
	${JAVA} ${JAVA_FLAGS} ${JLEX} ProtobufExp/Yylex

ProtobufExp/sym.java ProtobufExp/parser.java: ProtobufExp/ProtobufExp.cup
	${JAVA} ${JAVA_FLAGS} ${CUP} ${CUPFLAGS} ProtobufExp/ProtobufExp.cup
	mv sym.java parser.java ProtobufExp/

ProtobufExp/Yylex.class: ProtobufExp/Yylex.java ProtobufExp/sym.java

ProtobufExp/sym.class: ProtobufExp/sym.java

ProtobufExp/parser.class: ProtobufExp/parser.java ProtobufExp/sym.java

ProtobufExp/PrettyPrinter.class: ProtobufExp/PrettyPrinter.java

clean:
	rm -f ProtobufExp/Absyn/*.class ProtobufExp/*.class

distclean: vclean

vclean:
	 rm -f ProtobufExp/Absyn/Exp.java ProtobufExp/Absyn/EMsg.java ProtobufExp/Absyn/Field.java ProtobufExp/Absyn/GeneralField.java ProtobufExp/Absyn/FieldModifier.java ProtobufExp/Absyn/FieldModifier_required.java ProtobufExp/Absyn/FieldModifier_optional.java ProtobufExp/Absyn/FieldModifier_repeated.java ProtobufExp/Absyn/FieldType.java ProtobufExp/Absyn/FieldType_int32.java ProtobufExp/Absyn/FieldType_int64.java ProtobufExp/Absyn/FieldType_string.java ProtobufExp/Absyn/FieldType_bool.java ProtobufExp/Absyn/FieldType_bytes.java ProtobufExp/Absyn/FieldTypeIdent.java ProtobufExp/Absyn/FieldList.java ProtobufExp/Absyn/OneFieldList.java ProtobufExp/Absyn/ConsFieldList.java ProtobufExp/Absyn/Exp.class ProtobufExp/Absyn/EMsg.class ProtobufExp/Absyn/Field.class ProtobufExp/Absyn/GeneralField.class ProtobufExp/Absyn/FieldModifier.class ProtobufExp/Absyn/FieldModifier_required.class ProtobufExp/Absyn/FieldModifier_optional.class ProtobufExp/Absyn/FieldModifier_repeated.class ProtobufExp/Absyn/FieldType.class ProtobufExp/Absyn/FieldType_int32.class ProtobufExp/Absyn/FieldType_int64.class ProtobufExp/Absyn/FieldType_string.class ProtobufExp/Absyn/FieldType_bool.class ProtobufExp/Absyn/FieldType_bytes.class ProtobufExp/Absyn/FieldTypeIdent.class ProtobufExp/Absyn/FieldList.class ProtobufExp/Absyn/OneFieldList.class ProtobufExp/Absyn/ConsFieldList.class
	 rm -f ProtobufExp/Absyn/*.class
	 rmdir ProtobufExp/Absyn/
	 rm -f ProtobufExp/Yylex ProtobufExp/ProtobufExp.cup ProtobufExp/Yylex.java ProtobufExp/VisitSkel.java ProtobufExp/ComposVisitor.java ProtobufExp/AbstractVisitor.java ProtobufExp/FoldVisitor.java ProtobufExp/AllVisitor.java ProtobufExp/PrettyPrinter.java ProtobufExp/Skeleton.java ProtobufExp/Test.java ProtobufExp/sym.java ProtobufExp/parser.java ProtobufExp/*.class
	rm -f Makefile
	rmdir -p ProtobufExp/
