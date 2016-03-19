package ProtobufExp;

import ProtobufExp.Absyn.FieldTypeIdent;

public class FoldTypeToString extends FoldToCSharpType {
	private static FoldTypeToString instance = null;

	public static FoldTypeToString Instance() {
		if (instance == null) {
			instance = new FoldTypeToString();
		}
		return instance;
	}

	@Override
	public String visit(FieldTypeIdent p, Object arg) {
		return p.ident_;
	}

}
