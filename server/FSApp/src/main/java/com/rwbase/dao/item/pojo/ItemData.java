package com.rwbase.dao.item.pojo;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.util.StringUtils;

import com.playerdata.ItemCfgHelper;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.readonly.ItemDataIF;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rwproto.ItemBagProtos.EItemAttributeType;
import com.rwproto.ItemBagProtos.EItemTypeDef;

/**
 * @author HC
 * @date 2015年10月17日 下午3:46:46
 * @Description
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "item")
@SynClass
public class ItemData implements IMapItem, ItemDataIF {
	@Id
	private String id;// 道具Id
	private int modelId;// 物品Id
	private int count;// 物品数量
	@IgnoreSynField
	private String userId;// 角色Id
	// @JsonIgnore
	// @IgnoreSynField
	// private EItemTypeDef type;// 物品类型
	@SaveAsJson
	private ConcurrentHashMap<Integer, String> allExtendAttr = new ConcurrentHashMap<Integer, String>();// 扩展属性,
	
	public ItemData() {
	}

	public ItemData(ItemData itemData) {
		this.id = itemData.id;
		this.modelId = itemData.modelId;
		this.count = itemData.count;
		this.userId = itemData.userId;
		this.allExtendAttr = new ConcurrentHashMap<Integer, String>(itemData.allExtendAttr);
	}

	public boolean init(int modelId, int count) {
		if (!ItemCfgHelper.checkItem(modelId)) {
			System.out.println("Item's ID is Out of Range");
			return false;
		}
		if (count <= 0) {
			return false;
		}

		this.modelId = modelId;
		this.count = count;
		EItemTypeDef type = ItemCfgHelper.getItemType(modelId);
		if (type == EItemTypeDef.Magic) {
			allExtendAttr.put(EItemAttributeType.Magic_Exp_VALUE, "0");
			allExtendAttr.put(EItemAttributeType.Magic_Level_VALUE, "1");
			allExtendAttr.put(EItemAttributeType.Magic_State_VALUE, "0");
		} else if (type == EItemTypeDef.Magic_Piece) {
			allExtendAttr.put(EItemAttributeType.Magic_Exp_VALUE, "0");
			allExtendAttr.put(EItemAttributeType.Magic_Level_VALUE, "1");
			allExtendAttr.put(EItemAttributeType.Magic_State_VALUE, "0");
		} else if (type == EItemTypeDef.HeroEquip) {
			allExtendAttr.put(EItemAttributeType.Equip_AttachLevel_VALUE, "0");
			allExtendAttr.put(EItemAttributeType.Equip_AttachExp_VALUE, "0");
		} else if (type == EItemTypeDef.Piece) {
		} else if (type == EItemTypeDef.SoulStone) {
		} else if (type == EItemTypeDef.Gem) {
		} else if (type == EItemTypeDef.Consume) {
		}
		// this.type = type;
		return true;
	}

	/** 仅仅是为了防止出错 */
	public ConcurrentHashMap<Integer, String> getAllExtendAttr() {
		return allExtendAttr;
	}

	public void setExtendAttr(ConcurrentHashMap<Integer, String> m_ExtendAttr) {
		this.allExtendAttr = new ConcurrentHashMap<Integer, String>(m_ExtendAttr);
	}

	@JsonIgnore
	public String getExtendAttr(int itemAttrId) {
		return allExtendAttr.get(itemAttrId);
	}

	public void setExtendAttr(int itemAttrId, String nValue) {
		allExtendAttr.put(itemAttrId, nValue);
	}

	@JsonIgnore
	public Enumeration<Integer> getEnumerationKeys() {
		return this.allExtendAttr.keys();
	}

	public void modifyItemCount(int count) {
		count += count;
	}

	@JsonIgnore
	public int getMagicLevel() {
		String magicLevel = this.allExtendAttr.get(EItemAttributeType.Magic_Level_VALUE);
		return StringUtils.isEmpty(magicLevel) ? 1 : Integer.parseInt(magicLevel);
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
		// 设置物品类型
		// this.type = ItemCfgHelper.getItemType(modelId);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@JsonIgnore
	public EItemTypeDef getType() {
		// return type;
		return ItemCfgHelper.getItemType(modelId);
	}

	@Override
	public String getId() {
		return this.id;
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
}