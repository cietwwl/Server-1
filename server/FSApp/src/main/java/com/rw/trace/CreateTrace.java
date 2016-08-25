package com.rw.trace;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.annotation.NonSave;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.dao.cache.trace.DataValueParserMap;

public class CreateTrace {

	private static final String ENTER = "\r\n";// use windows
	private static final String FORMAT = "    ";
	private static final String PUBLIC = "public ";
	private static final String OLD = "entity";
	private static final String JSON = "JSONObject";
	private static final String WRITER = "writer";
	private static final String FILE_PATH = "src/main/java/";
	private static final String MAP = "jsonMap";
	private static final char SP = ' ';
	public static final String PARSER_PATH = "com.rw.trace.parser";
	private static final String SUPPORT_PATH = "com.rw.trace.support";

	private static HashSet<String> parserSet = new HashSet<String>();

	public static void main(String[] args) {
		DataTraceRegistrator[] classArray = DataTraceRegistrator.values();
		for (DataTraceRegistrator trace : classArray) {
			Class<?> clazz = trace.getDataTraceClass();
			if (parserSet.contains(clazz.getName())) {
				continue;
			}
			write(clazz, PARSER_PATH);
		}
	}

	public static void write(Class<?> clazz, String packagePath) {
		HashSet<String> importList = new HashSet<String>();
		importList.add(DataValueParser.class.getName());
		importList.add(clazz.getName());
		importList.add(JsonValueWriter.class.getName());
		importList.add(JSONObject.class.getName());
		Field[] fileds = clazz.getDeclaredFields();
		String simple = clazz.getSimpleName();
		ArrayList<FieldInfo> fieldList = new ArrayList<FieldInfo>();
		for (int i = 0; i < fileds.length; i++) {
			Field field = fileds[i];
			Class<?> fieldClass = field.getType();
			int mod = field.getModifiers();
			if (Modifier.isStatic(mod)) {
				continue;
			}
			if (Modifier.isFinal(mod)) {
				continue;
			}
			if (ignore(field)) {
				continue;
			}
			String name = field.getName();
			String name_ = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
			String getMethodName;
			if (fieldClass == Boolean.class || fieldClass == boolean.class) {
				getMethodName = "is" + name_;
			} else {
				getMethodName = "get" + name_;
			}
			String setMethodName = "set" + name_;
			Method getMethod = null;
			Method setMethod = null;
			try {
				getMethod = clazz.getDeclaredMethod(getMethodName);
				setMethod = clazz.getDeclaredMethod(setMethodName, field.getType());
			} catch (Throwable t) {
				// t.printStackTrace();
			}
			if (getMethod == null) {
				System.out.println("找不到get方法：" + getMethodName);
				continue;
			}
			if (setMethod == null) {
				System.out.println("找不到set方法：" + setMethodName);
				continue;
			}

			if (ignore(getMethod)) {
				continue;
			}
			Class<?> fieldType = field.getType();
			if (!check(fieldType)) {
				write(fieldType, SUPPORT_PATH);
				if (!parserSet.contains(fieldType.getName())) {
					System.err.println("解析:" + fieldType.getName() + " 出错");
					continue;
				}
			}
			if (!primitiveClasses.contains(fieldType) && !fieldType.getPackage().toString().contains("java.lang")) {
				importList.add(fieldType.getName().replace("$", "."));
			}
			fieldList.add(new FieldInfo(name, getMethodName, setMethodName, getMethod, setMethod, field));
		}
		String wname = JsonValueWriter.class.getSimpleName();
		StringBuilder sb = new StringBuilder();
		sb.append(PUBLIC).append("class ").append(simple).append("Parser implements DataValueParser<");
		sb.append(simple).append("> {").append(ENTER).append(ENTER);
		indent1(sb).append("private ").append(wname).append(" writer = ").append(wname).append('.');
		sb.append("getInstance();").append(ENTER).append(ENTER);

		copy(clazz, fieldList, sb, importList);
		recordAndUpdate(clazz, fieldList, sb, importList);
		hasChanged(clazz, fieldList, sb, importList);
		writeToJson(clazz, fieldList, sb, importList);
		sb.append("}");
		StringBuilder front = new StringBuilder();
		front.append("package ").append(packagePath).append(';').append(ENTER).append(ENTER);
		for (String s : importList) {
			front.append("import ").append(s).append(';').append(ENTER);
		}
		front.append(ENTER);

		try {
			String filePath = FILE_PATH + packagePath.replace(".", "/") + "/" + simple + "Parser.java";
			PrintWriter writer = new PrintWriter(filePath);
			writer.write(front.toString());
			writer.write(sb.toString());
			writer.flush();
			writer.close();
			System.out.println("生成文件:" + filePath);
			parserSet.add(clazz.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void writeToJson(Class<?> clazz, ArrayList<FieldInfo> fieldList, StringBuilder sb, HashSet<String> importSet) {
		String simple = clazz.getSimpleName();
		writeMethodHead(sb, JSON, "toJson", 1, simple);
		int size = fieldList.size();
		indent2(sb).append(JSON).append(SP).append("json = new JSONObject(").append(size).append(");").append(ENTER);
		for (FieldInfo field : fieldList) {
			Field f = field.field;
			Class<?> fc = f.getType();
			String name = field.name;
			if (DataValueParserMap.isPrimityType(fc)) {
				indent2(sb).append("json.put(").append(field.name_).append(", ");
				sb.append(OLD).append('.').append(field.getMethodName).append("());").append(ENTER);
			} else if (JsonValueWriter.getInstance().isCloneable(fc)) {
				indent2(sb).append("Object ").append(name).append("Json = writer.toJSON(");
				sb.append(OLD).append('.').append(field.getMethodName).append("());").append(ENTER);
				indent2(sb).append("if (").append(name).append("Json != null) {").append(ENTER);
				indent3(sb).append("json.put(").append(field.name_).append(", ").append(name).append("Json);").append(ENTER);
				writeEnd2(sb);
			}
		}
		indent2(sb).append("return json;").append(ENTER);
		indent1(sb).append('}').append(ENTER).append(ENTER);
	}

	private static void copy(Class<?> clazz, ArrayList<FieldInfo> fieldList, StringBuilder sb, HashSet<String> importSet) {
		String simple = clazz.getSimpleName();
		writeMethodHead(sb, simple, "copy", 1, simple);
		// line 3
		String copyName = simple.substring(0, 1).toLowerCase() + simple.substring(1, simple.length())+"Copy";
		indent2(sb).append(simple).append(SP).append(copyName).append(" = new ").append(simple).append("();").append(ENTER);
		for (FieldInfo fieldInfo : fieldList) {
			String setMethodName = fieldInfo.setMethodName;
			String getMethodName = fieldInfo.getMethodName;
			if (DataValueParserMap.isPrimityType(fieldInfo.field.getType())) {
				indent2(sb).append(copyName).append('.').append(setMethodName).append('(');
				sb.append(OLD).append('.').append(getMethodName).append("());").append(ENTER);
			} else if (JsonValueWriter.getInstance().isCloneable(fieldInfo.field.getType())) {
				indent2(sb).append(copyName).append('.').append(setMethodName).append('(').append(WRITER).append('.');
				sb.append("copyObject(").append(OLD).append('.').append(getMethodName).append("()));").append(ENTER);
			}
		}
		indent2(sb).append("return ").append(copyName).append(';').append(ENTER);
		indent1(sb).append("}").append(ENTER).append(ENTER);
	}

	private static StringBuilder writeMethodHead(StringBuilder sb, String returnName, String methodName, int params, String simple) {
		// line 1
		indent1(sb).append("@Override").append(ENTER);
		// line 2 method
		indent1(sb).append(PUBLIC).append(returnName).append(SP).append(methodName).append('(');
		for (int i = 0; i < params; i++) {
			sb.append(simple).append(SP);
			if (params == 1) {
				sb.append(OLD);
			} else {
				sb.append(OLD).append(i + 1);
				if (i == 0) {
					sb.append(", ");
				}
			}
		}
		sb.append(") {").append(ENTER);
		return sb;
	}

	private static StringBuilder writePrimitive(StringBuilder sb, String firstName, String secondName) {
		indent2(sb).append("if (").append(firstName).append(" != ").append(secondName).append(") {").append(ENTER);
		return sb;
	}

	private static StringBuilder writeEquals(StringBuilder sb, String firstName, String secondName) {
		indent2(sb).append("if (!").append(WRITER).append(".equals(").append(firstName).append(", ").append(secondName).append(")) {").append(ENTER);
		return sb;
	}

	private static StringBuilder writeHasChanged(StringBuilder sb, String firstName, String secondName) {
		indent2(sb).append("if (").append(WRITER).append(".hasChanged(").append(firstName).append(", ").append(secondName).append(")) {").append(ENTER);
		return sb;
	}

	private static void recordAndUpdate(Class<?> clazz, ArrayList<FieldInfo> fieldList, StringBuilder sb, HashSet<String> importSet) {
		String simple = clazz.getSimpleName();
		writeMethodHead(sb, JSON, "recordAndUpdate", 2, simple);
		indent2(sb).append(JSON).append(SP).append(MAP).append(" = null;").append(ENTER);
		for (FieldInfo fieldInfo : fieldList) {
			Field field = fieldInfo.field;
			String lowFiledName = fieldInfo.name;
			String getMethodName = fieldInfo.getMethodName;
			String setMethodName = fieldInfo.setMethodName;
			Class<?> returnType = field.getType();
			String firstName = lowFiledName + "1";
			String secondName = lowFiledName + "2";
			indent2(sb).append(getMethodType(field, importSet)).append(SP).append(firstName).append(" = entity1.").append(getMethodName).append("();").append(ENTER);
			indent2(sb).append(getMethodType(field, importSet)).append(SP).append(secondName).append(" = entity2.").append(getMethodName).append("();").append(ENTER);
			boolean isPrimitive = primitiveClasses.contains(returnType) || Enum.class.isAssignableFrom(returnType);
			boolean isEquals = equalsMap.contains(returnType);
			boolean isMap = Map.class.isAssignableFrom(returnType) || List.class.isAssignableFrom(returnType) || parserSet.contains(returnType.getName());
			if (isPrimitive) {
				writePrimitive(sb, firstName, secondName);
				indent3(sb).append("entity1.").append(setMethodName).append('(').append(secondName).append(");").append(ENTER);
				indent3(sb).append(MAP).append(" = writer.write(").append(MAP).append(", ").append(fieldInfo.name_).append(", ").append(secondName).append(");").append(ENTER);
				writeEnd2(sb);
			} else if (isEquals) {
				writeEquals(sb, firstName, secondName);
				indent3(sb).append("entity1.").append(setMethodName).append('(').append(secondName).append(");").append(ENTER);
				indent3(sb).append(MAP).append(" = writer.write(").append(MAP).append(", ").append(fieldInfo.name_).append(", ").append(secondName).append(");").append(ENTER);
				writeEnd2(sb);
			} else if (isMap) {
				importSet.add(Pair.class.getName());
				String pairName = lowFiledName + "Pair";
				indent2(sb).append("Pair<").append(getMethodType(field, importSet)).append(", JSONObject> ").append(pairName).append(" = writer.checkObject(").append(MAP).append(", ").append('"').append(lowFiledName).append("\", ");
				sb.append(firstName).append(", ").append(secondName).append(");").append(ENTER);
				indent2(sb).append("if (").append(pairName).append(" != null) {").append(ENTER);
				indent3(sb).append(firstName).append(" = ").append(pairName).append(".getT1();").append(ENTER);
				indent3(sb).append("entity1.").append(setMethodName).append('(').append(firstName).append(");").append(ENTER);
				indent3(sb).append(MAP).append(" = ").append(pairName).append(".getT2();").append(ENTER);
				indent2(sb).append("} else {").append(ENTER);
				indent3(sb).append(MAP).append(" = writer.compareSetDiff(").append(MAP).append(", ").append('"').append(lowFiledName).append("\", ");
				sb.append(firstName).append(", ").append(secondName).append(");").append(ENTER);
				indent2(sb).append("}").append(ENTER).append(ENTER);
			}
		}

		indent2(sb).append("return ").append(MAP).append(';').append(ENTER);
		indent1(sb).append("}").append(ENTER).append(ENTER);
	}

	private static void hasChanged(Class<?> clazz, ArrayList<FieldInfo> fieldList, StringBuilder sb, HashSet<String> importSet) {
		String simple = clazz.getSimpleName();
		writeMethodHead(sb, "boolean", "hasChanged", 2, simple);
		for (FieldInfo fieldInfo : fieldList) {
			Field field = fieldInfo.field;
			String getMethodName = fieldInfo.getMethodName + "()";
			String firstGet = "entity1." + getMethodName;
			String secondGet = "entity2." + getMethodName;
			Class<?> returnType = field.getType();
			boolean isPrimitive = primitiveClasses.contains(returnType) || Enum.class.isAssignableFrom(returnType);
			boolean isEquals = equalsMap.contains(returnType);
			boolean isMap = Map.class.isAssignableFrom(returnType) || List.class.isAssignableFrom(returnType) || parserSet.contains(returnType.getName());
			if (isPrimitive) {
				writePrimitive(sb, firstGet, secondGet);
				writeReturnFalse3(sb);
				writeEnd2(sb);
			} else if (isEquals) {
				writeEquals(sb, firstGet, secondGet);
				writeReturnFalse3(sb);
				writeEnd2(sb);
			} else if (isMap) {
				importSet.add(Pair.class.getName());
				writeHasChanged(sb, firstGet, secondGet);
				writeReturnFalse3(sb);
				writeEnd2(sb);
			}
		}
		indent2(sb).append("return false;").append(ENTER);
		indent1(sb).append("}").append(ENTER).append(ENTER);
	}

	private static HashSet<Class<?>> primitiveClasses = new HashSet<Class<?>>();
	private static HashSet<Class<?>> equalsMap = new HashSet<Class<?>>();

	private static boolean check(Class<?> clazz) {
		if (primitiveClasses.contains(clazz)) {
			return true;
		}
		if (equalsMap.contains(clazz)) {
			return true;
		}
		if (Map.class.isAssignableFrom(clazz)) {
			return true;
		}
		if (List.class.isAssignableFrom(clazz)) {
			return true;
		}
		if (Enum.class.isAssignableFrom(clazz)) {
			return true;
		}
		return false;
	}

	static {
		primitiveClasses.add(boolean.class);
		equalsMap.add(Boolean.class);

		primitiveClasses.add(char.class);
		equalsMap.add(Character.class);

		primitiveClasses.add(byte.class);
		equalsMap.add(Byte.class);

		primitiveClasses.add(short.class);
		equalsMap.add(Short.class);

		primitiveClasses.add(int.class);
		equalsMap.add(Integer.class);

		primitiveClasses.add(long.class);
		equalsMap.add(Long.class);

		primitiveClasses.add(float.class);
		equalsMap.add(Float.class);

		primitiveClasses.add(double.class);
		equalsMap.add(Double.class);

		equalsMap.add(BigInteger.class);
		equalsMap.add(BigDecimal.class);
		equalsMap.add(Enum.class);

		equalsMap.add(String.class);
		equalsMap.add(java.util.Date.class);
		equalsMap.add(java.sql.Date.class);
		equalsMap.add(java.sql.Time.class);
		equalsMap.add(java.sql.Timestamp.class);

	}

	public static String getMethodType(Field field, HashSet<String> importSet) {
		StringBuilder typeBuilder = new StringBuilder();
		Type mapMainType = field.getGenericType();
		// 判断是否为参数化类型，如 Collection<String>
		if (mapMainType instanceof ParameterizedType) {
			// 执行强制类型转换
			ParameterizedType parameterizedType = (ParameterizedType) mapMainType;
			// 获取基本类型信息
			Type basicType = parameterizedType.getRawType();
			String basic = basicType.toString().substring(basicType.toString().lastIndexOf(".") + 1);
			// 添加collection基本类型
			typeBuilder.append(basic).append("<");
			// 获取泛型类型的泛型参数
			Type[] types = parameterizedType.getActualTypeArguments();
			for (int i = 0; i < types.length; i++) {
				Type type = types[i];
				Class<?> typeClass = TypeUtils.getClass(type);
				if (typeClass == null) {
					System.err.println("解析属性泛型参数出错:" + field + "," + type.toString());
					return field.getType().getSimpleName();
				}
				String para = type.toString().substring(types[i].toString().lastIndexOf(".") + 1);
				if (!check(typeClass)) {
					write(typeClass, SUPPORT_PATH);
					if (!parserSet.contains(typeClass.getName())) {
						System.err.println("解析:" + typeClass.getName() + " 出错");
						continue;
					}
				}
				if (!primitiveClasses.contains(typeClass) && !typeClass.getPackage().toString().contains("java.lang")) {
					importSet.add(typeClass.getName().replace("$", "."));
				}
				typeBuilder.append(para);
				if (i < types.length - 1) {
					typeBuilder.append(", ");
				}
			}
			typeBuilder.append(">");
		} else {
			typeBuilder.append(field.getType().getSimpleName());
		}
		String type = typeBuilder.toString();
		return type;
	}

	private static void writeEnd2(StringBuilder sb) {
		indent2(sb).append('}').append(ENTER);
	}

	private static void writeReturnFalse3(StringBuilder sb) {
		indent3(sb).append("return true;").append(ENTER);
	}

	private static StringBuilder indent1(StringBuilder sb) {
		sb.append(FORMAT);
		return sb;
	}

	private static StringBuilder indent2(StringBuilder sb) {
		sb.append(FORMAT).append(FORMAT);
		return sb;
	}

	private static StringBuilder indent3(StringBuilder sb) {
		sb.append(FORMAT).append(FORMAT).append(FORMAT);
		return sb;
	}

	private static boolean ignore(Field field) {
		if (field.isAnnotationPresent(NonSave.class)) {
			return true;
		}
		if (field.isAnnotationPresent(Transient.class)) {
			return true;
		}
		if (field.isAnnotationPresent(JsonIgnore.class)) {
			return true;
		}
		return false;
	}

	private static boolean ignore(Method method) {
		if (method.isAnnotationPresent(JsonIgnore.class)) {
			return true;
		}
		if (method.isAnnotationPresent(Transient.class)) {
			return true;
		}
		return false;
	}

	private static class FieldInfo {
		private String name;
		private String getMethodName;
		private String setMethodName;
		private Method getMethod;
		private Method setMethod;
		private Field field;
		private String name_;

		public FieldInfo(String name, String getMethodName, String setMethodName, Method getMethod, Method setMethod, Field field) {
			super();
			this.name = name;
			this.getMethodName = getMethodName;
			this.setMethodName = setMethodName;
			this.getMethod = getMethod;
			this.setMethod = setMethod;
			this.field = field;
			this.name_ = '"' + name + '"';
		}

	}

}
