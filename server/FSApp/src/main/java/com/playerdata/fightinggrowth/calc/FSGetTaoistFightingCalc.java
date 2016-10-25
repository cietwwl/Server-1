package com.playerdata.fightinggrowth.calc;

import java.util.Map;
import java.util.Map.Entry;

import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfg;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper;
import com.rwbase.common.attribute.param.TaoistParam;
import com.rwbase.dao.fighting.TaoistFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.TaoistFightingCfg;

/**
 * @Author HC
 * @date 2016年10月25日 下午12:55:51
 * @desc 获取道术的战斗力
 **/

public class FSGetTaoistFightingCalc implements IFightingCalc {

	private TaoistMagicCfgHelper taoistMagicCfgHelper;
	private TaoistFightingCfgDAO taoistFightingCfgDAO;

	protected FSGetTaoistFightingCalc() {
		taoistMagicCfgHelper = TaoistMagicCfgHelper.getInstance();
		taoistFightingCfgDAO = TaoistFightingCfgDAO.getInstance();
	}

	@Override
	public int calc(Object param) {
		TaoistParam taoistParam = (TaoistParam) param;

		Map<Integer, Integer> taoistMap = taoistParam.getTaoistMap();
		if (taoistMap == null || taoistMap.isEmpty()) {
			return 0;
		}

		int fighting = 0;

		for (Entry<Integer, Integer> entry : taoistMap.entrySet()) {
			TaoistFightingCfg taoistFightingCfg = taoistFightingCfgDAO.getByLevel(entry.getValue());
			TaoistMagicCfg taoistMagicCfg = taoistMagicCfgHelper.getCfgById(String.valueOf(entry.getKey()));
			fighting += taoistFightingCfg.getFightingOfIndex(taoistMagicCfg.getTagNum());
		}

		return fighting;
	}
}