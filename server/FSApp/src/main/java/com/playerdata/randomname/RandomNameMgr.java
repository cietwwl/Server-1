package com.playerdata.randomname;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.log.GameLog;
import com.rw.fsutil.shutdown.IShutdownHandler;
import com.rw.fsutil.shutdown.ShutdownService;
import com.rwbase.common.dirtyword.CharFilter;
import com.rwbase.common.dirtyword.CharFilterFactory;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.dao.arena.ArenaRobotCfgDAO;
import com.rwbase.dao.arena.pojo.ArenaRobotCfg;

public class RandomNameMgr {

	private static final char FULL_SPACE = 12288; // 全角空格
	private static final char UIDENTIFIED = 0xfffd; // 乱码
	private static final int BOTTOM_LINE = 5000;

	private static String _randomNameDir = "./randomName";
	private static String _usableNamePathMale = _randomNameDir + File.separator + "usableNamesOfMale.txt";
	private static String _usableNamePathFemale = _randomNameDir + File.separator + "usableNamesOfFemale.txt";
	private static String _idFilePath = "./randomName/idFile.properties";
	private static String _allNamePathMale = RandomNameMgr.class.getResource("/config/randomName/allNamesOfMale.txt").getFile();
	private static String _allNamePathFemale = RandomNameMgr.class.getResource("/config/randomName/allNamesOfFemale.txt").getFile();

	private List<String> _allNamesMale = new ArrayList<String>(); // 所有的男性名字
	private List<String> _allNamesFemale = new ArrayList<String>(); // 所有的女性名字
	private final Queue<String> _usableNamesMale = new ConcurrentLinkedQueue<String>(); // 可用的男性名字
	private final Queue<String> _usableNamesFemale = new ConcurrentLinkedQueue<String>(); // 可用的女性名字
	private AtomicInteger _currentIdMale;
	private AtomicInteger _currentIdFemale;
	private final Map<String, RandomNameFetchRecord> _playerCurrentNames = new ConcurrentHashMap<String, RandomNameFetchRecord>();
	private boolean _hasRemoveSome;

	private static RandomNameMgr _instance = new RandomNameMgr();

	public static RandomNameMgr getInstance() {
		return _instance;
	}

	private int getStringLength(String str) {
		char[] cs = str.toCharArray();
		int length = 0;
		for (int i = cs.length; i-- > 0;) {
			if (cs[i] < 0x80) {
				length++;
			} else {
				length += 2;
			}
		}
		return length;
	}

