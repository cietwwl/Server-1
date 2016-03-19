package com.rwbase.dao.guildSecretArea;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rwbase.dao.guildSecretArea.projo.SecretUserSource;
import com.rwbase.dao.guildSecretArea.projo.SourceType;
import com.rwbase.dao.guildSecretArea.projo.ESecretType;



/**
 * 帮派秘境的防守信息，直接和用户对应
 * @author allen
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "secretarea_user_record")
@SynClass
public class SecretAreaUserRecord implements IMapItem//用户奖励信息，状态 
{
	@Id
	private String id;  //地图记录唯一id //秘境id
	
	private String userId; // 用户ID	
	@SaveAsJson
	private ESecretType secretType ;//秘境类型
	private int status;//状态 未领取 已领取 
	private String getGiftTime;//领取时间
	@SaveAsJson
	private List<SecretUserSource> userSourceList;//每个玩家玩家产出列表       奖励列表  钻石,金币，强化石，经验丹

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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getGetGiftTime() {
		return getGiftTime;
	}

	public void setGetGiftTime(String getGiftTime) {
		this.getGiftTime = getGiftTime;
	}


	public ESecretType getSecretType() {
		return secretType;
	}

	public void setSecretType(ESecretType secretType) {
		this.secretType = secretType;
	}

	public List<SecretUserSource> getUserSourceList() {
		return userSourceList;
	}

	public void setUserSourceList(List<SecretUserSource> userSourceList) {
		this.userSourceList = userSourceList;
	}
	
	
	
	
}
