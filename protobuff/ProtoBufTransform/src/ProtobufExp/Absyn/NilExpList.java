package ProtobufExp.Absyn; // Java Package generated by the BNF Converter.

public class NilExpList extends ExpList {
  public NilExpList() { }

  public <R,A> R accept(ProtobufExp.Absyn.ExpList.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof ProtobufExp.Absyn.NilExpList) {
      return true;
    }
    return false;
  }

  public int hashCode() {
    return 37;
  }


}
