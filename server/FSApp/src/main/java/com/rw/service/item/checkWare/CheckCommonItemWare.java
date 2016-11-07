package com.rw.service.item.checkWare;

import com.playerdata.ICheckItemWare;
import com.rwbase.dao.item.pojo.ItemData;

public class CheckCommonItemWare implements ICheckItemWare{

	@Override
	public boolean checkWare(ItemData item) {
		return false;
	}

}
