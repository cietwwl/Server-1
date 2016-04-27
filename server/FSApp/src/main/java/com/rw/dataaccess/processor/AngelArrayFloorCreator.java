package com.rw.dataaccess.processor;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.anglearray.pojo.db.TableAngleArrayFloorData;
import com.rwbase.dao.user.User;

public class AngelArrayFloorCreator implements DataExtensionCreator<TableAngleArrayFloorData> {

	@Override
	public TableAngleArrayFloorData create(String userId) {
		// 创建万仙阵的层数据
		TableAngleArrayFloorData floorData = new TableAngleArrayFloorData(userId);
		return floorData;
	}

}