package com.rw.service.item.checkWare;

import com.playerdata.ICheckItemWare;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.ItemBagProtos.EItemAttributeType;

public class CheckMagicWare implements ICheckItemWare{
	public boolean checkWare(ItemData item){
		return item.getExtendAttr(EItemAttributeType.Magic_State_VALUE).equals("1");
	}
}
