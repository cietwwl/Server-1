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
import com.rw.service.Privilege.datamodel.BoolPropertyWriter;
import com.rw.service.Privilege.datamodel.IPrivilegeConfigSourcer;
import com.rw.service.Privilege.datamodel.IntPropertyWriter;
import com.rw.service.Privilege.datamodel.PrivilegeConfigHelper;
import com.rw.service.Privilege.datamodel.arenaPrivilegeHelper;
import com.rw.service.Privilege.datamodel.peakArenaPrivilegeHelper;
import com.rwproto.MsgDef;
import com.rwproto.PrivilegeProtos.AllPrivilege;
import com.rwproto.PrivilegeProtos.ArenaPrivilegeNames;
import com.rwproto.PrivilegeProtos.PeakArenaPrivilegeNames;
import com.rwproto.PrivilegeProtos.PrivilegeProperty;
import com.rwproto.PrivilegeProtos.PrivilegeValue;

public class PrivilegeManager
		implements IPrivilegeWare, IPrivilegeManager, PlayerEventListener, IStreamListner<IPrivilegeProvider> {
	private Player m_player;
	private ArrayList<IPrivilegeProvider> privelegeProviders;
	private HashMap<Pair<IPrivilegeConfigSourcer<?>,IPrivilegeProvider>,AllPrivilege> cache;
	
	//TODO 如果配置发生变化，需要对每个玩家调用这个方法重新初始化
	public void initPrivilegeProvider() {
		cache = new HashMap<Pair<IPrivilegeConfigSourcer<?>,IPrivilegeProvider>, AllPrivilege>();
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
		Iterable<IPrivilegeConfigSourcer<?>> cfgSources = PrivilegeConfigHelper.getInstance().getSources();
		Collection<AllPrivilege> vals = cache.values();
		AllPrivilege.Builder result = null;
		for (IPrivilegeConfigSourcer<?> cfgsrc : cfgSources) {
			for (AllPrivilege pri : vals) {
				result = cfgsrc.combine(result, pri);
			}
		}
		return result;
	}

	//每个provider计算特权点，并缓存起来
	private void privilegeByProvider(List<IPrivilegeProvider> providers){
		Iterable<IPrivilegeConfigSourcer<?>> cfgSources = PrivilegeConfigHelper.getInstance().getSources();
		for (IPrivilegeConfigSourcer<?> cfgsrc : cfgSources) {
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
		// 回收资源
		privelegeProviders.clear();
		cache.clear();
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		//不会调用init
		//init(player);
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
		// 玩家初始化
		m_player = player;
		initPrivilegeProvider();
	}
	
	private <PrivilegeNameEnums extends Enum<PrivilegeNameEnums>> PrivilegeValue getPrivilegeProperty(
			PrivilegeProperty privilegeDataSet, PrivilegeNameEnums pname) {
		if (privilegeDataSet == null || pname == null) {
			return null;
		}
		
		if (privilegeDataSet.getKvCount() < pname.ordinal()+1){
			return null;
		}
		
		String priName = pname.name();
		PrivilegeValue kv = privilegeDataSet.getKv(pname.ordinal());
		if (!priName.equals(kv.getName())){
			return null;
		}
		return kv;
	}
	
	public <PrivilegeNameEnums extends Enum<PrivilegeNameEnums>> Integer getIntPrivilege(
			PrivilegeProperty privilegeDataSet, PrivilegeNameEnums pname) {
		PrivilegeValue kv = getPrivilegeProperty(privilegeDataSet,pname);
		if (kv == null){
			return null;
		}
		
		IntPropertyWriter pwriter = IntPropertyWriter.getShareInstance();
		return pwriter.extractVal(kv.getValue());
	}
	
	public <PrivilegeNameEnums extends Enum<PrivilegeNameEnums>> Boolean getBoolPrivilege(
			PrivilegeProperty privilegeDataSet, PrivilegeNameEnums pname) {
		PrivilegeValue kv = getPrivilegeProperty(privilegeDataSet,pname);
		if (kv == null){
			return null;
		}
		
		BoolPropertyWriter pwriter = BoolPropertyWriter.getShareInstance();
		return pwriter.extractVal(kv.getValue());
	}
	
	//竞技场
	private StreamImpl<PrivilegeProperty> arenaPrivilege = new StreamImpl<PrivilegeProperty>();

	@Override
	public IStream<PrivilegeProperty> getArenaPrivilege() {
		return arenaPrivilege;
	}

	@Override
	public Object getArenaPri(ArenaPrivilegeNames pname) {
		PrivilegeProperty currentPri = arenaPrivilege.sample();
		Object val = arenaPrivilegeHelper.getInstance().getValue(currentPri,pname);
		return val;
	}

	@Override
	public void putArenaPrivilege(IPrivilegeConfigSourcer<?> config,List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> newPrivilegeMap) {
		AllPrivilege.Builder all = putValueList(config, newPrivilegeMap);//tmp.setArena(pair.getT2());
		arenaPrivilege.fire(config.getValue(all).build());//all.getArena()
	}

	private AllPrivilege.Builder putValueList(IPrivilegeConfigSourcer<?> config,
			List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> newPrivilegeMap) {
		for (Pair<IPrivilegeProvider, PrivilegeProperty.Builder> pair : newPrivilegeMap) {
			Pair<IPrivilegeConfigSourcer<?>,IPrivilegeProvider> key=Pair.Create(config, pair.getT1());
			AllPrivilege old = cache.get(key);
			AllPrivilege.Builder tmp = AllPrivilege.newBuilder();
			config.setValue(tmp, pair.getT2());
			tmp = config.combine(tmp,old);
			cache.put(key, tmp.build());
		}
		AllPrivilege.Builder all = combinePrivilege();
		return all;
	}

	//巅峰竞技场
	private StreamImpl<PrivilegeProperty> peakArenaPrivilege = new StreamImpl<PrivilegeProperty>();
	
	@Override
	public IStream<PrivilegeProperty> getPeakArenaPrivilege() {
		return peakArenaPrivilege;
	}

	@Override
	public void putPeakArenaPrivilege(IPrivilegeConfigSourcer<?> config,List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> newPrivilegeMap) {
		AllPrivilege.Builder all = putValueList(config, newPrivilegeMap);//tmp.setPeakArena(pair.getT2());
		peakArenaPrivilege.fire(config.getValue(all).build());//all.getPeakArena()
	}

	@Override
	public Object getPeakArenaPri(PeakArenaPrivilegeNames pname) {
		PrivilegeProperty currentPri = peakArenaPrivilege.sample();
		Object val = peakArenaPrivilegeHelper.getInstance().getValue(currentPri,pname);
		return val;
	}

}
