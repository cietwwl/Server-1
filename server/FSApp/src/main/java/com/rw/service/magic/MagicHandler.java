package com.rw.service.magic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.common.RandomSeqGenerator;
import com.common.RefInt;
import com.common.Weight;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.ItemBagMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.MagicMgr;
import com.playerdata.Player;
import com.rw.fsutil.common.Pair;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.item.ConsumeCfgDAO;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.pojo.ConsumeCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwbase.dao.item.pojo.itembase.INewItem;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.item.pojo.itembase.NewItem;
import com.rwbase.dao.item.pojo.itembase.UseItem;
import com.rwbase.dao.magicweapon.CriticalEnhanceCfgDAO;
import com.rwbase.dao.magicweapon.CriticalSeqCfgDAO;
import com.rwbase.dao.magicweapon.MagicExpCfgDAO;
import com.rwbase.dao.magicweapon.MagicSmeltCfgDAO;
import com.rwbase.dao.magicweapon.pojo.CriticalEnhanceCfg;
import com.rwbase.dao.magicweapon.pojo.CriticalSeqCfg;
import com.rwbase.dao.magicweapon.pojo.MagicExpCfg;
import com.rwbase.dao.magicweapon.pojo.MagicSmeltCfg;
import com.rwproto.ItemBagProtos.EItemAttributeType;
import com.rwproto.ItemBagProtos.EItemTypeDef;
import com.rwproto.MagicServiceProtos.MagicItemData;
import com.rwproto.MagicServiceProtos.MsgMagicRequest;
import com.rwproto.MagicServiceProtos.MsgMagicResponse;
import com.rwproto.MagicServiceProtos.MsgMagicResponse.Builder;
import com.rwproto.MagicServiceProtos.eMagicResultType;

public class MagicHandler {

	private static MagicHandler instance;
	/** 法宝等级对应的经验 */
	

	private MagicHandler() {
	}

	public static MagicHandler getInstance() {
		if (instance == null) {
			instance = new MagicHandler();
		}
		return instance;
	}

	public ByteString wearMagicWeapon(Player player, MsgMagicRequest msgMagicRequest) {
		MsgMagicResponse.Builder msgMagicResponse = MsgMagicResponse.newBuilder();
		msgMagicResponse.setMagicType(msgMagicRequest.getMagicType());
		String magicWeaponSlotId = msgMagicRequest.getId();

		if (!player.getMagicMgr().wearMagic(magicWeaponSlotId)) {
			return SetReturnResponse(msgMagicResponse,null);
		}

		msgMagicResponse.setEMagicResultType(eMagicResultType.SUCCESS);
		return msgMagicResponse.build().toByteString();
	}

