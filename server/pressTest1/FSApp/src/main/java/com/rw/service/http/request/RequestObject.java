package com.rw.service.http.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("rawtypes")
public class RequestObject implements Serializable{
	
	private static final long serialVersionUID = -5182532647273106745L;
	
	private String className;
	private String methodName;
	
	private ArrayList<HashMap<Class, Object>> paramList = new ArrayList<HashMap<Class,Object>>();
	
	public RequestObject(){
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public ArrayList<HashMap<Class, Object>> getParamList() {
		return paramList;
	}
	
	public ArrayList<HashMap<Class, Object>> pushParam(Class key, Object value){
		HashMap<Class, Object> map = new HashMap<Class, Object>();
		map.put(key, value);
		paramList.add(map);
		return paramList;
	}
}
