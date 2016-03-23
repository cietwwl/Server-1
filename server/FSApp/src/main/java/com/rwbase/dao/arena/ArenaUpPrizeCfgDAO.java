package com.rwbase.dao.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.arena.pojo.ArenaUpPrizeCfg;
import com.rwbase.dao.arena.pojo.PrizeInfo;

public class ArenaUpPrizeCfgDAO extends CfgCsvDao<ArenaUpPrizeCfg> {

	public static ArenaUpPrizeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ArenaUpPrizeCfgDAO.class);
	}

	private HashMap<Integer, List<PrizeInfo>> prizeMap;

	@Override
	public Map<String, ArenaUpPrizeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arena/arenaUpPrize.csv", ArenaUpPrizeCfg.class);
		HashMap<Integer, List<PrizeInfo>> prizeMap_ = new HashMap<Integer, List<PrizeInfo>>();
		for (Object value : cfgCacheMap.values()) {
			ArenaUpPrizeCfg cfg = (ArenaUpPrizeCfg) value;
			int start = cfg.getStartRanking();
			int end = cfg.getEndRanking();
			ArrayList<PrizeInfo> list = new ArrayList<PrizeInfo>();
			String[] arrPrizes = cfg.getPrize().split(",");
			for (int i = 0; i < arrPrizes.length; i++) {
				String[] arrItem = arrPrizes[i].split("~");
				if (arrItem.length < 2)
					continue;
				int itemId = Integer.parseInt(arrItem[0]);
				float itemCount = Float.parseFloat(arrItem[1]);
				PrizeInfo info = new PrizeInfo();
				eSpecialItemId type = eSpecialItemId.getDef(itemId);
				if (type == null) {
					throw new RuntimeException("找不到eSpecialItemId：" + itemId);
				}
				info.setType(type);
				info.setCount(itemCount);
				list.add(info);
			}
			list.trimToSize();
			List<PrizeInfo> list_ = Collections.unmodifiableList(list);
			for (int i = start; i <= end; i++) {
				prizeMap_.put(i, list_);
			}
		}
		prizeMap = prizeMap_;
		return cfgCacheMap;
	}

	public List<PrizeInfo> getPrizeByRanking(int ranking) {
		if (this.prizeMap == null) {
			initJsonCfg();
		}
		return this.prizeMap.get(ranking);
	}
	
}
