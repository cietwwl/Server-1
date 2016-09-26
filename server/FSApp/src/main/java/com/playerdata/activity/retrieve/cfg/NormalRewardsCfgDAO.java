package com.playerdata.activity.retrieve.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class NormalRewardsCfgDAO extends CfgCsvDao<NormalRewardsCfg> {
	
	public static NormalRewardsCfgDAO getInstance(){
		return SpringContextUtil.getBean(NormalRewardsCfgDAO.class);
	}
	
//	private HashMap<Integer, NormalRewardsCfg> _vipCfgMapping = new HashMap<Integer, NormalRewardsCfg>();
	private HashMap<Integer, HashMap<Integer, NormalRewardsCfg>> _levelCfgMapping = new HashMap<Integer, HashMap<Integer,NormalRewardsCfg>>();
	
	
	
	@Override
	protected Map<String, NormalRewardsCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("rewardBack/NorRewardsList.csv", NormalRewardsCfg.class);
		HashMap<Integer, HashMap<Integer, NormalRewardsCfg>> _levelCfgMapping = new HashMap<Integer, HashMap<Integer,NormalRewardsCfg>>();
		
		for (NormalRewardsCfg cfgTmp : cfgCacheMap.values()) {
			List<Integer> levelList = new ArrayList<Integer>();
			List<Integer> vipList = new ArrayList<Integer>();
//			System.out.println("~~~~~~~~~~~~~id =" + cfgTmp.getId());
			parse(cfgTmp,levelList,vipList);
			for(Integer level : levelList){
				for(Integer vip : vipList){						
					HashMap<Integer, NormalRewardsCfg> _vipCfgMapping = _levelCfgMapping.get(level);
					if(_vipCfgMapping == null ||_vipCfgMapping.isEmpty()){
						_vipCfgMapping = new HashMap<Integer, NormalRewardsCfg>();
						_levelCfgMapping.put(level, _vipCfgMapping);
					}
					_vipCfgMapping.put(vip, cfgTmp);					
				}
			}			
		}
		this._levelCfgMapping = _levelCfgMapping;
		return cfgCacheMap;
	}
	
	/**
	 * 返回这个cfg涵盖的level和vip范畴
	 * @param cfgTmp
	 * @param levelList
	 * @param vipList
	 */
	private void parse(NormalRewardsCfg cfgTmp, List<Integer> levelList, List<Integer> vipList) {
		int[] intlevelarr = new int[2];		
		String[] levelarr = cfgTmp.getLevel().split("~");	
		subParse(levelList, intlevelarr, levelarr);		
		int[] intviparr = new int[2];
		String[] viparr = cfgTmp.getVIP().split("~");
		subParse(vipList, intviparr, viparr);
	}

	private void subParse(List<Integer> list,int[] intarr,String[] strarr){
		if(strarr.length>2 || strarr.length < 1){
			//配置表错误
			return;
		}
		for(int i = 0;i< intarr.length;i++){
			if(i >= strarr.length){
				//配置表是单列数据
				intarr[i] = intarr[i-1];
			}else{
				intarr[i] = Integer.parseInt(strarr[i]);
			}			
		}
		for(int i = intarr[0];i<=intarr[1];i++ ){
			list.add(i);
		}
	}

	public HashMap<Integer, HashMap<Integer, NormalRewardsCfg>> get_levelCfgMapping() {
		return _levelCfgMapping;
	}


	
	
	
