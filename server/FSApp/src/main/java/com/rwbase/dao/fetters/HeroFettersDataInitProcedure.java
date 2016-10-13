package com.rwbase.dao.fetters;

import com.rw.fsutil.concurrent.DataInitProcedure;

/**
 * @Author HC
 * @date 2016年10月12日 下午5:44:11
 * @desc
 **/

public class HeroFettersDataInitProcedure implements DataInitProcedure<String, HeroFettersData> {

	@Override
	public HeroFettersData firstInit(String key) {
		HeroFettersData heroFettersData = new HeroFettersData();
		return heroFettersData;
	}

	@Override
	public boolean hasChanged(String key, HeroFettersData value) {
		return false;
	}

	@Override
	public void update(String key, HeroFettersData value) {
	}
}