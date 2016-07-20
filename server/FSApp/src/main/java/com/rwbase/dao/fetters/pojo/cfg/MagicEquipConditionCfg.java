package com.rwbase.dao.fetters.pojo.cfg;

import java.util.List;
import java.util.Map;

import com.rwbase.dao.fetters.pojo.cfg.template.FettersSubConditionTemplate;


/**
 * 羁绊条件配置
 * @author Alex
 *
 * 2016年7月18日 上午11:33:12
 */
public class MagicEquipConditionCfg {

	
	private int uniqueId; //羁绊条件唯一的id
	
	private int itemModelId;
	
	private int conditionLevel;
	
	private int nextID;//下一个条件id
	
	private int type;//类型，用于区分法宝(1)还是神器(2)
	
	private int subType;//子类型，用于判断法宝是不是同一种类，因为同一种类法宝，1级可能是A,升级到2级可能就变成了B
	
	
//	private String heroModelID;//神器对应的的英雄id
	
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

	
	private Map<Integer, Integer> attrDataMap;// 羁绊增加属性
	private Map<Integer, Integer> precentAttrDataMap;// 羁绊增加的百分比属性
	private List<FettersSubConditionTemplate> subConditionList;// 子条件的列表
	
	
	
	public int getUniqueId() {
		return uniqueId;
	}

	

	public int getItemModelId() {
		return itemModelId;
	}



//	public String getHeroModelID() {
//		return heroModelID;
//	}



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



	/**
	 * 格式化属性数据及子条件列表
	 */
	public void formateData() {
		
		
	}
	
	
	
	
}
