package com.rwbase.dao.magicweapon.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

import com.common.HPCUtil;
import com.rwbase.dao.copy.pojo.ItemInfo;

/**
 * @Author HC
 * @date 2016年10月6日 下午5:16:51
 * @desc
 **/

public class MagicSmeltMaterialTemplate {
	private final int aptitude;
	private final List<ItemInfo> materialList;// 材料的列表

	public MagicSmeltMaterialTemplate(MagicSmeltMaterialCfg cfg) {
		this.aptitude = cfg.getAptitude();

		String needMaterials = cfg.getGoods();
		if (StringUtils.isEmpty(needMaterials)) {
			materialList = Collections.emptyList();
		} else {
			Map<Integer, Integer> map = HPCUtil.parseIntegerMap(needMaterials, ";", "_");

			List<ItemInfo> materialList = new ArrayList<ItemInfo>(map.size());

			for (Entry<Integer, Integer> e : map.entrySet()) {
				materialList.add(new ItemInfo(e.getKey(), e.getValue()));
			}

			this.materialList = Collections.unmodifiableList(materialList);
		}
	}

	/**
	 * 获取对应的资质
	 * 
	 * @return
	 */
	public int getAptitude() {
		return aptitude;
	}

	/**
	 * 获取需要的材料
	 * 
	 * @return
	 */
	public List<ItemInfo> getMaterialList() {
		return materialList;
	}
}