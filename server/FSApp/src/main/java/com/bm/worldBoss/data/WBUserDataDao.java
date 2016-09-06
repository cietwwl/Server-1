package com.bm.worldBoss.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.DataKVDao;


@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class WBUserDataDao extends DataKVDao<WBUserData>{
	
	private static WBUserDataDao instance  =  new WBUserDataDao();
	
//	 UserGameDataDao(){super();};
	
	public static WBUserDataDao getInstance(){
		return instance;
	}
}
