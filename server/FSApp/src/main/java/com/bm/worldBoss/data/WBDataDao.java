package com.bm.worldBoss.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.DataKVDao;


@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class WBDataDao extends DataKVDao<WBData>{
	
	private static WBDataDao instance  =  new WBDataDao();
	
//	 UserGameDataDao(){super();};
	
	public static WBDataDao getInstance(){
		return instance;
	}
}
