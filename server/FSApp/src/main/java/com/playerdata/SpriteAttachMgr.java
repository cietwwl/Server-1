package com.playerdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.common.IHeroAction;
import com.log.GameLog;
import com.playerdata.refactor.IDataMgrSingletone;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwbase.dao.spriteattach.SpriteAttachCfgDAO;
import com.rwbase.dao.spriteattach.SpriteAttachHolder;
import com.rwbase.dao.spriteattach.SpriteAttachItem;
import com.rwbase.dao.spriteattach.SpriteAttachRoleCfgDAO;
import com.rwbase.dao.spriteattach.SpriteAttachSyn;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachCfg;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachRoleCfg;

public class SpriteAttachMgr implements IDataMgrSingletone{

	
	public static SpriteAttachMgr _instance = new SpriteAttachMgr();
	
	public static SpriteAttachMgr getInstance() {
		return _instance;
	}
	
	public SpriteAttachHolder getSpriteAttachHolder(){
		return SpriteAttachHolder.getInstance();
	}
	
	protected SpriteAttachMgr() {
	}
	
	@Override
	public boolean load(String key) {
		return false;
	}

	@Override
	public boolean save(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	public void regDataChangeCallback(IHeroAction callback) {
		getSpriteAttachHolder().regDataChangeCallback(callback);
	}
	
	public void synAllData(Player player, Hero hero){
		getSpriteAttachHolder().synAllData(player, hero);
	}
	
	/**
	 * 检测灵蕴点是否激活
	 * @param player
	 * @param hero
	 * @param spriteAttachCfg
	 * @return
	 */
	public boolean checkSpriteAttachActive(Player player, Hero hero, SpriteAttachCfg spriteAttachCfg, Map<Integer, SpriteAttachItem> itemMap){
		String userId = player.getUserId();
		String heroId = hero.getUUId();
		int heroLevel = hero.getLevel();
		int heroModelId = hero.getModeId();
		String heroQuality = hero.getQualityId();
		return checkSpriteAttachActive(userId, heroId, heroLevel, heroModelId, heroQuality, spriteAttachCfg, itemMap);
	}
	
	/**
	 * 检查是否激活
	 * @param userId
	 * @param heroId
	 * @param heroLevel
	 * @param heroModelId
	 * @param heroQuality
	 * @param spriteAttachCfg
	 * @return
	 */
	private boolean checkSpriteAttachActive(String userId, String heroId, int heroLevel, int heroModelId, String heroQuality, SpriteAttachCfg spriteAttachCfg, Map<Integer, SpriteAttachItem> itemMap){
		int requireLevel = spriteAttachCfg.getLevel();
		int requireQuality = spriteAttachCfg.getQuality();
		Map<Integer, Integer> preSpriteRequireMap = spriteAttachCfg.getPreSpriteRequireMap();
		if(heroLevel < requireLevel){
			return false;
		}
		RoleQualityCfg roleQualityCfg = RoleQualityCfgDAO.getInstance().getConfig(heroQuality);
		
		if(roleQualityCfg.getQuality() < requireQuality){
			return false;
		}
		
		for (Iterator<Entry<Integer, Integer>> iterator = preSpriteRequireMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, Integer> entry = iterator.next();
			Integer spriteAttachId = entry.getKey();
			int spriteAttachRequireLevel = entry.getValue();
			SpriteAttachItem spriteAttachItem = itemMap.get(spriteAttachId);
			if(spriteAttachItem == null){
				GameLog.error("SpriteAttach", userId, "附灵配置的前置灵蕴点找不到:" + spriteAttachId + ", hero modelId:" + heroModelId);
				continue;
			}
			if(spriteAttachItem.getLevel() < spriteAttachRequireLevel){
				return false;
			}
			
		}
		
		return true;
	}
	
	/**
	 * 角色创建时检测
	 * @param player
	 * @param heroId
	 * @param heroLevel
	 * @param heroModelId
	 * @param heroQuality
	 * @return
	 */
	private SpriteAttachSyn checkSpriteAttachCreate(String userId, String heroId, int heroLevel, int heroModelId, String heroQuality) {
		SpriteAttachCfgDAO spriteAttachCfgDAO = SpriteAttachCfgDAO.getInstance();
		SpriteAttachRoleCfg spriteAttachRoleCfg = SpriteAttachRoleCfgDAO.getInstance().getCfgById(String.valueOf(heroModelId));

		HashMap<Integer, Integer> indexMap = spriteAttachRoleCfg.getIndexMap();
		SpriteAttachSyn spriteAttachSyn = new SpriteAttachSyn();
		spriteAttachSyn.setId(heroModelId);
		spriteAttachSyn.setOwnerId(heroId);
		Map<Integer, SpriteAttachItem> items = new HashMap<Integer, SpriteAttachItem>();
		for (Iterator<Entry<Integer, Integer>> iterator = indexMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, Integer> entry = iterator.next();
			Integer index = entry.getValue();
			Integer spriteId = entry.getKey();
			SpriteAttachCfg spriteAttachCfg = spriteAttachCfgDAO.getCfgById(String.valueOf(spriteId));

			if (checkSpriteAttachActive(userId, heroId, heroLevel, heroModelId, heroQuality, spriteAttachCfg, items)) {
				int spriteAttachId = spriteAttachCfg.getId();
				
				createSpriteAttachItem(spriteAttachSyn, index, spriteAttachId);
			}
		}

		return spriteAttachSyn;
	}
	
	/**
	 * 解锁时创建
	 * @param spriteAttachSyn
	 * @param index
	 * @param spriteAttachId
	 * @return
	 */
	public boolean createSpriteAttachItem(SpriteAttachSyn spriteAttachSyn, int index, int spriteAttachId){
		Map<Integer, SpriteAttachItem> items = spriteAttachSyn.getItemMap();
		SpriteAttachItem spriteAttachItem = items.get(index);
		if (spriteAttachItem != null) {
			if (spriteAttachItem.getSpriteAttachId() != spriteAttachId) {
				SpriteAttachItem item = craeteSpriteAttachItem(spriteAttachSyn, index, spriteAttachId);
				spriteAttachSyn.addItem(item);
				return true;
			}
		} else {
			SpriteAttachItem item = craeteSpriteAttachItem(spriteAttachSyn, index, spriteAttachId);
			spriteAttachSyn.addItem(item);
			return true;
		}
		return false;
	}
	
	/**
	 * 加入灵蕴点
	 * @param spriteAttachSyn
	 * @param index
	 * @param spriteAttachId
	 * @return
	 */
	private SpriteAttachItem craeteSpriteAttachItem(SpriteAttachSyn spriteAttachSyn, int index, int spriteAttachId){
		SpriteAttachItem item = new SpriteAttachItem();
		item.setSpriteAttachId(spriteAttachId);
		item.setLevel(1);
		item.setIndex(index);
		return item;
	}
	
	/**
	 * 角色创建时检测
	 * @param userId
	 * @param heroId
	 * @param heroLevel
	 * @param heroModelId
	 * @param heroQuality
	 * @return
	 */
	public List<SpriteAttachSyn> checkRoleCreate(String userId, String heroId, int heroLevel, int heroModelId, String heroQuality) {
		
		List<SpriteAttachSyn> list = new ArrayList<SpriteAttachSyn>();

		SpriteAttachSyn spriteAttachSyn = checkSpriteAttachCreate(userId, heroId, heroLevel, heroModelId, heroQuality);
		if (spriteAttachSyn != null) {
			list.add(spriteAttachSyn);
		}

		return list;
	}
	
	/**
	 * 角色专职
	 * @param player
	 */
	public void onCarrerChange(Player player){
		HeroMgr heroMgr = player.getHeroMgr();
		Hero hero = heroMgr.getMainRoleHero(player);
		SpriteAttachHolder spriteAttachHolder = getSpriteAttachHolder();
		SpriteAttachSyn spriteAttachSyn = spriteAttachHolder.getSpriteAttachSyn(hero.getId());
		SpriteAttachRoleCfg spriteAttachRoleCfg = SpriteAttachRoleCfgDAO.getInstance().getCfgById(String.valueOf(hero.getModeId()));
		HashMap<Integer,Integer> indexMap = spriteAttachRoleCfg.getIndexMap();
		Map<Integer, SpriteAttachItem> itemMap = spriteAttachSyn.getItemMap();
		for (Iterator<Entry<Integer, SpriteAttachItem>> iterator = itemMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, SpriteAttachItem> entry = iterator.next();
			Integer index = entry.getKey();
			SpriteAttachItem spriteAttachItem = entry.getValue();
			int newSpriteAttachId = 0;
			for (Iterator<Entry<Integer, Integer>> iterator2 = indexMap.entrySet().iterator(); iterator2.hasNext();) {
				Entry<Integer, Integer> entry2 = iterator2.next();
				Integer templateIndex = entry2.getValue();
				Integer id = entry2.getKey();
				if(templateIndex == index){
					newSpriteAttachId = id;
					break;
				}
				
			}
			spriteAttachItem.setSpriteAttachId(newSpriteAttachId);
		}
		spriteAttachHolder.updateItem(player, spriteAttachSyn);
	}
	
	/**
	 * 获取指定英雄的附灵信息
	 * @param player
	 * @param heroModelId
	 * @return
	 */
	public List<SpriteAttachItem> getHeroSpriteAttach(Player player, int heroModelId){
		HeroMgr heroMgr = player.getHeroMgr();
		Hero hero = heroMgr.getHeroByModerId(player, heroModelId);
		SpriteAttachHolder spriteAttachHolder = getSpriteAttachHolder();
		SpriteAttachSyn spriteAttachSyn = spriteAttachHolder.getSpriteAttachSyn(hero.getId());
		return spriteAttachSyn.getItems();
	}
}
