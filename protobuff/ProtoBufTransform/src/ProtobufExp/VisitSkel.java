package ProtobufExp;
import ProtobufExp.Absyn.*;
/*** BNFC-Generated Visitor Design Pattern Skeleton. ***/
/* This implements the common visitor design pattern.
   Tests show it to be slightly less efficient than the
   instanceof method, but easier to use. 
   Replace the R and A parameters with the desired return
   and context types.*/

public class VisitSkel
{
 static public class ExpListVisitor<R,A> implements ExpList.Visitor<R,A>
  {
    public R visit(ProtobufExp.Absyn.NilExpList p, A arg)
    { /* Code For NilExpList Goes Here */
      return null;
    }
    public R visit(ProtobufExp.Absyn.ConsExpList p, A arg)
    { /* Code For ConsExpList Goes Here */
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      p.explist_.accept(this, arg);
      return null;
    }

  }
 static public class ExpVisitor<R,A> implements Exp.Visitor<R,A>
  {
    public R visit(ProtobufExp.Absyn.EMsg p, A arg)
    { /* Code For EMsg Goes Here */
      //p.ident_;
      p.fieldlist_.accept(new FieldListVisitor<R,A>(), arg);
      return null;
    }

  }
 static public class FieldVisitor<R,A> implements Field.Visitor<R,A>
  {
    public R visit(ProtobufExp.Absyn.SimpleField p, A arg)
    { /* Code For SimpleField Goes Here */
      p.fieldmodifier_.accept(new FieldModifierVisitor<R,A>(), arg);
      p.fieldtype_.accept(new FieldTypeVisitor<R,A>(), arg);
      //p.ident_;
      //p.integer_;
      return null;
    }

  }
 static public class FieldModifierVisitor<R,A> implements FieldModifier.Visitor<R,A>
  {
    public R visit(ProtobufExp.Absyn.FieldModifier_required p, A arg)
    { /* Code For FieldModifier_required Goes Here */
      return null;
    }
    public R visit(ProtobufExp.Absyn.FieldModifier_optional p, A arg)
    { /* Code For FieldModifier_optional Goes Here */
      return null;
    }
    public R visit(ProtobufExp.Absyn.FieldModifier_repeated p, A arg)
    { /* Code For FieldModifier_repeated Goes Here */
      return null;
    }

  }
 static public class FieldTypeVisitor<R,A> implements FieldType.Visitor<R,A>
  {
    public R visit(ProtobufExp.Absyn.FieldType_int32 p, A arg)
    { /* Code For FieldType_int32 Goes Here */
      return null;
    }
    public R visit(ProtobufExp.Absyn.FieldType_int64 p, A arg)
    { /* Code For FieldType_int64 Goes Here */
      return null;
    }
    public R visit(ProtobufExp.Absyn.FieldType_float p, A arg)
    { /* Code For FieldType_float Goes Here */
      return null;
    }
    public R visit(ProtobufExp.Absyn.FieldType_double p, A arg)
    { /* Code For FieldType_double Goes Here */
      return null;
    }
    public R visit(ProtobufExp.Absyn.FieldType_string p, A arg)
    { /* Code For FieldType_string Goes Here */
      return null;
    }
    public R visit(ProtobufExp.Absyn.FieldType_bool p, A arg)
    { /* Code For FieldType_bool Goes Here */
      return null;
    }
    public R visit(ProtobufExp.Absyn.FieldType_bytes p, A arg)
    { /* Code For FieldType_bytes Goes Here */
      return null;
    }
    public R visit(ProtobufExp.Absyn.FieldTypeIdent p, A arg)
    { /* Code For FieldTypeIdent Goes Here */
      //p.ident_;
      return null;
    }

  }
 static public class FieldListVisitor<R,A> implements FieldList.Visitor<R,A>
  {
    public R visit(ProtobufExp.Absyn.OneFieldList p, A arg)
    { /* Code For OneFieldList Goes Here */
      p.field_.accept(new FieldVisitor<R,A>(), arg);
      return null;
    }
    public R visit(ProtobufExp.Absyn.ConsFieldList p, A arg)
    { /* Code For ConsFieldList Goes Here */
      p.field_.accept(new FieldVisitor<R,A>(), arg);
      p.fieldlist_.accept(new FieldListVisitor<R,A>(), arg);
      return null;
    }

  }
}