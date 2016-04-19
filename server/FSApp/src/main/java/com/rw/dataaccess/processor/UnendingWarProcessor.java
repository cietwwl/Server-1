package com.rw.dataaccess.processor;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.unendingwar.TableUnendingWar;

public class UnendingWarProcessor implements PlayerCreatedProcessor<TableUnendingWar> {

	@Override
	public TableUnendingWar create(PlayerCreatedParam param) {
		TableUnendingWar _table = new TableUnendingWar();
		_table.setUserId(param.getUserId());
		_table.setNum(0);
		return _table;
	}

}
