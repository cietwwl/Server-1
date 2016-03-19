package ProtobufExp;
import ProtobufExp.Absyn.*;
/** BNFC-Generated Composition Visitor
*/

public class ComposVisitor<A> implements
  ProtobufExp.Absyn.ExpList.Visitor<ProtobufExp.Absyn.ExpList,A>,
  ProtobufExp.Absyn.Exp.Visitor<ProtobufExp.Absyn.Exp,A>,
  ProtobufExp.Absyn.Field.Visitor<ProtobufExp.Absyn.Field,A>,
  ProtobufExp.Absyn.FieldModifier.Visitor<ProtobufExp.Absyn.FieldModifier,A>,
  ProtobufExp.Absyn.FieldType.Visitor<ProtobufExp.Absyn.FieldType,A>,
  ProtobufExp.Absyn.FieldList.Visitor<ProtobufExp.Absyn.FieldList,A>
{
/* ExpList */

 public ExpList visit(ProtobufExp.Absyn.NilExpList p, A arg)
  {
    return new ProtobufExp.Absyn.NilExpList();
  }
 public ExpList visit(ProtobufExp.Absyn.ConsExpList p, A arg)
  {
    Exp exp_ = p.exp_.accept(this, arg);
    ExpList explist_ = p.explist_.accept(this, arg);
    return new ProtobufExp.Absyn.ConsExpList(exp_, explist_);
  }
/* Exp */

 public Exp visit(ProtobufExp.Absyn.EMsg p, A arg)
  {
    String ident_ = p.ident_;
    FieldList fieldlist_ = p.fieldlist_.accept(this, arg);
    return new ProtobufExp.Absyn.EMsg(ident_, fieldlist_);
  }
/* Field */

 public Field visit(ProtobufExp.Absyn.SimpleField p, A arg)
  {
    FieldModifier fieldmodifier_ = p.fieldmodifier_.accept(this, arg);
    FieldType fieldtype_ = p.fieldtype_.accept(this, arg);
    String ident_ = p.ident_;
    Integer integer_ = p.integer_;
    return new ProtobufExp.Absyn.SimpleField(fieldmodifier_, fieldtype_, ident_, integer_);
  }
/* FieldModifier */

 public FieldModifier visit(ProtobufExp.Absyn.FieldModifier_required p, A arg)
  {
    return new ProtobufExp.Absyn.FieldModifier_required();
  }
 public FieldModifier visit(ProtobufExp.Absyn.FieldModifier_optional p, A arg)
  {
    return new ProtobufExp.Absyn.FieldModifier_optional();
  }
 public FieldModifier visit(ProtobufExp.Absyn.FieldModifier_repeated p, A arg)
  {
    return new ProtobufExp.Absyn.FieldModifier_repeated();
  }
/* FieldType */

 public FieldType visit(ProtobufExp.Absyn.FieldType_int32 p, A arg)
  {
    return new ProtobufExp.Absyn.FieldType_int32();
  }
 public FieldType visit(ProtobufExp.Absyn.FieldType_int64 p, A arg)
  {
    return new ProtobufExp.Absyn.FieldType_int64();
  }
 public FieldType visit(ProtobufExp.Absyn.FieldType_float p, A arg)
  {
    return new ProtobufExp.Absyn.FieldType_float();
  }
 public FieldType visit(ProtobufExp.Absyn.FieldType_double p, A arg)
  {
    return new ProtobufExp.Absyn.FieldType_double();
  }
 public FieldType visit(ProtobufExp.Absyn.FieldType_string p, A arg)
  {
    return new ProtobufExp.Absyn.FieldType_string();
  }
 public FieldType visit(ProtobufExp.Absyn.FieldType_bool p, A arg)
  {
    return new ProtobufExp.Absyn.FieldType_bool();
  }
 public FieldType visit(ProtobufExp.Absyn.FieldType_bytes p, A arg)
  {
    return new ProtobufExp.Absyn.FieldType_bytes();
  }
 public FieldType visit(ProtobufExp.Absyn.FieldTypeIdent p, A arg)
  {
    String ident_ = p.ident_;
    return new ProtobufExp.Absyn.FieldTypeIdent(ident_);
  }
/* FieldList */

 public FieldList visit(ProtobufExp.Absyn.OneFieldList p, A arg)
  {
    Field field_ = p.field_.accept(this, arg);
    return new ProtobufExp.Absyn.OneFieldList(field_);
  }
 public FieldList visit(ProtobufExp.Absyn.ConsFieldList p, A arg)
  {
    Field field_ = p.field_.accept(this, arg);
    FieldList fieldlist_ = p.fieldlist_.accept(this, arg);
    return new ProtobufExp.Absyn.ConsFieldList(field_, fieldlist_);
  }
}