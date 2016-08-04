package com.gm.task;

import java.util.HashMap;
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
import com.playerdata.FashionMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rwbase.common.NotifyChangeCallBack;
import com.rwbase.dao.fashion.FashionBeingUsed;
import com.rwbase.dao.fashion.FashionBeingUsedHolder;
import com.rwbase.dao.fashion.FashionCfg;
import com.rwbase.dao.fashion.FashionCommonCfg;
import com.rwbase.dao.fashion.FashionCommonCfgDao;
import com.rwbase.dao.fashion.FashionEffectCfg;
import com.rwbase.dao.fashion.FashionEffectCfgDao;
import com.rwbase.dao.fashion.FashionItem;
import com.rwbase.dao.fashion.FashionItemHolder;
import com.rwbase.dao.fashion.FashionUsedIF;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwproto.FishingServiceProtos.FishingData;

public class GmFindFishionSwingPetList implements IGmTask{

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
		NotifyChangeCallBack notifyProxy = new NotifyChangeCallBack();
		FashionItemHolder fashionItemHolder = new FashionItemHolder(player.getUserId(), notifyProxy);
		List<FashionItem> list = fashionItemHolder.getBroughtItemList();
		for(FashionItem fashionItem : list){
			Map<String, Object> map = new HashMap<String, Object>();			
			int type = fashionItem.getType();
			String name = "";
			int  isDress = 1;
			String remark = "";
			FashionEffectCfg fashionEffectCfg = FashionEffectCfgDao.getInstance().getCfgById(fashionItem.getFashionId()+"_"+player.getCareer());
			if(fashionEffectCfg==null){
				GameLog.error(LogModule.GmSender, player.getUserId(), "Gm指令查询用户时装-分职业出现了异常佣兵模板id =" + fashionItem.getFashionId(), null);
			}else{
				remark = fashionEffectCfg.getAttrData();
			}			
			FashionCommonCfg fashionCommonCfg = FashionCommonCfgDao.getInstance().getCfgById(fashionItem.getFashionId()+"");
			if(fashionCommonCfg==null){
				GameLog.error(LogModule.GmSender, player.getUserId(), "Gm指令查询用户时装-不分职业出现了异常佣兵模板id =" + fashionItem.getFashionId(), null);
			}else{
				name = fashionCommonCfg.getName();
			}
			FashionBeingUsedHolder holder = FashionBeingUsedHolder.getInstance();
			FashionBeingUsed fashUsing = holder.get(player.getUserId());
			FashionUsedIF fashionUsed = fashUsing;
			if (fashionUsed != null) {
				int wingId = fashionUsed.getWingId();
				int petId = fashionUsed.getPetId();
				int suitId = fashionUsed.getSuitId();
				if(type == 1){
					if(wingId == fashionItem.getFashionId()){
						isDress = 0;
					}
				}
				
				if(type == 2){
					if(suitId == fashionItem.getFashionId()){
						isDress = 0;
					}
				}
				
				if(type == 3){
					if(petId == fashionItem.getFashionId()){
						isDress = 0;
					}
				}				
			}			
			map.put("type",type);
			map.put("name",name);
			map.put("isDress",isDress);
			map.put("remark",remark);
			response.addResult(map);
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
