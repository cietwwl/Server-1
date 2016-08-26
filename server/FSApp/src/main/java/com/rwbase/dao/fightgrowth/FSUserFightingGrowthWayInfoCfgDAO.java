package com.rwbase.dao.fightgrowth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fightgrowth.pojo.FSUserFightingGrowthWayInfoCfg;

public class FSUserFightingGrowthWayInfoCfgDAO extends CfgCsvDao<FSUserFightingGrowthWayInfoCfg> {

	private List<String> _displaySeq; // 战力来源的选项的显示顺序
	
	public static FSUserFightingGrowthWayInfoCfgDAO getInstance() {
		return SpringContextUtil.getBean(FSUserFightingGrowthWayInfoCfgDAO.class);
	}
	
	public List<String> getDisplaySeqRO() {
		return _displaySeq;
	}
	
	@Override
	protected Map<String, FSUserFightingGrowthWayInfoCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map("fightingGrowth/FightingGrowthWayInfo.csv", FSUserFightingGrowthWayInfoCfg.class);
		for (Iterator<FSUserFightingGrowthWayInfoCfg> itr = cfgCacheMap.values().iterator(); itr.hasNext();) {
			FSUserFightingGrowthWayInfoCfg cfg = itr.next();
			String[] args = cfg.getGrowthWay().split(",");
			List<Integer> growthWayList = new ArrayList<Integer>(args.length);
			for (int i = 0; i < args.length; i++) {
				growthWayList.add(Integer.parseInt(args[0]));
			}
			cfg.setGrowthWayList(growthWayList);
		}
		List<FSUserFightingGrowthWayInfoCfg> list = new ArrayList<FSUserFightingGrowthWayInfoCfg>(this.cfgCacheMap.values());
		Collections.sort(list);
		_displaySeq = new ArrayList<String>(list.size());
		for(int i = 0; i < list.size(); i++) {
			FSUserFightingGrowthWayInfoCfg cfg = list.get(i);
			_displaySeq.add(cfg.getKey());
		}
		this._displaySeq = Collections.unmodifiableList(this._displaySeq);
		return cfgCacheMap;
	}

}
