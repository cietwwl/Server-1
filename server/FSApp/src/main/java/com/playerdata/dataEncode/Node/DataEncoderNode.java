package com.playerdata.dataEncode.Node;

public class DataEncoderNode {

	private String className;

	private String fieldName;

	private DataEncoderNode(){}

	public static DataEncoderNode fromStr(String classFieldStr){	
		
		String[] split = classFieldStr.split("-");
		DataEncoderNode node = new DataEncoderNode();
		node.className = split[0];
		node.fieldName = split[1];
		return node;
	}

	public String getClassName() {
		return className;
	}

	public String getFieldName() {
		return fieldName;
	}
	
	
	
}
