package com.rwbase.dao.item.pojo;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

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
import com.rwbase.dao.item.MagicCfgDAO;
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
	private HashMap<Integer, String> allExtendAttr = new HashMap<Integer, String>();// 扩展属性,

	public ItemData() {
	}

	public ItemData(ItemData itemData) {
		this.id = itemData.id;
		this.modelId = itemData.modelId;
		this.count = itemData.count;
		this.userId = itemData.userId;
		this.allExtendAttr = new HashMap<Integer, String>(itemData.allExtendAttr);
	}

	public boolean init(int modelId, int count) {

		ItemBaseCfg cfg = ItemCfgHelper.GetConfig(modelId);
		if (cfg == null) {
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
			MagicCfg magicCfg = null;
			if (cfg instanceof MagicCfg) {
				magicCfg = (MagicCfg) cfg;
			} else {
				magicCfg = MagicCfgDAO.getInstance().getCfgById(String.valueOf(modelId));
			}

			if (magicCfg == null) {
				return false;
			}

			allExtendAttr.put(EItemAttributeType.Magic_Exp_VALUE, "0");
			allExtendAttr.put(EItemAttributeType.Magic_Level_VALUE, "1");
			allExtendAttr.put(EItemAttributeType.Magic_State_VALUE, "0");
			allExtendAttr.put(EItemAttributeType.Magic_Aptitude_VALUE, String.valueOf(magicCfg.getFirstAptitude()));
			allExtendAttr.put(EItemAttributeType.Magic_AdvanceLevel_VALUE, String.valueOf(magicCfg.getUplevel()));
			// } else if (type == EItemTypeDef.Magic_Piece) {
			// allExtendAttr.put(EItemAttributeType.Magic_Exp_VALUE, "0");
			// allExtendAttr.put(EItemAttributeType.Magic_Level_VALUE, "1");
			// allExtendAttr.put(EItemAttributeType.Magic_State_VALUE, "0");
		} else if (type == EItemTypeDef.HeroEquip) {
			allExtendAttr.put(EItemAttributeType.Equip_AttachLevel_VALUE, "0");
			allExtendAttr.put(EItemAttributeType.Equip_AttachExp_VALUE, "0");
			// } else if (type == EItemTypeDef.Piece) {
			// } else if (type == EItemTypeDef.SoulStone) {
			// } else if (type == EItemTypeDef.Gem) {
			// } else if (type == EItemTypeDef.Consume) {
		}
		// this.type = type;
		return true;
	}

	/** 仅仅是为了防止出错 */
	public HashMap<Integer, String> getAllExtendAttr() {
		synchronized (this) {
			return new HashMap<Integer, String>(this.allExtendAttr);
		}
	}

	public void setAllExtendAttr(HashMap<Integer, String> m_ExtendAttr) {
		synchronized (this) {
			this.allExtendAttr = new HashMap<Integer, String>(m_ExtendAttr);
		}
	}

	@JsonIgnore
	public String getExtendAttr(int itemAttrId) {
		synchronized (this) {
			return allExtendAttr.get(itemAttrId);
		}
	}

	public void setExtendAttr(int itemAttrId, String nValue) {
		synchronized (this) {
			allExtendAttr.put(itemAttrId, nValue);
		}
	}

	@JsonIgnore
	public Enumeration<Integer> getEnumerationKeys() {
		ArrayList<Integer> list;
		synchronized (this) {
			list = new ArrayList<Integer>(allExtendAttr.keySet());
		}

		final Iterator<Integer> iterator = list.iterator();
		Enumeration<Integer> enumeration = new Enumeration<Integer>() {

			@Override
			public boolean hasMoreElements() {
				return iterator.hasNext();
			}

			@Override
			public Integer nextElement() {
				return iterator.next();
			}
		};
		return enumeration;
	}

	public void modifyItemCount(int count) {
		count += count;
	}

	@JsonIgnore
	public int getMagicLevel() {
		synchronized (this) {
			String magicLevel = this.allExtendAttr.get(EItemAttributeType.Magic_Level_VALUE);
			return StringUtils.isEmpty(magicLevel) ? 1 : Integer.parseInt(magicLevel);
		}
	}
	
	@JsonIgnore
	public int getMagicAptitude(){
		synchronized (this) {
			String magicAptitude = this.allExtendAttr.get(EItemAttributeType.Magic_Aptitude_VALUE);
			return StringUtils.isEmpty(magicAptitude) ? 1 : Integer.parseInt(magicAptitude);
		}
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