	/**
	 * 强化法宝
	 * 
	 * @param player
	 * @param msgMagicRequest
	 * @return
	 */
	public ByteString forgeMagicWeapon(Player player, MsgMagicRequest msgMagicRequest) {
		MsgMagicResponse.Builder msgMagicResponse = MsgMagicResponse.newBuilder();
		msgMagicResponse.setMagicType(msgMagicRequest.getMagicType());
		//请求不论成败都重置伪随机数列的种子
		int oldSeed = RefreshSeed(player,msgMagicResponse);
		
		int state = msgMagicRequest.getState();

		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		ItemData itemData = itemBagMgr.findBySlotId(msgMagicRequest.getId());
		if (itemData == null) {
			return SetReturnResponse(msgMagicResponse,"找不到法宝！");
		}
		state = getItemMagicState(itemData);

		List<MagicItemData> list = msgMagicRequest.getMagicItemDataList();
		if (list.isEmpty()) {
			return SetReturnResponse(msgMagicResponse,"请选择强化材料！");
		}

		// 法宝模版
		MagicCfg magicCfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(String.valueOf(itemData.getModelId()));
		if (magicCfg == null) {
			return SetReturnResponse(msgMagicResponse,"找不到法宝配置");
		}

		// 看看有没有材料的资源Id
		String[] trainItemIdArr = magicCfg.getTrainItemId().split(",");
		if (trainItemIdArr.length <= 0) {
			return SetReturnResponse(msgMagicResponse,"这个法宝没有配置强化材料！");
		}

		List<MagicExpCfg> listCfg = MagicExpCfgDAO.getInstance().getSortedCfg();

		int oldLevel = Integer.parseInt(itemData.getExtendAttr(EItemAttributeType.Magic_Level_VALUE));// 旧等级
		int oldExp = Integer.parseInt(itemData.getExtendAttr(EItemAttributeType.Magic_Exp_VALUE));
		MagicExpCfg curExpCfg = listCfg.get(oldLevel - 1);

		if (oldLevel > player.getLevel()) {
			return SetReturnResponse(msgMagicResponse,"法宝等级超过了玩家等级！");
		} else if (oldLevel == player.getLevel()) {// 如果等级相同
			if (oldExp >= curExpCfg.getExp()) {
				return SetReturnResponse(msgMagicResponse,"法宝经验满了！");
			}
		}

		// 验证经验是否爆满（达到进阶等级，且经验在进阶等级满了）
		RefInt totalExp=new RefInt();
		RefInt maxUpLevel = new RefInt();
		final Integer fullExpObj = GetUpgradeExp(itemData,player.getLevel(),totalExp,maxUpLevel);
		if (fullExpObj == null){//经验爆满或者有其他错误，返回值为空，不能再吃强化材料了
			return SetReturnResponse(msgMagicResponse,"法宝经验满了，无法强化！");
		}
		final int fullExp = fullExpObj;
		
		int matListCount = list.size();
		MaterialInfos matInfos = new MaterialInfos();
		matInfos.StoreIDs=new String[matListCount];
		matInfos.modelIDs=new int[matListCount];
		matInfos.materialCounts = new int[matListCount];
		matInfos.unitExps = new int[matListCount];
		{
			int i = 0;
			for(MagicItemData item : list){
				String matStoreId = item.getId();
				matInfos.StoreIDs[i]=matStoreId;
				ItemData magicMaterial = itemBagMgr.findBySlotId(matStoreId);
				if (magicMaterial == null) {
					return SetReturnResponse(msgMagicResponse,"背包中找不到材料");
				}
				int matModelId = magicMaterial.getModelId();
				matInfos.modelIDs[i]=matModelId;
				matInfos.materialCounts[i]=magicMaterial.getCount();

				ConsumeCfg cfg = (ConsumeCfg) ConsumeCfgDAO.getInstance().getCfgById(String.valueOf(matModelId));
				if (cfg == null) {
					return SetReturnResponse(msgMagicResponse,"材料没有配置强化经验");
				}
				
				matInfos.unitExps[i]=cfg.getMagicForgeExp();
				++i;
			}
		}
		
		final CriticalIntList criticalEnhanceList;
		RefInt addedExpObj = new RefInt();
		if (msgMagicRequest.getAutoForge()){
			// 生成暴击方案，不考虑达到进阶条件
			criticalEnhanceList = GenerateEnhancePlan(oldSeed,list,matInfos,itemBagMgr,fullExp,addedExpObj);
		}else{
			//读取客户端的暴击方案
			criticalEnhanceList = new CriticalIntList();
			criticalEnhanceList.addedTimes = new int[matListCount];
			criticalEnhanceList.useCounts = new int[matListCount];

			int i = 0;
			boolean hasCritical = false;
			for (MagicItemData item : list) {
				int useItemCount = item.getCount();
				int useCount = useItemCount;
				int materialCount = matInfos.materialCounts[i];
				if (useCount > materialCount){
					return SetReturnResponse(msgMagicResponse,
							String.format("材料个数太大:,使用数量=%i,背包数量=%i",
									useItemCount,materialCount));
				}
				
				int magicForgeExp = matInfos.unitExps[i];
				addedExpObj.value += useCount * magicForgeExp;
				
				int criticalForgeType = item.getCriticalForgeType();
				if (criticalForgeType < 0){
					return SetReturnResponse(msgMagicResponse,"暴击个数无效！");
				}
				
				hasCritical = hasCritical | criticalForgeType > 0;
				criticalEnhanceList.addedTimes[i] = criticalForgeType;
				criticalEnhanceList.useCounts[i] = useItemCount;
				++i;
			}
			
			if (hasCritical){
				// 验证客户端暴击方案
				if (!planIsOk(criticalEnhanceList.addedTimes,oldSeed,list,matInfos,itemBagMgr,fullExp,addedExpObj)){
					return SetReturnResponse(msgMagicResponse,"暴击计算有误！");
				}
			}
		}
		
		// 要使用的物品
		List<IUseItem> useItemList = new ArrayList<IUseItem>();
		int addedExp=0;
		for(int k = 0; k<matListCount; k++){
			int addedTime = criticalEnhanceList.addedTimes[k];
			int useCount = Math.min(criticalEnhanceList.useCounts[k], matInfos.materialCounts[k]);
			int unitExp = matInfos.unitExps[k];
			String matId = matInfos.StoreIDs[k];
			int increasedExp = (useCount+addedTime) * unitExp;
			if (addedExp + increasedExp >= fullExp){
				//调整使用数量,avExp是考虑暴击数量的平均经验
				int avExp = (useCount+addedTime) * unitExp / useCount;
				int newCount = (fullExp - addedExp) / avExp ;
				if ((fullExp - addedExp) % avExp > 0){
					newCount++;
				}
				useCount = newCount;
				increasedExp = newCount * avExp;
			}
			addedExp += increasedExp;
			if (useCount >0){
				IUseItem useItem = new UseItem(matId, useCount);
				useItemList.add(useItem);
			}
			if (addedExp >= fullExp){
				addedExp = fullExp;//截断经验
				break;
			}
		}
		
		if(addedExp <= 0){
			return SetReturnResponse(msgMagicResponse,"无法增加法宝经验！");
		}
		
		int newTotalExp = totalExp.value + addedExp;
		int newExp = oldExp;
		int newLevel=oldLevel;
		boolean hasChanged=false;
		//一定要从最高可能等级开始倒着来检查！
		for (int k = maxUpLevel.value;k>=oldLevel;k--){
			final Pair<Integer, Integer> testlvlPair = MagicExpCfgDAO.getInstance().getExpLst(k);
			final Pair<Integer, Integer> lastlvlPair = MagicExpCfgDAO.getInstance().getExpLst(k-1);
			Integer lvlFullExp = testlvlPair.getT2();
			Integer lastLvlFullExp = lastlvlPair.getT2();
			if (lastLvlFullExp <= newTotalExp && newTotalExp <= lvlFullExp){
				newLevel = k;
				newExp = newTotalExp - lastLvlFullExp;
				hasChanged=true;
				break;
			}
		}
		
		if(!hasChanged){
			return SetReturnResponse(msgMagicResponse,"法宝增加经验无效！");
		}
		
		if (!itemBagMgr.useLikeBoxItem(useItemList, null)) {
			return SetReturnResponse(msgMagicResponse,"无法使用强化材料！");
		}

		itemData.setExtendAttr(EItemAttributeType.Magic_Exp_VALUE, String.valueOf(newExp));
		itemData.setExtendAttr(EItemAttributeType.Magic_Level_VALUE, String.valueOf(newLevel));
		
		{
			String storeTotal = itemData.getExtendAttr(EItemAttributeType.Magic_Total_Exp_VALUE);
			int storeTotalExp = newTotalExp;
			if (!StringUtils.isBlank(storeTotal)){
				try{
					storeTotalExp = Integer.parseInt(storeTotal);
					storeTotalExp += addedExp;
				}catch(Exception ex){//忽略错误！
				}
			}
			itemData.setExtendAttr(EItemAttributeType.Magic_Total_Exp_VALUE, String.valueOf(storeTotalExp));
		}
		
		if (state == 1) {
			player.getMagicMgr().updateMagic();
		}
		UserEventMgr.getInstance().StrengthenMagicVitality(player, newLevel);
		itemBagMgr.updateItem(itemData);
		List<ItemData> updateItems = new ArrayList<ItemData>(1);
		updateItems.add(itemData);
		itemBagMgr.syncItemData(updateItems);
		
		player.getFresherActivityMgr().doCheck(eActivityType.A_MagicLv);
		msgMagicResponse.setEMagicResultType(eMagicResultType.SUCCESS);
		return msgMagicResponse.build().toByteString();
	}

