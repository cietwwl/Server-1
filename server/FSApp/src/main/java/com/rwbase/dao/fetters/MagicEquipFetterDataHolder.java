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
import com.sun.xml.internal.ws.message.saaj.SAAJHeader;


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
	 * @param version 版本 0表示强制同步
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
		
//		StringBuffer sb = new StringBuffer("同步羁绊数据：");
//		for (Integer id : item.getAllFetters()) {
//			sb.append("[").append(id).append("]");
//		}
//		System.out.println(sb.toString());
		SynMagicEquipFetterData synData = new SynMagicEquipFetterData(userID, item.getAllFetters());
				
		ClientDataSynMgr.synData(player, synData, syType, eSynOpType.UPDATE_SINGLE);
		
	}
	
	private MapItemStore<MagicEquipFetterRecord> getItemStore(){
		MapItemStoreCache<MagicEquipFetterRecord> itemStoreCache = MapItemStoreFactory.getMagicEquipFetterCache();
		return itemStoreCache.getMapItemStore(userID, MagicEquipFetterRecord.class);
	}
	
	/**
	 * 检查英雄羁绊数据
	 * @param fetter
	 * @param modelId
	 */
	public void checkFixEquipFetterRecord(MagicEquipConditionCfg fetter, int modelId) {
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		if(item == null){
			item = checkRecord();
		}
		List<Integer> fetterIDs = item.getFixEquipFetters();
		if(fetter == null){
			//降星到0,就要把目标英雄的神器羁绊去了
			List<Integer> temp = new ArrayList<Integer>();
			temp.addAll(fetterIDs);
			boolean remove = false;
			for (Integer id : temp) {
				MagicEquipConditionCfg cfg = FetterMagicEquipCfgDao.getInstance().getCfgById(String.valueOf(id));
				if(cfg.getHeroModelID() == modelId){
					remove = true;
					fetterIDs.remove(id);
					break;
				}
			}
			
			if(remove){
				item.setFixEquipFetters(fetterIDs);
				getItemStore().updateItem(item);
				dataVersion.incrementAndGet();
				
			}
			return;
		}
		



		List<Integer> clearOld = new ArrayList<Integer>();
		
		//检查数据库里有没有相同类型的旧数据
		for (Integer id : fetterIDs) {
			MagicEquipConditionCfg cfg = FetterMagicEquipCfgDao.getInstance().getCfgById(String.valueOf(id));
			if(id == fetter.getUniqueId()){
				//数据库已经有记录，就不做更新了
				return;
			}
			if(fetter.getUniqueId() != id && cfg.getType() == fetter.getType() && cfg.getSubType() == fetter.getSubType()){
				clearOld.add(id);
			}
		}
		
		fetterIDs.removeAll(clearOld);
		fetterIDs.add(fetter.getUniqueId());


		item.setFixEquipFetters(fetterIDs);
		getItemStore().updateItem(item);
		dataVersion.incrementAndGet();
		
	}
	
	private boolean checkSameElement(Set<MagicEquipConditionCfg> curCfgs, List<Integer> fetterIDs){
		boolean same = true;
		List<Integer> checkList = new ArrayList<Integer>(fetterIDs);
		for (MagicEquipConditionCfg cfg : curCfgs) {
			same = checkList.remove(new Integer(cfg.getUniqueId()));
			if(!same){
				break;
			}
		}
		
		if(!checkList.isEmpty()){
			same = false;
		}
		return same;
	}
	
	/**
	 * 检查数据库内法宝羁绊记录是否与当前集合一致，如果没有则进行添加
	 * @param curCfgs
	 * @param modelID TODO 英雄modelID
	 */
	public void compareMagicFetterRcord(Set<MagicEquipConditionCfg> curCfgs, int modelID){
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		if(item == null){
			item = checkRecord();
		}
		List<Integer> fetterIDs = item.getMagicFetters();
		//检查一下新数据与数据库保存的id是否一样,如果相同就直接返回,不做其他操作
		if(checkSameElement(curCfgs, fetterIDs)){
			return;
		}
		
		
		Set<MagicEquipConditionCfg> combineRecord = new HashSet<MagicEquipConditionCfg>();
		Set<MagicEquipConditionCfg> remove = new HashSet<MagicEquipConditionCfg>();
		

		List<MagicEquipConditionCfg> sameType = new ArrayList<MagicEquipConditionCfg>();
		//先找出数据库里多出来的记录，判断是否要保留
		for (Integer id : fetterIDs) {
			MagicEquipConditionCfg cfg = FetterMagicEquipCfgDao.getInstance().getCfgById(String.valueOf(id));
			sameType.clear();
			boolean exist = false;//新的集合里是否存在旧记录
			for (MagicEquipConditionCfg fetter : curCfgs) {
				if(fetter.getUniqueId() == id){
					exist = true;
				}else{
					//记录一下相同类型的新集合
					if(cfg.getType() == fetter.getType() && cfg.getSubType() == fetter.getSubType()){
						sameType.add(fetter);
					}
				}
				
			}
			
			if(!exist){
				//检查是否要保留   这里要判断一下英雄id，因为主角会转职
				if(cfg.recordOldData() && cfg.getHeroModelID() == modelID){
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

		
		item.setMagicFetters(newList);
		getItemStore().updateItem(item);
		dataVersion.incrementAndGet();
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
		List<Integer> temp = new ArrayList<Integer>();
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		List<Integer> fetterIDs = item.getFixEquipFetters();
		for (Integer id : fetterIDs) {
			MagicEquipConditionCfg cfg = FetterMagicEquipCfgDao.getInstance().getCfgById(String.valueOf(id));
			if(cfg.getHeroModelID() == modelId){
				temp.add(id);
			}
		}
		return temp;
	}


	/**
	 * 获取法宝的
	 * @return
	 */
	public List<Integer> getMagicFetters() {
		List<Integer> temp = new ArrayList<Integer>();
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		List<Integer> fetterIDs = item.getMagicFetters();
		temp.addAll(fetterIDs);
		return temp;
	}


	
}
