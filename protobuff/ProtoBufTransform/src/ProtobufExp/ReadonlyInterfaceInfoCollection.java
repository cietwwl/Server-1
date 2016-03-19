package ProtobufExp;

import java.util.LinkedList;
import java.util.function.Consumer;

import ProtobufExp.Absyn.NilExpList;

public class ReadonlyInterfaceInfoCollection {
	public LinkedList<ReadonlyInterfaceInfo> interfaceInfos;
	private String protobufName;
	public static final String indent = "\t";;
	
	public ReadonlyInterfaceInfoCollection(NilExpList p, String protoNm) {
		protobufName=protoNm;
	}

	public ReadonlyInterfaceInfoCollection(LinkedList<ReadonlyInterfaceInfo> cons, String protoNm) {
		interfaceInfos=cons;
		protobufName=protoNm;
	}

	public StringBuilder GenerateCSharpReadonlyInterface(){
		StringBuilder b = new StringBuilder();
		if (interfaceInfos == null) return b;
		b.append("using System.Collections.Generic;\n");
		b.append("namespace ");
		b.append(protobufName);
		b.append("{\n");
		interfaceInfos.forEach(new Consumer<ReadonlyInterfaceInfo>() {
			@Override
			public void accept(ReadonlyInterfaceInfo t) {
				b.append(t.GenerateReadonlyInterface(indent));
				b.append(t.GenerateInstance(indent));
			}
		});
		b.append("}\n");
		return b;
	}
	
	public StringBuilder GenerateCSharpExpose(){
		StringBuilder b = new StringBuilder();
		if (interfaceInfos == null) return b;
		b.append("#region Auto Generated: Readonly Interfaces Exposion\n");
		interfaceInfos.forEach(new Consumer<ReadonlyInterfaceInfo>() {
			@Override
			public void accept(ReadonlyInterfaceInfo t) {
				b.append(t.GenerateExpose(indent));
			}
		});
		b.append("#endregion\n");
		return b;
	}

	public StringBuilder GenerateServerImplExpose(){
		StringBuilder b = new StringBuilder();
		if (interfaceInfos == null) return b;
		b.append("#region Auto Generated: Implement Exposion using _ServerData\n");
		interfaceInfos.forEach(new Consumer<ReadonlyInterfaceInfo>() {
			@Override
			public void accept(ReadonlyInterfaceInfo t) {
				b.append(t.GenerateServerImplExpose(indent));
			}
		});
		b.append("#endregion\n");
		return b;
	}
	
	public StringBuilder GenerateUpdateFromServer(){
		StringBuilder b = new StringBuilder();
		if (interfaceInfos == null) return b;
		b.append("#region Auto Generated: Update Data From Server Responses\n");
		interfaceInfos.forEach(new Consumer<ReadonlyInterfaceInfo>() {
			@Override
			public void accept(ReadonlyInterfaceInfo t) {
				b.append(t.GenerateUpdateFromServer(indent));
			}
		});
		b.append("#endregion\n");
		return b;
	}
	
	public StringBuilder GenerateServerState(){
		StringBuilder b = new StringBuilder();
		if (interfaceInfos == null) return b;
		b.append("#region Auto Generated: Store Server Responses\n");
		b.append("public sealed partial class ServerState{\n");
		interfaceInfos.forEach(new Consumer<ReadonlyInterfaceInfo>() {
			@Override
			public void accept(ReadonlyInterfaceInfo t) {
				b.append(t.GenerateServerState(indent));
			}
		});
		b.append("}\n");
		b.append("#endregion\n");
		return b;
	}
	
	public StringBuilder GenerateDataMgr(String moduleName){
		StringBuilder b = new StringBuilder();
		if (interfaceInfos == null) return b;
		b.append("#region Auto Generated: Data Manager Storing Methods\n");
		b.append("public sealed partial class ");
		b.append(moduleName);
		b.append("DataMgr{\n");
		
		String dataVar = "_"+moduleName+"Data";
		b.append(indent);
		b.append("private ");
		b.append(moduleName);
		b.append("Data ");
		b.append(dataVar);
		b.append(";\n");
		
		String methodName = "Update"+moduleName+"Data";

		interfaceInfos.forEach(new Consumer<ReadonlyInterfaceInfo>() {
			@Override
			public void accept(ReadonlyInterfaceInfo t) {
				b.append(t.GenerateDataMgr(indent,dataVar,methodName));
			}
		});
		b.append("}\n");
		b.append("#endregion\n");
		return b;
	}
}