	private ByteString SetReturnResponse(MsgMagicResponse.Builder msgMagicResponse,String tips) {
		if (!StringUtils.isBlank(tips)) msgMagicResponse.setResultTip(tips);
		msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
		return msgMagicResponse.build().toByteString();
	}

	/**
	 * 剩余有多少值达到经验爆满，
	 * 经验爆满是如下条件：达到进阶等级（不超过玩家等级），且经验在进阶等级满了
	 * 经验爆满或者有其他错误，返回值为空，不能再吃强化材料了
	 * @param maxUpLevel 当返回值非空才有意义
	 * @param totalExp 当返回值非空才有意义
	 * @param itemData
	 * @return 剩余有多少值达到满经验
	 */
	private Integer GetUpgradeExp(ItemData item,int playerLevel, RefInt totalExp, RefInt maxUpLevel) {
		Integer result = null;
		do{
			String lvlStr = item.getExtendAttr(EItemAttributeType.Magic_Level_VALUE);
			int lvl = -1;
			try{
				lvl = Integer.parseInt(lvlStr);
				if (lvl<0) {
					//无法获取法宝等级！
					break;
				}
			}catch(Exception ex){
				//无法获取法宝等级！
				break;
			}
			
			int itemModelId = item.getModelId();
			MagicCfg magicCfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(String.valueOf(itemModelId));
			if (magicCfg == null){
				//无法获取法宝配置
				break;
			}

			int uplevel = magicCfg.getUplevel();
			if (uplevel <= 0){
				//这个法宝不能进阶
				uplevel = playerLevel;
			}
			
			if (uplevel >0 && lvl > uplevel){
				//数据异常：超过了法宝进阶等级！
				break;
			}
			
			// 检查经验是否已满
			String expStr = item.getExtendAttr(EItemAttributeType.Magic_Exp_VALUE);
			int curExp = -1;
			try{
				curExp = Integer.parseInt(expStr);
				if (curExp<0) {
					//无法获取法宝经验值！
					break;
				}
			}catch(Exception ex){
				//无法获取法宝经验值！
				break;
			}
			
			// 不能超过玩家等级
			maxUpLevel.value = Math.min(uplevel, playerLevel);
			final Pair<Integer, Integer> lvlUpPair = MagicExpCfgDAO.getInstance().getExpLst(maxUpLevel.value);
			if (lvlUpPair == null){
				//无法获取进阶对应的满经验值！
				break;
			}
			
			if (lvl>1){
				final Pair<Integer, Integer> lvlCurPair = MagicExpCfgDAO.getInstance().getExpLst(lvl-1);
				if (lvlCurPair == null){
					//无法获取法宝等级对应的满经验值！
					break;
				}
				curExp = curExp + lvlCurPair.getT2();
			}
			totalExp.value=curExp;
			
			final Integer fullExp = lvlUpPair.getT2();
			if (curExp >= fullExp){
				//法宝经验爆满！
				break;
			}
			
			// magic.level <= magic.upLevel and magic.exp < magic.upFullExp
			result = fullExp - curExp;
			break;
		}while(true);

		return result;
	}

