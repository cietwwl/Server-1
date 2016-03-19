package ProtobufExp;

import ProtobufExp.Absyn.*;

/** BNFC-Generated All Visitor */
public interface AllVisitor<R,A> extends
  ProtobufExp.Absyn.ExpList.Visitor<R,A>,
  ProtobufExp.Absyn.Exp.Visitor<R,A>,
  ProtobufExp.Absyn.Field.Visitor<R,A>,
  ProtobufExp.Absyn.FieldModifier.Visitor<R,A>,
  ProtobufExp.Absyn.FieldType.Visitor<R,A>,
  ProtobufExp.Absyn.FieldList.Visitor<R,A>
{}
