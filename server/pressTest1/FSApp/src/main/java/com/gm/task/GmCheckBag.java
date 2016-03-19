package com.gm.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.SocketHelper;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.ItemBagProtos.EItemTypeDef;

public class GmCheckBag implements IGmTask {

	private final static Map<Integer, List<EItemTypeDef>> ConditionMap = new HashMap<Integer, List<EItemTypeDef>>();
	
	static{
		EItemTypeDef[] values = EItemTypeDef.values();
		List<EItemTypeDef> ALL = Arrays.asList(values);
		ConditionMap.put(0, ALL);
		List<EItemTypeDef> Equips = new ArrayList<EItemTypeDef>();
		Equips.add(EItemTypeDef.HeroEquip);
		Equips.add(EItemTypeDef.RoleEquip);
		ConditionMap.put(1, Equips);
		List<EItemTypeDef> Items = new ArrayList<EItemTypeDef>();
		Items.add(EItemTypeDef.Fashion);
		Items.add(EItemTypeDef.Piece);
		Items.add(EItemTypeDef.Magic);
		Items.add(EItemTypeDef.Magic_Piece);
		Items.add(EItemTypeDef.Gem);
		Items.add(EItemTypeDef.Consume);
		Items.add(EItemTypeDef.SoulStone);
		Items.add(EItemTypeDef.HeroItem);
		Items.add(EItemTypeDef.SpecialItem);
		ConditionMap.put(2, Items);
		
	}
	
	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		try {
			String roleId = (String) request.getArgs().get("roleId");
			int serverId = Integer.parseInt(request.getArgs().get("serverId").toString());
			int type = Integer.parseInt(request.getArgs().get("type").toString());

			if (StringUtils.isBlank(roleId) || serverId <= 0) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}
			Player player = PlayerMgr.getInstance().find(roleId);
			if (player == null) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}
			List<EItemTypeDef> list = ConditionMap.get(type);
			if (list == null) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}
			int count = 0;
			for (EItemTypeDef eType : list) {
				List<ItemData> items = player.getItemBagMgr().getItemListByType(eType);
				for (ItemData itemData : items) {
					int modelId = itemData.getModelId();
					ItemBaseCfg cfg = ItemCfgHelper.GetConfig(modelId);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("itemId", itemData.getModelId());
					map.put("itemName", cfg.getName());
					map.put("position", 0);
					map.put("uniCode", itemData.getId());
					map.put("amount", itemData.getCount());
					map.put("others", cfg.getDescription());
					
					response.addResult(map);
					count++;
				}
			}
			response.setStatus(0);
			response.setCount(count);

		} catch (Exception ex) {

			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