//	private void parse(NormalRewardsCfg cfgTmp) {
//		String jubaoNorRewards = cfgTmp.getJubaoNorRewards();
//		HashMap<Integer, Integer> jubaoNorRewardsMap = new HashMap<Integer, Integer>();
//		setMap(jubaoNorRewardsMap,jubaoNorRewards);		
//		cfgTmp.setJubaoNorRewardsMap(jubaoNorRewardsMap);
//		
//		String lianqiNorRewards = cfgTmp.getLianqiNorRewards();
//		HashMap<Integer, Integer> lianqiNorRewardsMap = new HashMap<Integer, Integer>();
//		setMap(lianqiNorRewardsMap,lianqiNorRewards);		
//		cfgTmp.setJubaoNorRewardsMap(lianqiNorRewardsMap);
//		
//		String penglaiNorRewards = cfgTmp.getPenglaiNorRewards();
//		HashMap<Integer, Integer> penglaiNorRewardsMap = new HashMap<Integer, Integer>();
//		setMap(penglaiNorRewardsMap,penglaiNorRewards);		
//		cfgTmp.setJubaoNorRewardsMap(penglaiNorRewardsMap);
//		
//		String kunlunNorRewards = cfgTmp.getKunlunNorRewards();
//		HashMap<Integer, Integer> kunlunNorRewardsMap = new HashMap<Integer, Integer>();
//		setMap(kunlunNorRewardsMap,kunlunNorRewards);		
//		cfgTmp.setJubaoNorRewardsMap(kunlunNorRewardsMap);
//		
//		String wanxianNorRewards = cfgTmp.getWanxianNorRewards();
//		HashMap<Integer, Integer> wanxianNorRewardsMap = new HashMap<Integer, Integer>();
//		setMap(wanxianNorRewardsMap,wanxianNorRewards);		
//		cfgTmp.setJubaoNorRewardsMap(wanxianNorRewardsMap);
//		
//		String fengshenNorRewards = cfgTmp.getFengshenNorRewards();
//		HashMap<Integer, Integer> fengshenNorRewardsMap = new HashMap<Integer, Integer>();
//		setMap(fengshenNorRewardsMap,fengshenNorRewards);		
//		cfgTmp.setJubaoNorRewardsMap(fengshenNorRewardsMap);
//		
//		String qiankunNorRewards = cfgTmp.getQiankunNorRewards();
//		HashMap<Integer, Integer> qiankunNorRewardsMap = new HashMap<Integer, Integer>();
//		setMap(qiankunNorRewardsMap,qiankunNorRewards);		
//		cfgTmp.setJubaoNorRewardsMap(qiankunNorRewardsMap);
//		
//		String xinmo1NorRewards = cfgTmp.getXinmo1NorRewards();
//		HashMap<Integer, Integer> xinmo1NorRewardsMap = new HashMap<Integer, Integer>();
//		setMap(xinmo1NorRewardsMap,xinmo1NorRewards);		
//		cfgTmp.setJubaoNorRewardsMap(xinmo1NorRewardsMap);
//		
//		String xinmo2NorRewards = cfgTmp.getXinmo1NorRewards();
//		HashMap<Integer, Integer> xinmo2NorRewardsMap = new HashMap<Integer, Integer>();
//		setMap(xinmo2NorRewardsMap,xinmo2NorRewards);		
//		cfgTmp.setJubaoNorRewardsMap(xinmo2NorRewardsMap);
//		
//		String xinmo3NorRewards = cfgTmp.getXinmo1NorRewards();
//		HashMap<Integer, Integer> xinmo3NorRewardsMap = new HashMap<Integer, Integer>();
//		setMap(xinmo3NorRewardsMap,xinmo3NorRewards);		
//		cfgTmp.setJubaoNorRewardsMap(xinmo3NorRewardsMap);
//		
//		String xinmo4NorRewards = cfgTmp.getXinmo1NorRewards();
//		HashMap<Integer, Integer> xinmo4NorRewardsMap = new HashMap<Integer, Integer>();
//		setMap(xinmo4NorRewardsMap,xinmo4NorRewards);		
//		cfgTmp.setJubaoNorRewardsMap(xinmo4NorRewardsMap);
//		
//		String xinmo5NorRewards = cfgTmp.getXinmo1NorRewards();
//		HashMap<Integer, Integer> xinmo5NorRewardsMap = new HashMap<Integer, Integer>();
//		setMap(xinmo5NorRewardsMap,xinmo5NorRewards);		
//		cfgTmp.setJubaoNorRewardsMap(xinmo5NorRewardsMap);
//		
//		String xinmo6NorRewards = cfgTmp.getXinmo1NorRewards();
//		HashMap<Integer, Integer> xinmo6NorRewardsMap = new HashMap<Integer, Integer>();
//		setMap(xinmo6NorRewardsMap,xinmo6NorRewards);		
//		cfgTmp.setJubaoNorRewardsMap(xinmo6NorRewardsMap);		
//	}
//
//	private void setMap(HashMap<Integer, Integer> Map, String Rewards) {
//		String[] Reward = Rewards.split(";");
//		for (String idAndCount : Reward) {
//			String[] temp = idAndCount.split(":");
//			String id = temp[0];
//			String count = temp[1];
//			Map.put(Integer.parseInt(id), Integer.parseInt(count));
//		}		
//	}
}
