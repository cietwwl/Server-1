package com.rw.dataaccess.processor;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.vip.pojo.TableVip;

public class VipProcessor implements PlayerCreatedProcessor<TableVip>{

	@Override
	public TableVip create(PlayerCreatedParam param) {
		TableVip tableVip = new TableVip();
		tableVip.setUserId(param.getUserId());
		return tableVip;
	}

}
