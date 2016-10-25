package com.playerdata.fightinggrowth.calc;

import java.util.List;

import com.rwbase.common.attribute.param.GemParam;
import com.rwbase.dao.fighting.GemFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.OneToOneTypeFightingCfg;
import com.rwbase.dao.item.GemCfgDAO;
import com.rwbase.dao.item.pojo.GemCfg;

/**
 * @Author HC
 * @date 2016年10月25日 上午11:33:06
 * @desc
 **/

public class FSGetGemFightingCalc implements IFightingCalc {

	private GemFightingCfgDAO gemFightingCfgDAO;
	private GemCfgDAO gemCfgDAO;

	protected FSGetGemFightingCalc() {
		gemFightingCfgDAO = GemFightingCfgDAO.getInstance();
		gemCfgDAO = GemCfgDAO.getInstance();
	}

	@Override
	public int calc(Object param) {
		GemParam gemParam = (GemParam) param;
		List<String> gemList = gemParam.getGemList();
		if (gemList == null || gemList.isEmpty()) {
			return 0;
		}

		int fighting = 0;

		for (int i = 0, size = gemList.size(); i < size; i++) {
			GemCfg gemCfg = gemCfgDAO.getCfgById(gemList.get(i));
			OneToOneTypeFightingCfg gemFightingCfg = gemFightingCfgDAO.getCfgById(String.valueOf(gemCfg.getGemLevel()));
			fighting += gemFightingCfg.getFighting();
		}

		return fighting;
	}
}