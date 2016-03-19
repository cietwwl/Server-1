package ProtobufExp;

import java.util.LinkedList;

import ProtobufExp.Absyn.ConsExpList;
import ProtobufExp.Absyn.ExpList;
import ProtobufExp.Absyn.NilExpList;

public class FoldExpList implements ExpList.Visitor<LinkedList<ReadonlyInterfaceInfo>, Object> {
	private static FoldExpList instance = null;

	public static FoldExpList Instance() {
		if (instance == null) {
			instance = new FoldExpList();
		}
		return instance;
	}

	@Override
	public LinkedList<ReadonlyInterfaceInfo> visit(NilExpList p, Object arg) {
		return null;
	}

	@Override
	public LinkedList<ReadonlyInterfaceInfo> visit(ConsExpList p, Object arg) {
		ReadonlyInterfaceInfo exp = p.exp_.accept(FoldExp.Instance(), arg);
		LinkedList<ReadonlyInterfaceInfo> cons = p.explist_.accept(this, arg);
		if (cons == null) cons = new LinkedList<>();
		cons.addFirst(exp);
		return cons;
	}

}
