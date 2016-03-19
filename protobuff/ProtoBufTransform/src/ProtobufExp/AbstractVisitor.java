package ProtobufExp;
import ProtobufExp.Absyn.*;
/** BNFC-Generated Abstract Visitor */
public class AbstractVisitor<R,A> implements AllVisitor<R,A> {
/* ExpList */
    public R visit(ProtobufExp.Absyn.NilExpList p, A arg) { return visitDefault(p, arg); }
    public R visit(ProtobufExp.Absyn.ConsExpList p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(ProtobufExp.Absyn.ExpList p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* Exp */
    public R visit(ProtobufExp.Absyn.EMsg p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(ProtobufExp.Absyn.Exp p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* Field */
    public R visit(ProtobufExp.Absyn.SimpleField p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(ProtobufExp.Absyn.Field p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* FieldModifier */
    public R visit(ProtobufExp.Absyn.FieldModifier_required p, A arg) { return visitDefault(p, arg); }
    public R visit(ProtobufExp.Absyn.FieldModifier_optional p, A arg) { return visitDefault(p, arg); }
    public R visit(ProtobufExp.Absyn.FieldModifier_repeated p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(ProtobufExp.Absyn.FieldModifier p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* FieldType */
    public R visit(ProtobufExp.Absyn.FieldType_int32 p, A arg) { return visitDefault(p, arg); }
    public R visit(ProtobufExp.Absyn.FieldType_int64 p, A arg) { return visitDefault(p, arg); }
    public R visit(ProtobufExp.Absyn.FieldType_float p, A arg) { return visitDefault(p, arg); }
    public R visit(ProtobufExp.Absyn.FieldType_double p, A arg) { return visitDefault(p, arg); }
    public R visit(ProtobufExp.Absyn.FieldType_string p, A arg) { return visitDefault(p, arg); }
    public R visit(ProtobufExp.Absyn.FieldType_bool p, A arg) { return visitDefault(p, arg); }
    public R visit(ProtobufExp.Absyn.FieldType_bytes p, A arg) { return visitDefault(p, arg); }
    public R visit(ProtobufExp.Absyn.FieldTypeIdent p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(ProtobufExp.Absyn.FieldType p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* FieldList */
    public R visit(ProtobufExp.Absyn.OneFieldList p, A arg) { return visitDefault(p, arg); }
    public R visit(ProtobufExp.Absyn.ConsFieldList p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(ProtobufExp.Absyn.FieldList p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }

}
