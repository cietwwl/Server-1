package com.rwbase.dao.magic;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;

/*
 * @author HC
 * @date 2015年10月15日 下午5:12:49
 * @Description 
 */
@Table(name = "magic")
@SynClass
public class Magic implements IMapItem {
	@Id
	private String id;// 角色Id
	private String magicId;// 法宝Id

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getMagicId() {
		return magicId;
	}

	public void setMagicId(String magicId) {
		this.magicId = magicId;
	}
}