	/**
	 * 验证客户端的暴击方案
	 * @param criticalEnhanceList 客户端的暴击方案
	 * @param list 客户端传递的材料列表
	 * @param matInfos 
	 * @param checkPlan 服务端生成的暴击方案
	 * @return 两者是否一致
	 */
	private boolean planIsOk(int[] criticalEnhanceList, int oldSeed, List<MagicItemData> list,
			MaterialInfos matInfos, ItemBagMgr itemBagMgr,int fullExp,RefInt accumulation) {
		final CriticalIntList checkPlan = GenerateEnhancePlan(oldSeed,list,matInfos,itemBagMgr,fullExp,accumulation);
		for(int i = 0; i< list.size(); i++){
			//客户端的暴击数可以比服务端生成的少！
			if (checkPlan.addedTimes[i] < criticalEnhanceList[i]){
				return false;
			}
		}
		return true;
	}

	/**
	 * 通过输入的种子构造暴击方案
	 * @param seed
	 * @param list
	 * @param matInfos 
	 * @param fullExp
	 * @return 返回数组的大小应该是跟list.size()一样
	 */
	private CriticalIntList GenerateEnhancePlan(int seed, List<MagicItemData> list,MaterialInfos matInfos, ItemBagMgr itemBagMgr,
			int fullExp,RefInt accumulation) {
		CriticalIntList result = new CriticalIntList();
		int lstSize = list.size();
		int[] addedTimes = new int[lstSize];
		int[] useCounts = new int[lstSize];
		result.addedTimes = addedTimes;
		result.useCounts=useCounts;
		
		int i=0;
		accumulation.value = 0;
		RefInt useCount = new RefInt();
		for (MagicItemData item : list) {
			int itemCount = Math.min(item.getCount(), matInfos.materialCounts[i]);
			addedTimes[i]=GenerateEnhancePlanForOneItem(seed,itemCount,itemBagMgr,fullExp,
					matInfos.unitExps[i],accumulation,useCount,matInfos.modelIDs[i]);
			useCounts[i]=useCount.value;
			i++;
		}
		return result;
	}
	
