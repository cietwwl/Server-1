package ProtobufExp.Absyn; // Java Package generated by the BNF Converter.

public class FieldType_int64 extends FieldType {
  public FieldType_int64() { }

  public <R,A> R accept(ProtobufExp.Absyn.FieldType.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof ProtobufExp.Absyn.FieldType_int64) {
      return true;
    }
    return false;
  }

  public int hashCode() {
    return 37;
  }


}
