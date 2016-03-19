package ProtobufExp.Absyn; // Java Package generated by the BNF Converter.

public class FieldTypeIdent extends FieldType {
  public final String ident_;
  public FieldTypeIdent(String p1) { ident_ = p1; }

  public <R,A> R accept(ProtobufExp.Absyn.FieldType.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof ProtobufExp.Absyn.FieldTypeIdent) {
      ProtobufExp.Absyn.FieldTypeIdent x = (ProtobufExp.Absyn.FieldTypeIdent)o;
      return this.ident_.equals(x.ident_);
    }
    return false;
  }

  public int hashCode() {
    return this.ident_.hashCode();
  }


}
