package com.rw.service.friend.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.playerdata.Player;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class RecommandCfgDAO extends CfgCsvDao<RecommandCfg> {

	private ArrayList<WeightEntry> list;

	public static RecommandCfgDAO getInstance(){
		return SpringContextUtil.getBean(RecommandCfgDAO.class);
	}
	
	@Override
	protected Map<String, RecommandCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("friend/recommand.csv", RecommandCfg.class);
		HashMap<Integer, WeightEntryBuilder> weightEntryMap = new HashMap<Integer, WeightEntryBuilder>();
		for (RecommandCfg recommandCfg : cfgCacheMap.values()) {
			int t = recommandCfg.getType();
			RecommandType type = RecommandType.getType(t);
			if (type == null) {
				throw new ExceptionInInitializerError("can not find friend recommand type:" + type);
			}
			PlusCalculator plusCalculator = type.getParser().parse(recommandCfg.getWeightSet());
			WeightEntryBuilder builder = weightEntryMap.get(t);
			if (builder == null) {
				builder = new WeightEntryBuilder(type.getExtractor());
				weightEntryMap.put(t, builder);
			}
			builder.add(Integer.parseInt(recommandCfg.getSource()), plusCalculator);
		}
		ArrayList<WeightEntry> list = new ArrayList<WeightEntry>(weightEntryMap.size());
		for (WeightEntryBuilder builder : weightEntryMap.values()) {
			list.add(builder.build());
		}
		this.list = list;
		return cfgCacheMap;
	}

	//这里可以把第一个参数的结果缓存起来
	public int getWeight(Player player, Player otherPlayer) {
		int total = 0;
		for (int i = list.size(); --i >= 0;) {
			total += list.get(i).getWeight(player, otherPlayer);
		}
		return total;
	}
}
