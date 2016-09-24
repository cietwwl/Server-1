package com.playerdata.activity.retrieve.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class PerfectRewardsCfgDAO extends CfgCsvDao<PerfectRewardsCfg>{

	public static PerfectRewardsCfgDAO getInstance(){
		return SpringContextUtil.getBean(PerfectRewardsCfgDAO.class);
	}
	
	private HashMap<Integer, HashMap<Integer, PerfectRewardsCfg>> _levelCfgMapping = new HashMap<Integer, HashMap<Integer,PerfectRewardsCfg>>();
	
	
	@Override
	protected Map<String, PerfectRewardsCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("rewardBack/PerRewardsList.csv", PerfectRewardsCfg.class);
		HashMap<Integer, HashMap<Integer, PerfectRewardsCfg>> _levelCfgMapping = new HashMap<Integer, HashMap<Integer,PerfectRewardsCfg>>();
		
		for (PerfectRewardsCfg cfgTmp : cfgCacheMap.values()) {
			List<Integer> levelList = new ArrayList<Integer>();
			List<Integer> vipList = new ArrayList<Integer>();
//			System.out.println("~~~~~~~~~~~~~id =" + cfgTmp.getId());
			parse(cfgTmp,levelList,vipList);
			for(Integer level : levelList){
				for(Integer vip : vipList){						
					HashMap<Integer, PerfectRewardsCfg> _vipCfgMapping = _levelCfgMapping.get(level);
					if(_vipCfgMapping == null ||_vipCfgMapping.isEmpty()){
						_vipCfgMapping = new HashMap<Integer, PerfectRewardsCfg>();
						_levelCfgMapping.put(level, _vipCfgMapping);
					}
					_vipCfgMapping.put(vip, cfgTmp);					
				}
			}			
		}
		this._levelCfgMapping = _levelCfgMapping;
		return cfgCacheMap;
	}

	private void parse(PerfectRewardsCfg cfgTmp, List<Integer> levelList, List<Integer> vipList) {
		int[] intlevelarr = new int[2];		
		String[] levelarr = cfgTmp.getLevel().split("~");	
		subParse(levelList, intlevelarr, levelarr);		
		int[] intviparr = new int[2];
		String[] viparr = cfgTmp.getVip().split("~");
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

	public HashMap<Integer, HashMap<Integer, PerfectRewardsCfg>> get_levelCfgMapping() {
		return _levelCfgMapping;
	}
	
	
//	private void parse(PerfectRewardsCfg cfgTmp) {
//		String jubaoPerRewards = cfgTmp.getJubaoPerRewards();
//		HashMap<Integer, Integer> jubaoPerRewardsMap = new HashMap<Integer, Integer>();
//		setMap(jubaoPerRewardsMap,jubaoPerRewards);		
//		cfgTmp.setJubaoPerRewardsMap(jubaoPerRewardsMap);
//		
//		String lianqiPerRewards = cfgTmp.getLianqiPerRewards();
//		HashMap<Integer, Integer> lianqiPerRewardsMap = new HashMap<Integer, Integer>();
//		setMap(lianqiPerRewardsMap,lianqiPerRewards);		
//		cfgTmp.setJubaoPerRewardsMap(lianqiPerRewardsMap);
//		
//		String penglaiPerRewards = cfgTmp.getPenglaiPerRewards();
//		HashMap<Integer, Integer> penglaiPerRewardsMap = new HashMap<Integer, Integer>();
//		setMap(penglaiPerRewardsMap,penglaiPerRewards);		
//		cfgTmp.setJubaoPerRewardsMap(penglaiPerRewardsMap);
//		
//		String kunlunPerRewards = cfgTmp.getKunlunPerRewards();
//		HashMap<Integer, Integer> kunlunPerRewardsMap = new HashMap<Integer, Integer>();
//		setMap(kunlunPerRewardsMap,kunlunPerRewards);		
//		cfgTmp.setJubaoPerRewardsMap(kunlunPerRewardsMap);
//		
//		String wanxianPerRewards = cfgTmp.getWanxianPerRewards();
//		HashMap<Integer, Integer> wanxianPerRewardsMap = new HashMap<Integer, Integer>();
//		setMap(wanxianPerRewardsMap,wanxianPerRewards);		
//		cfgTmp.setJubaoPerRewardsMap(wanxianPerRewardsMap);
//		
//		String fengshenPerRewards = cfgTmp.getFengshenPerRewards();
//		HashMap<Integer, Integer> fengshenPerRewardsMap = new HashMap<Integer, Integer>();
//		setMap(fengshenPerRewardsMap,fengshenPerRewards);		
//		cfgTmp.setJubaoPerRewardsMap(fengshenPerRewardsMap);
//		
//		String qiankunPerRewards = cfgTmp.getQiankunPerRewards();
//		HashMap<Integer, Integer> qiankunPerRewardsMap = new HashMap<Integer, Integer>();
//		setMap(qiankunPerRewardsMap,qiankunPerRewards);		
//		cfgTmp.setJubaoPerRewardsMap(qiankunPerRewardsMap);
//		
//		String xinmo1PerRewards = cfgTmp.getXinmo1PerRewards();
//		HashMap<Integer, Integer> xinmo1PerRewardsMap = new HashMap<Integer, Integer>();
//		setMap(xinmo1PerRewardsMap,xinmo1PerRewards);		
//		cfgTmp.setJubaoPerRewardsMap(xinmo1PerRewardsMap);
//		
//		String xinmo2PerRewards = cfgTmp.getXinmo1PerRewards();
//		HashMap<Integer, Integer> xinmo2PerRewardsMap = new HashMap<Integer, Integer>();
//		setMap(xinmo2PerRewardsMap,xinmo2PerRewards);		
//		cfgTmp.setJubaoPerRewardsMap(xinmo2PerRewardsMap);
//		
//		String xinmo3PerRewards = cfgTmp.getXinmo1PerRewards();
//		HashMap<Integer, Integer> xinmo3PerRewardsMap = new HashMap<Integer, Integer>();
//		setMap(xinmo3PerRewardsMap,xinmo3PerRewards);		
//		cfgTmp.setJubaoPerRewardsMap(xinmo3PerRewardsMap);
//		
//		String xinmo4PerRewards = cfgTmp.getXinmo1PerRewards();
//		HashMap<Integer, Integer> xinmo4PerRewardsMap = new HashMap<Integer, Integer>();
//		setMap(xinmo4PerRewardsMap,xinmo4PerRewards);		
//		cfgTmp.setJubaoPerRewardsMap(xinmo4PerRewardsMap);
//		
//		String xinmo5PerRewards = cfgTmp.getXinmo1PerRewards();
//		HashMap<Integer, Integer> xinmo5PerRewardsMap = new HashMap<Integer, Integer>();
//		setMap(xinmo5PerRewardsMap,xinmo5PerRewards);		
//		cfgTmp.setJubaoPerRewardsMap(xinmo5PerRewardsMap);
//		
//		String xinmo6PerRewards = cfgTmp.getXinmo1PerRewards();
//		HashMap<Integer, Integer> xinmo6PerRewardsMap = new HashMap<Integer, Integer>();
//		setMap(xinmo6PerRewardsMap,xinmo6PerRewards);		
//		cfgTmp.setJubaoPerRewardsMap(xinmo6PerRewardsMap);	
//		
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
