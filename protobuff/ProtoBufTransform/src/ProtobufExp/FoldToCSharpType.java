package ProtobufExp;

import ProtobufExp.Absyn.*;

public class FoldToCSharpType implements FieldType.Visitor<String, Object> {
	private static FoldToCSharpType instance = null;

	public static FoldToCSharpType Instance() {
		if (instance == null) {
			instance = new FoldToCSharpType();
		}
		return instance;
	}
	
	@Override
	public String visit(FieldType_int32 p, Object arg) {
		return "int";
	}

	@Override
	public String visit(FieldType_int64 p, Object arg) {
		return "long";
	}

	@Override
	public String visit(FieldType_float p, Object arg) {
		return "float";
	}

	@Override
	public String visit(FieldType_double p, Object arg) {
		return "double";
	}

	@Override
	public String visit(FieldType_string p, Object arg) {
		return "string";
	}

	@Override
	public String visit(FieldType_bool p, Object arg) {
		return "bool";
	}

	@Override
	public String visit(FieldType_bytes p, Object arg) {
		return "byte[]";
	}

	@Override
	public String visit(FieldTypeIdent p, Object arg) {
		return "I" + p.ident_;
	}

}
