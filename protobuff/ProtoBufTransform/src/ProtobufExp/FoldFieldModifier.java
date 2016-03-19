package ProtobufExp;

import ProtobufExp.Absyn.FieldModifier;
import ProtobufExp.Absyn.FieldModifier_optional;
import ProtobufExp.Absyn.FieldModifier_repeated;
import ProtobufExp.Absyn.FieldModifier_required;

public class FoldFieldModifier implements FieldModifier.Visitor<FieldModifierEnum, Object> {
	private static FoldFieldModifier instance = null;

	public static FoldFieldModifier Instance() {
		if (instance == null) {
			instance = new FoldFieldModifier();
		}
		return instance;
	}

	@Override
	public FieldModifierEnum visit(FieldModifier_required p, Object arg) {
		return FieldModifierEnum.Required;
	}

	@Override
	public FieldModifierEnum visit(FieldModifier_optional p, Object arg) {
		return FieldModifierEnum.Optional;
	}

	@Override
	public FieldModifierEnum visit(FieldModifier_repeated p, Object arg) {
		return FieldModifierEnum.Repeated;
	}

}
