package ProtobufExp;

//Object Algebra Signature
public interface ProtobufOA<ExpList,Exp,FieldType,FieldList,Field,FieldModifier> {
	ExpList NilExpList();
	ExpList ConsExpList(Exp head,ExpList tail);
	Exp EMsg(String ident,FieldList flst);
	Field SimpleField(FieldModifier modifier,FieldType ty,String ident,Integer seqId);
	FieldModifier FieldModifier_required();
	FieldModifier FieldModifier_optional();
	FieldModifier FieldModifier_repeated();
	FieldType FieldType_int32();
	FieldType FieldType_int64();
	FieldType FieldType_float();
	FieldType FieldType_double();
	FieldType FieldType_string();
	FieldType FieldType_bool();
	FieldType FieldType_bytes();
	FieldType FieldTypeIdent(String ident);
	FieldList OneFieldList(Field field_);
	FieldList ConsFieldList(Field field_, FieldList fieldlist_);
}
