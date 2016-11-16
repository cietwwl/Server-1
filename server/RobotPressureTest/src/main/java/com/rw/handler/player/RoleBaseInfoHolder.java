package com.rw.handler.player;

import java.util.List;

import com.rw.Client;
import com.rw.actionHelper.ActionEnum;
import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.battle.army.RoleBaseInfo;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class RoleBaseInfoHolder {
	private SynDataListHolder<RoleBaseInfo> listHolder = new SynDataListHolder<RoleBaseInfo>(RoleBaseInfo.class);
	
	private RoleBaseInfo roleBaseInfo;
	
	public void syn(Client client, MsgDataSyn msgDataSyn){
		listHolder.Syn(msgDataSyn);
		List<RoleBaseInfo> itemList = listHolder.getItemList();
		for(RoleBaseInfo roleBase : itemList){
			try{
				Long.parseLong(roleBase.getId());
				roleBaseInfo = roleBase;
				break;
			}catch(Exception ex){
				
			}
		}
		if(null != roleBaseInfo){
			if(roleBaseInfo.getCareerType() <= 0 && roleBaseInfo.getLevel() > 10){
				client.getRateHelper().addActionToQueue(ActionEnum.SelectCareer);
			}
		}
	}

	public RoleBaseInfo getRoleBaseInfo() {
		return roleBaseInfo;
	}
}
