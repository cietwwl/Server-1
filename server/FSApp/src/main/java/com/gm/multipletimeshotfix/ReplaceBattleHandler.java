package com.gm.multipletimeshotfix;

import java.util.concurrent.Callable;

public class ReplaceBattleHandler implements Callable<Object> {

//	private void replaceHandler(Class<?> targetClass, String fieldName, Object refObject, Object newObj, StringBuilder strBld) throws Exception {
//		Field declaredField = targetClass.getDeclaredField(fieldName);
//		declaredField.setAccessible(true);
//		declaredField.set(refObject, newObj);
//		declaredField.setAccessible(false);
//		Method[] allMethods = targetClass.getDeclaredMethods();
//		for (Method m : allMethods) {
//			if (m.getReturnType() == targetClass) {
//				m.setAccessible(true);
//				strBld.append(targetClass.getSimpleName()).append("#").append(m.getName()).append("=").append(m.invoke(null)).append("|");
//				m.setAccessible(false);
//			}
//		}
//	}
//
//	@SuppressWarnings("rawtypes")
//	//@Override
//	public Object call() throws Exception {
//		Field fNettyController = GameLogicTask.class.getDeclaredField("nettyControler");
//		Field fCommandMap = FsNettyControler.class.getDeclaredField("commandMap");
//
//		fNettyController.setAccessible(true);
//		fCommandMap.setAccessible(true);
//
//		FsNettyControler controller = (FsNettyControler) fNettyController.get(null);
//		@SuppressWarnings("unchecked")
//		Map<Command, FsService> map = (Map<Command, FsService>) fCommandMap.get(controller);
//		map.put(Command.MSG_BATTLE_TOWER, new BattleTowerServiceHF());
//
//		StringBuilder strBld = new StringBuilder();
//		CopyHandlerHF copyHandler = new CopyHandlerHF();
//		replaceHandler(CopyHandler.class, "instance", null, copyHandler, strBld);
//		replaceHandler(CopyService.class, "copyHandler", map.get(Command.MSG_CopyService), copyHandler, strBld);
//
//		ArenaHandlerHF arenaHandler = new ArenaHandlerHF();
//
//		replaceHandler(ArenaHandler.class, "instance", null, arenaHandler, strBld);
//		replaceHandler(ArenaService.class, "arenaHandler", map.get(Command.MSG_ARENA), arenaHandler, strBld);
//
//		PeakArenaHandlerHF peakArenaHandler = new PeakArenaHandlerHF();
//
//		replaceHandler(PeakArenaHandler.class, "instance", null, peakArenaHandler, strBld);
//		replaceHandler(PeakArenaService.class, "peakArenaHandler", map.get(Command.MSG_PEAK_ARENA), peakArenaHandler, strBld);
//
//		MagicSecretHandlerHF magicHandler = new MagicSecretHandlerHF();
//
//		replaceHandler(MagicSecretHandler.class, "instance", null, magicHandler, strBld);
//		replaceHandler(MagicSecretService.class, "mHandler", map.get(Command.MSG_MAGIC_SECRET), magicHandler, strBld);
//
//		GroupCopyBattleHandlerHF groupHandler = new GroupCopyBattleHandlerHF();
//		replaceHandler(GroupCopyBattleHandler.class, "instance", null, groupHandler, strBld);
//
//		replaceHandler(RandomBossMsgHandler.class, "handler", null, new RandomBossMsgHandlerHF(), strBld);
//
//		TeamBattleHandlerHF teamBattleHandler = new TeamBattleHandlerHF();
//		
//		replaceHandler(TeamBattleHandler.class, "instance", null, teamBattleHandler, strBld);
//
//		replaceHandler(TeamBattleService.class, "mHandler", map.get(Command.MSG_TEAM_BATTLE), teamBattleHandler, strBld);
//		
//		
//		fCommandMap.setAccessible(false);
//		fNettyController.setAccessible(false);
//
//		strBld.append("map.get(Command.MSG_BATTLE_TOWER)=" + map.get(Command.MSG_BATTLE_TOWER));
//		return "替换成功！" + strBld;
//	}
	
	public Object call() {
		return "Nothing happen";
	}

}
