package com.rwbase.dao.guildSecretArea.projo;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rwbase.dao.guildSecretArea.projo.ESecretType;

@SynClass
public class SourceType {
	public SourceType(){
		
	
	}
	private int materialType;//资源类型
	@SaveAsJson
	private ESecretType secretType;//秘境类型
	private int num;//数量
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getMaterialType() {
		return materialType;
	}
	public void setMaterialType(int materialType) {
		this.materialType = materialType;
	}
	public ESecretType getSecretType() {
		return secretType;
	}
	public void setSecretType(ESecretType secretType) {
		this.secretType = secretType;
	}
}
