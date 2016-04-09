package com.gm.task;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.playerdata.Hero;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.ItemBagProtos.EItemTypeDef;

public class GmViewEquipments implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try {
			
			Map<String, Object> args = request.getArgs();
			String roleId = GmUtils.parseString(args, "roleId");
			int serverId = GmUtils.parseInt(args, "serverId");
			
			if (StringUtils.isBlank(roleId) || serverId <= 0) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}
			Player player = PlayerMgr.getInstance().find(roleId);
			if (player == null) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}
			List<Hero> heroList = player.getHeroMgr().getAllHeros(new Comparator<Hero>() {
				public int compare(Hero o1, Hero o2) {
					if (o1.getFighting() < o2.getFighting())
						return 1;
					if (o1.getFighting() > o2.getFighting())
						return -1;
					return 0;
				}
			});
			int count = 0;
			for (Hero hero : heroList) {
				List<EquipItem> equipList = hero.getEquipMgr().getEquipList();
				for (EquipItem equipItem : equipList) {
					Map<String, Object> map = new HashMap<String, Object>();
					int modelId = equipItem.getModelId();
					ItemBaseCfg cfg = ItemCfgHelper.GetConfig(modelId);
					map.put("itemId", equipItem.getModelId());
					map.put("itemName", cfg.getName());
					map.put("attrs", cfg.getDescription());
					map.put("uniCode", equipItem.getId());
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