	private final static int SeqCtl1=19;
	private final static int SeqCtl2=23;
	private final static int deltaCtl = 31;
	
	/**
	 * 
	 * @param seed 非负数
	 * @param ctl 大于零的质数
	 * @param range 应该大于零
	 * @return
	 */
	private int GeneratePsudoRandomSeq(int seed,int ctl,int range){
		return (seed  * ctl+deltaCtl) % range;
	}
	
	static class CriticalIntList{
		int[] addedTimes;
		int[] useCounts;
	}
	
	static class MaterialInfos{
		String[] StoreIDs;
		int[] modelIDs;
		int[] materialCounts;
		int[] unitExps;
	}
	
	/**
	 * 无效配置会一律返回 0 ！
	 * 但是无效配置如果是在遍历中间编组的时候发生，则返回当前累计的暴击倍数
	 * 因为客户端不是一次性获得数量，而是一个一个来计算，为了兼容这种情况而没有清零
	 * @param seed
	 * @param itemCount
	 * @param 返回的useCount <= itemCount
	 * @param itemBagMgr
	 * @param fullExp 
	 * @param unitExp 
	 * @param accumulation 的增量 == unitExp * (result + useCount)
	 * 如果 accumulation >= fullExp（不能再用了）， accumulation - unitExp < fullExp
	 * 如果 accumulation < fullExp (用完了)， useCount == itemCount
	 * @return
	 */
	private int GenerateEnhancePlanForOneItem(int seed, int itemCount,ItemBagMgr itemBagMgr, 
			int fullExp, int unitExp, RefInt accumulation,RefInt useCount,int magicMaterialModelID){
		useCount.value = 0;
		int result = 0;
		if (accumulation.value >= fullExp){
			return result;
		}
		if (unitExp <= 0) return result;
		if (itemCount <= 0) return result;
		
		//find the config plan groups for the item
		final CriticalEnhanceCfg planCfg = (CriticalEnhanceCfg)CriticalEnhanceCfgDAO.getInstance().getCfgById(String.valueOf(magicMaterialModelID));
		if (planCfg == null) {
			return result;
		}
		
		final int[] planGroups = planCfg.getPlans();
		final int groupSize = planGroups.length;
		
		if (groupSize <= 0){
			return result;
		}
		
		int count = 0;
		int tmpseed = seed;
		int groupIndex=0;
		int startIndex=-1;
		
		RandomSeqGenerator gen = new RandomSeqGenerator(seed, planGroups, CriticalSeqCfgDAO.getInstance(), MagicMgr.SeedRange);
		
		do{
			//progress to next config sequence
			tmpseed = GeneratePsudoRandomSeq(tmpseed , SeqCtl1, MagicMgr.SeedRange);
			groupIndex = tmpseed % groupSize;
			final int planConfigKey = planGroups[groupIndex];
			final CriticalSeqCfg seqCfg = CriticalSeqCfgDAO.getInstance().getCfgById(String.valueOf(planConfigKey));
			if (seqCfg == null) {
				//不清零，因为客户端不是一次性获得数量，而是一个一个来计算，为了兼容这种情况这里不清零
				//result = 0;
				GameLog.error("法宝", "强化", "配置错误：CriticalSeq表缺少ID "+planConfigKey);
				return result;
			}
			
			final int[] seqList = seqCfg.getSeqList();
			final int seqSize = seqList.length;
			if (seqSize <= 0){
				//不清零，因为客户端不是一次性获得数量，而是一个一个来计算，为了兼容这种情况这里不清零
				//result = 0;
				GameLog.error("法宝", "强化", "配置错误：CriticalSeq表ID="+planConfigKey+" 对应的序列无效！");
				return result;
			}
			
			if (startIndex == -1) {//first round
				startIndex = GeneratePsudoRandomSeq(seed , SeqCtl2, seqSize);
			}else{
				startIndex = 0;
			}
			
			//start counting hittings
			for (int i = startIndex; i < seqSize; i++){
				if (count < itemCount){
					int testgen = gen.nextNum();
					if (seqList[i] != testgen){
						System.out.println("testgen="+testgen+",old="+seqList[i]);
					}
					int addedTimes = seqList[i] > 0 ? seqList[i] -1 : 0;
					int addedExp = (addedTimes+1)*unitExp;
					count++;
					result += addedTimes;
					accumulation.value += addedExp;
					useCount.value ++;
					if (accumulation.value >= fullExp){
						return result;
					}
				}else{
					break;
				}
			}
		}while(count < itemCount);
		
		return result;
	}

