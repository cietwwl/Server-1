package com.gm.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.playerdata.ItemBagMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.ItemBagProtos.EItemTypeDef;

public class GmCheckBag implements IGmTask {

	private final static Map<Integer, List<EItemTypeDef>> ConditionMap = new HashMap<Integer, List<EItemTypeDef>>();

	private final static int TYPE_ALL = 0;
	private final static int TYEP_EQUIP = 1;
	private final static int TYPE_PIECE = 2;
	private final static int TYPE_GEM = 3;
	private final static int TYPE_CONSUME = 4;

	static {
		EItemTypeDef[] values = EItemTypeDef.values();
		List<EItemTypeDef> ALL = Arrays.asList(values);
		ConditionMap.put(TYPE_ALL, ALL);
		List<EItemTypeDef> Equips = new ArrayList<EItemTypeDef>();
		Equips.add(EItemTypeDef.HeroEquip);
		Equips.add(EItemTypeDef.RoleEquip);
		ConditionMap.put(TYEP_EQUIP, Equips);

		List<EItemTypeDef> pieces = new ArrayList<EItemTypeDef>();
		pieces.add(EItemTypeDef.Piece);
		ConditionMap.put(TYPE_PIECE, pieces);
		List<EItemTypeDef> gems = new ArrayList<EItemTypeDef>();
		gems.add(EItemTypeDef.Gem);
		ConditionMap.put(TYPE_GEM, gems);

		List<EItemTypeDef> consumes = new ArrayList<EItemTypeDef>();
		consumes.add(EItemTypeDef.Consume);
		consumes.add(EItemTypeDef.Fashion);
		consumes.add(EItemTypeDef.Magic);
		consumes.add(EItemTypeDef.SoulStone);
		consumes.add(EItemTypeDef.HeroItem);
		consumes.add(EItemTypeDef.SpecialItem);
		ConditionMap.put(TYPE_CONSUME, consumes);
	}

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		try {
			Map<String, Object> args = request.getArgs();
			String roleId = GmUtils.parseString(request.getArgs(), "roleId");
			int serverId = GmUtils.parseInt(args, "serverId");
			int type = GmUtils.parseInt(args, "type");

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
			ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
			for (EItemTypeDef eType : list) {
				List<ItemData> items = itemBagMgr.getItemListByType(player.getUserId(), eType);
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
