package com.playerdata.charge.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.playerdata.charge.dao.ChargeInfoSubRecording;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwproto.ChargeServiceProto.ChargeCfgData;

public class ChargeCfgDao extends CfgCsvDao<ChargeCfg> {
	
	private List<ChargeCfgData> allCfgDatas;
	public static ChargeCfgDao getInstance() {
		return SpringContextUtil.getBean(ChargeCfgDao.class);
	}
	
	@Override
	public Map<String, ChargeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Charge/ChargeCfg.csv", ChargeCfg.class);

		for (Iterator<String> keyItr = cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			ChargeCfg cfg = cfgCacheMap.get(keyItr.next());
			cfg.setMoneyYuan(cfg.getMoneyCount() / 100);
		}
		
		List<ChargeCfg> cfgList = new ArrayList<ChargeCfg>(cfgCacheMap.values());
		Collections.sort(cfgList, new ChargeCfgComparator());
		List<ChargeCfgData> protoList = new ArrayList<ChargeCfgData>();
		for(int i = 0; i < cfgList.size(); i++) {
			ChargeCfg cfg = cfgList.get(i);
			ChargeCfgData.Builder builder = ChargeCfgData.newBuilder();
			builder.setId(cfg.getId());
			builder.setKey(cfg.getId());
			builder.setChargeType(Integer.parseInt(cfg.getChargeType().getCfgId()));
			builder.setSlotWithCard(cfg.getSlotWithCard());
			builder.setSlotWithoutCard(cfg.getSlotWithoutCard());
			builder.setIcon(cfg.getIcon());
			builder.setTitle(cfg.getTitle());
			builder.setProductName(cfg.getProductName());
			builder.setDesc(cfg.getDesc());
			builder.setProductDesc(cfg.getProductDesc());
			builder.setRecommend(cfg.getRecommend());
			builder.setGoldCount(cfg.getGoldCount());
			builder.setVipExp(cfg.getVipExp());
			builder.setMoneyCount(cfg.getMoneyCount());
			builder.setExtraGive(cfg.getExtraGive());
			builder.setGoldCount(cfg.getGoldCount());
			builder.setGiveCount(cfg.getGiveCount());
			builder.setDaysDraw(cfg.getDayDraw());
			builder.setExtraGift(String.valueOf(cfg.getExtraGiftId()));
			protoList.add(builder.build());
		}
		allCfgDatas = Collections.unmodifiableList(protoList);
		return cfgCacheMap;
	}
	
	public ChargeCfg getConfig(String cfgId){
		ChargeCfg cfg = getCfgById(cfgId);
		return cfg;
	}
	
	
	public ChargeInfoSubRecording newSubItem(String subItemId){		
		ChargeInfoSubRecording target = new ChargeInfoSubRecording();
		target.setId(subItemId);
		target.setCount(0);
		return target;
	}
	
	public List<ChargeCfgData> getAllCfgProtos() {
		return allCfgDatas;
	}
	

	
	
	private static class ChargeCfgComparator implements Comparator<ChargeCfg> {

		@Override
		public int compare(ChargeCfg o1, ChargeCfg o2) {
			if (o1.getChargeType() == ChargeTypeEnum.MonthCard) {
				return -1;
			}
			if (o2.getChargeType() == ChargeTypeEnum.MonthCard) {
				return 1;
			}
			if(o1.getChargeType() == ChargeTypeEnum.VipMonthCard) {
				return -1;
			}
			if(o2.getChargeType() == ChargeTypeEnum.VipMonthCard) {
				return 1;
			}
			return o1.getGoldCount() - o2.getGoldCount();
		}
		
	}
	
}
