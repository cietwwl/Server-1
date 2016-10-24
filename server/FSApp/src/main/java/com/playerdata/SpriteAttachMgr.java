package com.playerdata;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.common.IHeroAction;
import com.log.GameLog;
import com.playerdata.refactor.IDataMgrSingletone;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwbase.dao.spriteattach.SpriteAttachHolder;
import com.rwbase.dao.spriteattach.SpriteAttachItem;
import com.rwbase.dao.spriteattach.SpriteAttachSyn;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachCfg;

public class SpriteAttachMgr implements IDataMgrSingletone{

	
	public static final SpriteAttachMgr _INSTANCE = new SpriteAttachMgr();
	
	public static SpriteAttachMgr getInstance() {
		return _INSTANCE;
	}
	
	public SpriteAttachHolder getSpriteAttachHolder(){
		return SpriteAttachHolder.getInstance();
	}
	
	protected SpriteAttachMgr() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean load(String key) {
		// TODO Auto-generated method stub
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
	public boolean checkSpriteAttachActive(Player player, Hero hero, SpriteAttachCfg spriteAttachCfg){
		int requireLevel = spriteAttachCfg.getLevel();
		int requireQuality = spriteAttachCfg.getQuality();
		Map<Integer, Integer> preSpriteRequireMap = spriteAttachCfg.getPreSpriteRequireMap();
		Map<Integer, SpriteAttachItem> itemMap = getSpriteAttachHolder().getSpriteAttachItemMap(hero.getUUId());
		if(hero.getLevel() < requireLevel){
			return false;
		}
		RoleQualityCfg roleQualityCfg = RoleQualityCfgDAO.getInstance().getConfig(hero.getQualityId());
		
		if(roleQualityCfg.getQuality() < requireQuality){
			return false;
		}
		
		for (Iterator<Entry<Integer, Integer>> iterator = preSpriteRequireMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, Integer> entry = iterator.next();
			Integer spriteAttachId = entry.getKey();
			int spriteAttachRequireLevel = entry.getValue();
			SpriteAttachItem spriteAttachItem = itemMap.get(spriteAttachId);
			if(spriteAttachItem == null){
				GameLog.error("SpriteAttach", player.getUserId(), "附灵配置的前置灵蕴点找不到:" + spriteAttachId + ", hero modelId:" + hero.getModeId());
				continue;
			}
			if(spriteAttachItem.getLevel() < spriteAttachRequireLevel){
				return false;
			}
			
		}
		
		return true;
	}
}
