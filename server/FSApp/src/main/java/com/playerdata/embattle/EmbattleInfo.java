package com.playerdata.embattle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Id;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;

/*
 * @author HC
 * @date 2016年7月14日 下午6:39:18
 * @Description 
 */
@SynClass
public class EmbattleInfo implements IMapItem {
	@IgnoreSynField
	private String userId;// 角色的Id
	@Id
	private int type;// 阵容类型，阵容信息从BattleCommon中的eBattlePositonType
	private List<EmbattlePositionInfo> posInfo;// 阵容信息

	public EmbattleInfo() {
		posInfo = new ArrayList<EmbattlePositionInfo>();
	}

	public EmbattleInfo(String userId, int type) {
		this();
		this.userId = userId;
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public List<EmbattlePositionInfo> getPosInfo() {
		return posInfo;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setPosInfo(List<EmbattlePositionInfo> posInfo) {
		this.posInfo = posInfo;
	}

	/**
	 * 更新或者增加阵容信息
	 * 
	 * @param key
	 * @param updateInfo
	 */
	public void updateOrAddEmbattleInfo(String key, List<EmbattleHeroPosition> updateInfo) {
		if (posInfo == null || posInfo.isEmpty()) {
			return;
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
	public void removeEmbattleInfo(String key) {
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

	@Override
	public String getId() {
		return String.valueOf(type);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}