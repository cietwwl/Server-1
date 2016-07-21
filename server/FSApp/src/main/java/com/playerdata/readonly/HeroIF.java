package com.playerdata.readonly;

import com.rwbase.dao.hero.pojo.RoleBaseInfoIF;
import com.rwbase.dao.role.pojo.RoleCfg;

/**
 * 佣兵接口
 * 
 * @author Jamaz
 *
 */
public interface HeroIF {

	/**
	 * 获取Hero实体
	 * 
	 * @return
	 */
	public RoleBaseInfoIF getHeroData();

	/**
	 * 获取佣兵等级
	 * 
	 * @return
	 */
	public int GetHeroLevel();

	/**
	 * 获取佣兵品质
	 * 
	 * @return
	 */
	public int GetHeroQuality();

	/**
	 * 获取战力
	 * 
	 * @return
	 */
	public int getFighting();

	/**
	 * 获取等级
	 * 
	 * @return
	 */
	public int getLevel();

	public int getCareer();

	/**
	 * 模型id
	 * 
	 * @return
	 */
	public int getModelId();

	/**
	 * templateId
	 * 
	 * @return
	 */
	public String getTemplateId();

	/**
	 * 品质id
	 * 
	 * @return
	 */
	public String getQualityId();

	public RoleCfg getHeroCfg();

	public AttrMgrIF getAttrMgr();

	// /**
	// * 获取法宝信息
	// *
	// * @return
	// */
	// public ItemDataIF getMagic();
}
