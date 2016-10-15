package com.rwbase.dao.fetters;

import com.rw.fsutil.concurrent.DataInitProcedure;
import com.rw.fsutil.concurrent.DataInitProcedureFactory;

/**
 * @Author HC
 * @date 2016年10月12日 下午5:55:40
 * @desc
 **/

public class HeroFettersDataInitFactory implements DataInitProcedureFactory<String, HeroFettersData> {

	private static HeroFettersDataInitProcedure procedure = new HeroFettersDataInitProcedure();

	@Override
	public DataInitProcedure<String, HeroFettersData> getProcedure() {
		return procedure;
	}
}