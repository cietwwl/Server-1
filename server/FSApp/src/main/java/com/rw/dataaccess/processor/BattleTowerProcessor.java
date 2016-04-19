package com.rw.dataaccess.processor;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.battletower.pojo.db.TableBattleTower;

public class BattleTowerProcessor implements PlayerCreatedProcessor<TableBattleTower>{

	@Override
	public TableBattleTower create(PlayerCreatedParam param) {
		TableBattleTower tableBattleTower =  new TableBattleTower(param.getUserId());
		return tableBattleTower;
	}

}
