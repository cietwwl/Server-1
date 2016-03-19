package com.playerdata;

import java.util.ArrayList;
import java.util.List;

import com.rwbase.dao.item.ConsumeCfgDAO;
import com.rwbase.dao.item.GemCfgDAO;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.PieceCfgDAO;
import com.rwbase.dao.item.RoleEquipCfgDAO;
import com.rwbase.dao.item.SoulStoneCfgDAO;
import com.rwbase.dao.item.pojo.ConsumeCfg;
import com.rwbase.dao.item.pojo.GemCfg;
import com.rwbase.dao.item.pojo.HeroEquipCfg;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwbase.dao.item.pojo.PieceCfg;
import com.rwbase.dao.item.pojo.RoleEquipCfg;
import com.rwbase.dao.item.pojo.SoulStoneCfg;
import com.rwproto.ItemBagProtos.EItemTypeDef;

public class ItemCfgHelper {
	// private static ItemCfgHelper m_instance = null;
	//
	// public static ItemCfgHelper getInstance() {
	// if (m_instance == null) {
	// m_instance = new ItemCfgHelper();
	// }
	// return m_instance;
	// }

	public static boolean checkItem(int id) {
		if ((id >= 600001 && id < 807000) || (id > 802000 && id < 803000))
			return true;
		return false;
	}

	/**
	 * 获取物品的类型
	 * 
	 * @param id
	 * @return
	 */
	public static EItemTypeDef getItemType(int id) {
		EItemTypeDef type = null;
		if (id > 602000 && id < 604000)
			type = EItemTypeDef.Magic;
		else if (id > 604000 && id < 606000)
			type = EItemTypeDef.Magic_Piece;
		else if (id > 700000 && id < 702000)
			type = EItemTypeDef.HeroEquip;
		else if ((id > 702000 && id < 704000) || (id > 707000 && id < 708000))
			type = EItemTypeDef.Piece;
		else if ((id > 704000 && id < 706000) || (id > 708000 && id < 709000))
			type = EItemTypeDef.SoulStone;
		else if (id > 800000 && id < 801000)
			type = EItemTypeDef.Gem;
		else if ((id > 801000 && id < 807000) || (id > 802000 && id < 803000))
			type = EItemTypeDef.Consume;
		else if (id > 202001 && id < 400000)
			type = EItemTypeDef.HeroItem;
		return type;
	}

	public static ConsumeCfg getConsumeCfg(int id) {
		return (ConsumeCfg) ConsumeCfgDAO.getInstance().getCfgById(String.valueOf(id));
	}

	public static RoleEquipCfg getRoleEquipCfg(int id) {
		return (RoleEquipCfg) RoleEquipCfgDAO.getInstance().getCfgById(String.valueOf(id));
	}

	public static HeroEquipCfg getHeroEquipCfg(int id) {
		return (HeroEquipCfg) HeroEquipCfgDAO.getInstance().getCfgById(String.valueOf(id));
	}

	public static MagicCfg getMagicCfg(int id) {
		return (MagicCfg) MagicCfgDAO.getInstance().getCfgById(String.valueOf(id));
	}

	public static GemCfg getGemCfg(int id) {
		return (GemCfg) GemCfgDAO.getInstance().getCfgById(String.valueOf(id));
	}

	public static PieceCfg getPieceCfg(int id) {
		return (PieceCfg) PieceCfgDAO.getInstance().getCfgById(String.valueOf(id));
	}

	public static SoulStoneCfg getSoulStoneCfg(int id) {
		return (SoulStoneCfg) SoulStoneCfgDAO.getInstance().getCfgById(String.valueOf(id));
	}
	
	/**
	 * 获取指定类型的宝石列表
	 * @param gemType
	 * @return
	 */
	public static List<GemCfg> getGemCfgByType(int gemType){
		List<GemCfg> result = new ArrayList<GemCfg>();
		List<GemCfg> allCfg = GemCfgDAO.getInstance().getAllCfg();
		for (GemCfg gemCfg : allCfg) {
			if(gemCfg.getGemType() == gemType){
				result.add(gemCfg);
			}
		}
		return result;
	}

	// public static List<PlayerInitialItemCfg> getPlayerInitialItemAllCfg() {
	// return PlayerInitialItemCfgDAO.getInstance().getAllCfg();
	// }

	public static ItemBaseCfg GetConfig(int nId) {
		ItemBaseCfg cfg = null;
		EItemTypeDef type = getItemType(nId);
		if (type == EItemTypeDef.Piece) {
			cfg = getPieceCfg(nId);
		} else if (type == EItemTypeDef.Gem) {
			cfg = getGemCfg(nId);
		} else if (type == EItemTypeDef.HeroEquip) {
			cfg = getHeroEquipCfg(nId);
		} else if (type == EItemTypeDef.Magic || type == EItemTypeDef.Magic_Piece) {
			cfg = getMagicCfg(nId);
		} else if (type == EItemTypeDef.SoulStone) {
			cfg = getSoulStoneCfg(nId);
		} else if (type == EItemTypeDef.Consume) {
			cfg = getConsumeCfg(nId);
		}
		return cfg;
	}
}