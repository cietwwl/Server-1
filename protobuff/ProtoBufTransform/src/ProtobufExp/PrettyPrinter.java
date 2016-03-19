package ProtobufExp;
import ProtobufExp.Absyn.*;

public class PrettyPrinter
{
  //For certain applications increasing the initial size of the buffer may improve performance.
  private static final int INITIAL_BUFFER_SIZE = 128;
  private static final int INDENT_WIDTH = 2;
  //You may wish to change the parentheses used in precedence.
  private static final String _L_PAREN = new String("(");
  private static final String _R_PAREN = new String(")");
  //You may wish to change render
  private static void render(String s)
  {
    if (s.equals("{"))
    {
       buf_.append("\n");
       indent();
       buf_.append(s);
       _n_ = _n_ + INDENT_WIDTH;
       buf_.append("\n");
       indent();
    }
    else if (s.equals("(") || s.equals("["))
       buf_.append(s);
    else if (s.equals(")") || s.equals("]"))
    {
       backup();
       buf_.append(s);
       buf_.append(" ");
    }
    else if (s.equals("}"))
    {
       int t;
       _n_ = _n_ - INDENT_WIDTH;
       for(t=0; t<INDENT_WIDTH; t++) {
         backup();
       }
       buf_.append(s);
       buf_.append("\n");
       indent();
    }
    else if (s.equals(","))
    {
       backup();
       buf_.append(s);
       buf_.append(" ");
    }
    else if (s.equals(";"))
    {
       backup();
       buf_.append(s);
       buf_.append("\n");
       indent();
    }
    else if (s.equals("")) return;
    else
    {
       buf_.append(s);
       buf_.append(" ");
    }
  }


