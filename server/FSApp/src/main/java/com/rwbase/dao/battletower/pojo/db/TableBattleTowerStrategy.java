package com.rwbase.dao.battletower.pojo.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import com.rwbase.dao.battletower.pojo.BattleTowerRoleInfo;
import com.rwbase.dao.battletower.pojo.cfg.dao.BattleTowerConfigCfgDao;

/*
 * @author HC
 * @date 2015年9月2日 下午4:53:05
 * @Description 试练塔战略:共有数据全靠自觉做好数据保护了
 */
@Table(name = "battle_tower_strategy")
public class TableBattleTowerStrategy {
	@Id
	private int battleTowerGroupId;// 组Id
	private List<BattleTowerRoleInfo> roleInfoList;// 试练塔战略中的记录

	public TableBattleTowerStrategy() {
		this.roleInfoList = new ArrayList<BattleTowerRoleInfo>();
	}

	public int getBattleTowerGroupId() {
		return battleTowerGroupId;
	}

	public void setBattleTowerGroupId(int battleTowerGroupId) {
		this.battleTowerGroupId = battleTowerGroupId;
	}

	public List<BattleTowerRoleInfo> getRoleInfoList() {
		return new ArrayList<BattleTowerRoleInfo>(this.roleInfoList);
	}

	public void setRoleInfoList(List<BattleTowerRoleInfo> roleInfoList) {
		this.roleInfoList = roleInfoList;
	}

	/**
	 * 增加试练塔攻略角色信息
	 * 
	 * <pre>
	 *  <b>[注]：共有数据，全靠调用模块的自觉性来保护数据</b>
	 * </pre>
	 * 
	 * @param roleInfo
	 */
	public synchronized void addBattleTowerRoleInfo(BattleTowerRoleInfo roleInfo) {
		if (roleInfo == null) {
			return;
		}

		for (BattleTowerRoleInfo old : roleInfoList) {
			if (old.getUserId().equals(roleInfo.getUserId())){
				return;
			}
		}
		
		int size = BattleTowerConfigCfgDao.getCfgDao().getUniqueCfg().getStrategyCacheRecordSize();// 战略长度

		this.roleInfoList.add(roleInfo);// 添加到列表

		if (this.roleInfoList.size() > size) {
			this.roleInfoList.remove(0);
		}
	}
}