	private void fillUsableNames(AtomicInteger currentId, int usingId, String currentNamePath, List<String> allnames, Queue<String> targetQueue) throws IOException {
		List<String> tempList;
		File file = new File(currentNamePath);
		if (file.exists() && currentId.get() == usingId) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(currentNamePath), "UTF-8"));
			tempList = new ArrayList<String>();
			String name;
			while ((name = br.readLine()) != null && (name = name.trim()).length() > 0) {
				if (getStringLength(name) > 12) {
					// 大于12个字符
					continue;
				} else if (name.indexOf(UIDENTIFIED) >= 0) {
					continue;
				} else if (name.indexOf(FULL_SPACE) >= 0) {
					continue;
				}
				tempList.add(name);
			}
			br.close();
		} else {
			if (currentId.get() == 0) {
				tempList = new ArrayList<String>(allnames);
			} else {
				tempList = generateNames(allnames, currentId);
			}
		}
		Collections.shuffle(tempList, new Random());
		targetQueue.addAll(tempList);
		tempList.clear();
	}

	private void loadAllNames(String namePath, List<String> allNames) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(namePath), "UTF-8"));
		String name;
		while ((name = br.readLine()) != null && (name = name.trim()).length() > 0) {
			allNames.add(name);
		}
		br.close();
		Collections.shuffle(allNames, new Random());
	}

	private Set<String> getNamesOfRobot() {
		ArenaRobotCfg arenaRobotCfg = ArenaRobotCfgDAO.getInstance().getCfgById("7");
		String[] robotNames = arenaRobotCfg.getData().split(",");
		Set<String> robotNamesSet = new HashSet<String>();
		for (int i = 0; i < robotNames.length; i++) {
			robotNamesSet.add(robotNames[i]);
		}
		return robotNamesSet;
	}
	
	protected List<String> generateNames(List<String> allNames, AtomicInteger currentId) {
		List<String> tempList = new ArrayList<String>();
		String tempName;
		for (int i = 0; i < allNames.size(); i++) {
			tempName = allNames.get(i) + currentId;
			if (getStringLength(tempName) > 12) {
				continue;
			}
			tempList.add(tempName);
		}
		currentId.incrementAndGet();
		return tempList;
	}

	public void init() {
		try {
			File file = new File(_randomNameDir);
			if(!file.exists()) {
				file.mkdir();
			}
			loadAllNames(_allNamePathMale, _allNamesMale);
			loadAllNames(_allNamePathFemale, _allNamesFemale);

			Properties pr = new Properties();
			File idFile = new File(_idFilePath);
			int usingIdMale;
			int usingIdFemale;
			if (idFile.exists()) {
				pr.load(new FileInputStream(_idFilePath));
				_currentIdMale = new AtomicInteger(Integer.parseInt(pr.getProperty("currentIdMale")));
				_currentIdFemale = new AtomicInteger(Integer.parseInt(pr.getProperty("currentIdFemale")));
				usingIdMale = Integer.parseInt(pr.getProperty("usingIdMale"));
				usingIdFemale = Integer.parseInt(pr.getProperty("usingIdFemale"));
			} else {
				_currentIdMale = new AtomicInteger();
				_currentIdFemale = new AtomicInteger();
				usingIdMale = 0;
				usingIdFemale = 0;
			}

			fillUsableNames(_currentIdMale, usingIdMale, _usableNamePathMale, _allNamesMale, _usableNamesMale);
			fillUsableNames(_currentIdFemale, usingIdFemale, _usableNamePathFemale, _allNamesFemale, _usableNamesFemale);
			_hasRemoveSome |= processDirtyWord(_allNamesFemale);
			_hasRemoveSome |= processDirtyWord(_allNamesMale);

			Set<String> namesOfRobot = getNamesOfRobot();
			_usableNamesFemale.removeAll(namesOfRobot);
			_usableNamesMale.removeAll(namesOfRobot);
			
			_hasRemoveSome |= _allNamesFemale.removeAll(namesOfRobot);
			_hasRemoveSome |= _allNamesMale.removeAll(namesOfRobot);
			
			ShutdownService.registerShutdownService(new RandomNameShutdownHandler());
			FSGameTimerMgr.getInstance().submitMinuteTask(new RandomNameChecker(), 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// 处理名字中的敏感字符
	private boolean processDirtyWord(Collection<String> names) {

		boolean hasRemoveSome = false;

		CharFilter charFilter = CharFilterFactory.getCharFilter();

		for (Iterator<String> it = names.iterator(); it.hasNext();) {
			String name = it.next();
			String dirword = charFilter.checkDirtyWord(name, true);
			if (dirword != null) {
				it.remove();
				hasRemoveSome = true;
				GameLog.warn("RandomNameManager", "processDirtyWord", "！！！！！！！！名字：" + name + " 已被剔除，包含非法字符：" + dirword + "！！！！！！！！");
			}
		}

		return hasRemoveSome;
	}
	
	private String fetchRandomName(boolean isFemale) {
		String name = isFemale ? _usableNamesFemale.poll() : _usableNamesMale.poll();
		if (name == null) {
			name = "";
		}
		return name;
	}
	
	private void saveName(Charset cs, String path, Collection<String> nameList) throws Exception {
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(path)), cs);
		for (Iterator<String> itr = nameList.iterator(); itr.hasNext();) {
			writer.write(itr.next());
			writer.write("\n");
		}
		writer.flush();
		writer.close();
	}
	
	/**
	 * 检查随机名字的数量是否到达底线
	 */
	protected void checkUsableRandomNameSize() {
		if (_usableNamesFemale.size() < BOTTOM_LINE) {
			FSGameTimerMgr.getInstance().submitSecondTask(new RandomNameGenerateTask(_allNamesFemale, _currentIdFemale, _usableNamesFemale), 1);
		}
		if (_usableNamesMale.size() < BOTTOM_LINE) {
			FSGameTimerMgr.getInstance().submitSecondTask(new RandomNameGenerateTask(_allNamesMale, _currentIdMale, _usableNamesMale), 1);
		}
	}

	/**
	 * 
	 * 获取一个随机名字
	 * 
	 * @param accountId 账号id
	 * @param isFemale 是否女性
	 * @return
	 */
	public String getRandomName(String accountId, boolean isFemale) {
		String name = fetchRandomName(isFemale);
		RandomNameFetchRecord preRecord = _playerCurrentNames.put(accountId, new RandomNameFetchRecord(name, isFemale));
		if (preRecord != null) {
			Queue<String> preUsableNames = null;
			if (preRecord.isFemale()) {
				preUsableNames = _usableNamesFemale;
			} else {
				preUsableNames = _usableNamesMale;
			}
			String previous = preRecord.getFetchName();
			if (previous != null && previous.length() > 0) {
				preUsableNames.add(previous);
			}
		}
		return name;
	}
	
	/**
	 * 
	 * 销毁一个名字
	 * 
	 * @param name
	 */
	public void destroyName( String name) {
		_usableNamesFemale.remove(name);
		_usableNamesMale.remove(name);
	}

	/**
	 * 
	 * 通知某个名字使用了
	 * 
	 * @param accountId
	 * @param name
	 */
	public void notifyNameUsed(String accountId, String name) {
		RandomNameFetchRecord fetchRecord = _playerCurrentNames.remove(accountId);
		boolean removed = false;
		if (fetchRecord != null) {
			String fetchName = fetchRecord.getFetchName();
			if (fetchName.length() > 0) {
				if (!fetchName.equals(name)) {
					// 两个名字不一样
					Queue<String> queue = fetchRecord.isFemale() ? _usableNamesFemale : _usableNamesMale;
					queue.add(fetchName); // 把之前取的名字加回去
				} else {
					removed = true;
				}
			}
		}
		if (!removed) {
			destroyName(name);
		}
	}

	protected void saveNames() {
		try {
			Charset cs = Charset.forName("UTF-8");

			if (_hasRemoveSome) {
				saveName(cs, _allNamePathMale, _allNamesMale);
				saveName(cs, _allNamePathFemale, _allNamesFemale);
				_hasRemoveSome = false;
			}

			saveName(cs, _usableNamePathMale, _usableNamesMale);
			saveName(cs, _usableNamePathFemale, _usableNamesFemale);

			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(_idFilePath)), cs);
			Properties pr = new Properties();
			pr.put("currentIdFemale", String.valueOf(_currentIdFemale));
			pr.put("usingIdFemale", String.valueOf(_currentIdFemale));
			pr.put("currentIdMale", String.valueOf(_currentIdMale));
			pr.put("usingIdMale", String.valueOf(_currentIdMale));
			pr.store(writer, null);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			GameLog.error("RandomNameMgr", "shutdown", "随机名字数据回写时出现异常！！", e);
		}
	}

	protected static class RandomNameShutdownHandler implements IShutdownHandler {

		@Override
		public void notifyShutdown() {
			RandomNameMgr.getInstance().saveNames();
		}

	}
}
