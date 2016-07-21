package com.bm.player;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.teaminfo.TeamInfoListenerPlayerChange;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.bm.GFOnlineListenerPlayerChange;
import com.rw.service.store.StoreListenerPlayerChange;
import com.rwbase.dao.group.GroupListenerPlayerChange;

/*
 * @author HC
 * @date 2016年2月1日 下午2:32:46
 * @Description 个人数据修改观察者，负责发布消息
 */
public class PlayerChangePopertyObserver implements Observer {

	private List<PlayerChangePopertySubscribe> subscribeList;

	public PlayerChangePopertyObserver() {
		subscribeList = new ArrayList<PlayerChangePopertySubscribe>();
		initSubscribeList();
	}

	private void initSubscribeList() {
		new GroupListenerPlayerChange(this);
		new TeamInfoListenerPlayerChange(this);
		new StoreListenerPlayerChange(this);
		new GFOnlineListenerPlayerChange(this);
	}

	public void addSubscribe(PlayerChangePopertySubscribe subscribe) {
		if (subscribe == null) {
			return;
		}

		this.subscribeList.add(subscribe);
	}

	@Override
	public void playerChangeName(Player p) {
		if (subscribeList.isEmpty()) {
			return;
		}

		for (int i = 0, size = subscribeList.size(); i < size; i++) {
			PlayerChangePopertySubscribe sub = subscribeList.get(i);
			sub.playerChangeName(p);
		}
	}

	@Override
	public void playerChangeLevel(Player p) {
		if (subscribeList.isEmpty()) {
			return;
		}

		for (int i = 0, size = subscribeList.size(); i < size; i++) {
			PlayerChangePopertySubscribe sub = subscribeList.get(i);
			sub.playerChangeLevel(p);
		}
	}

	@Override
	public void playerChangeVipLevel(Player p) {
		if (subscribeList.isEmpty()) {
			return;
		}

		for (int i = 0, size = subscribeList.size(); i < size; i++) {
			PlayerChangePopertySubscribe sub = subscribeList.get(i);
			sub.playerChangeVipLevel(p);
		}
	}

	@Override
	public void playerChangeTemplateId(Player p) {
		if (subscribeList.isEmpty()) {
			return;
		}

		for (int i = 0, size = subscribeList.size(); i < size; i++) {
			PlayerChangePopertySubscribe sub = subscribeList.get(i);
			sub.playerChangeTemplateId(p);
		}
	}

	@Override
	public void playerChangeHeadIcon(Player p) {
		if (subscribeList.isEmpty()) {
			return;
		}

		for (int i = 0, size = subscribeList.size(); i < size; i++) {
			PlayerChangePopertySubscribe sub = subscribeList.get(i);
			sub.playerChangeHeadIcon(p);
		}
	}

	@Override
	public int getObserverType() {
		return ObserverFactory.ObserverType.PLAYER_CHANER.ordinal();
	}

	@Override
	public void playerChangeHeadBox(Player p) {
		// TODO Auto-generated method stub
		if (subscribeList.isEmpty()) {
			return;
		}

		for (int i = 0, size = subscribeList.size(); i < size; i++) {
			PlayerChangePopertySubscribe sub = subscribeList.get(i);
			sub.playerChangeHeadBox(p);
		}
	}
}