package com.rw.service.Privilege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.common.PlayerEventListener;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.common.stream.IStream;
import com.rw.fsutil.common.stream.IStreamListner;
import com.rw.fsutil.common.stream.StreamImpl;
import com.rw.service.Privilege.datamodel.IPrivilegeConfigSourcer;
import com.rw.service.Privilege.datamodel.PrivilegeConfigHelper;
import com.rwproto.MsgDef;
import com.rwproto.PrivilegeProtos.*;
import com.rwproto.PrivilegeProtos.PrivilegeProperty.Builder;

public class PrivilegeManager
		implements IPrivilegeWare, IPrivilegeManager, PlayerEventListener, IStreamListner<IPrivilegeProvider> {
	private Player m_player;
	private ArrayList<IPrivilegeProvider> privelegeProviders;
	private HashMap<Pair<IPrivilegeConfigSourcer<?>,IPrivilegeProvider>,AllPrivilege> cache;
	private HashMap<Class<? extends Enum<?>>,IStream<PrivilegeProperty>> privilegeNameRouter;

	//TODO 如果配置发生变化，需要对每个玩家调用这个方法重新初始化
	public void initPrivilegeProvider() {
		privilegeNameRouter = new HashMap<Class<? extends Enum<?>>,IStream<PrivilegeProperty>>();
		privilegeNameRouter.put(ArenaPrivilegeNames.class, arenaPrivilege);
		privilegeNameRouter.put(PeakArenaPrivilegeNames.class, peakArenaPrivilege);
		privilegeNameRouter.put(PvePrivilegeNames.class, pvePrivilege);
		privilegeNameRouter.put(GroupPrivilegeNames.class, groupPrivilege);
		privilegeNameRouter.put(StorePrivilegeNames.class, storePrivilege);
		privilegeNameRouter.put(CopyPrivilegeNames.class, copyPrivilege);
		privilegeNameRouter.put(LoginPrivilegeNames.class, loginPrivilege);
		privilegeNameRouter.put(HeroPrivilegeNames.class, heroPrivilege);
		privilegeNameRouter.put(GeneralPrivilegeNames.class, otherPrivilege);
		
		cache = new HashMap<Pair<IPrivilegeConfigSourcer<?>,IPrivilegeProvider>, AllPrivilege>();
		privelegeProviders = new ArrayList<IPrivilegeProvider>(2);
		// 月卡特权比VIP要优先
		IPrivilegeProvider mprovider = MonthCardPrivilegeMgr.getShareInstance().getPrivilige(m_player);
		privelegeProviders.add(mprovider);
		mprovider.getPrivilegeProvider().subscribe(this);
		
		IPrivilegeProvider provider = m_player.getVipMgr();
		privelegeProviders.add(provider);
		provider.getPrivilegeProvider().subscribe(this);
		
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
		System.out.println("特权发生变化:"+provider.getClass().getName());
	}

	@Override
	public void onClose(IStream<IPrivilegeProvider> stream) {
		// 回收资源
		IPrivilegeProvider pro = stream.sample();
		privelegeProviders.remove(pro);
		//cache.clear();
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
	
	private <PrivilegeNameEnums extends Enum<PrivilegeNameEnums>> PrivilegeProperty getPrivilegeDataSet(PrivilegeNameEnums pname){
		if (pname == null) {
			GameLog.error("特权", m_player.getUserId(), "getPrivilegeDataSet:无效特权名");
			return null;
		}
		IStream<PrivilegeProperty> iStream = privilegeNameRouter.get(pname.getClass());
		if (iStream == null) {
			GameLog.error("特权", m_player.getUserId(), "getPrivilegeDataSet:找不到特权名对应的数据:"+pname);
			return null;
		}
		return iStream.sample();
	}
	
	@Override
	public <PrivilegeNameEnums extends Enum<PrivilegeNameEnums>> int getIntPrivilege(PrivilegeNameEnums pname) {
		PrivilegeProperty privilegeDataSet = getPrivilegeDataSet(pname);
		Integer result = PrivilegeConfigHelper.getInstance().getIntPrivilege(privilegeDataSet, pname);
		return result != null ? result : 0;
	}
	
	@Override
	public <PrivilegeNameEnums extends Enum<PrivilegeNameEnums>> boolean getBoolPrivilege(PrivilegeNameEnums pname) {
		PrivilegeProperty privilegeDataSet = getPrivilegeDataSet(pname);
		Boolean result = PrivilegeConfigHelper.getInstance().getBoolPrivilege(privilegeDataSet, pname);
		return result != null ? result : false;
	}
	
	private AllPrivilege.Builder putValueList(IPrivilegeConfigSourcer<?> config,
			List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> newPrivilegeMap) {
		@SuppressWarnings("rawtypes")
		IPrivilegeConfigSourcer tmpcfg = config;
		for (Pair<IPrivilegeProvider, PrivilegeProperty.Builder> pair : newPrivilegeMap) {
			Pair<IPrivilegeConfigSourcer<?>,IPrivilegeProvider> key=Pair.<IPrivilegeConfigSourcer<?>,IPrivilegeProvider>Create(tmpcfg, pair.getT1());
			AllPrivilege old = cache.get(key);
			AllPrivilege.Builder tmp = AllPrivilege.newBuilder();
			config.setValue(tmp, pair.getT2());
			tmp = config.combine(tmp,old);
			cache.put(key, tmp.build());
		}
		AllPrivilege.Builder all = combinePrivilege();
		return all;
	}

	//竞技场
	private StreamImpl<PrivilegeProperty> arenaPrivilege = new StreamImpl<PrivilegeProperty>();
	@Override
	public IStream<PrivilegeProperty> getArenaPrivilege() {
		return arenaPrivilege;
	}
	@Override
	public void putArenaPrivilege(IPrivilegeConfigSourcer<?> config,List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> newPrivilegeMap) {
		AllPrivilege.Builder all = putValueList(config, newPrivilegeMap);
		Builder newValue = config.getValue(all);
		PrivilegeProperty oldValue = arenaPrivilege.sample();
		if (config.eq(oldValue,newValue)){
			return;
		}
		arenaPrivilege.fire(newValue.build());
	}

	//巅峰竞技场
	private StreamImpl<PrivilegeProperty> peakArenaPrivilege = new StreamImpl<PrivilegeProperty>();
	@Override
	public IStream<PrivilegeProperty> getPeakArenaPrivilege() {
		return peakArenaPrivilege;
	}
	@Override
	public void putPeakArenaPrivilege(IPrivilegeConfigSourcer<?> config,List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> newPrivilegeMap) {
		AllPrivilege.Builder all = putValueList(config, newPrivilegeMap);
		peakArenaPrivilege.fire(config.getValue(all).build());
	}

	//PVE试炼
	private StreamImpl<PrivilegeProperty> pvePrivilege = new StreamImpl<PrivilegeProperty>();
	@Override
	public void putPvePrivilege(IPrivilegeConfigSourcer<?> config,
			List<Pair<IPrivilegeProvider, Builder>> newPrivilegeMap) {
		AllPrivilege.Builder all = putValueList(config, newPrivilegeMap);
		pvePrivilege.fire(config.getValue(all).build());
	}
	@Override
	public IStream<PrivilegeProperty> getPVEPrivilege() {
		return pvePrivilege;
	}

	//副本
	private StreamImpl<PrivilegeProperty> copyPrivilege = new StreamImpl<PrivilegeProperty>();
	@Override
	public void putCopyPrivilege(IPrivilegeConfigSourcer<?> config,
			List<Pair<IPrivilegeProvider, Builder>> newPrivilegeMap) {
		AllPrivilege.Builder all = putValueList(config, newPrivilegeMap);
		copyPrivilege.fire(config.getValue(all).build());
	}
	@Override
	public IStream<PrivilegeProperty> getCopyPrivilege() {
		return copyPrivilege;
	}

	//其他模块
	private StreamImpl<PrivilegeProperty> otherPrivilege = new StreamImpl<PrivilegeProperty>();
	@Override
	public void putGeneralPrivilege(IPrivilegeConfigSourcer<?> config,
			List<Pair<IPrivilegeProvider, Builder>> newPrivilegeMap) {
		AllPrivilege.Builder all = putValueList(config, newPrivilegeMap);
		otherPrivilege.fire(config.getValue(all).build());
	}
	@Override
	public IStream<PrivilegeProperty> getGeneralPrivilege() {
		return otherPrivilege;
	}

	//帮派
	private StreamImpl<PrivilegeProperty> groupPrivilege = new StreamImpl<PrivilegeProperty>();
	@Override
	public void putGroupPrivilege(IPrivilegeConfigSourcer<?> config,
			List<Pair<IPrivilegeProvider, Builder>> newPrivilegeMap) {
		AllPrivilege.Builder all = putValueList(config, newPrivilegeMap);
		groupPrivilege.fire(config.getValue(all).build());
	}
	@Override
	public IStream<PrivilegeProperty> getGroupPrivilege() {
		return groupPrivilege;
	}

	//英雄
	private StreamImpl<PrivilegeProperty> heroPrivilege = new StreamImpl<PrivilegeProperty>();
	@Override
	public void putHeroPrivilege(IPrivilegeConfigSourcer<?> config,
			List<Pair<IPrivilegeProvider, Builder>> newPrivilegeMap) {
		AllPrivilege.Builder all = putValueList(config, newPrivilegeMap);
		heroPrivilege.fire(config.getValue(all).build());
	}
	@Override
	public IStream<PrivilegeProperty> getHeroPrivilege() {
		return heroPrivilege;
	}

	//登陆
	private StreamImpl<PrivilegeProperty> loginPrivilege = new StreamImpl<PrivilegeProperty>();
	@Override
	public void putLoginPrivilege(IPrivilegeConfigSourcer<?> config,
			List<Pair<IPrivilegeProvider, Builder>> newPrivilegeMap) {
		AllPrivilege.Builder all = putValueList(config, newPrivilegeMap);
		loginPrivilege.fire(config.getValue(all).build());
	}
	@Override
	public IStream<PrivilegeProperty> getLoginPrivilege() {
		return loginPrivilege;
	}

	//商店
	private StreamImpl<PrivilegeProperty> storePrivilege = new StreamImpl<PrivilegeProperty>();
	@Override
	public void putStorePrivilege(IPrivilegeConfigSourcer<?> config,
			List<Pair<IPrivilegeProvider, Builder>> newPrivilegeMap) {
		AllPrivilege.Builder all = putValueList(config, newPrivilegeMap);
		storePrivilege.fire(config.getValue(all).build());
	}
	@Override
	public IStream<PrivilegeProperty> getStorePrivilege() {
		return storePrivilege;
	}
}
