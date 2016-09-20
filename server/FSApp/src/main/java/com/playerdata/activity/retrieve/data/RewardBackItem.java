package com.playerdata.activity.retrieve.data;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class RewardBackItem implements IMapItem{
	@Id
	private String id ;
	
	private String userId;
	
	@CombineSave
	private long lastSingleTime;//上一次触发隔日的登陆或5点刷新时间
	
	
	
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}

}
