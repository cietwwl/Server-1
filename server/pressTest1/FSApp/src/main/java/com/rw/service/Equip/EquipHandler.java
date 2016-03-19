package com.rw.service.Equip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.protobuf.ByteString;
import com.playerdata.EquipMgr;
import com.playerdata.Hero;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwbase.common.enu.ECareer;
import com.rwbase.common.enu.EHeroQuality;
import com.rwbase.dao.item.ComposeCfgDAO;
import com.rwbase.dao.item.pojo.ComposeCfg;
import com.rwbase.dao.item.pojo.HeroEquipCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwproto.EquipProtos.EquipEventType;
import com.rwproto.EquipProtos.EquipResponse;
import com.rwproto.EquipProtos.TagMate;
import com.rwproto.ErrorService.ErrorType;


public class EquipHandler {

	private static EquipHandler instance = new EquipHandler();

	public static EquipHandler getInstance() {
		return instance;
	}

	/**
	 * 进阶
	 * @param player 
	 * @param roleId
	 * @return
	 */
	public ByteString advance(Player player, String roleId) {
		EquipResponse.Builder response = EquipResponse.newBuilder();
		response.setEventType(EquipEventType.Advance);
		EquipMgr pEquipMgr = getEquipMgr(player, roleId);
		if(pEquipMgr == null){
			response.setError(ErrorType.NOT_ROLE);
			return response.build().toByteString();
		}
		boolean canUpgrade = pEquipMgr.getEquipCount() >= 6;
		if(canUpgrade){
			Hero role = player.getHeroMgr().getHeroById(roleId);		
			
			RoleQualityCfg pNextCfg = RoleQualityCfgDAO.getInstance().getNextConfig(role.getQualityId());
			if(pNextCfg != null){
				if(roleId.equals(player.getUserId()) && 
					player.getCareer() == ECareer.None.ordinal() && pNextCfg.getQuality() > EHeroQuality.Green.ordinal()){
					player.NotifyCommonMsg("没有职业不能进下一阶！");
				}else{
					pEquipMgr.EquipAdvance(pNextCfg.getId(),true);
					response.setError(ErrorType.SUCCESS);
				}
			}
		}else{
			response.setError(ErrorType.NOT_EQUIP_ADVANCE);
		}
		return response.build().toByteString();
	}
	
	
	/**
	 * 装备附灵
	 * @param player 
	 * @param roleId
	 * @param equipIndex
	 * @param mateList 
	 * @return
	 */
	public ByteString equipAttach(Player player, String roleId, int equipIndex, List<TagMate> mateList) {
		EquipResponse.Builder response = EquipResponse.newBuilder();
		response.setEventType(EquipEventType.Equip_Attach);
		EquipMgr pEquipMgr = getEquipMgr(player, roleId);
		if(pEquipMgr == null){
			response.setError(ErrorType.NOT_ROLE);
			return response.build().toByteString();
		}
		for (TagMate mate : mateList) {//循环遍历统计物品的总附灵经验
			ItemData itemData= player.getItemBagMgr().findBySlotId(mate.getId());
			if(itemData == null || itemData.getCount() < mate.getCount()){
				response.setError(ErrorType.NOT_ENOUGH_MATE);
				return response.build().toByteString();
			}
		}
		
		int result = pEquipMgr.EquipAttach(equipIndex,mateList);//增加装备的附灵经验
		switch (result) {
		case -1:
			response.setError(ErrorType.NOT_EQUIP);
			break;
		case -2:
			response.setError(ErrorType.NOT_ENOUGH_COIN);
			break;
		case 0:
			response.setError(ErrorType.SUCCESS);
			break;
		}
		
		return response.build().toByteString();
	}
	/**
	 * 装备一键附灵
	 * @param player 
	 * @param roleId
	 * @param equipIndex 
	 * @return
	 */
	public ByteString equipOnekeyAttach(Player player, String roleId, int equipIndex) {
		EquipResponse.Builder response = EquipResponse.newBuilder();
		response.setEventType(EquipEventType.Equip_OnekeyAttach);
		EquipMgr pEquipMgr = getEquipMgr(player, roleId);
		if(pEquipMgr == null){
			response.setError(ErrorType.NOT_ROLE);
			return response.build().toByteString();
		}
	
		int result = pEquipMgr.EquipOneKeyAttach(equipIndex);//一键附灵
		switch (result) {
		case -1:
			response.setError(ErrorType.NOT_EQUIP);
			break;
		case -2:
			response.setError(ErrorType.NOT_ENOUGH_GOLD);
			break;
		case 0:
			player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Hero_Strength, 1);
			response.setError(ErrorType.SUCCESS);
			break;
		default:
			break;
		}
		return response.build().toByteString();
	}
	
	/**
	 * 装备合成
	 * @param player 
	 * @param equipId
	 * @return
	 */
	public ByteString equipCompose(Player player, int equipId) {
		EquipResponse.Builder response = EquipResponse.newBuilder();
		response.setEventType(EquipEventType.Equip_Compose);
		List<Integer> ids = GetComposeIds(player, equipId);
		
		if(checkCompose(player,equipId) != 1){
			response.setError(ErrorType.NOT_ENOUGH_MATE);
			return response.build().toByteString();
		}
		
		int cost = 0;
		for (Integer id : ids) {
			ComposeCfg cfg = ComposeCfgDAO.getInstance().getCfg(id);
			cost += cfg.getCost();
		}
		if(cost > player.getUserGameDataMgr().getCoin()){
			response.setError(ErrorType.NOT_ENOUGH_COIN);
			return response.build().toByteString();
		}
		if(compose(player,equipId)){
			player.getUserGameDataMgr().addCoin(-cost);
			response.setError(ErrorType.SUCCESS);
		}
		return response.build().toByteString();
	}
	
	public static List<Integer> GetComposeIds(Player player,int id){
		ComposeCfg cfg = ComposeCfgDAO.getInstance().getCfg(id);
		if (cfg == null)return null;
		List<Integer> ids = new ArrayList<Integer>();
		HashMap<Integer,Integer> mate = ComposeCfgDAO.getInstance().getMate(id);
		Iterator<Entry<Integer,Integer>> iter = mate.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer,Integer> entry = iter.next();
			if(entry.getValue() > player.getItemBagMgr().getItemCountByModelId(entry.getKey())){
				List<Integer> nextIds = GetComposeIds(player,entry.getKey());
				if(nextIds != null){
					ids.addAll(nextIds);
				}
			}
		}
		ids.add(id);
		return ids;
	}
	   
	/**
	 * 检查物品是否可合成
	 * @param player
	 * @param id
	 * @return -1:底层物品不能合成；0：材料不足不能合成；1：可合成
	 */
	public static int checkCompose(Player player, int id){
		List<Integer> ids = GetComposeIds (player,id);
		if (ids == null) {
			return -1;
		}
		for (Integer nid : ids) {
			HashMap<Integer,Integer> mate = ComposeCfgDAO.getInstance().getMate(nid);
			Iterator<Entry<Integer,Integer>> iter = mate.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<Integer,Integer> entry = iter.next();
				if(entry.getValue() > player.getItemBagMgr().getItemCountByModelId(entry.getKey())){
					int canMateCompose = checkCompose(player, entry.getKey());
					if(canMateCompose != 1){
						return 0;
					}
				}
			}
		}
		return 1;
	}
	
	public boolean compose(Player player, int id){
		HashMap<Integer,Integer> mate = ComposeCfgDAO.getInstance().getMate(id);
		if(mate == null){
			return false;
		}
		Iterator<Entry<Integer,Integer>> iter = mate.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer,Integer> entry = iter.next();
			if(entry.getValue() > player.getItemBagMgr().getItemCountByModelId(entry.getKey())){
				compose(player,entry.getKey());
			}else{
				player.getItemBagMgr().useItemByCfgId(entry.getKey(), entry.getValue());
			}
		}
		player.getItemBagMgr().addItem(id, 1);
		return true;
	}
	
	/**
	 * 穿上装备
	 * @param player 
	 * @param roleId
	 * @param equipIndex
	 * @param bagSlot 
	 * @return
	 */
	public ByteString wearEquip(Player player, String roleId, int equipIndex) {
		EquipResponse.Builder response = EquipResponse.newBuilder();
		response.setEventType(EquipEventType.Wear_Equip);
		response.setEquipIndex(equipIndex);
		EquipMgr pEquipMgr = getEquipMgr(player, roleId);
		if(pEquipMgr == null){
			response.setError(ErrorType.NOT_ROLE);
			return response.build().toByteString();
		}
		Hero role = player.getHeroMgr().getHeroById(roleId);
		List<Integer> equips = RoleQualityCfgDAO.getInstance().getEquipList(role.getQualityId());	
		if(equips.size() == 0){	
			response.setError(ErrorType.FAIL);
			return response.build().toByteString();
		}
		int equipId = equips.get(equipIndex);
		int count = player.getItemBagMgr().getItemCountByModelId(equipId);
		if(count <= 0){
			response.setError(ErrorType.NOT_EQUIP);
			return response.build().toByteString();
		}
		HeroEquipCfg pHeroEquipCfg = ItemCfgHelper.getHeroEquipCfg(equipId);
		RoleType pRoleType = getRoleType(player, roleId);
		boolean isEnoughLevel = true;
		if(pRoleType == RoleType.Hero){
			Hero pHero = player.getHeroMgr().getHeroById(roleId);
			isEnoughLevel = pHeroEquipCfg.getLevel() <=  pHero.getHeroData().getLevel();
		}else{
			isEnoughLevel = pHeroEquipCfg.getLevel() <= player.getLevel();
		}
		if(!isEnoughLevel){
			response.setError(ErrorType.NOT_ENOUGH_LEVEL);
			return response.build().toByteString();
		}

		try {
			System.out.println("tt");
			if(pEquipMgr.WearEquip(equipIndex)){				
				response.setError(ErrorType.SUCCESS);
			}else{
				response.setError(ErrorType.FAIL);
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			response.setError(ErrorType.FAIL);
		}
		return response.build().toByteString();
	}
	private EquipMgr getEquipMgr(Player player, String roleId){

		if(player.getUserId().equals(roleId)){
			return player.getMainRoleHero().getEquipMgr();
		}
		Hero pHero = player.getHeroMgr().getHeroById(roleId);
		if(pHero!=null){
			return pHero.getEquipMgr();
		}
		return null;
	}

	private RoleType getRoleType(Player player, String roleId){
		if(player.getUserId().equals(roleId)){
			return RoleType.Player;
		}
		return RoleType.Hero;
	} 
	
}

enum RoleType
{
	Player ,//角色
	Hero ,//佣兵
};