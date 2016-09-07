package com.rw.dataaccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreLoadType {

	private static List<DataKVType> preLoadList;

	static {
		ArrayList<DataKVType> preLoadList_ = new ArrayList<DataKVType>();
		preLoadList_.add(DataKVType.USER_GAME_DATA);
		preLoadList_.add(DataKVType.USER_HERO);
		preLoadList_.add(DataKVType.SIGN);
		preLoadList_.add(DataKVType.EMAIL);
		preLoadList_.add(DataKVType.GAMBLE);
		preLoadList_.add(DataKVType.STORE);
		preLoadList_.add(DataKVType.SEVEN_DAY_GIF);
		preLoadList_.add(DataKVType.BATTLE_TOWER);
		preLoadList_.add(DataKVType.PLOT_PROGRESS);
		preLoadList_.add(DataKVType.GUIDE_PROGRESS);
		preLoadList_.add(DataKVType.GROUP_SECRET_BASE);
		preLoadList_.add(DataKVType.USER_CHAT);
		preLoadList_.add(DataKVType.USER_GFIGHT_DATA);
		preLoadList_.add(DataKVType.USER_TEAMBATTLE_DATA);
		preLoadList = Collections.unmodifiableList(preLoadList_);
	}

	public static List<DataKVType> getPreLoadKVType() {
		return preLoadList;
	}
}
