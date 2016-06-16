package com.bm.groupChamp.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GroupChampArmyItemHolder{
	
	final private eSynType synType = eSynType.GroupChampArmyData;
	

	public List<GroupChampArmyItem> getItemList(String champId)	
	{
		
		List<GroupChampArmyItem> itemList = new ArrayList<GroupChampArmyItem>();
		Enumeration<GroupChampArmyItem> mapEnum = getItemStore(champId).getEnum();
		while (mapEnum.hasMoreElements()) {
			GroupChampArmyItem item = (GroupChampArmyItem) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, GroupChampArmyItem item){
		item.incrVersion();
		getItemStore(item.getGroupId()).updateItem(item);
		
	}
	
	
	public GroupChampArmyItem getItem(String champId, String groupId){		
		List<GroupChampArmyItem> itemList = getItemList(champId);
		GroupChampArmyItem target = null;
		for (GroupChampArmyItem itemTmp : itemList) {
			if(StringUtils.equals(groupId, itemTmp.getGroupId()) ){
				target = itemTmp;
				break;
			}
		}
		return target;
	}	

	
	public void synSingle(Player player, String champId, String groupId, int version){
		
		GroupChampArmyItem item = getItem(champId,groupId);
		if(item!=null && item.getVersion() != version){
			ClientDataSynMgr.updateData(player, item , synType, eSynOpType.UPDATE_SINGLE);				
		}
		
	}
		

	
	private MapItemStore<GroupChampArmyItem> getItemStore(String ownerId) {
		MapItemStoreCache<GroupChampArmyItem> cache = MapItemStoreFactory.getGroupChampArmyItemCache();
		return cache.getMapItemStore(ownerId, GroupChampArmyItem.class);
	}
	
}
