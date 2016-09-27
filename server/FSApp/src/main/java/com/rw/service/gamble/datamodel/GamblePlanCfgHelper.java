package com.rw.service.gamble.datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.gamble.GambleLogicHelper;
import com.rwbase.common.config.CfgCsvHelper;
/*
<bean class="com.rw.service.gamble.datamodel.GamblePlanCfgHelper"  init-method="init" />
*/

public class GamblePlanCfgHelper extends CfgCsvDao<GamblePlanCfg> {
	public static GamblePlanCfgHelper getInstance() {
		return SpringContextUtil.getBean(GamblePlanCfgHelper.class);
	}

	private Map<Integer, List<GamblePlanCfg>> typeLevelMapping;
	private Map<Integer,Integer> maxHistoryCountMap;
	private Map<Integer,List<GamblePlanCfg>> guaranteeGroupMap;
	
	@Override
	public Map<String, GamblePlanCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("gamble/GamblePlanCfg.csv", GamblePlanCfg.class);
		typeLevelMapping = new HashMap<Integer, List<GamblePlanCfg>>();
		maxHistoryCountMap = new HashMap<Integer, Integer>();
		guaranteeGroupMap = new HashMap<Integer, List<GamblePlanCfg>>();
		
		Collection<GamblePlanCfg> vals = cfgCacheMap.values();
		for (GamblePlanCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
			List<GamblePlanCfg> lst = typeLevelMapping.get(cfg.getDropType());
			if (lst == null){
				lst = new ArrayList<GamblePlanCfg>();
				typeLevelMapping.put(cfg.getDropType(), lst);
			}else{
				GamblePlanCfg other = lst.get(0);//至少需要一个配置
				if (other.getMoneyNum() != cfg.getMoneyNum() || other.getMoneyType() != cfg.getMoneyType()){
					throw new RuntimeException("钓鱼台配置有错，相同类型的抽卡类型配置的货币类型或者金额必须一致！"+"关键字:"+other.getKey()+",另一个关键字"+cfg.getKey());
				}
			}
			lst.add(cfg);
			
			Integer old = maxHistoryCountMap.get(cfg.getDropType());
			if (old == null || old > cfg.getMaxCheckCount()){
				maxHistoryCountMap.put(cfg.getDropType(), cfg.getMaxCheckCount());
			}
			
			lst = guaranteeGroupMap.get(cfg.getGuaranteeGroupIndex());
			if (lst == null){
				lst = new ArrayList<GamblePlanCfg>();
				guaranteeGroupMap.put(cfg.getGuaranteeGroupIndex(), lst);
			}else{
				GamblePlanCfg other = lst.get(0);//至少需要一个配置
				if (!other.getGuaranteeCheckNum().equals(cfg.getGuaranteeCheckNum())
						|| other.getChargeExclusiveCount() != cfg.getChargeExclusiveCount()
						|| other.getFreeExclusiveCount() != cfg.getFreeExclusiveCount()){
					throw new RuntimeException("钓鱼台配置有错，相同保底次数分组的行，收费保底检索次数,唯一性检查次数（收费组和免费组）必须一致！"+"关键字:"+other.getKey()+",另一个关键字"+cfg.getKey());
				}
			}
			lst.add(cfg);
		}
		
		return cfgCacheMap;
	}
	
	@Override
	public void CheckConfig() {
		//TODO 检查配置：唯一性的存在可能的判断
		//TODO 跨表检查，group id 是否有效 物品ID是否有效
		Collection<GamblePlanCfg> vals = cfgCacheMap.values();
		for (GamblePlanCfg cfg : vals) {
			if (!GambleLogicHelper.isValidHeroOrItemId(String.valueOf(cfg.getGoods()))){
				throw new RuntimeException("无效道具ID:"+cfg.getGoods()+",key="+cfg.getKey());
			}
			//收集可能掉落的英雄或者物品ID
			IDropGambleItemPlan plan = cfg.getChargePlan();
			plan.ReInitPreviewData();
			if (cfg.getDropType() == 5){
				//如果下面三行抛异常，说明钻石十连抽配置的保底英雄有问题！
				int groupKey = cfg.obtainSpecialGuaranteeGroupId();
				GambleDropGroup group = GambleDropCfgHelper.getInstance().getGroup(groupKey);
				specialGuaranteeHero = group.getPlans()[0];
			}
		}
		
	}
	
	private String specialGuaranteeHero;
	
	public String getSpecialGuaranteeHero(){
		return specialGuaranteeHero;
	}

	public GamblePlanCfg getConfig(int dropType,int level){
		List<GamblePlanCfg> lst = typeLevelMapping.get(dropType);
		if (lst == null) {
			GameLog.error("钓鱼台", "dropType="+dropType, "找不到抽卡类型");
			return null;
		}
		
		for (GamblePlanCfg gamblePlanCfg : lst) {
			if (gamblePlanCfg.inLevelSegment(level)){
				return gamblePlanCfg;
			}
		}
		GameLog.error("钓鱼台", "dropType="+dropType, "找不到等级段,level="+level);
		return null;
	}
	
	public Iterable<GamblePlanCfg> getConfigList(int dropType){
		Iterable<GamblePlanCfg> lst = typeLevelMapping.get(dropType);
		return lst;
	}

	public int getMaxHistoryCount(int dropType) {
		return maxHistoryCountMap.get(dropType);
	}
	
	public List<GamblePlanCfg> getCfgOfSameGroup(GamblePlanCfg mainCfg){
		int groupIndex = mainCfg.getGuaranteeGroupIndex();
		List<GamblePlanCfg> lst = guaranteeGroupMap.get(groupIndex);
		return lst;
	}
}