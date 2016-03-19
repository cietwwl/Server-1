package com.rwbase.dao.magicweapon;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.common.Pair;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.magicweapon.pojo.MagicExpCfg;

public class MagicExpCfgDAO extends CfgCsvDao<MagicExpCfg> {

	private static MagicExpCfgDAO instance = new MagicExpCfgDAO();
	
	//按照等级排序的数组，每个对里面第一个值是当前升级额外所需经验，第二个值是升满当前等级所需经验值
	private Pair<Integer, Integer> [] expLst;
	/**
	 * 每个对里面第一个值是当前升级额外所需经验，第二个值是升满当前等级所需经验值
	 * @param level
	 * @return
	 */
	public Pair<Integer, Integer> getExpLst(int level) {
		if (0<=level && level < expLst.length){
			return expLst[level];
		}
		else{
			return null;
		}
	}
	
	private List<MagicExpCfg> sortedCfg;
	
	/**
	 * 按等级排序号的配置
	 * @return
	 */
	public List<MagicExpCfg> getSortedCfg() {
		return sortedCfg;
	}
	
	private MagicExpCfgDAO(){}
	public static MagicExpCfgDAO getInstance(){
		return instance;
	}
	
	@Override
	public Map<String, MagicExpCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("MagicWeapon/MagicExp.csv", MagicExpCfg.class);
		int count = cfgCacheMap.size();
		int accumulator = 0;
		expLst = new Pair[count+1];
		expLst[0] =  Pair.Create(0,0);
		// TODO 假设关键字与等级报纸一致！
		for (int i = 1; i <= count; i++){
			MagicExpCfg cfg = cfgCacheMap.get(String.valueOf(i));
			if (cfg == null){
				GameLog.error("法宝", "配置错误", "MagicExp表缺少了等级："+i);
				continue;
			}
			int level = cfg.getLevel();
			if (i != level){
				GameLog.error("法宝", "配置错误", "MagicExp表的关键字应该与等级的值一样！等级值："+level);
			}
			int exp = cfg.getExp();
			accumulator += exp;
			Pair<Integer, Integer> pair = Pair.Create(exp, accumulator);
			expLst[i] = pair;
		}
		
		sortedCfg = MagicExpCfgDAO.getInstance().getAllCfg();
		Collections.sort(sortedCfg, magicExpComparator);

		return cfgCacheMap;
	}
	
	private static Comparator<MagicExpCfg> magicExpComparator = new Comparator<MagicExpCfg>() {
		public int compare(MagicExpCfg o1, MagicExpCfg o2) {
			int l1 = o1.getLevel();
			int l2 = o2.getLevel();
			return l1 - l2;
		}
	};
}
