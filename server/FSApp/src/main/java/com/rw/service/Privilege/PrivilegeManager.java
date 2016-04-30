package com.rw.service.Privilege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.common.PlayerEventListener;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.common.stream.IStream;
import com.rw.fsutil.common.stream.IStreamListner;
import com.rw.fsutil.common.stream.StreamImpl;
import com.rw.service.Privilege.datamodel.IPrivilegeConfigSourcer;
import com.rw.service.Privilege.datamodel.PrivilegeConfigHelper;
import com.rwproto.MsgDef;
import com.rwproto.PrivilegeProtos.AllPrivilege;
import com.rwproto.PrivilegeProtos.ArenaPrivilege;
import com.rwproto.PrivilegeProtos.PeakArenaPrivilege;

public class PrivilegeManager
		implements IPrivilegeWare, IPrivilegeManager, PlayerEventListener, IStreamListner<IPrivilegeProvider> {
	private Player m_player;
	private ArrayList<IPrivilegeProvider> privelegeProviders;
	private HashMap<Pair<IPrivilegeConfigSourcer,IPrivilegeProvider>,AllPrivilege.Builder> cache;
	
	//TODO 如果配置发生变化，需要对每个玩家调用这个方法重新初始化
	public void initPrivilegeProvider() {
		cache = new HashMap<Pair<IPrivilegeConfigSourcer,IPrivilegeProvider>, AllPrivilege.Builder>();
		privelegeProviders = new ArrayList<IPrivilegeProvider>(2);
		IPrivilegeProvider provider = m_player.getVipMgr();
		privelegeProviders.add(provider);
		provider.getPrivilegeProvider().subscribe(this);
		// TODO 月卡特权待增加，月卡应该比VIP要优先
		
		//每个provider计算特权点，并缓存起来
		privilegeByProvider(privelegeProviders);
		//综合各个provider的结果：求最大值
		combinePrivilege();
	}
	
	// 综合各个provider的结果
	private AllPrivilege.Builder combinePrivilege() {
		Iterable<IPrivilegeConfigSourcer> cfgSources = PrivilegeConfigHelper.getInstance().getSources();
		Collection<AllPrivilege.Builder> vals = cache.values();
		AllPrivilege.Builder result = AllPrivilege.newBuilder();
		for (AllPrivilege.Builder pri : vals) {
			for (IPrivilegeConfigSourcer cfgsrc : cfgSources) {
				result = cfgsrc.combine(result, pri);
			}
		}
		return result;
	}

	//每个provider计算特权点，并缓存起来
	private void privilegeByProvider(List<IPrivilegeProvider> providers){
		Iterable<IPrivilegeConfigSourcer> cfgSources = PrivilegeConfigHelper.getInstance().getSources();
		for (IPrivilegeConfigSourcer cfgsrc : cfgSources) {
			// 使用double dispatch方式，将特权分派到对应的特权数据流中:
			// IPrivilegeConfigSourcer->XXXCfgHelper->IPrivilegeWare->putXXXprivilege
			cfgsrc.putPrivilege(this, providers);
		}
	}

	@Override
	public void onChange(IPrivilegeProvider provider) {
		ArrayList<IPrivilegeProvider> lst = new ArrayList<IPrivilegeProvider>(1);
		lst.add(provider);
		privilegeByProvider(lst);
		sendPrivilegeData();
	}

	@Override
	public void onClose() {
		// TODO 回收资源
		privelegeProviders.clear();
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		// TODO 玩家第一次创建
		m_player = player;
		initPrivilegeProvider();
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		// 发送特权数据到客户端
		sendPrivilegeData();
	}

	private void sendPrivilegeData() {
		AllPrivilege.Builder pri = combinePrivilege();
		m_player.SendMsg(MsgDef.Command.MSG_PRIVILEGE, pri.build().toByteString());
	}

	@Override
	public void init(Player player) {
		// TODO 玩家初始化
		m_player = player;
		//TODO 检查是否重复初始化
		initPrivilegeProvider();
	}
	
	
	//竞技场
	private StreamImpl<ArenaPrivilege> arenaPrivilege = new StreamImpl<ArenaPrivilege>();

	@Override
	public IStream<ArenaPrivilege> getArenaPrivilege() {
		return arenaPrivilege;
	}

	@Override
	public void putArenaPrivilege(IPrivilegeConfigSourcer config,List<Pair<IPrivilegeProvider, ArenaPrivilege.Builder>> newPrivilegeMap) {
		// 更新缓存
		for (Pair<IPrivilegeProvider, ArenaPrivilege.Builder> pair : newPrivilegeMap) {
			Pair<IPrivilegeConfigSourcer,IPrivilegeProvider> key=Pair.Create(config, pair.getT1());
			AllPrivilege.Builder old = cache.get(key);
			if (old == null){
				old = AllPrivilege.newBuilder();
			}
			AllPrivilege.Builder tmp = AllPrivilege.newBuilder();
			tmp.setArena(pair.getT2());
			old = config.combine(old, tmp);
			cache.put(key, old);
		}
		AllPrivilege.Builder all = combinePrivilege();
		arenaPrivilege.fire(all.getArena());
	}

	//巅峰竞技场
	private StreamImpl<PeakArenaPrivilege> peakArenaPrivilege = new StreamImpl<PeakArenaPrivilege>();
	
	@Override
	public IStream<PeakArenaPrivilege> getPeakArenaPrivilege() {
		return peakArenaPrivilege;
	}

	@Override
	public void putPeakArenaPrivilege(IPrivilegeConfigSourcer config,List<Pair<IPrivilegeProvider, PeakArenaPrivilege.Builder>> newPrivilegeMap) {
		for (Pair<IPrivilegeProvider, PeakArenaPrivilege.Builder> pair : newPrivilegeMap) {
			Pair<IPrivilegeConfigSourcer,IPrivilegeProvider> key=Pair.Create(config, pair.getT1());
			AllPrivilege.Builder old = cache.get(key);
			if (old == null){
				old = AllPrivilege.newBuilder();
			}
			AllPrivilege.Builder tmp = AllPrivilege.newBuilder();
			tmp.setPeakArena(pair.getT2());
			old = config.combine(old, tmp);
			cache.put(key, old);
		}
		AllPrivilege.Builder all = combinePrivilege();
		peakArenaPrivilege.fire(all.getPeakArena());
	}

}
