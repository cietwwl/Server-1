package com.playerdata.hero;

import com.playerdata.eRoleType;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.user.pojo.LevelCfg;

/**
 * 
 * 英雄数据的接口规范
 * 
 * @author CHEN.P
 *
 */
public interface IHero extends IMapItem, IHeroDelegator {
	
	/**
	 * 
	 * 获取UUID
	 * 
	 * @return
	 */
	public String getUUId();
	
	/**
	 * 
	 * 获取对象类型
	 * 
	 * @return
	 */
	public eRoleType getRoleType();
	
	/**
	 * 
	 * <pre>
	 * 同步英雄信息到客户端
	 * </pre>
	 * 
	 * @param version
	 */
	public void sync(int version);
	
	/**
	 * 
	 * @param immediately
	 */
	public void save(boolean immediately);
	
	/**
	 * 
	 */
	public void save();
	
	/**
	 * 
	 * 获取英雄的模板数据
	 * 
	 * @return
	 */
	public RoleCfg getHeroCfg();
	
	/**
	 * 
	 * 获取英雄的升级经验信息
	 * 
	 * @return
	 */
	public LevelCfg getLevelCfg();
	
	/**
	 * 
	 * 获取英雄的模型id
	 * 
	 * @return
	 */
	public int getModelId();
	
	/**
	 * 
	 * 获取英雄的模板id
	 * 
	 * @return
	 */
	public String getTemplateId();
	
	/**
	 * 
	 * @param templateId
	 */
	public void setTemplateId(String templateId);
	
	/**
	 * 
	 * 是否可以升星
	 * 
	 * @return
	 */
	public int canUpgradeStar();
	
	/**
	 * 
	 * @return
	 */
	public int getHeroQuality();
	
	/**
	 * 
	 * @param level
	 */
	public void SetHeroLevel(int level);
	
	/**
	 * 
	 * @param exp
	 */
	public void setHeroExp(long exp);
	
	/**
	 * 
	 * @return
	 */
	public int getFighting();
	
	/**
	 * 
	 * 设置星级
	 * 
	 * @param star
	 */
	public void setStarLevel(int star);

	/**
	 * 
	 * 获取星级
	 * 
	 * @return
	 */
	public int getStarLevel();
	
	/**
	 * 
	 * @param qualityId
	 */
	public void setQualityId(String qualityId);
	
	/**
	 * 
	 * @return
	 */
	public String getQualityId();
	
	/**
	 * 
	 * @param level
	 */
	public void gmEditHeroLevel(int level);
	
	/**
	 * 
	 */
	public void gmCheckActiveSkill();
	
	/**
	 * 
	 * @param heroExp
	 * @return
	 */
	public int addHeroExp(long heroExp);
	
	/**
	 * 
	 * 是否主角
	 * 
	 * @return
	 */
	public boolean isMainRole();
	
}
