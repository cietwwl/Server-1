package com.rwbase.dao.praise;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.praise.db.PraiseData;

/**
 * @Author HC
 * @date 2016年10月13日 下午4:42:22
 * @desc 点赞的数据创建处理
 **/

public class PraiseCreator implements DataExtensionCreator<PraiseData> {

	@Override
	public PraiseData create(String key) {
		PraiseData data = new PraiseData(key);
		return data;
	}
}