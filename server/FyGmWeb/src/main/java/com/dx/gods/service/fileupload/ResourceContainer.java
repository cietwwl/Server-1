package com.dx.gods.service.fileupload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.format.annotation.DateTimeFormat;

import com.dx.gods.common.log.GMLogger;
import com.dx.gods.common.utils.GlobalValue;
import com.dx.gods.common.utils.NetUtils;
import com.dx.gods.common.utils.UtilTools;

/**
 * 资源的容器 记录所有的资源版本信息
 * 
 * @author lida
 *
 */
public class ResourceContainer {

	public final static int RESOURCE_TYPE_SERVER = 1; // 服务器资源类型
	public final static int RESOURCE_TYPE_CLIENT = 2; // 客户端资源类型

	private static ResourceContainer instance = new ResourceContainer();
	
	private static AtomicInteger resourceFileNo = new AtomicInteger(10);

	/**
	 * <大版本号，<小版本号,ResourceVersion>>
	 */
	public final static ConcurrentHashMap<String, ConcurrentHashMap<String, ResourceVersion>> ClientResMap = new ConcurrentHashMap<String, ConcurrentHashMap<String, ResourceVersion>>();
	/**
	 * <资源类型，ResourceVersion> 资源类型：1: RESOURCE_TYPE_SERVER 2:
	 * RESOURCE_TYPE_CLIENT
	 */
	public final static Map<Integer, ResourceVersion> CurrentResMap = new HashMap<Integer, ResourceVersion>();

	public static ResourceVersion getCurrentResVersion(int type) {
		return CurrentResMap.get(type);
	}

	public static void setCurrentResVersion(int type,
			ResourceVersion currentResVersion) {
		CurrentResMap.put(type, currentResVersion);
		if (type == RESOURCE_TYPE_CLIENT) {
			String mainVersionNo = currentResVersion.getMainVersionNo();
			String subVersionNo = currentResVersion.getSubVersionNo();
			if (ClientResMap.containsKey(mainVersionNo)) {
				Map<String, ResourceVersion> map = ClientResMap
						.get(mainVersionNo);
				if (!map.containsKey(subVersionNo)) {
					map.put(subVersionNo, currentResVersion);
				}
			} else {
				ConcurrentHashMap<String, ResourceVersion> map = new ConcurrentHashMap<String, ResourceVersion>();
				map.put(subVersionNo, currentResVersion);
				ClientResMap.put(mainVersionNo, map);
			}
		}
		saveResourceFile();
	}

	/**
	 * 返回null 表示没有更新
	 * 
	 * @param mainVersionNo
	 * @param subVersionNo
	 * @return
	 */
	public static String getClientUpdateInfo(String mainVersionNo, String subVersionNo, String versionTime, String clientSeqType) {
		ResourceVersion version = CurrentResMap.get(RESOURCE_TYPE_CLIENT);
		if (version == null) {
			return null;
		}
		if (version.getMainVersionNo().equals(mainVersionNo) && version.getSubVersionNo().equals(subVersionNo)) {
			return null;
		}
		StringBuilder sb = new StringBuilder();

		String strFtpPath = "";
		if(GlobalValue.GM_TYPE == GlobalValue.GM_TYPE_IN){
			strFtpPath = "ftp://" + GlobalValue.FTP_HOSTNAME + ":" + GlobalValue.FTP_PORT + "//patch//client//";
		}else{
			strFtpPath = "ftp://" + GlobalValue.SUB_FTP_HOSTNAME + ":" + GlobalValue.SUB_FTP_PORT + "//patch//client//";
		}
		NetUtils.packNetMessage("ftp_path", strFtpPath, sb);
		NetUtils.packNetMessage("ftp_loginname", GlobalValue.GM_TYPE == GlobalValue.GM_TYPE_IN ? GlobalValue.FTP_LOGIN_NAME : GlobalValue.SUB_FTP_LOGIN_NAME, sb);
		NetUtils.packNetMessage("ftp_loginpwd", GlobalValue.GM_TYPE == GlobalValue.GM_TYPE_IN ? GlobalValue.FTP_LOGIN_PWD : GlobalValue.SUB_FTP_LOGIN_PWD, sb);
		NetUtils.packNetMessage("mainversionno", version.getMainVersionNo(), sb);
		NetUtils.packNetMessage("subversionno", version.getSubVersionNo(), sb);
		
		List<ResourceVersion> updateResourceList = getUpdateResourceList(mainVersionNo, subVersionNo, versionTime, clientSeqType);
		
		int count = updateResourceList.size();
		if (count <= 0) {
			return null;
		}
		// 暂时只获取当前版本的信息
		
		Collections.sort(updateResourceList, new Comparator<ResourceVersion>() {

			@Override
			public int compare(ResourceVersion o1, ResourceVersion o2) {
				long time1 = Long.parseLong(o1.getSubVersionNo());
				long time2 = Long.parseLong(o2.getSubVersionNo());
				if(time1 < time2){
					return -1;
				}else{
					return 1;
				}
			}
		});
		NetUtils.packNetMessage("file_count", count, sb);

		for (int i = 0; i < count; i++) {
			ResourceVersion temp = updateResourceList.get(i);
			StringBuilder sb1 = new StringBuilder();
			
			sb1.append(temp.getMainVersionNo()).append(",")
					.append(temp.getSubVersionNo()).append(",")
					.append(temp.getMd5Value()).append(",")
					.append(temp.getFileSize()).append(",")
					.append(temp.getFileName()).append(",");
			NetUtils.packNetMessage("file" + i, sb1.toString(), sb);
		}

		return sb.toString();
	}
	
