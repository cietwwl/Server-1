package ProtobufExp.Absyn; // Java Package generated by the BNF Converter.

public abstract class SComment implements java.io.Serializable {
  public abstract <R,A> R accept(SComment.Visitor<R,A> v, A arg);
  public interface Visitor <R,A> {
    public R visit(ProtobufExp.Absyn.EmptyCmt p, A arg);
    public R visit(ProtobufExp.Absyn.OneLineCmt p, A arg);

  }

}
