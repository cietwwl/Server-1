package com.rwbase.dao.fetters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.fetters.pojo.MagicEquipFetterRecord;
import com.rwbase.dao.fetters.pojo.SynMagicEquipFetterData;
import com.rwbase.dao.fetters.pojo.cfg.MagicEquipConditionCfg;
import com.rwbase.dao.fetters.pojo.cfg.dao.FetterMagicEquipCfgDao;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;


/**
 * 法宝神器羁绊holder
 * @author Alex
 *
 * 2016年7月18日 上午9:54:58
 */
public class MagicEquipFetterDataHolder {

	
	private static final eSynType syType = eSynType.MAGICEQUIP_FETTER;
	
	private AtomicInteger dataVersion = new AtomicInteger(0);
	
	private final String userID;

	
	
	public MagicEquipFetterDataHolder(String userID) {
		this.userID = userID;
		checkRecord();
	}
	

	/**
	 * 检查是否有记录，如果没有就新生成
	 */
	private MagicEquipFetterRecord checkRecord() {
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		if(item == null){
			item = new MagicEquipFetterRecord();
			item.setId(userID);
			item.setUserId(userID);
			getItemStore().addItem(item);
		}
		return item;
	}


	/**
	 * 同步所有法宝神器羁绊数据
	 * @param player
	 * @param version 版本
	 */
	public void synAllData(Player player, int version){
		if(version != 0 && version == dataVersion.get()){
			return;
		}
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		if(item == null){
			item = checkRecord();
			return;
		}
		if(item.isEmpty()){
			return;
		}
		
		SynMagicEquipFetterData synData = new SynMagicEquipFetterData(userID, item.getAllFetters());
				
		ClientDataSynMgr.synData(player, synData, syType, eSynOpType.UPDATE_SINGLE);
		
	}
	
	private MapItemStore<MagicEquipFetterRecord> getItemStore(){
		MapItemStoreCache<MagicEquipFetterRecord> itemStoreCache = MapItemStoreFactory.getMagicEquipFetterCache();
		return itemStoreCache.getMapItemStore(userID, MagicEquipFetterRecord.class);
	}
	
	
	/**
	 * 检查数据库内记录是否与当前集合一致，如果没有则进行添加
	 * @param curCfgs
	 * @param type TODO 类型，用于判断是法宝还是神器
	 * @param modelID TODO 英雄modelID
	 */
	public void compareRcord(Set<MagicEquipConditionCfg> curCfgs, int type, int modelID){
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		if(item == null){
			item = checkRecord();
		}
		
		Set<MagicEquipConditionCfg> combineRecord = new HashSet<MagicEquipConditionCfg>();
		Set<MagicEquipConditionCfg> remove = new HashSet<MagicEquipConditionCfg>();
		

		List<Integer> fetterIDs;
		if(type == FetterMagicEquipCfgDao.TYPE_FIXEQUIP){
			fetterIDs = item.getFixEquipFetters();
		}else{
			fetterIDs = item.getMagicFetters();
		}
		List<MagicEquipConditionCfg> sameType = new ArrayList<MagicEquipConditionCfg>();
		//先找出数据库里多出来的记录，判断是否要保留
		for (Integer id : fetterIDs) {
			MagicEquipConditionCfg cfg = FetterMagicEquipCfgDao.getInstance().getCfgById(String.valueOf(id));
			sameType.clear();
			boolean exist = false;
			for (MagicEquipConditionCfg fetter : curCfgs) {
				if(fetter.getUniqueId() == id){
					exist = true;
				}
				
				//记录一下相同类型的新集合
				if(fetter.getUniqueId() != id && cfg.getType() == fetter.getType() && cfg.getSubType() == fetter.getSubType()){
					sameType.add(fetter);
				}
			}
			
			if(!exist){
				//检查是否要保留
				if(cfg.recordOldData() && Integer.parseInt(cfg.getHeroModelID()) == modelID){
					//如果是要保留，则判断新集合内是否有相同类型记录，比较两个等级，保留最高
					boolean del = false;
					for (MagicEquipConditionCfg temp : sameType) {
						if(temp.getConditionLevel() <= cfg.getConditionLevel()){
							remove.add(temp);
						}else{
							del = true;
						}
					}
					if(!del){
						combineRecord.add(cfg);
					}
				}
			}
		}
		
		//添加入当前的羁绊
		combineRecord.addAll(curCfgs);
		//删除等级低的
		combineRecord.removeAll(remove);
		
		
		List<Integer> newList = new ArrayList<Integer>();
		for (MagicEquipConditionCfg m : combineRecord) {
			newList.add(m.getUniqueId());
		}

		if(type == FetterMagicEquipCfgDao.TYPE_FIXEQUIP){
			item.setFixEquipFetters(newList);
		}else if(type == FetterMagicEquipCfgDao.TYPE_MAGICWEAPON){
			item.setMagicFetters(newList);
		}
		getItemStore().updateItem(item);
	}
	
	
	
	
	public int getVersion(){
		return dataVersion.get();
	}


	/**
	 * 获取英雄的神器羁绊列表
	 * @param modelId
	 * @return
	 */
	public List<Integer> getFixEquipFetterByModelID(int modelId) {
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		List<Integer> fetterIDs = item.getFixEquipFetters();
		return Collections.unmodifiableList(fetterIDs);
	}


	/**
	 * 获取法宝的
	 * @return
	 */
	public List<Integer> getMagicFetters() {
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		List<Integer> fetterIDs = item.getMagicFetters();
		return Collections.unmodifiableList(fetterIDs);
	}
}
