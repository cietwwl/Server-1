package com.rwbase.dao.targetSell;

import com.rw.fsutil.cacheDao.DataKVDao;


/**
 * 精准营销数据
 * @author Alex
 * 2016年9月17日 下午3:42:55
 */
public class BenefitDataDAO  extends DataKVDao<TargetSellRecord>{
	
	private static BenefitDataDAO dao = new BenefitDataDAO();
	
	protected BenefitDataDAO(){}
	
	public static BenefitDataDAO getDao(){
		return dao;
	}

}
