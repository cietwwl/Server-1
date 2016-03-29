package com.rwbase.dao.giftcode.dao;

import com.rw.fsutil.cacheDao.DataRdbDao;
import com.rwbase.dao.giftcode.GiftCodeData;

/*
 * @author HC
 * @date 2016年3月18日 上午10:42:43
 * @Description 兑换码记录的DAO
 */
public class GiftCodeDataDAO extends DataRdbDao<GiftCodeData> {
	private static GiftCodeDataDAO dao = new GiftCodeDataDAO();

	public static GiftCodeDataDAO getDAO() {
		return dao;
	}

	public void addGiftCodeData(GiftCodeData data) {
		if (data == null) {
			return;
		}

		dao.saveOrUpdate(data);
	}
}