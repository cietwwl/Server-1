package com.playerdata.activity.retrieve.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.playerdata.activity.limitHeroType.ActivityLimitHeroRankRecord;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class RewardBackCfgDAO extends CfgCsvDao<RewardBackCfg>{
	
	public static RewardBackCfgDAO getInstance(){
		return SpringContextUtil.getBean(RewardBackCfgDAO.class);
	}	

	@Override
	protected Map<String, RewardBackCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("rewardBack/RewardsBack.csv", RewardBackCfg.class);
		for (RewardBackCfg cfgTmp : cfgCacheMap.values()) {		
			parse(cfgTmp);
		}
		return cfgCacheMap;
	}
	
	/**将分段消耗的数据解析成从低到高的排序  区域1_区域1性价比   区域2_区域2性价比   区域3_区域3性价比   */
	private void parse(RewardBackCfg cfgTmp) {
		String str = cfgTmp.getNormalCost2();
		String perStr = cfgTmp.getPerfectCost2();
		if(!StringUtils.isBlank(str)){			
			List<CostOrder> costOrderList = new ArrayList<CostOrder>();
			costOrderList = subParse(str);			
			cfgTmp.setNormalCostList(costOrderList);
		}
		if(!StringUtils.isBlank(perStr)){
			List<CostOrder> costOrderList = new ArrayList<CostOrder>();
			costOrderList = subParse(perStr);			
			cfgTmp.setPerfectCostList(costOrderList);
		}		
	}

	private List<CostOrder> subParse(String str) {
		List<CostOrder> costOrderList = new ArrayList<CostOrder>();
		String[] strs = str.split(";");
		for(String cost : strs){
			String[] widthAndCost =cost.split(":");
			CostOrder order = new CostOrder();
			order.setWidth(Integer.parseInt(widthAndCost[0]));
			order.setCost(Float.parseFloat(widthAndCost[1]));
			costOrderList.add(order);
		}			
		reSort(costOrderList);
		return costOrderList;
	}	
	
	/**
	 * 降序排序，相同积分时先到先上
	 * 
	 * @param response
	 */
	private void reSort(List<CostOrder> list) {
		Collections.sort(list, new Comparator<CostOrder>() {
			@Override
			public int compare(CostOrder o1,CostOrder o2) {
				// TODO Auto-generated method stub
				if (o1.getWidth() > o2.getWidth()) {
					return 1;
				}  else {
					return -1;
				}
			}
		});
	}
	
	
}