  //  print and show methods are defined for each category.
  public static String print(ProtobufExp.Absyn.ExpList foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(ProtobufExp.Absyn.ExpList foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(ProtobufExp.Absyn.Exp foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(ProtobufExp.Absyn.Exp foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(ProtobufExp.Absyn.Field foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(ProtobufExp.Absyn.Field foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(ProtobufExp.Absyn.FieldModifier foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(ProtobufExp.Absyn.FieldModifier foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(ProtobufExp.Absyn.FieldType foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(ProtobufExp.Absyn.FieldType foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(ProtobufExp.Absyn.FieldList foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(ProtobufExp.Absyn.FieldList foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  /***   You shouldn't need to change anything beyond this point.   ***/

  private static void pp(ProtobufExp.Absyn.ExpList foo, int _i_)
  {
    if (foo instanceof ProtobufExp.Absyn.NilExpList)
    {
       ProtobufExp.Absyn.NilExpList _nilexplist = (ProtobufExp.Absyn.NilExpList) foo;
       if (_i_ > 0) render(_L_PAREN);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof ProtobufExp.Absyn.ConsExpList)
    {
       ProtobufExp.Absyn.ConsExpList _consexplist = (ProtobufExp.Absyn.ConsExpList) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_consexplist.exp_, 0);
       pp(_consexplist.explist_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(ProtobufExp.Absyn.Exp foo, int _i_)
  {
    if (foo instanceof ProtobufExp.Absyn.EMsg)
    {
       ProtobufExp.Absyn.EMsg _emsg = (ProtobufExp.Absyn.EMsg) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("message");
       pp(_emsg.ident_, 0);
       render("{");
       pp(_emsg.fieldlist_, 0);
       render("}");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(ProtobufExp.Absyn.Field foo, int _i_)
  {
    if (foo instanceof ProtobufExp.Absyn.SimpleField)
    {
       ProtobufExp.Absyn.SimpleField _simplefield = (ProtobufExp.Absyn.SimpleField) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_simplefield.fieldmodifier_, 0);
       pp(_simplefield.fieldtype_, 0);
       pp(_simplefield.ident_, 0);
       render("=");
       pp(_simplefield.integer_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(ProtobufExp.Absyn.FieldModifier foo, int _i_)
  {
    if (foo instanceof ProtobufExp.Absyn.FieldModifier_required)
    {
       ProtobufExp.Absyn.FieldModifier_required _fieldmodifier_required = (ProtobufExp.Absyn.FieldModifier_required) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("required");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof ProtobufExp.Absyn.FieldModifier_optional)
    {
       ProtobufExp.Absyn.FieldModifier_optional _fieldmodifier_optional = (ProtobufExp.Absyn.FieldModifier_optional) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("optional");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof ProtobufExp.Absyn.FieldModifier_repeated)
    {
       ProtobufExp.Absyn.FieldModifier_repeated _fieldmodifier_repeated = (ProtobufExp.Absyn.FieldModifier_repeated) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("repeated");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(ProtobufExp.Absyn.FieldType foo, int _i_)
  {
    if (foo instanceof ProtobufExp.Absyn.FieldType_int32)
    {
       ProtobufExp.Absyn.FieldType_int32 _fieldtype_int32 = (ProtobufExp.Absyn.FieldType_int32) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("int32");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof ProtobufExp.Absyn.FieldType_int64)
    {
       ProtobufExp.Absyn.FieldType_int64 _fieldtype_int64 = (ProtobufExp.Absyn.FieldType_int64) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("int64");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof ProtobufExp.Absyn.FieldType_float)
    {
       ProtobufExp.Absyn.FieldType_float _fieldtype_float = (ProtobufExp.Absyn.FieldType_float) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("float");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof ProtobufExp.Absyn.FieldType_double)
    {
       ProtobufExp.Absyn.FieldType_double _fieldtype_double = (ProtobufExp.Absyn.FieldType_double) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("double");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof ProtobufExp.Absyn.FieldType_string)
    {
       ProtobufExp.Absyn.FieldType_string _fieldtype_string = (ProtobufExp.Absyn.FieldType_string) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("string");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof ProtobufExp.Absyn.FieldType_bool)
    {
       ProtobufExp.Absyn.FieldType_bool _fieldtype_bool = (ProtobufExp.Absyn.FieldType_bool) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("bool");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof ProtobufExp.Absyn.FieldType_bytes)
    {
       ProtobufExp.Absyn.FieldType_bytes _fieldtype_bytes = (ProtobufExp.Absyn.FieldType_bytes) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("bytes");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof ProtobufExp.Absyn.FieldTypeIdent)
    {
       ProtobufExp.Absyn.FieldTypeIdent _fieldtypeident = (ProtobufExp.Absyn.FieldTypeIdent) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_fieldtypeident.ident_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(ProtobufExp.Absyn.FieldList foo, int _i_)
  {
    if (foo instanceof ProtobufExp.Absyn.OneFieldList)
    {
       ProtobufExp.Absyn.OneFieldList _onefieldlist = (ProtobufExp.Absyn.OneFieldList) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_onefieldlist.field_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof ProtobufExp.Absyn.ConsFieldList)
    {
       ProtobufExp.Absyn.ConsFieldList _consfieldlist = (ProtobufExp.Absyn.ConsFieldList) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_consfieldlist.field_, 0);
       pp(_consfieldlist.fieldlist_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }


  private static void sh(ProtobufExp.Absyn.ExpList foo)
  {
    if (foo instanceof ProtobufExp.Absyn.NilExpList)
    {
       ProtobufExp.Absyn.NilExpList _nilexplist = (ProtobufExp.Absyn.NilExpList) foo;
       render("NilExpList");
    }
    if (foo instanceof ProtobufExp.Absyn.ConsExpList)
    {
       ProtobufExp.Absyn.ConsExpList _consexplist = (ProtobufExp.Absyn.ConsExpList) foo;
       render("(");
       render("ConsExpList");
       sh(_consexplist.exp_);
       sh(_consexplist.explist_);
       render(")");
    }
  }

  private static void sh(ProtobufExp.Absyn.Exp foo)
  {
    if (foo instanceof ProtobufExp.Absyn.EMsg)
    {
       ProtobufExp.Absyn.EMsg _emsg = (ProtobufExp.Absyn.EMsg) foo;
       render("(");
       render("EMsg");
       sh(_emsg.ident_);
       sh(_emsg.fieldlist_);
       render(")");
    }
  }

  private static void sh(ProtobufExp.Absyn.Field foo)
  {
    if (foo instanceof ProtobufExp.Absyn.SimpleField)
    {
       ProtobufExp.Absyn.SimpleField _simplefield = (ProtobufExp.Absyn.SimpleField) foo;
       render("(");
       render("SimpleField");
       sh(_simplefield.fieldmodifier_);
       sh(_simplefield.fieldtype_);
       sh(_simplefield.ident_);
       sh(_simplefield.integer_);
       render(")");
    }
  }

  private static void sh(ProtobufExp.Absyn.FieldModifier foo)
  {
    if (foo instanceof ProtobufExp.Absyn.FieldModifier_required)
    {
       ProtobufExp.Absyn.FieldModifier_required _fieldmodifier_required = (ProtobufExp.Absyn.FieldModifier_required) foo;
       render("FieldModifier_required");
    }
    if (foo instanceof ProtobufExp.Absyn.FieldModifier_optional)
    {
       ProtobufExp.Absyn.FieldModifier_optional _fieldmodifier_optional = (ProtobufExp.Absyn.FieldModifier_optional) foo;
       render("FieldModifier_optional");
    }
    if (foo instanceof ProtobufExp.Absyn.FieldModifier_repeated)
    {
       ProtobufExp.Absyn.FieldModifier_repeated _fieldmodifier_repeated = (ProtobufExp.Absyn.FieldModifier_repeated) foo;
       render("FieldModifier_repeated");
    }
  }

  private static void sh(ProtobufExp.Absyn.FieldType foo)
  {
    if (foo instanceof ProtobufExp.Absyn.FieldType_int32)
    {
       ProtobufExp.Absyn.FieldType_int32 _fieldtype_int32 = (ProtobufExp.Absyn.FieldType_int32) foo;
       render("FieldType_int32");
    }
    if (foo instanceof ProtobufExp.Absyn.FieldType_int64)
    {
       ProtobufExp.Absyn.FieldType_int64 _fieldtype_int64 = (ProtobufExp.Absyn.FieldType_int64) foo;
       render("FieldType_int64");
    }
    if (foo instanceof ProtobufExp.Absyn.FieldType_float)
    {
       ProtobufExp.Absyn.FieldType_float _fieldtype_float = (ProtobufExp.Absyn.FieldType_float) foo;
       render("FieldType_float");
    }
    if (foo instanceof ProtobufExp.Absyn.FieldType_double)
    {
       ProtobufExp.Absyn.FieldType_double _fieldtype_double = (ProtobufExp.Absyn.FieldType_double) foo;
       render("FieldType_double");
    }
    if (foo instanceof ProtobufExp.Absyn.FieldType_string)
    {
       ProtobufExp.Absyn.FieldType_string _fieldtype_string = (ProtobufExp.Absyn.FieldType_string) foo;
       render("FieldType_string");
    }
    if (foo instanceof ProtobufExp.Absyn.FieldType_bool)
    {
       ProtobufExp.Absyn.FieldType_bool _fieldtype_bool = (ProtobufExp.Absyn.FieldType_bool) foo;
       render("FieldType_bool");
    }
    if (foo instanceof ProtobufExp.Absyn.FieldType_bytes)
    {
       ProtobufExp.Absyn.FieldType_bytes _fieldtype_bytes = (ProtobufExp.Absyn.FieldType_bytes) foo;
       render("FieldType_bytes");
    }
    if (foo instanceof ProtobufExp.Absyn.FieldTypeIdent)
    {
       ProtobufExp.Absyn.FieldTypeIdent _fieldtypeident = (ProtobufExp.Absyn.FieldTypeIdent) foo;
       render("(");
       render("FieldTypeIdent");
       sh(_fieldtypeident.ident_);
       render(")");
    }
  }

  private static void sh(ProtobufExp.Absyn.FieldList foo)
  {
    if (foo instanceof ProtobufExp.Absyn.OneFieldList)
    {
       ProtobufExp.Absyn.OneFieldList _onefieldlist = (ProtobufExp.Absyn.OneFieldList) foo;
       render("(");
       render("OneFieldList");
       sh(_onefieldlist.field_);
       render(")");
    }
    if (foo instanceof ProtobufExp.Absyn.ConsFieldList)
    {
       ProtobufExp.Absyn.ConsFieldList _consfieldlist = (ProtobufExp.Absyn.ConsFieldList) foo;
       render("(");
       render("ConsFieldList");
       sh(_consfieldlist.field_);
       sh(_consfieldlist.fieldlist_);
       render(")");
    }
  }


  private static void pp(Integer n, int _i_) { buf_.append(n); buf_.append(" "); }
  private static void pp(Double d, int _i_) { buf_.append(d); buf_.append(" "); }
  private static void pp(String s, int _i_) { buf_.append(s); buf_.append(" "); }
  private static void pp(Character c, int _i_) { buf_.append("'" + c.toString() + "'"); buf_.append(" "); }
  private static void sh(Integer n) { render(n.toString()); }
  private static void sh(Double d) { render(d.toString()); }
  private static void sh(Character c) { render(c.toString()); }
  private static void sh(String s) { printQuoted(s); }
  private static void printQuoted(String s) { render("\"" + s + "\""); }
  private static void indent()
  {
    int n = _n_;
    while (n > 0)
    {
      buf_.append(" ");
      n--;
    }
  }
  private static void backup()
  {
     if (buf_.charAt(buf_.length() - 1) == ' ') {
      buf_.setLength(buf_.length() - 1);
    }
  }
  private static void trim()
  {
     while (buf_.length() > 0 && buf_.charAt(0) == ' ')
        buf_.deleteCharAt(0); 
    while (buf_.length() > 0 && buf_.charAt(buf_.length()-1) == ' ')
        buf_.deleteCharAt(buf_.length()-1);
  }
  private static int _n_ = 0;
  private static StringBuilder buf_ = new StringBuilder(INITIAL_BUFFER_SIZE);
}

