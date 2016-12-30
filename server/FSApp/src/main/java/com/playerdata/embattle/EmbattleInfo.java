package com.playerdata.embattle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.SaveAsJson;

/*
 * @author HC
 * @date 2016年7月14日 下午6:39:18
 * @Description 
 */
@SynClass
@Table(name = "embattle_pos")
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class EmbattleInfo implements IMapItem {
	@IgnoreSynField
	@Id
	private String id;// 阵容的Id
	@IgnoreSynField
	private String userId;// 角色的Id
	private int type;// 阵容类型
	@SaveAsJson
	private List<EmbattlePositionInfo> posInfo;

	public EmbattleInfo() {
		posInfo = new ArrayList<EmbattlePositionInfo>();
	}

	public EmbattleInfo(String userId, int type) {
		this();
		this.userId = userId;
		this.type = type;
		this.id = userId + "_" + type;
	}

	/**
	 * 更新或者增加阵容信息
	 * 
	 * @param key
	 * @param updateInfo
	 */
	public void updateOrAddEmbattleInfo(String key, List<EmbattleHeroPosition> updateInfo) {
		if (posInfo == null) {
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

	/**
	 * 获取阵容信息
	 * 
	 * @param key
	 * @return
	 */
	public EmbattlePositionInfo getEmbattlePositionInfo(String key) {
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

	@Override
	public String getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public int getType() {
		return type;
	}
	
	public List<EmbattlePositionInfo> getAll() {
		return Collections.unmodifiableList(posInfo);
	}
}