package ProtobufExp;

import ProtobufExp.Absyn.FieldType;
import ProtobufExp.Absyn.FieldTypeIdent;
import ProtobufExp.Absyn.SimpleField;

public class FieldInfo {
	public static final String get_property_decl_postfix = "{get;}\n";
	public static final String get_property_prefix = "{get{return this.";

	public FieldInfo(FieldModifierEnum mod, SimpleField p) {
		modifier = mod;
		type = p.fieldtype_;
		name = p.ident_;
		seqId = p.integer_;
	}

	public FieldType type;
	public String name;
	public FieldModifierEnum modifier;
	public int seqId;

	public StringBuilder GenerateReadonlyProperty(String indent) {
		StringBuilder result=new StringBuilder();
		String nm = Rename(name);
		if (modifier == FieldModifierEnum.Repeated){
			result.append(indent);
			result.append("int ");
			result.append(nm);
			result.append("Count"
					+ get_property_decl_postfix);
			
			result.append(indent);
			result.append("IEnumerable<");
			result.append(type.accept(FoldToCSharpType.Instance(), this));
			result.append("> ");
			result.append(nm);
			result.append("List"
					+ get_property_decl_postfix);
			
		}else{
			result.append(indent);
			result.append(type.accept(FoldToCSharpType.Instance(), this));
			result.append(" ");
			if (type instanceof FieldTypeIdent){
				result.append(nm);
			}else{
				result.append(name);
			}
			result.append(get_property_decl_postfix);
		}
		return result;
	}
	
	public StringBuilder GenerateImpl(String indent) {
		StringBuilder result=new StringBuilder();
		String readonlyType = type.accept(FoldToCSharpType.Instance(), this);
		String nm = Rename(name);
		if (modifier == FieldModifierEnum.Repeated){
			result.append(indent);
			result.append("public ");
			result.append("int ");
			result.append(nm);
			result.append("Count");
			result.append(get_property_prefix);
			result.append(name);
			result.append(".Count");
			result.append(";}}\n");
			
			result.append(indent);
			result.append("public ");
			result.append("IEnumerable<");
			result.append(readonlyType);
			result.append("> ");
			result.append(nm);
			result.append("List");
			result.append(get_property_prefix);
			result.append(name);
			result.append(".ToReadonly<");
			result.append(type.accept(FoldTypeToString.Instance(), this));
			result.append(",");
			result.append(readonlyType);
			result.append(">()");
			result.append(";}}\n");
			
		}else{
			if (type instanceof FieldTypeIdent){
				result.append(indent);
				result.append("public ");
				result.append(readonlyType);
				result.append(" ");
				result.append(nm);
				result.append(get_property_prefix);
				result.append(name);
				result.append(";}}\n");
			}//else no need!
		}
		return result;
	}
	
	private String Rename(String nm) {
		if (nm!=null&&nm.length() > 0){
			char ch = nm.charAt(0);
			if (!Character.isUpperCase(ch)){
				char first = Character.toUpperCase(ch);
				String left = nm.substring(1);
				return first+left;
			}else{
				return nm+"_";
			}
		}
		return nm;
	}
}
