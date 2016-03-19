package ProtobufExp;

import java.util.LinkedList;

import ProtobufExp.Absyn.ConsExpList;
import ProtobufExp.Absyn.ExpList;
import ProtobufExp.Absyn.NilExpList;

public class FoldExpCollection implements ExpList.Visitor<ReadonlyInterfaceInfoCollection, String> {
	private static FoldExpCollection instance = null;

	public static FoldExpCollection Instance() {
		if (instance == null) {
			instance = new FoldExpCollection();
		}
		return instance;
	}

	@Override
	public ReadonlyInterfaceInfoCollection visit(NilExpList p, String protoNm) {
		return new ReadonlyInterfaceInfoCollection(p, protoNm);
	}

	@Override
	public ReadonlyInterfaceInfoCollection visit(ConsExpList p, String protoNm) {
		LinkedList<ReadonlyInterfaceInfo> cons = p.accept(FoldExpList.Instance(), protoNm);
		return new ReadonlyInterfaceInfoCollection(cons,protoNm);
	}

}
