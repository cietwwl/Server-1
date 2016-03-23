package com.rwbase.dao.copy.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.log.GameLog;
import com.playerdata.Player;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.RandomUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.copy.pojo.ItemInfo;

public class ItemProbabilityCfgDAO extends CfgCsvDao<ItemProbabilityCfg>{

	public static ItemProbabilityCfgDAO getInstance() {
		return SpringContextUtil.getBean(ItemProbabilityCfgDAO.class);
	}
		
	@Override
	public Map<String, ItemProbabilityCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("copy/ItemProbability.csv",ItemProbabilityCfg.class);
		return cfgCacheMap; 
	}
	/**获取物品掉落列表
	 * @param player */
	public List<ItemInfo> getListItemInfo(String[] arrDropItem, Player player) 
	{	
		List<ItemInfo> list = new ArrayList<ItemInfo>();
		Map<Integer,ItemInfo> itemMap=new HashMap<Integer,ItemInfo>();
		ItemInfo itemInfo;
		for (int j = 0; j < arrDropItem.length; j++) 
		{
			DropItemCfg dropItem = (DropItemCfg) DropItemCfgDAO.getInstance().getCfgById(arrDropItem[j]);
			if (dropItem != null) 
			{
				int times = dropItem.getMax();
				for (int i = 0; i < times; i++) 
				{
					itemInfo = getItemInfo(dropItem,player);
					if(itemInfo != null&&itemInfo.getItemNum()>0)
					{
						ItemInfo tempItemInfo=itemMap.get(itemInfo.getItemID());
						if(tempItemInfo==null)
						{
							tempItemInfo=itemInfo;
							itemMap.put(tempItemInfo.getItemID(), tempItemInfo);
						}else
						{
							tempItemInfo.setItemNum(tempItemInfo.getItemNum()+itemInfo.getItemNum());
						}
						
					}
				}
				
				
			}
			else
			{
				GameLog.debug("ItemProbabilityDAO getTempProbList  DropItem 配置表错误 id 不存在:"+arrDropItem[j]);
			}
		}
		list.addAll(itemMap.values());
		return list;
	}
	
	public ItemInfo getItemInfo(DropItemCfg dropItem, Player player) 
	{
		List<Integer> list = new ArrayList<Integer>();
		list.add(dropItem.getNoItemProb());
		list.add(dropItem.getWhiteProb());
		list.add(dropItem.getGreenProb());
		list.add(dropItem.getBlueProb());
		list.add(dropItem.getPurpleProb());
		list.add(dropItem.getGoldProb());
		int index=RandomUtil.getRandonIndex(list);
		if (index==0) 
		{
			return null;
		}else{
			//获取掉落方案的全部物品列表
			int dropID=Integer.valueOf(dropItem.getId());
			List<ItemProbabilityCfg> listItemProbability = getItemsProbabilities(index, dropID);
			int size = listItemProbability.size();	
			if( size<= 0)
			{
				GameLog.debug("ItemProbabilityDAO getItemInfo selectedItemProbList 副本掉落方案为空："+dropID);
				return null;
			}else{
				List<Integer> listRandom = new ArrayList<Integer>();
				for (int i = 0; i < size; i++) 
				{
					listRandom.add(listItemProbability.get(i).getProbability());
				}
				int indexRandom=RandomUtil.getRandonIndex(listRandom);
				return getItem(listItemProbability.get(indexRandom));
			}
		}
	}
	/*
	 * 加载物品...
	 */
	private ItemInfo getItem(ItemProbabilityCfg selectedItemProb)
	{
		int itemNum = 0 ;
		ItemInfo tagItemInfo = new ItemInfo();
		int itemID = selectedItemProb.getItemid();
		if(selectedItemProb.getMax() == 0)	//如果最大掉落数量为0则为配置出错,不过此处依然将其作为数量返回...
		{
			GameLog.debug("ItemProbabilityDAO getItem ItemProbability 最大掉落数量为0:"+selectedItemProb.getItemid());
			itemNum = selectedItemProb.getNum();
		}
		else 
		{
			int tempi = new Random().nextInt(selectedItemProb.getMax());	//随机一个数量...
			int tempj = selectedItemProb.getNum();	//保底掉落数量...
			itemNum = tempi + tempj;	//最终数量以两者相加为准...
		}
		tagItemInfo.setItemID(itemID);
		tagItemInfo.setItemNum(itemNum);
		return tagItemInfo;
	}
	
	/*
	 * 根据所抽中的品质和掉落方案ID选出符合条件的物品信息，组成列表...
	 */
	private List<ItemProbabilityCfg> getItemsProbabilities(int quality, int formulaID) 
	{
		List<ItemProbabilityCfg> listItemProbability = ItemProbabilityCfgDAO.getInstance().getAllCfg();    //获取所有物品的掉落权重... 
		List<ItemProbabilityCfg> listResult = new ArrayList<ItemProbabilityCfg>();			 //获取所需要掉落的物品权重信息列表...
		
		for (ItemProbabilityCfg itemProbability : listItemProbability)
		{
			if (itemProbability.getItemsFormula() == formulaID && itemProbability.getQuality() == quality)
			{
				listResult.add(itemProbability);
			}	
		}
		
		return listResult;
	}
	
}
