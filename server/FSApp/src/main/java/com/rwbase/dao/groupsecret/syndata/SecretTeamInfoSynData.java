package com.rwbase.dao.groupsecret.syndata;

import java.util.Map;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.groupsecret.syndata.base.DefendUserInfoSynData;

/*
 * @author HC
 * @date 2016年5月30日 下午2:41:03
 * @Description 
 */
@SynClass
public class SecretTeamInfoSynData {
	private final String id;// 秘境的Id
	private final Map<Integer, DefendUserInfoSynData> map;// 每个驻守点的信息
	private final int version;// 版本

	public SecretTeamInfoSynData(String id, Map<Integer, DefendUserInfoSynData> map, int version) {
		this.id = id;
		this.map = map;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	public Map<Integer, DefendUserInfoSynData> getMap() {
		return map;
	}
}