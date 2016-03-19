package ProtobufExp;

import ProtobufExp.Absyn.Field;
import ProtobufExp.Absyn.SimpleField;

public class FoldField implements Field.Visitor<FieldInfo, Object> {
	private static FoldField instance = null;

	public static FoldField Instance() {
		if (instance == null) {
			instance = new FoldField();
		}
		return instance;
	}

	@Override
	public FieldInfo visit(SimpleField p, Object arg) {
		FieldModifierEnum modifier = p.fieldmodifier_.accept(FoldFieldModifier.Instance(), arg);
		return new FieldInfo(modifier,p);
	}

}
