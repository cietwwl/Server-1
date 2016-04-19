package com.rw.dataaccess.processor;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.anglearray.pojo.db.TableAngleArrayFloorData;

public class AngelArrayFloorProcessor implements PlayerCreatedProcessor<TableAngleArrayFloorData> {

	@Override
	public TableAngleArrayFloorData create(PlayerCreatedParam param) {
		// 创建万仙阵的层数据
		TableAngleArrayFloorData floorData = new TableAngleArrayFloorData(param.getUserId());
		return floorData;
	}

}
