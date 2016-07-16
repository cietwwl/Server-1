package com.playerdata.embattle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;

/*
 * @author HC
 * @date 2016年7月14日 下午6:39:18
 * @Description 
 */
@SynClass
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class EmbattleInfo implements IMapItem {
	@IgnoreSynField
	@Id
	private String userId;// 角色的Id
	private Map<Integer, List<EmbattlePositionInfo>> map;

	public EmbattleInfo() {
		map = new HashMap<Integer, List<EmbattlePositionInfo>>();
	}

	public EmbattleInfo(String userId) {
		this();
		this.userId = userId;
	}

	/**
	 * 更新或者增加阵容信息
	 * 
	 * @param key
	 * @param updateInfo
	 */
	public void updateOrAddEmbattleInfo(int type, String key, List<EmbattleHeroPosition> updateInfo) {
		if (map == null) {
			return;
		}

		List<EmbattlePositionInfo> posInfo = map.get(type);
		if (posInfo == null) {
			posInfo = new ArrayList<EmbattlePositionInfo>();
			map.put(type, posInfo);
		}

		boolean hasValue = false;
		for (int i = 0, size = posInfo.size(); i < size; i++) {
			EmbattlePositionInfo embattlePositionInfo = posInfo.get(i);
			if (embattlePositionInfo.getKey().equals(key)) {
				embattlePositionInfo.setPos(updateInfo);

				hasValue = true;
				break;
			}
		}

		if (!hasValue) {
			posInfo.add(new EmbattlePositionInfo(key, updateInfo));
		}
	}

	/**
	 * 删除某个阵容信息
	 * 
	 * @param key
	 */
	public void removeEmbattleInfo(int type, String key) {
		if (map == null || map.isEmpty()) {
			return;
		}

		List<EmbattlePositionInfo> posInfo = map.get(type);
		if (posInfo == null || posInfo.isEmpty()) {
			return;
		}

		Iterator<EmbattlePositionInfo> itr = posInfo.iterator();
		while (itr.hasNext()) {
			EmbattlePositionInfo next = itr.next();
			if (key.equals(next.getKey())) {
				itr.remove();
			}
		}
	}

	/**
	 * 获取阵容信息
	 * 
	 * @param key
	 * @return
	 */
	public EmbattlePositionInfo getEmbattlePositionInfo(int type, String key) {
		if (map == null || map.isEmpty()) {
			return null;
		}

		List<EmbattlePositionInfo> posInfo = map.get(type);
		if (posInfo == null || posInfo.isEmpty()) {
			return null;
		}

		Iterator<EmbattlePositionInfo> itr = posInfo.iterator();
		while (itr.hasNext()) {
			EmbattlePositionInfo next = itr.next();
			if (key.equals(next.getKey())) {
				return next;
			}
		}

		return null;
	}

	@JsonIgnore
	@Override
	public String getId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}