	private static List<ResourceVersion> getUpdateResourceList(String mainVersionNo, String subVersionNo, String versionTime, String clientSeqType){
		List<ResourceVersion> list = new LinkedList<ResourceVersion>();
		Date dtVersionTime = UtilTools.parseDateTime(versionTime, "yyyy-MM-dd HH:mm:ss");
		ConcurrentHashMap<String,ResourceVersion> map = ClientResMap.get(mainVersionNo);
		for (Iterator<Entry<String, ResourceVersion>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, ResourceVersion> entry = iterator.next();
			ResourceVersion rv = entry.getValue();
			String strDt = rv.getSubVersionNo();
			
			Date dt1 = UtilTools.parseDateTime(strDt, "yyyyMMddHHmmss");
			long l1 = Long.parseLong(strDt);
			long l2 = Long.parseLong(subVersionNo);
			if(dt1.getTime() > dtVersionTime.getTime() && l1 > l2){
				list.add(rv);
			}
		}
		return list;
	}

	/**
	 * 返回-1表示没有资源更新
	 * @param response
	 * @param mainVersionNo
	 * @param subVersionNo
	 * @param versionTime
	 * @param clientSeqType
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void processClientResUpdateRequest(HttpServletResponse response, String mainVersionNo, String subVersionNo, String versionTime, String clientSeqType) 
			throws UnsupportedEncodingException, IOException {
		response.addHeader("Content-type", "text/html;charset=UTF-8");
		String info = getClientUpdateInfo(mainVersionNo, subVersionNo, versionTime, clientSeqType);
		if (info == null) {
			info = "-1";
		}
		OutputStream stream = response.getOutputStream();
		stream.write(info.getBytes("UTF-8"));
		stream.close();
	}

	/**
	 * 保存当前资源版本管理文件 格式：
	 * 
	 */
	public static void saveResourceFile() {
		SAXBuilder sb = new SAXBuilder();
		File file = new File(GlobalValue.VERSION_CONFIG_PATH+"/"+GlobalValue.VERSION_CONFIG_FILE_NAME);
		try {
			if (file.exists()) {
				file.delete();
			}
			
			Document doc = new Document();
			Element rootElement = new Element("root");
			ResourceVersion serverVersion = CurrentResMap
					.get(RESOURCE_TYPE_SERVER);
			if (serverVersion != null) {
				createResourceVersionNode(rootElement, serverVersion,
						RESOURCE_TYPE_SERVER);
			}

			for (Iterator<Entry<String, ConcurrentHashMap<String, ResourceVersion>>> iterator = ClientResMap
					.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, ConcurrentHashMap<String, ResourceVersion>> next = iterator
						.next();
				Map<String, ResourceVersion> value = next.getValue();
				for (Iterator<Entry<String, ResourceVersion>> iterator2 = value
						.entrySet().iterator(); iterator2.hasNext();) {
					Entry<String, ResourceVersion> next2 = iterator2.next();
					createResourceVersionNode(rootElement, next2.getValue(),
							RESOURCE_TYPE_CLIENT);
				}

			}
			
			Element rv = new Element("resourceFileNo");
			rv.setAttribute("value", String.valueOf(resourceFileNo.get()));
			rootElement.addContent(rv);
			
			doc.setRootElement(rootElement);
			XMLOutputter out = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			out.setFormat(format);
			out.output(doc, new FileOutputStream(file));
			
			if(GlobalValue.GM_TYPE != GlobalValue.GM_TYPE_INTERNET){
				uploadVersionInfoToFtp();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void uploadVersionInfoToFtp() {
		String versionFileName;
		if (GlobalValue.GM_TYPE == GlobalValue.GM_TYPE_IN) {
			versionFileName = GlobalValue.VERSION_CONFIG_FILE_NAME.replace(".xml", resourceFileNo.getAndIncrement() + ".xml");
		} else {
			versionFileName = GlobalValue.VERSION_CONFIG_FILE_NAME;
		}
		FtpManager
				.uploadFileToFtp(GlobalValue.VERSION_FILE_FTP_PATH,
						GlobalValue.VERSION_CONFIG_PATH + "/"
								+ GlobalValue.VERSION_CONFIG_FILE_NAME,
						versionFileName);
	}

	@SuppressWarnings("deprecation")
	public static void createResourceVersionNode(Element head,
			ResourceVersion ver, int versionType) {
		
		Element version = new Element("Resource");
		
		Element type = new Element("type");
		Element mainVersionNo = new Element("mainVersionNo");
		Element subVersionNo = new Element("subVersionNo");
		Element fileSize = new Element("fileSize");
		Element md5Value = new Element("md5Value");
		Element fileName = new Element("fileName");
		version.addContent(type);
		version.addContent(mainVersionNo);
		version.addContent(subVersionNo);
		version.addContent(fileSize);
		version.addContent(md5Value);
		version.addContent(fileName);
		type.setAttribute("value", String.valueOf(versionType));
		mainVersionNo.setAttribute("value", ver.getMainVersionNo());
		subVersionNo.setAttribute("value", ver.getSubVersionNo());
		fileSize.setAttribute("value", String.valueOf(ver.getFileSize()));
		md5Value.setAttribute("value", ver.getMd5Value());
		fileName.setAttribute("value", ver.getFileName());
		head.addContent(version);
	}

	/**
	 * 读取资源版本文件
	 */
	@SuppressWarnings("unchecked")
	public static void readResourceFile() {
		File file = new File(GlobalValue.VERSION_CONFIG_PATH + "/"
				+ GlobalValue.VERSION_CONFIG_FILE_NAME);
		if (!file.exists()) {
			return;
		}
		SAXBuilder sb = new SAXBuilder();

		try {
			Document doc = sb.build(file);
			Element rootElement = doc.getRootElement();
			List<Element> elements = rootElement.getChildren();
			for (Element element : elements) {
				if (element.getName().equals("Resource")) {
					List<Element> elements2 = element.getChildren();
					int type = 0;
					String mainVersionNo = "";
					String subVersionNo = "";
					long fileSize = 0;
					String md5Value = "";
					String fileName = "";
					for (Element element2 : elements2) {
						String value = element2.getAttribute("value").getValue();
						if (element2.getName().equals("type")) {
							
							type = Integer.parseInt(value);
						}
						if (element2.getName().equals("mainVersionNo")) {
							mainVersionNo = value;
						}
						if (element2.getName().equals("subVersionNo")) {
							subVersionNo = value;
						}
						if (element2.getName().equals("fileSize")) {
							fileSize = Long.parseLong(value);
						}
						if (element2.getName().equals("md5Value")) {
							md5Value = value;
						}
						if (element2.getName().equals("fileName")) {
							fileName = value;
						}
					}
					ResourceVersion rv = new ResourceVersion(mainVersionNo,
							subVersionNo, fileSize, md5Value, fileName, type);
					addToCache(rv, true);

				}
				if(element.getName().equals("resourceFileNo")){
					int temp = Integer.parseInt(element.getAttribute("value").getValue());
					resourceFileNo.set(++temp);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void addToCache(ResourceVersion rv, boolean blninit) {
		int type = rv.getResourceType();
		if (type == RESOURCE_TYPE_SERVER) {
			CurrentResMap.put(type, rv);
		} else {
			if (CurrentResMap.containsKey(type)) {
				ResourceVersion tempRv = CurrentResMap.get(type);
				if (ClientResMap.containsKey(rv.getMainVersionNo())) {
					Map<String, ResourceVersion> map = ClientResMap.get(rv
							.getMainVersionNo());
					map.put(rv.getSubVersionNo(), rv);
				} else {
					ConcurrentHashMap<String, ResourceVersion> map = new ConcurrentHashMap<String, ResourceVersion>();
					map.put(rv.getSubVersionNo(), rv);
					ClientResMap.put(rv.getMainVersionNo(), map);
				}
				if (compareResvion(rv, tempRv)) {
					CurrentResMap.put(type, rv);
				}
			} else {
				CurrentResMap.put(type, rv);
				ConcurrentHashMap<String, ResourceVersion> map = new ConcurrentHashMap<String, ResourceVersion>();
				map.put(rv.getSubVersionNo(), rv);
				ClientResMap.put(rv.getMainVersionNo(), map);
			}
		}

		if (!blninit) {
			saveResourceFile();
		}
	}

	public static List<ResourceVersion> readResourceFile(String path) {
		List<ResourceVersion> list = new ArrayList<ResourceVersion>();
		File file = new File(path);
		if (!file.exists()) {
			return list;
		}
		SAXBuilder sb = new SAXBuilder();

		try {
			Document doc = sb.build(file);
			Element rootElement = doc.getRootElement();
			List<Element> elements = rootElement.getChildren();
			for (Element element : elements) {
				if (element.getName().equals("Resource")) {
					List<Element> elements2 = element.getChildren();
					int type = 0;
					String mainVersionNo = "";
					String subVersionNo = "";
					long fileSize = 0;
					String md5Value = "";
					String fileName = "";
					for (Element element2 : elements2) {
						if (element2.getName().equals("type")) {
							type = Integer.parseInt(element2.getAttributeValue("value")
									.toString());
						}
						if (element2.getName().equals("mainVersionNo")) {
							mainVersionNo = element2.getAttributeValue("value")
									.toString();
						}
						if (element2.getName().equals("subVersionNo")) {
							subVersionNo = element2.getAttributeValue("value")
									.toString();
						}
						if (element2.getName().equals("fileSize")) {
							fileSize = Long.parseLong(element2.getAttributeValue(
									"value").toString());
						}
						if (element2.getName().equals("md5Value")) {
							md5Value = element2.getAttributeValue("value").toString();
						}
						if (element2.getName().equals("fileName")) {
							fileName = element2.getAttributeValue("value").toString();
						}
					}
					ResourceVersion rv = new ResourceVersion(mainVersionNo,
							subVersionNo, fileSize, md5Value, fileName, type);

					list.add(rv);
				}
				if(element.getName().equals("resourceFileNo")){
					int temp = Integer.parseInt(element.getAttribute("value").getValue());
					resourceFileNo.set(++temp);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public static boolean compareResvion(ResourceVersion rv1,
			ResourceVersion rv2) {
		long mainVersionNo1 = Long.parseLong(rv1.getMainVersionNo());
		long mainVersionNo2 = Long.parseLong(rv2.getMainVersionNo());
		long subVersionNo1 = Long.parseLong(rv1.getSubVersionNo());
		long subVersionNo2 = Long.parseLong(rv2.getSubVersionNo());
		if (mainVersionNo1 > mainVersionNo2) {
			return true;
		} else {
			if (mainVersionNo1 == mainVersionNo2) {
				if (subVersionNo1 > subVersionNo2) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
}
