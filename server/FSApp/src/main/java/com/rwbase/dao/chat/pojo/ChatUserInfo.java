package com.rwbase.dao.chat.pojo;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/*
 * @author HC
 * @date 2016年6月27日 下午5:12:12
 * @Description 聊天人的基础信息
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ChatUserInfo {
	
	private static final String _KEY_USER_ID = "1";
	private static final String _KEY_HEAD_IMAGE = "3";
	private static final String _KEY_USER_NAME = "2";
	private static final String _KEY_LEVEL = "4";
	private static final String _KEY_GROUP_ID = "5";
	private static final String _KEY_GROUP_NAME = "6";
	private static final String _KEY_HEAD_BOX = "7";
	private static final String _KEY_CAREER_TYPE = "8";
	private static final String _KEY_GENDER = "9";
	private static final String _KEY_VIP_LV = "10";
	private static final String _KEY_FASHION_TEMPLATE_ID = "11";
	

	private static final Map<String, Field> _fieldsOfNewKeys;
	private static final Map<String, Field> _fieldsOfOldKeys;
	
	static {

		Map<String, Field> fieldsOfNewKeys = new HashMap<String, Field>();
		Map<String, Field> fieldsOfOldKeys = new HashMap<String, Field>();

		Field[] allFields = ChatUserInfo.class.getDeclaredFields();
		for (int i = 0; i < allFields.length; i++) {
			Field f = allFields[i];
			if (f.isAnnotationPresent(JsonProperty.class)) {
				f.setAccessible(true);
				JsonProperty jp = f.getAnnotation(JsonProperty.class);
				fieldsOfNewKeys.put(jp.value(), f);
				fieldsOfOldKeys.put(f.getName(), f);
			}
		}
		_fieldsOfNewKeys = Collections.unmodifiableMap(fieldsOfNewKeys);
		_fieldsOfOldKeys = Collections.unmodifiableMap(fieldsOfOldKeys);
	}

	@JsonProperty(_KEY_USER_ID)
	private String userId;// 角色Id
	
	@JsonProperty(_KEY_USER_NAME)
	private String userName;// 角色名字
	
	@JsonSerialize(include = Inclusion.NON_NULL)
	@JsonProperty(_KEY_HEAD_IMAGE)
	private String headImage;// 头像
	
	@JsonProperty(_KEY_LEVEL)
	private int level;// 角色等级
	
	@JsonSerialize(include = Inclusion.NON_NULL)
	@JsonProperty(_KEY_GROUP_ID)
	private String groupId;// 帮会Id
	
	@JsonSerialize(include = Inclusion.NON_NULL)
	@JsonProperty(_KEY_GROUP_NAME)
	private String groupName;// 帮会名字
	
	@JsonSerialize(include = Inclusion.NON_NULL)
	@JsonProperty(_KEY_HEAD_BOX)
	private String headbox;// 头像品质框
	
	@JsonProperty(_KEY_CAREER_TYPE)
	private int careerType; // 职业类型
	
	@JsonProperty(_KEY_GENDER)
	private int gender; // 性别
	
	@JsonProperty(_KEY_VIP_LV)
	@JsonSerialize(include=Inclusion.NON_DEFAULT)
	private int vipLv; // VIP等級
	
	@JsonProperty(_KEY_FASHION_TEMPLATE_ID)
	@JsonSerialize(include=Inclusion.NON_DEFAULT)
	private int fashionTemplateId;
	
	private static  ChatUserInfo handleJsonMap(Map<String, Object> map, Map<String, Field> fieldMap) throws Exception {
		ChatUserInfo cui = new ChatUserInfo();
		for (Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator(); itr.hasNext();) {
			Map.Entry<String, Object> entry = itr.next();
			Field f = fieldMap.get(entry.getKey());
			f.set(cui, entry.getValue());
		}
		return cui;
	}
	
	@JsonCreator
	public static ChatUserInfo forValue(Map<String, Object> map) throws Exception {
		Map<String, Field> fieldMap;
		if (map.containsKey(_KEY_USER_ID)) {
			fieldMap = _fieldsOfNewKeys;
		} else {
			fieldMap = _fieldsOfOldKeys;
		}
		return handleJsonMap(map, fieldMap);
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setHeadbox(String headbox) {
		this.headbox = headbox;
	}
	
	public void setCareerType(int pCareerType) {
		this.careerType = pCareerType;
	}
	
	public void setGender(int pGender) {
		this.gender = pGender;
	}
	
	public void setVipLv(int pVipLv) {
		this.vipLv = pVipLv;
	}
	
	public void setFashionTemplateId(int pFashionTemplateId) {
		this.fashionTemplateId = pFashionTemplateId;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getHeadImage() {
		return headImage;
	}

	public int getLevel() {
		return level;
	}

	public String getHeadbox() {
		return headbox;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}
	
	public int getCareerType() {
		return careerType;
	}
	
	public int getGender() {
		return gender;
	}
	
	public int getVipLv() {
		return vipLv;
	}
	
	public int getFashionTemplateId() {
		return fashionTemplateId;
	}

	@Override
	public String toString() {
		return "ChatUserInfo [userId=" + userId + ", userName=" + userName + ", headImage=" + headImage + ", level=" + level + ", groupId=" + groupId + ", groupName=" + groupName + ", headbox="
				+ headbox + ", careerType=" + careerType + ", gender=" + gender + "]";
	}
}