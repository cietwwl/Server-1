package com.playerdata;

import com.playerdata.readonly.AttrMgrIF;
import com.rwbase.common.attrdata.RoleAttrData;

//public class AttrMgr extends IDataMgr implements AttrMgrIF {
//
//	private RoleAttrDataHolder roleAttrDataHolder;
//
//	public void init(Hero pRole) {
//		initPlayer(pRole);
//		roleAttrDataHolder = new RoleAttrDataHolder(m_pPlayer, pRole);
//
//	}
//
//	public RoleAttrData reCal() {
//		return roleAttrDataHolder.reCal();
//	}
//
//	public RoleAttrData getRoleAttrData() {
//		return roleAttrDataHolder.get();
//	}
//
//	public AttrData getTotalAttrData() {
//		return roleAttrDataHolder.get().getTotalData();
//	}
//
//	public void syncAllAttr(int version) {
//		roleAttrDataHolder.syn(version);
//
//	}
//
//	public List<TagAttriData> getAttrList() {
//		// 此方法一不支持，请自行修改，联系allen
//		List<TagAttriData> list = new ArrayList<TagAttriData>();
//		System.out.println("AttrMgr[getAttrList] 此方法已不支持，联系allen");
//		// for(eAttrIdDef eAttr:eAttrIdDef.values()){
//		// if(eAttr.getOrder() > eAttrIdDef.BATTLE_BEGIN.getOrder() && eAttr.getOrder() < eAttrIdDef.BATTLE_END.getOrder()){
//		// list.add(AttrToProbuff(eAttr).build());
//		// }
//		// }
//		return list;
//	}
//
//}

public interface AttrMgr extends AttrMgrIF {
	
	public RoleAttrData reCal();
}
