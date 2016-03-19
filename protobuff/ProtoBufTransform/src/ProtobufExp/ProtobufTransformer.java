package ProtobufExp;

import ProtobufExp.Absyn.*;

public class ProtobufTransformer<A extends ProtobufOA<TExpList, TExp, TFieldType, TFieldList, TField, TFieldModifier>, TExpList, TExp, TFieldType, TFieldList, TField, TFieldModifier>
		implements ProtobufExp.Absyn.ExpList.Visitor<TExpList, A>, ProtobufExp.Absyn.Exp.Visitor<TExp, A>,
		ProtobufExp.Absyn.Field.Visitor<TField, A>, ProtobufExp.Absyn.FieldModifier.Visitor<TFieldModifier, A>,
		ProtobufExp.Absyn.FieldType.Visitor<TFieldType, A>, ProtobufExp.Absyn.FieldList.Visitor<TFieldList, A> {

	@Override
	public TFieldList visit(OneFieldList p, A arg) {
		return arg.OneFieldList(p.field_.accept(this, arg));
	}

	@Override
	public TFieldList visit(ConsFieldList p, A arg) {
		return arg.ConsFieldList(p.field_.accept(this, arg), p.fieldlist_.accept(this, arg));
	}

	@Override
	public TFieldType visit(FieldType_int32 p, A arg) {
		return arg.FieldType_int32();
	}

	@Override
	public TFieldType visit(FieldType_int64 p, A arg) {
		return arg.FieldType_int64();
	}

	@Override
	public TFieldType visit(FieldType_float p, A arg) {
		return arg.FieldType_float();
	}

	@Override
	public TFieldType visit(FieldType_double p, A arg) {
		return arg.FieldType_double();
	}

	@Override
	public TFieldType visit(FieldType_string p, A arg) {
		return arg.FieldType_string();
	}

	@Override
	public TFieldType visit(FieldType_bool p, A arg) {
		return arg.FieldType_bool();
	}

	@Override
	public TFieldType visit(FieldType_bytes p, A arg) {
		return arg.FieldType_bytes();
	}

	@Override
	public TFieldType visit(FieldTypeIdent p, A arg) {
		return arg.FieldTypeIdent(p.ident_);
	}

	@Override
	public TFieldModifier visit(FieldModifier_required p, A arg) {
		return arg.FieldModifier_required();
	}

	@Override
	public TFieldModifier visit(FieldModifier_optional p, A arg) {
		return arg.FieldModifier_optional();
	}

	@Override
	public TFieldModifier visit(FieldModifier_repeated p, A arg) {
		return arg.FieldModifier_repeated();
	}

	@Override
	public TField visit(SimpleField p, A arg) {
		return arg.SimpleField(p.fieldmodifier_.accept(this, arg), p.fieldtype_.accept(this, arg), p.ident_, p.integer_);
	}

	@Override
	public TExp visit(EMsg p, A arg) {
		return arg.EMsg(p.ident_, p.fieldlist_.accept(this, arg));
	}

	@Override
	public TExpList visit(NilExpList p, A arg) {
		return arg.NilExpList();
	}

	@Override
	public TExpList visit(ConsExpList p, A arg) {
		return arg.ConsExpList(p.exp_.accept(this, arg), p.explist_.accept(this, arg));
	}

}
