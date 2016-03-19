package ProtobufExp;

import java.util.LinkedList;
import java.util.function.Consumer;

import ProtobufExp.Absyn.EMsg;


public class ReadonlyInterfaceInfo {
	private static final String csharp_interface_decl = "public partial interface ";
	private static final String readonly_prefix="I";
	private static final String csharp_readonly_interface_decl_prefix = csharp_interface_decl+readonly_prefix;
	private static final Object csharp_partial_class_decl = "public partial class ";
	//raw data
	public String implName;
	public LinkedList<FieldInfo> fieldInfos;
	
	public ReadonlyInterfaceInfo(LinkedList<FieldInfo> flst, EMsg arg) {
		fieldInfos=flst;
		implName=arg.ident_;
	}

	public StringBuilder GenerateReadonlyInterface(String indent){
		StringBuilder result=new StringBuilder();
		if(fieldInfos == null) return result;
		result.append(indent);
		result.append(csharp_readonly_interface_decl_prefix);
		result.append(implName);
		result.append("{\n");
		String fIndent = indent+ReadonlyInterfaceInfoCollection.indent;
		fieldInfos.forEach(new Consumer<FieldInfo>() {
			@Override
			public void accept(FieldInfo finfo) {
				result.append(finfo.GenerateReadonlyProperty(fIndent));
			}
		});
		result.append(indent);
		result.append("}\n");
		return result;
	}
	
	public StringBuilder GenerateInstance(String indent){
		StringBuilder result=new StringBuilder();
		if(fieldInfos == null) return result;
		result.append(indent);
		result.append(csharp_partial_class_decl);
		result.append(implName);
		result.append(" : ");
		result.append(readonly_prefix);
		result.append(implName);
		
		result.append("{\n");
		String fIndent = indent+ReadonlyInterfaceInfoCollection.indent;
		fieldInfos.forEach(new Consumer<FieldInfo>() {
			@Override
			public void accept(FieldInfo finfo) {
				result.append(finfo.GenerateImpl(fIndent));
			}
		});
		result.append(indent);
		result.append("}\n");
		return result;
	}

	public StringBuilder GenerateExpose(String indent){
		StringBuilder result=new StringBuilder();
		result.append(readonly_prefix);
		result.append(implName);
		result.append(" ");
		result.append(implName);
		result.append("_");
		result.append(FieldInfo.get_property_decl_postfix);
		result.append("\n");
		return result;
	}
	
	public StringBuilder GenerateServerImplExpose(String indent){
		StringBuilder result=new StringBuilder();
		result.append("public ");
		result.append(readonly_prefix);
		result.append(implName);
		result.append(" ");
		result.append(implName);
		result.append("_");
		result.append(FieldInfo.get_property_prefix);
		result.append("_ServerData._");
		result.append(implName);
		result.append(";}}\n\n");
		return result;
	}

	public StringBuilder GenerateUpdateFromServer(String indent){
		StringBuilder result=new StringBuilder();
		result.append(indent);
		result.append("public void Update(");
		result.append(implName);
		result.append(" resp){\n");
		result.append(indent);
		result.append(indent);
		result.append("if (resp == null) return;\n");
		result.append(indent);
		result.append(indent);
		result.append("_ServerData._");
		result.append(implName);
		result.append(" = resp;\n");
		result.append(indent);
		result.append("}\n\n");
		return result;
	}

	public StringBuilder GenerateServerState(String indent){
		StringBuilder result=new StringBuilder();
		result.append(indent);
		result.append("public ");
		result.append(implName);
		result.append(" _");
		result.append(implName);
		result.append(";\n");
		return result;
	}

	public StringBuilder GenerateDataMgr(String indent, String dataVar,String methodName) {
		StringBuilder result=new StringBuilder();
		String indent2=indent+indent;
		
		result.append(indent);
		result.append("public void ");
		result.append(methodName);
		result.append("(");
		result.append(implName);
		result.append(" resp){\n");
		
		result.append(indent2);
		result.append(dataVar);
		result.append(".Update(resp);\n");
		
		result.append(indent2);
		result.append("_dataStream.FireNonNull(");
		result.append(dataVar);
		result.append(");\n");
		
		result.append(indent);
		result.append("}\n\n");
		return result;
	}
}
