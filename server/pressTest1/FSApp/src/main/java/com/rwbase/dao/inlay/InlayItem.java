package com.rwbase.dao.inlay;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;


@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "inlay_item")
@SynClass
public class InlayItem implements IMapItem,InlayItemIF{
	@Id
	private String id;
	private String ownerId; // 父级ID	

	@CombineSave
	private int slotId; //镶嵌的位置
	@CombineSave
	private int modelId;
	
	public String getId() {
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
	public int getSlotId() {
		return slotId;
	}
	public void setSlotId(int slotId) {
		this.slotId = slotId;
	}
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	

}