	/**
	 * 熔炼法宝
	 * @param player
	 * @param msgMagicRequest
	 * @return
	 */
	public ByteString smeltMagicWeapon(Player player, MsgMagicRequest msgMagicRequest) {
		MsgMagicResponse.Builder msgMagicResponse = MsgMagicResponse.newBuilder();
		msgMagicResponse.setMagicType(msgMagicRequest.getMagicType());
		List<MagicItemData> list = msgMagicRequest.getMagicItemDataList();

		int size = list.size();

		// 检查是否有重复的Id
		List<IUseItem> useItemList = new ArrayList<IUseItem>(list.size());// 使用的物品
		List<INewItem> newItemList = new ArrayList<INewItem>();// 新创建的物品

		int materialQuality = 0;
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		for (int i = 0; i < size; i++) {
			MagicItemData data = list.get(i);
			ItemData itemData = itemBagMgr.findBySlotId(data.getId());
			if (itemData == null) {
				return SetReturnResponse(msgMagicResponse,null);
			}

			if ("1".equalsIgnoreCase(itemData.getExtendAttr(EItemAttributeType.Magic_State_VALUE))) {
				return SetReturnResponse(msgMagicResponse,null);
			}

			MagicCfg cfg = ItemCfgHelper.getMagicCfg(itemData.getModelId());
			if (cfg == null) {
				return SetReturnResponse(msgMagicResponse,null);
			}

			int quality = cfg.getQuality();
			if (i == 0) {
				materialQuality = quality;
			} else if (materialQuality != quality) {// 品质不相同
				return SetReturnResponse(msgMagicResponse,null);
			}

			// 数量不能小于0
			if (data.getCount() <= 0) {
				return SetReturnResponse(msgMagicResponse,null);
			}

			// 超出拥有的数量
			if (data.getCount() > itemData.getCount()) {
				return SetReturnResponse(msgMagicResponse,null);
			}

			IUseItem useItem = new UseItem(data.getId(), data.getCount());
			useItemList.add(useItem);
		}

		int quality = materialQuality + 1;// 熔炼要产生新的法宝品质
		MagicSmeltCfg smeltCfg = (MagicSmeltCfg) MagicSmeltCfgDAO.getInstance().getCfgById(String.valueOf(quality));
		if (smeltCfg == null) {
			return SetReturnResponse(msgMagicResponse,null);
		}

		// 需求材料的个数
		if (size < smeltCfg.getNum() || size <= 0) {
			return SetReturnResponse(msgMagicResponse,null);
		}

		// cost money
		long playerCoin = player.getUserGameDataMgr().getCoin();
		int cost = smeltCfg.getCost();
		if (playerCoin < cost) {
			return SetReturnResponse(msgMagicResponse,null);
		}

		// 概率Map
		Map<Integer, Integer> proMap = new HashMap<Integer, Integer>();
		List<MagicCfg> listCfg = MagicCfgDAO.getInstance().getAllCfg();
		for (MagicCfg cfg : listCfg) {
			if (cfg.getQuality() == quality) {
				proMap.put(cfg.getId(), cfg.getSmeltperc());
			}
		}

		Weight<Integer> weightMap = new Weight<Integer>(proMap);// 权重Map
		Integer ranResult = weightMap.getRanResult();
		if (ranResult == null) {
			return SetReturnResponse(msgMagicResponse,null);
		}

		int resultId = ranResult.intValue();
		INewItem newItem = new NewItem(resultId, 1, null);
		newItemList.add(newItem);

		// 扣钱
		if (player.getUserGameDataMgr().addCoin(-cost) == -1) {
			return SetReturnResponse(msgMagicResponse,null);
		}

		// 消耗物品
		boolean success = itemBagMgr.useLikeBoxItem(useItemList, newItemList);
		if (!success) {
			return SetReturnResponse(msgMagicResponse,null);
		}

		msgMagicResponse.setNewMagicModelId(resultId);
		msgMagicResponse.setEMagicResultType(eMagicResultType.SUCCESS);
		return msgMagicResponse.build().toByteString();
	}

