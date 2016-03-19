package ProtobufExp;

import java.util.LinkedList;

import ProtobufExp.Absyn.EMsg;
import ProtobufExp.Absyn.Exp;

public class FoldExp implements Exp.Visitor<ReadonlyInterfaceInfo, Object> {
	private static FoldExp instance = null;

	public static FoldExp Instance() {
		if (instance == null) {
			instance = new FoldExp();
		}
		return instance;
	}

	@Override
	public ReadonlyInterfaceInfo visit(EMsg p, Object arg) {
	     LinkedList<FieldInfo> flst = p.fieldlist_.accept(FoldFieldList.Instance(), arg);
		return new ReadonlyInterfaceInfo(flst,p);
	}

}
