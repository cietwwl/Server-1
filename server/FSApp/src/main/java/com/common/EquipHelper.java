package com.common;

/*
 * @author HC
 * @date 2016年4月16日 下午2:45:11
 * @Description 
 */
public class EquipHelper {

	/**
	 * 根绝装备的品质获取装备附灵等级的初始值
	 * 
	 * @param quality
	 * @return
	 */
	public static int getEquipAttachInitId(int quality) {// 根据品质获取佣兵装备初始强化ID
		int id = 0;
		switch (quality) {
		case 1:
			id = 1000;
			break;
		case 2:
			id = 2000;
			break;
		case 3:
			id = 3000;
			break;
		case 4:
			id = 4000;
			break;
		default:
			break;
		}
		return id;
	}
}