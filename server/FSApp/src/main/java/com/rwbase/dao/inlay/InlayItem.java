package com.rwbase.dao.inlay;

import javax.persistence.Id;
import javax.persistence.Table;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.dao.annotation.OwnerId;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "inlay_item")
@SynClass
public class InlayItem implements RoleExtProperty, InlayItemIF {
	@Id
	private Integer id;
	@OwnerId
	private String ownerId; // 父级ID

	private int slotId; // 镶嵌的位置
	
	private int modelId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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