	/**
	 * 法宝进阶，成功则替换ModelID，不修改原来的状态（例如可以继续穿在身上）
	 * @param player
	 * @param msgMagicRequest
	 * @return
	 */
	public ByteString upgradeMagicWeapon(Player player, MsgMagicRequest msgMagicRequest) {
		MsgMagicResponse.Builder response = MsgMagicResponse.newBuilder();
		response.setMagicType(msgMagicRequest.getMagicType());
		
		do{// do-while-break 模拟goto
			final String magicStoreId = msgMagicRequest.getId();
			final ItemBagMgr bagMgr = player.getItemBagMgr();
			final ItemData item = bagMgr.findBySlotId(magicStoreId);
			if (item == null){
				fillResponseInfo(response,false, "找不到物品！");
				break;
			}
			
			if (EItemTypeDef.Magic != item.getType()){
				fillResponseInfo(response,false, "不是法宝，不能进阶！");
				break;
			}
			
			// 法宝配置
			final int itemModelId = item.getModelId();
			final MagicCfg magicCfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(String.valueOf(itemModelId));
			if (magicCfg == null) {
				fillResponseInfo(response,false, "无法找到法宝配置！");
				break;
			}
			
			//配置是否可进阶
			final String upgradeToModel = magicCfg.getUpMagic();
			final MagicCfg upToCfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(String.valueOf(upgradeToModel));
			if (upToCfg == null) {
				fillResponseInfo(response,false, "这个法宝不能进阶！");
				break;
			}
			
			// 检查等级
			final String lvlStr = item.getExtendAttr(EItemAttributeType.Magic_Level_VALUE);
			int lvl = -1;
			try{
				lvl = Integer.parseInt(lvlStr);
				if (lvl<0) {
					fillResponseInfo(response,false, "无法获取法宝等级！");
					break;
				}
			}catch(Exception ex){
				fillResponseInfo(response,false, "无法获取法宝等级！");
				break;
			}
			
			int uplevel = magicCfg.getUplevel();
			if (uplevel <= 0){
				fillResponseInfo(response,false, "这个法宝不能进阶！");
				break;
			}
			
			if (lvl < uplevel){
				fillResponseInfo(response,false, "法宝没有达到进阶等级！");
				break;
			}
			
			if (lvl > uplevel){
				fillResponseInfo(response,false, "数据异常，这个法宝不能进阶！");
				break;
			}
			
			if (player.getLevel() < uplevel){
				fillResponseInfo(response,false, "玩家没有达到法宝进阶等级！");
				break;
			}
			
			// 检查经验是否已满
			final String expStr = item.getExtendAttr(EItemAttributeType.Magic_Exp_VALUE);
			int curExp = -1;
			try{
				curExp = Integer.parseInt(expStr);
				if (curExp<0) {
					fillResponseInfo(response,false, "无法获取法宝经验值！");
					break;
				}
			}catch(Exception ex){
				fillResponseInfo(response,false, "无法获取法宝经验值！");
				break;
			}
			
			final Pair<Integer, Integer> lvlUpPair = MagicExpCfgDAO.getInstance().getExpLst(uplevel);
			final Integer upExp = lvlUpPair.getT1();
			if (curExp < upExp){
				fillResponseInfo(response,false, "法宝经验未满！");
				break;
			}

			//检查消耗的货币
			final int cost = magicCfg.getUpMagicCost();
			if (cost <= 0){
				fillResponseInfo(response,false, "法宝进阶配置的货币数量无效！");
				break;
			}
			
			final eSpecialItemId currencyType = eSpecialItemId.getDef(magicCfg.getUpMagicMoneyType());
			if (currencyType == null){
				fillResponseInfo(response,false, "法宝进阶配置的货币类型无效！");
				break;
			}
			
			//检查材料
			boolean enoughMat = true;
			final List<Pair<Integer, Integer>> lst = magicCfg.getUpgradeNeedGoodList();
			for (Pair<Integer, Integer> pair : lst) {
				if (bagMgr.getItemCountByModelId(pair.getT1()) < pair.getT2()){
					enoughMat = false;
					break;
				}
			}
			if (!enoughMat){
				fillResponseInfo(response,false, "法宝进阶材料不足！");
				break;
			}

			//扣金币和扣材料
			if (!player.getUserGameDataMgr().deductCurrency(currencyType, cost)) {
				fillResponseInfo(response,false, "货币不足！");
				break;
			}
			
			for (Pair<Integer, Integer> pair : lst) {
				final boolean useItemResult = bagMgr.useItemByCfgId(pair.getT1(), pair.getT2());
				if (!useItemResult){
					GameLog.error("法宝", "进阶", "扣除背包物品失败！物品ID："+pair.getT1());
				}
			}
			
			//换modelID
			item.setModelId(upToCfg.getId());
			
			//通知背包模块属性被更改
			final List<ItemData> updateItems = new ArrayList<ItemData>(1);
			updateItems.add(item);
			bagMgr.syncItemData(updateItems);
			
			int state = getItemMagicState(item);
			
			if (state == 1) {
				player.getMagicMgr().updateMagic();
			}

			response.setNewMagicModelId(upToCfg.getId());
			fillResponseInfo(response,true, "进阶成功！");
			break;
		}while(true);
		
		return response.build().toByteString();
	}

