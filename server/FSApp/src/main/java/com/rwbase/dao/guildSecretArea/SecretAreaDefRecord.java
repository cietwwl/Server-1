package com.rwbase.dao.guildSecretArea;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rwbase.dao.guildSecretArea.projo.SourceType;
import com.rwbase.dao.guildSecretArea.projo.ESecretType;



/**
 * 帮派秘境的防守信息，直接和用户对应
 * @author allen
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "secretarea_def_record")
@SynClass
public class SecretAreaDefRecord implements IMapItem
{
	@Id
	private String id;  //地图记录唯一id
	
	private String userId; // 进攻的用户ID
	
	private String secretId; //进攻秘境id
	@SaveAsJson
	private ESecretType secretType;//秘境类型
	private String attrackUserId;//进攻玩家id
	private String guildName;//所属公会
	private String regiondName;//所属区
	private String attrackUserName;//进攻玩家名
	private String attrackTime;//进攻时间
	private int attrackCount;//进攻次数
	private int isWin;//胜利失败
	private int keyNum;//获得密钥
	@SaveAsJson
	private List<SourceType> sourceNumList;//奖励列表  钻石,金币，强化石，经验丹
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSecretId() {
		return secretId;
	}

	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}

	public String getAttrackUserId() {
		return attrackUserId;
	}

	public void setAttrackUserId(String attrackUserId) {
		this.attrackUserId = attrackUserId;
	}

	public String getGuildName() {
		return guildName;
	}

	public void setGuildName(String guildName) {
		this.guildName = guildName;
	}

	public String getRegiondName() {
		return regiondName;
	}

	public void setRegiondName(String regiondName) {
		this.regiondName = regiondName;
	}

	public String getAttrackUserName() {
		return attrackUserName;
	}

	public void setAttrackUserName(String attrackUserName) {
		this.attrackUserName = attrackUserName;
	}

	public int getAttrackCount() {
		return attrackCount;
	}

	public void setAttrackCount(int attrackCount) {
		this.attrackCount = attrackCount;
	}


	public int getKeyNum() {
		return keyNum;
	}

	public void setKeyNum(int keyNum) {
		this.keyNum = keyNum;
	}


	public List<SourceType> getSourceNumList() {
		return sourceNumList;
	}

	public void setSourceNumList(List<SourceType> sourceNumList) {
		this.sourceNumList = sourceNumList;
	}

	public ESecretType getSecretType() {
		return secretType;
	}

	public void setSecretType(ESecretType secretType) {
		this.secretType = secretType;
	}

	public String getAttrackTime() {
		return attrackTime;
	}

	public void setAttrackTime(String attrackTime) {
		this.attrackTime = attrackTime;
	}

	public int getIsWin() {
		return isWin;
	}

	public void setIsWin(int isWin) {
		this.isWin = isWin;
	}
	
	
	
	
}
