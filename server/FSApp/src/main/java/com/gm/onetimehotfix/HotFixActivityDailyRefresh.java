package com.gm.onetimehotfix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeCfgDAO;
import com.rw.fsutil.dao.cache.DataKVCache;
import com.rw.fsutil.dao.optimize.DataValueAction;
import com.rw.fsutil.util.DateUtils;
import com.rw.manager.GameManager;

public class HotFixActivityDailyRefresh implements Callable<Void>{

	@Override
	public Void call() throws Exception {
		ActivityDailyDiscountTypeCfgDAO.getInstance().reload();
		long TODAY_FIVE_MIL = DateUtils.getHour(System.currentTimeMillis(), 5);
		String serverId = GameManager.getServerId();
		BufferedReader reader = new BufferedReader(new FileReader(new File(getClass().getResource("").getPath() + serverId)));
		String temp;
		List<String> hotfixPlayerIdList = new ArrayList<String>();
		while((temp = reader.readLine())!=null){
			hotfixPlayerIdList.add(temp);
		}
		reader.close();
		hotfixPlayersActivity(hotfixPlayerIdList, new HotFixActivityActiion(TODAY_FIVE_MIL));
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void hotfixPlayersActivity(List<String> hotfixPlayerIdList, DataValueAction<Player> readerAction){
		Field instanceField;
		try {
			instanceField = PlayerMgr.class.getDeclaredField("cache");
			instanceField.setAccessible(true);
			DataKVCache<String, Player> cache = (DataKVCache<String, Player>)instanceField.get(PlayerMgr.getInstance());
			cache.rangeRead(hotfixPlayerIdList, readerAction);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
