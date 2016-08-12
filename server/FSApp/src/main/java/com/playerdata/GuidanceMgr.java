package com.playerdata;

import java.util.List;

import com.common.RefParam;
import com.playerdata.common.PlayerEventListener;
import com.rw.fsutil.common.stream.IStream;
import com.rw.fsutil.common.stream.IStreamListner;
import com.rw.service.guide.NewGuideService;
import com.rw.service.guide.datamodel.GiveItemCfg;
import com.rw.service.guide.datamodel.GiveItemCfgDAO;

public class GuidanceMgr implements PlayerEventListener, IStreamListner<Integer>{
	private Player player;
	private boolean isSubscribed = false;
	private void init(){
		if (isSubscribed) return;
		player.getLevelNotification().subscribe(this);
		isSubscribed = true;
	}
	
	@Override
	public void onClose(IStream<Integer> whichStream) {
		isSubscribed = false;
	}

	@Override
	public void onChange(Integer newLevel) {
		List<GiveItemCfg> cfgLst = GiveItemCfgDAO.getInstance().getAutoSentCfg(newLevel);
		if (cfgLst != null && cfgLst.size() > 0){
			RefParam<String> outTip = new RefParam<String>();
			for (GiveItemCfg giveItemCfg : cfgLst) {
				NewGuideService.giveItem(giveItemCfg, player, outTip);
			}
		}
	}
	
	@Override
	public void notifyPlayerCreated(Player player) {
		this.player = player;
		init();
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		this.player = player;
		init();
	}

	@Override
	public void init(Player player) {
		this.player = player;
		init();
	}

}
