package ProtobufExp;

import java.util.LinkedList;

import ProtobufExp.Absyn.ConsFieldList;
import ProtobufExp.Absyn.FieldList;
import ProtobufExp.Absyn.OneFieldList;

public class FoldFieldList implements FieldList.Visitor<LinkedList<FieldInfo>, Object> {
	private static FoldFieldList instance = null;

	public static FoldFieldList Instance() {
		if (instance == null) {
			instance = new FoldFieldList();
		}
		return instance;
	}

	@Override
	public LinkedList<FieldInfo> visit(OneFieldList p, Object arg) {
		FieldInfo f = p.field_.accept(FoldField.Instance(), arg);
		LinkedList<FieldInfo> result = new LinkedList<>();
		result.add(f);
		return result;
	}

	@Override
	public LinkedList<FieldInfo> visit(ConsFieldList p, Object arg) {
		FieldInfo f = p.field_.accept(FoldField.Instance(), arg);
		LinkedList<FieldInfo> result = p.fieldlist_.accept(this, arg);
		result.addFirst(f);
		return result;
	}

}
