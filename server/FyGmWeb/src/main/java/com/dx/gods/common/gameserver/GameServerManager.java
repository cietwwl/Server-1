package com.dx.gods.common.gameserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

public class GameServerManager {
	
	/**
	 * 服务器列表的信息从登陆服获取
	 */
	public final static Map<Integer, GameServer> ServerMap = new HashMap<Integer, GameServer>();
	public final static Map<String, List<GameServer>>VersionServerMap = new HashMap<String, List<GameServer>>();
	public static GSService gsService;
	
	public static String getGSServiceUrl(){
		if(gsService != null){
			return gsService.getAddress() + ":" + gsService.getPort();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static void init(Element element)throws Exception{
		try{
			if (element.getName().equals("GSService")) {
				String address = element.getAttributeValue("address");
				int port = Integer.parseInt(element.getAttributeValue("port"));
				gsService = new GSService(address, port);
			}
			if (element.getName().equals("ServerList")) {
				List<Element> elements2 = element.getChildren();
				for (Element e1 : elements2) {
					int id = Integer.parseInt(e1.getAttributeValue("id"));
					String serverName = e1.getAttributeValue("name");
					String ip = e1.getAttributeValue("ip");
					int port = Integer.parseInt(e1.getAttributeValue("port"));
					String httpurl = e1.getAttributeValue("httpurl");
					int httpport = Integer.parseInt(e1.getAttributeValue("httpport"));
					boolean isLinux = Boolean.parseBoolean(e1.getAttributeValue("islinux"));
					String versionId = e1.getAttributeValue("versionId");
					GameServer gameServer = new GameServer(id, ip, port, httpurl, httpport, serverName, isLinux, versionId);
					ServerMap.put(id, gameServer);
					if(VersionServerMap.containsKey(versionId)){
						List<GameServer> list= VersionServerMap.get(versionId);
						list.add(gameServer);
					}else{
						List<GameServer> list = new ArrayList<GameServer>();
						list.add(gameServer);
						VersionServerMap.put(versionId, list);
					}
				}
			}
		}catch(Exception ex){
			throw new Exception("init GameServerManager异常："+ex.getMessage());
		}
	}
}
