package com.rw.dataaccess.processor;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.gamble.pojo.TableGamble;

public class GambleProcessor implements PlayerCreatedProcessor<TableGamble>{

	@Override
	public TableGamble create(PlayerCreatedParam param) {
		TableGamble tableGamble = new TableGamble();
		tableGamble.setUserId(param.getUserId());
		return tableGamble;
	}

}
