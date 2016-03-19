package com.rwbase.common.attrdata;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_table_role_attr")
public class TableAttr {

	@Id
	private String userId;
//	private HashMap<EAttriSys, AttrData> attriSysList = new HashMap<EAttriSys, AttrData>();//属性列表, EAttriSys:属性系统, AttrData:属性数据
	private AttrData attrData;
	public TableAttr()
	{
	
//		for(EAttriSys eAttriSys:EAttriSys.values()){
//			this.getAttriSysList().put(eAttriSys, new AttrData());
//		}
	}
	public TableAttr(String userIdP, AttrData attrDataP){
		this.userId = userIdP;
		this.attrData = attrDataP;
	}
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public AttrData getAttrData() {
		return attrData;
	}
	public void setAttrData(AttrData attrData) {
		this.attrData = attrData;
	}
//	public HashMap<EAttriSys, AttrData> getAttriSysList() {
//		return attriSysList;
//	}
//
//	public void setAttriSysList(HashMap<EAttriSys, AttrData> attriSysList) {
//		this.attriSysList = attriSysList;
//	}

}
