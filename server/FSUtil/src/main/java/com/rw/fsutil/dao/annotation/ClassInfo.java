package com.rw.fsutil.dao.annotation;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import com.rw.fsutil.util.jackson.JsonUtil;

public class ClassInfo {

	private Class<?> clazz;

	private String tableName;
	// 主键属性集
	private Field primaryKeyField;

	private FieldEntry ownerField;

	// 在数据库以单列保存的属性集
	private HashMap<String, FieldEntry> singleFieldsMap = new HashMap<String, FieldEntry>();
	// 以字段名字映射当前属性集
	private HashMap<String, FieldEntry> fieldsMap = new HashMap<String, FieldEntry>();

	private FieldEntry[] singleFields;

	// 在数据库合并成一列保存的属性集
	private FieldEntry[] combineSaveFields;// combineSave的属性集合
	// 数据库中合并保存的列名
	private String combineColumnName;
	// 更新列名集(不包括ownerId、primaryKey、IgnoreUpdate)
	private String[] updateColumns;
	// 更新属性集
	private FieldEntry[] updateSingleFields;
	// 插入列名集(全集)
	private String[] insertColumns;
	// 插入属性集
	private FieldEntry[] insertSingleFields;
	// 读取列名集(不包括ownerId)
	private String[] selectColumns;
	// 读取属性集
	private FieldEntry[] selectSingleFields;
	// 除cfgId外的属性集
	private Field[] attachmentFields;

	public Object newInstance() throws Exception {
		return clazz.newInstance();
	}

	public ClassInfo(Class<?> clazzP, String ownerColumnName) {
		try {
			init(clazzP, ownerColumnName);
		} catch (Exception e) {
			throw (new RuntimeException("初始化ClassInfo失败 clazzP:" + clazzP.toString(), e));
		}
	}

	public ClassInfo(Class<?> clazzP) {
		this(clazzP, null);
	}

