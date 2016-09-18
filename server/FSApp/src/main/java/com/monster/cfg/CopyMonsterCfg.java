package com.monster.cfg;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 怪物模版  没有完全读取所有怪物属性，后面按需求再添加
 * @author Alex
 * 2016年6月22日 下午4:10:59
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CopyMonsterCfg {

	
	private String Id;
	private String name;
	/**怪物类别*/
	private int type;
	/**怪物描述*/
	private String description;
	/**模型id*/
	private int modeId;
	
	/**生命*/
	private int life;
	
	/**血条数量*/
	private int HpCount;
	/**品质*/
	private int quality;
	
	/**星级*/
	private int starLevel;
	/**头像*/
	private String imageName;
	
	private int energy;
	
	
	public int getEnergy() {
		return energy;
	}
	public String getId() {
		return Id;
	}
	public String getName() {
		return name;
	}
	public int getType() {
		return type;
	}
	public String getDescription() {
		return description;
	}
	
	
	public int getModeId() {
		return modeId;
	}
	public int getLife() {
		return life;
	}
	public int getHpCount() {
		return HpCount;
	}
	public int getQuality() {
		return quality;
	}
	public int getStarLevel() {
		return starLevel;
	}
	public String getImageName() {
		return imageName;
	}
	
	
}
