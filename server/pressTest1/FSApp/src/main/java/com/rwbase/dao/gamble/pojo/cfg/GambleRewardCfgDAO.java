package com.rwbase.dao.gamble.pojo.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.gamble.pojo.EGambleWeight;
import com.rwproto.GambleServiceProtos.EGambleType;

public class GambleRewardCfgDAO extends CfgCsvDao<GambleRewardCfg>{
	private static GambleRewardCfgDAO m_instance;
	public static GambleRewardCfgDAO getInstance(){
		if(m_instance == null) {
			m_instance = new GambleRewardCfgDAO();
		}
		return m_instance;
	}
	
	private EGambleType gambleTypes[] = new EGambleType[]{EGambleType.PRIMARY, EGambleType.MIDDLE, EGambleType.ADVANCED};
	private Map<EGambleWeight, Map<EGambleType, Map<Integer, List<GambleRewardCfg>>>> weightMap = new HashMap<EGambleWeight, Map<EGambleType,Map<Integer,List<GambleRewardCfg>>>>();
	private void init(){
		//author：lida 方便热加载改动一下这里的初始化
		//initJsonCfg();
		getAllCfg();
		
		Iterator<GambleRewardCfg> it = cfgCacheMap.values().iterator();
		while(it.hasNext()){
			GambleRewardCfg cfg = (GambleRewardCfg)it.next();
			EGambleWeight weightGroup = EGambleWeight.valueOf(cfg.getWeightGroup());
			if(!weightMap.containsKey(weightGroup)){
				weightMap.put(weightGroup, new HashMap<EGambleType, Map<Integer,List<GambleRewardCfg>>>());
			}
			for(EGambleType gambleType : gambleTypes){
				if(!weightMap.get(weightGroup).containsKey(gambleType)){
					weightMap.get(weightGroup).put(gambleType, new HashMap<Integer, List<GambleRewardCfg>>());
				}
				if(!weightMap.get(weightGroup).get(gambleType).containsKey(cfg.getOrder())){
					weightMap.get(weightGroup).get(gambleType).put(cfg.getOrder(), new ArrayList<GambleRewardCfg>());
				}
				if(cfg.hasGambleType(gambleType)){
					weightMap.get(weightGroup).get(gambleType).get(cfg.getOrder()).add(cfg);
				}
			}
		}
	}
	
	/**
	 * 根据垂钓类型与抽奖类型返回
	 * @param group
	 * @param type
	 * @return
	 */
	public List<GambleRewardCfg> getWeightGroup(EGambleWeight group, EGambleType type){
		return getWeightGroup(group, type, 0);
	}
	/**
	 * 根据垂钓类型与抽奖类型返回
	 * @param group
	 * @param type
	 * @return
	 */
	public  List<GambleRewardCfg> getWeightGroup(EGambleWeight group, EGambleType type, int count){
		synchronized (this) {
			if(weightMap.size() == 0){
				init();			
			}
		}
		if(weightMap.containsKey(group)){
			if(weightMap.get(group).containsKey(type)){
				if(weightMap.get(group).get(type).containsKey(count)){
					return weightMap.get(group).get(type).get(count);
				}
			}
		}
		return new ArrayList<GambleRewardCfg>();
	}

	@Override
	public Map<String, GambleRewardCfg> initJsonCfg() 
	{
		cfgCacheMap = CfgCsvHelper.readCsv2Map("gamble/gambleReward.csv",GambleRewardCfg.class);		
		return cfgCacheMap;
	}
}