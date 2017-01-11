package com.rw.service.unendingwar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.protobuf.ByteString;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.rw.service.dropitem.DropItemManager;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.unendingwar.CfgUnendingWar;
import com.rwbase.dao.unendingwar.CfgUnendingWarDAO;
import com.rwbase.dao.unendingwar.TableUnendingWar;
import com.rwproto.PrivilegeProtos.PvePrivilegeNames;
import com.rwproto.UnendingWarProtos.EUnendingWarType;
import com.rwproto.UnendingWarProtos.UnendingWarResponse;

public class UnendingWarHandler {
	private static UnendingWarHandler instance = new UnendingWarHandler();

	protected UnendingWarHandler() {
	}

	public static UnendingWarHandler getInstance() {
		return instance;
	}

	/*** 获取基本信息 ****/
	public ByteString getInfo(Player player) {
		UnendingWarResponse.Builder res = UnendingWarResponse.newBuilder();
		res.setType(EUnendingWarType.BaseMsg);

		TableUnendingWar table = player.unendingWarMgr.getTable();
		res.setZhCj(table.getZhCj());
		res.setNum(table.getNum());
		res.setDqCj(table.getDqCj());
		res.setResetNum(table.getResetNum());

		return res.build().toByteString();
	}

	/*** 进入副本 ****/
	public ByteString endMap(Player player, int eMap) {
		UnendingWarResponse.Builder res = UnendingWarResponse.newBuilder();
		res.setType(EUnendingWarType.OtherMsg);

		TableUnendingWar table = player.unendingWarMgr.getTable();
		table.setDqCj(0);
		res.setZhCj(table.getZhCj());
		res.setNum(player.getUnendingWarMgr().getTable().getNum());
		res.setDqCj(table.getDqCj());

		player.unendingWarMgr.save();

		return res.build().toByteString();
	}

	/*** 增加副本次数 ****/
	public ByteString addNum(Player player) {
		player.unendingWarMgr.getTable().setDqCj(player.unendingWarMgr.getTable().getDqCj() + 1);
		if (player.unendingWarMgr.getTable().getZhCj() < player.unendingWarMgr.getTable().getDqCj()) {
			player.unendingWarMgr.getTable().setZhCj(player.unendingWarMgr.getTable().getDqCj());
		}
		player.unendingWarMgr.save();
		return getInfo(player);
	}

	public ByteString end(Player player, int num) {
		UnendingWarResponse.Builder res = UnendingWarResponse.newBuilder();
		res.setType(EUnendingWarType.EndMsg);

		res.setZhCj(player.unendingWarMgr.getTable().getZhCj());
		res.setNum(player.unendingWarMgr.getTable().getNum());
		res.setDqCj(player.unendingWarMgr.getTable().getDqCj());
		res.setResetNum(player.unendingWarMgr.getTable().getResetNum());

		return res.build().toByteString();
	}

	/*** 发送结算奖励 ****/
	private void setEnd(Player player, int num, int cMap) {
		// int dq = player.unendingWarMgr.getTable().getDqCj();
		if (player.unendingWarMgr.getTable().getZhCj() < num) {
			player.unendingWarMgr.getTable().setZhCj(num);
		}

		if (cMap > 0) {
			player.unendingWarMgr.getTable().getCj().put(cMap, num);
			if (num == 15) {
				MainMsgHandler.getInstance().sendPmdSl(player, cMap);
			}
		}

		player.unendingWarMgr.save();

	}

	/*** 重置副本 ****/
	public ByteString ResetNum(Player player) {
		// int count = player.getVipMgr().GetMaxPrivilege(EPrivilegeDef.WARFARE_COPY_RESET_TIMES);
		// by franky
		int count = player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.warfareResetCnt);
		count = count > 0 ? count : 1;

		int num = player.unendingWarMgr.getTable().getResetNum();
		if (num < count) {
			player.unendingWarMgr.getTable().setResetNum(num + 1);
		}

		player.unendingWarMgr.getTable().setNum(0);

		player.unendingWarMgr.save();
		player.getUserGameDataMgr().addGold(-20);

		return getInfo(player);
	}

	/*** 获取对应的奖励 ****/
	public List<? extends ItemInfo> getJlItem(Player player, int num, int cMap, AtomicInteger unendingCoin) {
		this.setEnd(player, num, cMap);
		ArrayList<Integer> dropList = new ArrayList<Integer>();
		int magicSecretCoin = 0;
		for (int j = 1; j <= num; j++) {
			CfgUnendingWar cfgUnendingWar = (CfgUnendingWar) CfgUnendingWarDAO.getInstance().getCfg(cMap, j);
			if (cfgUnendingWar == null || cfgUnendingWar.jl1 == null) {
				continue;
			}
			/*** 加奖励到背包 ****/
			// TODO 不应该运行时分割字符串，应修改无尽战火配置表 modify@2015-12-18 by Jamaz //添加货币
			// player.getItemBagMgr().addItem(eSpecialItemId.MagicSecretCoin.getValue(), cfgUnendingWar.uNum);
			magicSecretCoin += cfgUnendingWar.uNum;
			unendingCoin.addAndGet(cfgUnendingWar.uNum);

			String[] array = cfgUnendingWar.jl1.split(",");
			for (int i = 0; i < array.length; i++) {
				dropList.add(Integer.parseInt(array[i]));
			}
		}
		// TODO DropItemManaer可优化成一个方法调用，少一次数据库操作和减少遍历操作
		List<? extends ItemInfo> listItemBattle = null;
		try {
			DropItemManager.getInstance().pretreatDrop(player, dropList, cMap, false);
			listItemBattle = DropItemManager.getInstance().extractDropPretreatment(player, cMap, true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		List<ItemInfo> addItems;
		if (listItemBattle != null) {
			// addJlItem(listItemBattle, player);
			// return listItemBattle;
			addItems = new ArrayList<ItemInfo>(listItemBattle.size() + 1);
			addItems.addAll(listItemBattle);
		} else {
			// return Collections.EMPTY_LIST;
			addItems = new ArrayList<ItemInfo>(1);
			listItemBattle = Collections.emptyList();
		}
		if (magicSecretCoin > 0) {
			addItems.add(new ItemInfo(eSpecialItemId.MagicSecretCoin.getValue(), magicSecretCoin));
		}
		addJlItem(addItems, player);
		return listItemBattle;
	}

	/*** 加奖励到背包 ****/
	private void addJlItem(List<? extends ItemInfo> addList, Player player) {

		// for (ItemInfo item : addList) {
		// player.getItemBagMgr().addItem(item.getItemID(), item.getItemNum());
		// }
		List<ItemInfo> list = new ArrayList<ItemInfo>(addList);
		ItemBagMgr.getInstance().addItem(player, list);

	}

	/*** 增加对应该波数获得的货币 ****/
	public int getJlNum(Player player, int num, int cMap) {

		int uNum = 0;

		CfgUnendingWar cfgUnendingWar = (CfgUnendingWar) CfgUnendingWarDAO.getInstance().getCfg(cMap, num);
		if (cfgUnendingWar == null) {
			return 0;
		}
		uNum = cfgUnendingWar.uNum;

		if (uNum > 0) {
			ItemBagMgr.getInstance().addItem(player, eSpecialItemId.MagicSecretCoin.getValue(), uNum);

		}

		return uNum;

	}

}
