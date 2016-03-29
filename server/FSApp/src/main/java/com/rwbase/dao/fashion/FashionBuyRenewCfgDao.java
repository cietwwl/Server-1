package com.rwbase.dao.fashion;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwproto.FashionServiceProtos;

public class FashionBuyRenewCfgDao extends CfgCsvDao<FashionBuyRenewCfg> {
	public static FashionBuyRenewCfgDao getInstance() {
		return SpringContextUtil.getBean(FashionBuyRenewCfgDao.class);
	}

	/**
	 * Pair<Map<String, FashionBuyRenewCfg>,Map<String, FashionBuyRenewCfg>>
	 * (buyPlans,renewPlans)
	 */
	private Map<Integer,Pair<Map<String, FashionBuyRenewCfg>,Map<String, FashionBuyRenewCfg>>> buyRenewPlans;
	private FashionServiceProtos.FashionBuyRenewCfg configProto;
	
	public FashionServiceProtos.FashionBuyRenewCfg getConfigProto() {
		return configProto;
	}

	@Override
	public Map<String, FashionBuyRenewCfg> initJsonCfg() {
		
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fashion/FashionBuyRenewCfg.csv", FashionBuyRenewCfg.class);
		Collection<FashionBuyRenewCfg> values =  cfgCacheMap.values();
		buyRenewPlans = new HashMap<Integer, Pair<Map<String, FashionBuyRenewCfg>,Map<String, FashionBuyRenewCfg>>>();
		for (FashionBuyRenewCfg cfg : values) {
			cfg.ExtraInit();
			Map<String, FashionBuyRenewCfg> plan=null,buyPlans=null,renewPlans=null;
			Pair<Map<String, FashionBuyRenewCfg>,Map<String, FashionBuyRenewCfg>> pair = buyRenewPlans.get(cfg.getId());
			if (pair == null){
				buyPlans= new HashMap<String, FashionBuyRenewCfg>();
				renewPlans= new HashMap<String, FashionBuyRenewCfg>();
				pair = Pair.Create(buyPlans, renewPlans);
			}else{
				buyPlans = pair.getT1();
				renewPlans = pair.getT2();
			}
			switch (cfg.getPayType()) {
			case Buy:
				plan = buyPlans;
				break;
			case Renew:
				plan = renewPlans;
				break;
			default:
				GameLog.error("时装", "配置FashionBuyRenewCfg.csv", "未知类型:"+cfg.getPayType());
				break;
			}
			if (plan != null){
				plan.put(cfg.getKey(),cfg);
				buyRenewPlans.put(cfg.getId(), pair);
			}
		}
		
		initConfigProto();
		return cfgCacheMap;
	}

	private void initConfigProto() {
		FashionServiceProtos.FashionBuyRenewCfg.Builder configBuilder = FashionServiceProtos.FashionBuyRenewCfg.newBuilder();
		Set<Integer> keys = buyRenewPlans.keySet();
		for (Integer fashionKey : keys) {
			FashionServiceProtos.FashionBuyRenew.Builder oneFashionCfg = FashionServiceProtos.FashionBuyRenew.newBuilder();
			oneFashionCfg.setFashionId(fashionKey);
			Pair<Map<String, FashionBuyRenewCfg>,Map<String, FashionBuyRenewCfg>> pair = buyRenewPlans.get(fashionKey);
			Map<String, FashionBuyRenewCfg> buyPlans=null,renewPlans=null;
			buyPlans = pair.getT1();
			Set<String> planKeys = buyPlans.keySet();
			for (String planKey : planKeys) {
				FashionServiceProtos.PayCfg.Builder buyPlan=FashionServiceProtos.PayCfg.newBuilder();
				FashionBuyRenewCfg cfg = buyPlans.get(planKey);
				buyPlan.setDay(cfg.getDay());
				buyPlan.setPayment(cfg.getNum());
				buyPlan.setCoinType(cfg.getCoinType().getValue());
				buyPlan.setPlanId(cfg.getKey());
				oneFashionCfg.addBuyCfg(buyPlan);
			}
			
			renewPlans = pair.getT2();
			planKeys = renewPlans.keySet();
			for (String planKey : planKeys) {
				FashionServiceProtos.PayCfg.Builder renewPlan=FashionServiceProtos.PayCfg.newBuilder();
				FashionBuyRenewCfg cfg = renewPlans.get(planKey);
				renewPlan.setDay(cfg.getDay());
				renewPlan.setPayment(cfg.getNum());
				renewPlan.setCoinType(cfg.getCoinType().getValue());
				renewPlan.setPlanId(cfg.getKey());
				oneFashionCfg.addRenewPlan(renewPlan);
			}
			configBuilder.addBuyRenewList(oneFashionCfg);
		}
		configProto = configBuilder.build();
	}
	
	public FashionBuyRenewCfg getConfig(String id){
		FashionBuyRenewCfg cfg = (FashionBuyRenewCfg)getCfgById(id);
		return cfg;
	}
	
	public FashionBuyRenewCfg getConfig(int fashionID){
		FashionBuyRenewCfg cfg = (FashionBuyRenewCfg)getCfgById(String.valueOf(fashionID));
		return cfg;
	}
	
	public FashionBuyRenewCfg getBuyConfig(int renewFashionId, String planId) {
		Pair<Map<String, FashionBuyRenewCfg>,Map<String, FashionBuyRenewCfg>> pair = buyRenewPlans.get(renewFashionId);
		if (pair == null) return null;
		return pair.getT1().get(planId);
	}

	public FashionBuyRenewCfg getRenewConfig(int renewFashionId, String planId) {
		Pair<Map<String, FashionBuyRenewCfg>,Map<String, FashionBuyRenewCfg>> pair = buyRenewPlans.get(renewFashionId);
		if (pair == null) return null;
		return pair.getT2().get(planId);
	}
}
