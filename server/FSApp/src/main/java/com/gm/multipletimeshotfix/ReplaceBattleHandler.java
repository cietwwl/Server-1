package com.gm.multipletimeshotfix;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;

import com.playerdata.mgcsecret.service.MagicSecretHandler;
import com.rw.controler.FsNettyControler;
import com.rw.controler.GameLogicTask;
import com.rw.service.FsService;
import com.rw.service.PeakArena.PeakArenaHandler;
import com.rw.service.arena.ArenaHandler;
import com.rw.service.copy.CopyHandler;
import com.rw.service.groupCopy.GroupCopyBattleHandler;
import com.rw.service.ranodmBoss.RandomBossMsgHandler;
import com.rwproto.MsgDef.Command;

public class ReplaceBattleHandler implements Callable<Object> {
	
	private void replaceHandler(Class<?> targetClass, String fieldName, Object newObj, StringBuilder strBld) throws Exception {
		Field declaredField = targetClass.getDeclaredField(fieldName);
		declaredField.setAccessible(true);
		declaredField.set(null, newObj);
		declaredField.setAccessible(false);
		Method[] allMethods = targetClass.getDeclaredMethods();
		for (Method m : allMethods) {
			if (m.getReturnType() == targetClass) {
				m.setAccessible(true);
				strBld.append(targetClass.getSimpleName()).append("#").append(m.getName()).append("=").append(m.invoke(null)).append("|");
				m.setAccessible(false);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object call() throws Exception {
		StringBuilder strBld = new StringBuilder();
		replaceHandler(CopyHandler.class, "instance", new CopyHandlerHF(), strBld);
		replaceHandler(ArenaHandler.class, "instance", new ArenaHandlerHF(), strBld);
		replaceHandler(PeakArenaHandler.class, "instance", new PeakArenaHandlerHF(), strBld);
		replaceHandler(MagicSecretHandler.class, "instance", new MagicSecretHandlerHF(), strBld);
		replaceHandler(GroupCopyBattleHandler.class, "instance", new GroupCopyBattleHandlerHF(), strBld);
		replaceHandler(RandomBossMsgHandler.class, "handler", new RandomBossMsgHandlerHF(), strBld);	

		Field fNettyController = GameLogicTask.class.getDeclaredField("nettyControler");
		Field fCommandMap = FsNettyControler.class.getDeclaredField("commandMap");
		
		fNettyController.setAccessible(true);
		fCommandMap.setAccessible(true);

		FsNettyControler controller = (FsNettyControler) fNettyController.get(null);
		@SuppressWarnings("unchecked")
		Map<Command, FsService> map = (Map<Command, FsService>) fCommandMap.get(controller);
		map.put(Command.MSG_BATTLE_TOWER, new BattleTowerServiceHF());

		fCommandMap.setAccessible(false);
		fNettyController.setAccessible(false);
		
		strBld.append("map.get(Command.MSG_BATTLE_TOWER)=" + map.get(Command.MSG_BATTLE_TOWER));
		return "替换成功！" + strBld;
	}

}