	private int getItemMagicState(final ItemData item) {
		int state = 0;//默认可以认为不是穿戴在身上的
		try{
			String stateStr=item.getExtendAttr(EItemAttributeType.Magic_State_VALUE);
			if (!StringUtils.isBlank(stateStr)){
				state = Integer.parseInt(stateStr);
			}
		}catch(Exception ex){
		}
		return state;
	}
	
	
	private void fillResponseInfo(Builder response, boolean isSuccess, String tip) {
		if (!StringUtils.isBlank(tip)) response.setResultTip(tip);
		response.setEMagicResultType(isSuccess ? eMagicResultType.SUCCESS : eMagicResultType.FAIL);
	}
	
	/**
	 * 生成新的随机数种子
	 * @param player
	 * @param msgMagicResponse
	 * @return 返回旧的随机数种子
	 */
	private int RefreshSeed(Player player, MsgMagicResponse.Builder msgMagicResponse) {
		MagicMgr magicMgr = player.getMagicMgr();
		int result = magicMgr.RefreshSeed();
		msgMagicResponse.setCriticalRamdom(magicMgr.getRandomSeed());
		return result;
	}

	public ByteString getRandomSeed(Player player, MsgMagicRequest msgMagicRequest) {
		MsgMagicResponse.Builder response = MsgMagicResponse.newBuilder();
		response.setMagicType(msgMagicRequest.getMagicType());
		
		//丢掉旧的种子
		RefreshSeed(player,response);
		fillResponseInfo(response,true, null);
		return response.build().toByteString();
	}


}