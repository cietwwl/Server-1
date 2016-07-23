package com.rwbase.dao.fetters.pojo.cfg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.common.HPCUtil;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.dao.fetters.FettersBM;
import com.rwbase.dao.fetters.pojo.cfg.dao.FetterMagicEquipCfgDao;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersSubConditionTemplate;


/**
 * 羁绊条件配置
 * @author Alex
 *
 * 2016年7月18日 上午11:33:12
 */
public class MagicEquipConditionCfg {

	
	private int uniqueId; //羁绊条件唯一的id
	
	private String itemModelId;
	
	private int conditionLevel; //羁绊等级
	
	private int nextID;//下一个条件id
	
	private int type;//类型，用于区分法宝(1)还是神器(2)
	
	private int subType;//子类型，用于判断法宝是不是同一种类，因为同一种类法宝，1级可能是A,升级到2级可能就变成了B
	
	private int recordOldData;//是否保存旧记录，如神器降星和法宝分解，0=不保留，1=保留
	
	private String heroModelID;//神器对应的的英雄id
	
	private String fettersName;
	
	private String fettersDesc;//羁绊描述
	
	private String fettersAttrDesc; //羁绊增加属性描述
	
	private String fettersAttrData; //羁绊增加属性
	
	private String fettersPrecentAttrData; //羁绊增加万分比属性
	
	
	/**
	 * 条件类型：
	 * 1：英雄品质 
	 * 2：星数 
	 * 3：等级
	 * 4：战斗力 
	 */
	private String subConditionValue; //子条件值  

	private List<Integer> modelIDList;
	private Map<Integer, Integer> attrDataMap;// 羁绊增加属性
	private Map<Integer, Integer> precentAttrDataMap;// 羁绊增加的百分比属性

	private Map<Integer, Map<Integer,Integer>> conditionMap;//条件集合<key=modelID, value=条件集合>
	
	
	
	public int getUniqueId() {
		return uniqueId;
	}

	

	public String getFettersName() {
		return fettersName;
	}



	public String getFettersDesc() {
		return fettersDesc;
	}



	public int getConditionLevel() {
		return conditionLevel;
	}

	public int getNextID() {
		return nextID;
	}

	public String getFettersAttrDesc() {
		return fettersAttrDesc;
	}

	public String getFettersAttrData() {
		return fettersAttrData;
	}

	public String getFettersPrecentAttrData() {
		return fettersPrecentAttrData;
	}

	public String getSubConditionValue() {
		return subConditionValue;
	}



	public int getType() {
		return type;
	}



	public List<Integer> getModelIDList() {
		return modelIDList;
	}



	public String getHeroModelID() {
		return heroModelID;
	}



	public int getSubType() {
		return subType;
	}



	public Map<Integer, Integer> getAttrDataMap() {
		return attrDataMap;
	}



	public Map<Integer, Integer> getPrecentAttrDataMap() {
		return precentAttrDataMap;
	}



	public boolean recordOldData(){
		return recordOldData > 0;
	}

	public Map<Integer, Map<Integer, Integer>> getConditionMap() {
		return conditionMap;
	}



	/**
	 * 格式化属性数据及子条件列表
	 */
	public void formateData() {
		
		this.attrDataMap = AttributeUtils.parseAttrDataStr2Map("MagicEquipFetterCfg", fettersAttrData);
		// ===============================增加的百分比属性
		this.precentAttrDataMap = AttributeUtils.parseAttrDataStr2Map("MagicEquipFetterCfg", fettersPrecentAttrData);
		
		
		
		if(StringUtils.isEmpty(itemModelId) || StringUtils.isEmpty(subConditionValue)){
			throw new ExceptionInInitializerError(String.format("唯一Id[%s]解析法宝神器羁绊表，表中的id，或者条件值有空", uniqueId));
		}
		
		
		String[] ids = itemModelId.split(",");
		
		
		String[] str = subConditionValue.split(",");
		
		List<Integer> idsList = new ArrayList<Integer>();
		
		if(ids.length != str.length){
			throw new ExceptionInInitializerError(String.format("唯一Id[%s]解析法宝神器羁绊表，条件不对应，表中的id数[%s]，条件值长度[%s]",
					uniqueId, ids.length, str.length));
		}
		
		Map<Integer,Map<Integer, Integer>> map = new HashMap<Integer, Map<Integer, Integer>>();
		
		for (int i = 0; i < ids.length; i++) {
			Map<Integer, Integer> conditionMap = Collections.unmodifiableMap(HPCUtil.parseIntegerMap(str[i], ";", "_"));
			if(type == FetterMagicEquipCfgDao.TYPE_MAGICWEAPON && (conditionMap.containsKey(FettersBM.SubConditionType.STAR.type)
					|| conditionMap.containsKey(FettersBM.SubConditionType.QUALITY.type)
					|| conditionMap.containsKey(FettersBM.SubConditionType.FIGHTING.type))){
				throw new ExceptionInInitializerError(String.format("唯一Id[%s]解析法宝神器羁绊表，表中的类型为法宝，但条件值有星级或品阶或战斗力", uniqueId));
			}
			map.put(Integer.parseInt(ids[i]), conditionMap);
			idsList.add(Integer.parseInt(ids[i]));
		}
		
		conditionMap = Collections.unmodifiableMap(map);
	
		modelIDList = Collections.unmodifiableList(idsList);
		
	}




	
	
	
}