	private void init(Class<?> clazzP, String ownerColumnName) throws IntrospectionException, Exception {
		this.clazz = clazzP;
		ArrayList<FieldEntry> combinSaveList = new ArrayList<FieldEntry>();
		ArrayList<String> updateColumns = new ArrayList<String>();
		ArrayList<String> insertColumns = new ArrayList<String>();
		ArrayList<String> selectColumns = new ArrayList<String>();
		Field[] fields = clazzP.getDeclaredFields();
		ArrayList<FieldEntry> singleFields = new ArrayList<FieldEntry>();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Transient.class)) {
				continue;
			}
			if (field.isAnnotationPresent(NonSave.class)) {
				continue;
			}
			int mod = field.getModifiers();
			if (Modifier.isStatic(mod)) {
				continue;
			}
			field.setAccessible(true);
			boolean isPrimaryKey = field.isAnnotationPresent(Id.class);
			if (isPrimaryKey) {
				this.primaryKeyField = field;
			}

			String fieldName = field.getName();
			boolean combineSave = field.isAnnotationPresent(CombineSave.class);
			JavaType collectionType = null;
			boolean saveAsJson = field.isAnnotationPresent(SaveAsJson.class);
			if (combineSave || saveAsJson) {
				if (ConcurrentHashMap.class.isAssignableFrom(field.getType())) {
					collectionType = ClassHelper.getJUCMapGenericJavaType(field);
				} else if (ClassHelper.isList(field)) {
					collectionType = ClassHelper.getListGenericJavaType(field);
				} else if (ClassHelper.isMap(field)) {
					collectionType = ClassHelper.getMapGenericJavaType(field);
				}
			} else {
				// CHEN.P @ 2016-07-13 21:39 处理Column标注 BEGIN
				// CombineSave和SaveAsJson不用管
				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					String columnName = column.name();
					if (columnName != null && (columnName = columnName.trim()).length() > 0) {
						// filedNameMap.remove(fieldName);
						// filedNameMap.put(columnName, field);
						fieldName = columnName;
					}
				}
				// CHEN.P @ 2016-07-13 21:39 END
			}
			FieldEntry fieldEntry = new FieldEntry(field, fieldName, saveAsJson, collectionType, isPrimaryKey);
			this.singleFieldsMap.put(fieldName, fieldEntry);
			this.fieldsMap.put(field.getName(), fieldEntry);
			if (combineSave) {
				// 按数据库列名缓存Field
				CombineSave combineField = field.getAnnotation(CombineSave.class);
				String columnName = combineField.Column();
				if (combineColumnName != null && !combineColumnName.equals(columnName)) {
					throw new ExceptionInInitializerError("多于一个combineSave列名：" + tableName + "," + combineColumnName + "," + columnName);
				}
				combineColumnName = columnName;
				combinSaveList.add(fieldEntry);
			} else {
				singleFields.add(fieldEntry);
				insertColumns.add(fieldName);
				if (ownerColumnName != null && ownerColumnName.equals(fieldName)) {
					if (ownerField != null) {
						throw new ExceptionInInitializerError("多于一个ownerId字段：" + tableName + "," + ownerField.field + "," + fieldEntry.field);
					}
					ownerField = fieldEntry;
				} else {
					selectColumns.add(fieldName);
					if (!isPrimaryKey && !field.isAnnotationPresent(IgnoreUpdate.class)) {
						updateColumns.add(fieldName);
					}
				}
			}
		}
		// 添加合并字段
		if (this.combineColumnName != null) {
			updateColumns.add(combineColumnName);
			insertColumns.add(combineColumnName);
			selectColumns.add(combineColumnName);
		}
		tableName = this.getTableName(clazzP);
		// 单列属性
		this.singleFields = new FieldEntry[singleFields.size()];
		singleFields.toArray(this.singleFields);
		// 合并属性
		this.combineSaveFields = new FieldEntry[combinSaveList.size()];
		combinSaveList.toArray(this.combineSaveFields);
		// 更新列名集
		this.updateColumns = new String[updateColumns.size()];
		updateColumns.toArray(this.updateColumns);
		this.updateSingleFields = createSingleFields(this.updateColumns);
		// 插入列名集
		this.insertColumns = new String[insertColumns.size()];
		insertColumns.toArray(this.insertColumns);
		this.insertSingleFields = createSingleFields(this.insertColumns);
		// 读取列名集
		this.selectColumns = new String[selectColumns.size()];
		selectColumns.toArray(this.selectColumns);
		this.selectSingleFields = createSingleFields(this.selectColumns);
		ArrayList<Field> attachmentList = new ArrayList<Field>(insertColumns.size());
		for (int i = 0; i < insertSingleFields.length; i++) {
			Field f = insertSingleFields[i].field;
			if (!f.isAnnotationPresent(ConfigId.class)) {
				attachmentList.add(f);
			}
		}
		this.attachmentFields = new Field[attachmentList.size()];
		attachmentList.toArray(attachmentFields);
	}

	private FieldEntry[] createSingleFields(String[] nameArray) {
		ArrayList<FieldEntry> entryList = new ArrayList<FieldEntry>();
		for (int i = 0; i < nameArray.length; i++) {
			String name = nameArray[i];
			if (this.combineColumnName != null && this.combineColumnName.equals(name)) {
				continue;
			}
			FieldEntry fieldEntry = this.singleFieldsMap.get(name);
			if (fieldEntry == null) {
				throw new ExceptionInInitializerError("class info columns error:" + name + " not exist!");
			}
			entryList.add(fieldEntry);
		}
		FieldEntry[] fieldArray = new FieldEntry[entryList.size()];
		entryList.toArray(fieldArray);
		return fieldArray;
	}

	private String getTableName(Class<?> clazz) {
		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = clazz.getAnnotation(Table.class);
			return table.name();
		}
		return null;
	}

	public Field getIdField() {
		return primaryKeyField;
	}

	public String getPrimaryKey() {
		String primaryKey = primaryKeyField.getName();
		return primaryKey;
	}

	public String getTableName() {
		return tableName;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String toJson(Object target) throws Exception {
		return JsonUtil.writeValue(target);
	}

	public Object fromJson(String json) throws Exception {
		return JsonUtil.readValue(json, clazz);
	}

	public Collection<FieldEntry> getFields() {
		return singleFieldsMap.values();
	}

	public FieldEntry[] getSingleFields() {
		return singleFields;
	}

	/**
	 * 以列名获取属性(不是属性名)
	 * 
	 * @param name
	 * @return
	 */
	public FieldEntry getSingleField(String name) {
		return singleFieldsMap.get(name);
	}

	/**
	 * 以属性名字获取属性
	 * 
	 * @param name
	 * @return
	 */
	public Field getField(String name) {
		FieldEntry entry = this.fieldsMap.get(name);
		return entry == null ? null : entry.field;
	}

	public boolean isCombineSave(String columnName) {
		if (this.combineColumnName == null) {
			return false;
		}
		return this.combineColumnName.equals(columnName);
	}

	/**
	 * <pre>
	 * 获取对象中合并保存为一列的属性，如果没有合并属性，返回null
	 * @see CombineSave
	 * </pre>
	 * 
	 * @return
	 */
	public FieldEntry[] getCombineSaveFields() {
		return combineSaveFields;
	}

	/**
	 * <pre>
	 * 如果此ClassInfo关联的对象存在{@link CombineSave}的字符，返回指定列名
	 * </pre>
	 * 
	 * @return
	 */
	public String getCombineColumnName() {
		return combineColumnName;
	}

	public String[] getUpdateColumns() {
		return updateColumns;
	}

	public String[] getInsertColumns() {
		return insertColumns;
	}

	public String getOwnerFieldName() {
		return this.ownerField == null ? null : this.ownerField.columnName;
	}

	public Field getOwnerField() {
		return this.ownerField == null ? null : this.ownerField.field;
	}

	public String[] getSelectColumns() {
		return selectColumns;
	}

	public FieldEntry[] getSelectSingleFields() {
		return selectSingleFields;
	}

	/**
	 * 提取指定对象插入时的参数
	 * 
	 * @param pojo
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public List<Object> extractInsertAttributes(Object pojo) throws IllegalArgumentException, IllegalAccessException {
		return extracAttributes(pojo, this.insertSingleFields);
	}

	/**
	 * 提取指定对象更新时的参数
	 * 
	 * @param pojo
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public List<Object> extractUpdateAttributes(Object pojo) throws IllegalArgumentException, IllegalAccessException {
		return extracAttributes(pojo, this.updateSingleFields);
	}

	private ArrayList<Object> extracAttributes(Object pojo, FieldEntry[] singleFields) throws IllegalArgumentException, IllegalAccessException {
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		for (int i = 0, len = singleFields.length; i < len; i++) {
			FieldEntry fieldEntry = singleFields[i];
			Object value = fieldEntry.field.get(pojo);
			if (fieldEntry.saveAsJson) {
				// TODO 此处可优化
				String jsonValue = JsonUtil.writeValue(value);
				fieldValues.add(jsonValue);
			} else {
				fieldValues.add(value);
			}
		}

		FieldEntry[] combineFields = this.combineSaveFields;
		int len = combineFields.length;
		if (len > 0) {
			HashMap<String, String> combineMap = new HashMap<String, String>(len, 1.0f);
			for (int i = 0; i < len; i++) {
				FieldEntry fieldEntry = combineFields[i];
				Object value = fieldEntry.field.get(pojo);
				String jsonValue = JsonUtil.writeValue(value);
				combineMap.put(fieldEntry.columnName, jsonValue);
			}
			fieldValues.add(JsonUtil.writeValue(combineMap));
		}
		return fieldValues;
	}

	// 提取数据库列名
	public void extractColumn(StringBuilder insertFields, StringBuilder insertHolds, StringBuilder updateFieldNames) throws IllegalAccessException {
		String[] columns = getInsertColumns();
		for (int i = 0, len = columns.length; i < len; i++) {
			String columnName = columns[i];
			addSplit(insertFields).append(columnName);
			addSplit(insertHolds).append("?");
		}
		columns = getUpdateColumns();
		for (int i = 0, len = columns.length; i < len; i++) {
			String columnName = columns[i];
			addSplit(updateFieldNames).append(columnName).append("=?");
		}
	}
	
	private StringBuilder addSplit(StringBuilder sb) {
		if (sb.length() > 0) {
			sb.append(",");
		}
		return sb;
	}

	public <T> Object[] extractUpdateParams(Object key, T t) {
		try {
			List<Object> list = extractUpdateAttributes(t);
			int size = list.size();
			Object[] array = new Object[size + 1];
			list.toArray(array);
			array[size] = key;
			return array;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String extractAttachment(Object entity) throws IllegalArgumentException, IllegalAccessException {
		int len = this.attachmentFields.length;
		HashMap<String, Object> map = new HashMap<String, Object>(len);
		for (int i = 0; i < len; i++) {
			Field field = attachmentFields[i];
			map.put(field.getName(), field.get(entity));
		}
		return JsonUtil.writeValue(map);
	}

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, JsonGenerationException, JsonMappingException, IOException {
		TTTT t = new TTTT();
		ObjectMapper MAPPER = new ObjectMapper();
//		MAPPER.configure(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT, state)
		ClassInfo cl = new ClassInfo(TTTT.class);
		long s = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {

//			StringWriter sw = new StringWriter();
//			MAPPER.writeValue(sw, t);
//			String a = sw.toString();
			// String a = JsonUtil.writeValue(t);
			 String a = MAPPER.writeValueAsString(t);
			// String a = MAPPER.
			// String a = cl.extractAttachment(t);
//			 System.out.println(a);
//			 a.i
			// String a = cl.extractAttachment(t);
		}
		System.out.println(System.currentTimeMillis() - s);
	}

	static class TTTT {

		private String name = "aaaaahia";
		private int level = 5;
		private long startTime = System.currentTimeMillis();
		private long endTime = System.currentTimeMillis();
		private boolean flag = true;
		private boolean test = false;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public long getStartTime() {
			return startTime;
		}

		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}

		public long getEndTime() {
			return endTime;
		}

		public void setEndTime(long endTime) {
			this.endTime = endTime;
		}

		public boolean isFlag() {
			return flag;
		}

		public void setFlag(boolean flag) {
			this.flag = flag;
		}

		public boolean isTest() {
			return test;
		}

		public void setTest(boolean test) {
			this.test = test;
		}

	}
}
