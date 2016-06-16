package com.bm.groupChamp.data;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "group_champ_army_item")
public class GroupChampArmyItem implements  IMapItem {

	@Id
	private String id;
	
	private String champId;
	
	private String groupId;
	
	private int version;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getChampId() {
		return champId;
	}
	public void setChampId(String champId) {
		this.champId = champId;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	public void incrVersion(){
		this.version ++;
	}
	

	
	
	
}
