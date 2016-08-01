package com.gm.task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.TaoistMgr;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfg;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwproto.ItemBagProtos.EItemTypeDef;
import com.rwproto.TaoistMagicProtos.TaoistInfo;

public class GmFindDaoistList implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try{		
			String roleId = GmUtils.parseString(request.getArgs(), "roleId");
			Player player = getPlayer(roleId);
			if(player != null){
				setInfo(player,response);
				response.setStatus(0);
			}else{
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ROLE_NOT_FOUND.getStatus()));
			}
					
		}catch (Exception e){
			SocketHelper.processException(e, response);
		}		
		return response;
	}
	
	private void setInfo(Player player, GmResponse response) {
//		ItemBagMgr itemBagMgr = player.getItemBagMgr();
//		List<ItemData> itemMap = itemBagMgr.getItemListByType(EItemTypeDef.Magic);
//		ItemData itemMagic = player.getMagic();
//		for(ItemData item :itemMap){
//			Map<String, Object> map = new HashMap<String, Object>();
//			MagicCfg magicCfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(String.valueOf(item.getModelId()));
//			String magicName = "";
//			String magicLev = "";
//			String magicInfo = "";
//			int magicId = 0;
//			if(magicCfg==null){
//				GameLog.error(LogModule.GmSender, player.getUserId(), "Gm指令查询用户法宝出现了异常佣兵模板id =" + item.getModelId(), null);
//				}else{
//					magicName = magicCfg.getName();
//					magicLev = magicCfg.getQuality()+"";
//					magicInfo = magicCfg.getDescription();
//					magicId = magicCfg.getId();
//				}
//			map.put("equipName", magicName);
//			map.put("equipLev", magicLev);
//			
//			if(itemMagic.getModelId()==magicId){
//				map.put("isEquip", 0);
//			}else{
//				map.put("isEquip", 1);
//			}
//			map.put("equipStr", magicInfo);
//			response.addResult(map);
//		}
		Iterable<TaoistInfo> list = player.getTaoistMgr().getMagicList();
		Iterator<TaoistInfo> iterator = list.iterator();
		while(iterator.hasNext()){
			Map<String, Object> map = new HashMap<String, Object>();
			TaoistInfo info = iterator.next();
			String name = "";
			int level = info.getLevel();
			String remark = "";
			int type = 0;
			TaoistMagicCfg taoistMagicCfg = TaoistMagicCfgHelper.getInstance().getCfgById(info.getTaoistID()+"");
			if(taoistMagicCfg==null){
				GameLog.error(LogModule.GmSender, player.getUserId(), "Gm指令查询用户道术出现了异常佣兵模板id =" + info.getTaoistID(), null);
			}else{
//				name = taoistMagicCfg.get
				type = taoistMagicCfg.getTagNum();
				
				
			}
			
			
			
		}
		
	}


	private Player getPlayer(String roleId) {
		Player player = null;
		if(StringUtils.isNotBlank(roleId)){
			player = PlayerMgr.getInstance().find(roleId);
		}		
		return player;
	}	
}
