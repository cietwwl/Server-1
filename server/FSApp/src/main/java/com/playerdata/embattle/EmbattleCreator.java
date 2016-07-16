package com.playerdata.embattle;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

/*
 * @author HC
 * @date 2016年7月15日 下午10:55:19
 * @Description 
 */
public class EmbattleCreator implements DataExtensionCreator<EmbattleInfo> {

	@Override
	public EmbattleInfo create(String key) {
		EmbattleInfo info = new EmbattleInfo();
		info.setUserId(key);
		return info;
	}
}