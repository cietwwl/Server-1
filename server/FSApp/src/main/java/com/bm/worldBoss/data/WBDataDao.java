package com.bm.worldBoss.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.gameworld.GameWorldObjDAO;
import com.rwbase.gameworld.GameWorldKey;


@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class WBDataDao {
	
	private static WBDataDao instance  =  new WBDataDao();
	
	final private GameWorldKey key = GameWorldKey.WORLD_BOSS;
	
//	 UserGameDataDao(){super();};
	
	public static WBDataDao getInstance(){
		return instance;
	}
	
	public WBData get(){
		
		final Class<WBData> clazz = WBData.class;
		WBData wbdata = GameWorldObjDAO.getInstance().get(key, clazz);
		return wbdata;
		
	}
	
	public boolean update(WBData wbData){
		return GameWorldObjDAO.getInstance().update(key, wbData);
	}
}
