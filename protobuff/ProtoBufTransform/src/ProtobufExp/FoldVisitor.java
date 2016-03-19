package ProtobufExp;

import ProtobufExp.Absyn.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/** BNFC-Generated Fold Visitor */
public abstract class FoldVisitor<R,A> implements AllVisitor<R,A> {
    public abstract R leaf(A arg);
    public abstract R combine(R x, R y, A arg);

/* ExpList */
    public R visit(ProtobufExp.Absyn.NilExpList p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(ProtobufExp.Absyn.ConsExpList p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      r = combine(p.explist_.accept(this, arg), r, arg);
      return r;
    }

/* Exp */
    public R visit(ProtobufExp.Absyn.EMsg p, A arg) {
      R r = leaf(arg);
      r = combine(p.fieldlist_.accept(this, arg), r, arg);
      return r;
    }

/* Field */
    public R visit(ProtobufExp.Absyn.SimpleField p, A arg) {
      R r = leaf(arg);
      r = combine(p.fieldmodifier_.accept(this, arg), r, arg);
      r = combine(p.fieldtype_.accept(this, arg), r, arg);
      return r;
    }

/* FieldModifier */
    public R visit(ProtobufExp.Absyn.FieldModifier_required p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(ProtobufExp.Absyn.FieldModifier_optional p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(ProtobufExp.Absyn.FieldModifier_repeated p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* FieldType */
    public R visit(ProtobufExp.Absyn.FieldType_int32 p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(ProtobufExp.Absyn.FieldType_int64 p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(ProtobufExp.Absyn.FieldType_float p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(ProtobufExp.Absyn.FieldType_double p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(ProtobufExp.Absyn.FieldType_string p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(ProtobufExp.Absyn.FieldType_bool p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(ProtobufExp.Absyn.FieldType_bytes p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(ProtobufExp.Absyn.FieldTypeIdent p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* FieldList */
    public R visit(ProtobufExp.Absyn.OneFieldList p, A arg) {
      R r = leaf(arg);
      r = combine(p.field_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(ProtobufExp.Absyn.ConsFieldList p, A arg) {
      R r = leaf(arg);
      r = combine(p.field_.accept(this, arg), r, arg);
      r = combine(p.fieldlist_.accept(this, arg), r, arg);
      return r;
    }


}
