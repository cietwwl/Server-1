package com.rwbase.dao.vip;

import com.playerdata.Player;
import com.playerdata.charge.cfg.VipGiftCfgDao;
import com.playerdata.charge.dao.ChargeInfoDao;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.vip.pojo.TableVip;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class VipDataHolder {

	private TableVipDAO tableVipDAO = TableVipDAO.getInstance();
	private String userId;
	final private eSynType synType = eSynType.VIP_DATA;

	public VipDataHolder(String userId) {
		this.userId = userId;
	}

	public void syn(Player player, int version) {
		TableVip tableVip = get();
		if (tableVip!=null) {
			ClientDataSynMgr.synData(player, tableVip, synType, eSynOpType.UPDATE_SINGLE);
		}

	}

	public TableVip get() {
		return tableVipDAO.get(userId);
	}

	public void update(Player player) {
		TableVip tableVip = get();
		if (tableVip != null) {
			ClientDataSynMgr.updateData(player, tableVip, synType, eSynOpType.UPDATE_SINGLE);
		}
	}

	public void flush() {
	}

}
