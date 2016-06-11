package com.rwbase.dao.groupsecret.pojo.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

import com.log.GameLog;

/*
 * @author HC
 * @date 2016年6月7日 下午3:03:03
 * @Description 
 */
public class GroupSecretLevelGetResTemplate {
	public static class Drop {
		public final int leftMinutes;// 驻守剩余时间
		public final int dropId;// 掉落方案Id

		public Drop(int leftMinutes, int dropId) {
			this.leftMinutes = leftMinutes;
			this.dropId = dropId;
		}
	}

	private final int level;// 可以掉落的Id
	private final int levelGroupId;// 等级组Id
	private final int diamondDropId;// 钻石掉落组的Id
	private final int robDiamond;// 掠夺的钻石
	private final int robGSRatio;// 掠夺帮派物资的权重
	private final int robGERatio;// 掠夺帮派经验的权重
	private final int robRatio;// 掠夺资源的权重
	private final float productRatio;// 每分钟产出资源的权重
	private final float groupSupplyRatio;// 每分钟帮派物资的产出权重
	private final float groupExpRatio;// 每分钟帮派经验的产出权重
	private final List<GroupSecretLevelGetResTemplate.Drop> dropIdBasedOnJoinTimeList;// 掉落宝石方案对应的加入时间剩余

	public GroupSecretLevelGetResTemplate(GroupSecretLevelGetResCfg cfg) {
		this.level = cfg.getLevel();
		this.levelGroupId = cfg.getLevelGroupId();
		this.diamondDropId = cfg.getDiamondDropId();
		this.robDiamond = cfg.getRobDiamond();
		this.robGSRatio = cfg.getRobGSRatio();
		this.robGERatio = cfg.getRobGERatio();
		this.robRatio = cfg.getRobRatio();

		try {
			String dropIdBasedOnJoinTime = cfg.getDropIdBasedOnJoinTime();
			if (StringUtils.isEmpty(dropIdBasedOnJoinTime)) {
				this.dropIdBasedOnJoinTimeList = Collections.emptyList();
			} else {
				String[] arr1 = dropIdBasedOnJoinTime.split(";");
				int len = arr1.length;
				List<GroupSecretLevelGetResTemplate.Drop> dropIdBasedOnJoinTimeList = new ArrayList<GroupSecretLevelGetResTemplate.Drop>(len);
				for (int i = 0; i < len; i++) {
					String[] arr2 = arr1[i].split("_");
					dropIdBasedOnJoinTimeList.add(new GroupSecretLevelGetResTemplate.Drop(Integer.valueOf(arr2[0]), Integer.valueOf(arr2[1])));
				}

				this.dropIdBasedOnJoinTimeList = Collections.unmodifiableList(dropIdBasedOnJoinTimeList);
			}

			this.productRatio = Float.parseFloat(cfg.getProductRatio());
			this.groupSupplyRatio = Float.parseFloat(cfg.getGroupSupplyRatio());
			this.groupExpRatio = Float.parseFloat(cfg.getGroupExpRatio());
		} catch (Exception e) {
			GameLog.error("解析秘境资源表", "GroupSecretResourceTemplate", "解析过程中把产出权重，物资权重，贡献权重，加入时间对应掉落方案中的某一个出现了异常");
			throw new ExceptionInInitializerError(e);
		}
	}

	public int getLevel() {
		return level;
	}

	public int getLevelGroupId() {
		return levelGroupId;
	}

	public int getDiamondDropId() {
		return diamondDropId;
	}

	public int getRobDiamond() {
		return robDiamond;
	}

	public int getRobGSRatio() {
		return robGSRatio;
	}

	public int getRobGERatio() {
		return robGERatio;
	}

	public int getRobRatio() {
		return robRatio;
	}

	public float getProductRatio() {
		return productRatio;
	}

	public float getGroupSupplyRatio() {
		return groupSupplyRatio;
	}

	public float getGroupExpRatio() {
		return groupExpRatio;
	}

	public List<Drop> getDropIdBasedOnJoinTimeList() {
		return dropIdBasedOnJoinTimeList;
	}
}