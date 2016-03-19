package com.rwbase.dao.guildSecretArea;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.guildSecretArea.projo.ESecretType;
import com.rwbase.dao.guildSecretArea.projo.SecretArmy;
import com.rwproto.SecretAreaProtos.EAttrackType;


/**
 * 秘境的信息 一个帮派秘境一个
 * @author allen
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "secretarea_info")
@SynClass
public class SecretAreaInfo
{
	@Id
	private String id;  //秘籍唯一id
	private String ownerId; 	// 拥有者ID
	private boolean closed;		//是否关闭
	private boolean protect;	//是否保护时间
	private long begainTime;
	private long fightTime;		//保护状态   记录战斗开始时间（保护时间10分钟）
	private long endTime;		//秘境结束时间
	private int robcount; //被掠夺次数
	private ESecretType secretType;		//资源类型
	private EAttrackType eAttrackType;		//秘境类型
	private ConcurrentHashMap<String,SecretArmy> secretArmyMap;	//<用户id,驻守信息>
	public String getId() {//@IgnoreSynField
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	@JsonIgnore
	public Enumeration<String> getJoinUserIdList() {
		return secretArmyMap.keys();
	}

	public long getFightTime() {
		return fightTime;
	}

	public void setFightTime(long fightTime) {
		this.fightTime = fightTime;
	}
	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public boolean getClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public boolean getProtect() {
		return protect;
	}

	public void setProtect(boolean protect) {
		this.protect = protect;
	}

	public ConcurrentHashMap<String,SecretArmy> getSecretArmyMap() {
		return secretArmyMap;
	}
	@JsonIgnore
	public Enumeration<SecretArmy> getArmyEnumeration() {
		return secretArmyMap.elements();
	}
	@JsonIgnore
	public Enumeration<String> getSecretArmyKeys() {
		return secretArmyMap.keys();
	}

	public void setSecretArmyMap(ConcurrentHashMap<String,SecretArmy> secretArmyMap) {
		this.secretArmyMap = secretArmyMap;
	}
	public ESecretType getSecretType() {
		return secretType;
	}

	public void setSecretType(ESecretType secretType) {
		this.secretType = secretType;
	}

	public EAttrackType geteAttrackType() {
		return eAttrackType;
	}

	public void seteAttrackType(EAttrackType eAttrackType) {
		this.eAttrackType = eAttrackType;
	}

	public long getBegainTime() {
		return begainTime;
	}

	public void setBegainTime(long begainTime) {
		this.begainTime = begainTime;
	}

	public int getRobcount() {
		return robcount;
	}

	public void setRobcount(int robcount) {
		this.robcount = robcount;
	}


	
}
