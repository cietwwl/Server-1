package com.dx.gods.service.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.tmatesoft.sqljet.core.internal.lang.SqlParser.result_column_return;
import org.tmatesoft.svn.core.wc.SVNClientManager;

import com.dx.gods.common.log.GMLogger;
import com.dx.gods.common.utils.SvnUtil;
import com.dx.gods.common.utils.UtilTools;

public class FileSVNCache {
	/**
	 * <svnid, <filepath, FileSVNInfo>>
	 */
	public final static ConcurrentHashMap<String, ConcurrentHashMap<String, FileSVNInfo>> FILE_SVN_CACHE = new ConcurrentHashMap<String, ConcurrentHashMap<String, FileSVNInfo>>();
	/**
	 * <svnid, <filepath, FileSVNInfo>>
	 */
	public final static ConcurrentHashMap<String, ConcurrentHashMap<String, FileSVNInfo>> UN_GENERATE_MAP = new ConcurrentHashMap<String, ConcurrentHashMap<String,FileSVNInfo>>();
	
	public final static String VERSION_FILE_NAME = "SVNVersionInfo.xml";
	public final static String CACHE_FOLDER_NAME = "Cache";
	public final static String UPLOAD_FOLDER_NAME = "UPLOAD";
	public final static String UPLOAD_CLIENT_FOLDER_NAME = "UPLOAD_CLIENT/Assets/Config";
	public final static String UPLOAD_ZIPFILE_FOLDER_NAME = "ZIP";

	public static void getFileSVNInfo(SVNClientManager svnClinet, String path,
			ConcurrentHashMap<String, FileSVNInfo> fileMap, String[] filters, int deeper) throws Exception {

		File file = new File(path);
		File[] listFiles = file.listFiles();
		deeper--;
		for (File file2 : listFiles) {
			if(file2.getName().indexOf(VERSION_FILE_NAME)!= -1 || file2.getName().indexOf(CACHE_FOLDER_NAME)!=-1){
				continue;
			}
			if (file2.isDirectory()) {
				if(deeper == 0){
					continue;
				}
				getFileSVNInfo(svnClinet, file2.getAbsolutePath(), fileMap, filters, deeper);
			} else {
				if (UtilTools.checkFilter(file2.getName(), filters)) {
					long svnVersionNo = SvnUtil.getFileSVNVersionNo(svnClinet,
							file2);
					FileSVNInfo svnInfo = new FileSVNInfo(file2.getName(),
							file2.getAbsolutePath(), svnVersionNo);
					fileMap.put(file2.getAbsolutePath(), svnInfo);
				}
			}
		}

	}
	
	/**
	 * 保存对应svn work copy的文件版本信息
	 * xml 格式
	 * <root>
	 * <svnworkcopypath></svnworkcopypath>
	 * <svnInfo>
	 * <filesvninfo>
	 * 	<name/>
	 * 	<path/>
	 * 	<svnVersion/>
	 *  <blnGenerate/>
	 * </filesvninfo>
	 * <svnInfo>
	 * </root>
	 * @param svnWorkCopyPath
	 */
	public static void saveFileSVNInfo(String svnWorkCopyPath, String svnId){
		if(FILE_SVN_CACHE.containsKey(svnId)){
			ConcurrentHashMap<String,FileSVNInfo> map = FILE_SVN_CACHE.get(svnId);
			
			SAXBuilder sb = new SAXBuilder();
			File file = new File(svnWorkCopyPath + "/" + VERSION_FILE_NAME);
			try{
				if(file.exists()){
					file.delete();
				}
				
				Document doc = new Document();
				Element rootElement = new Element("root");
				
				Element e_svnworkcopypath = new Element("svnworkcopypath");
				rootElement.addContent(e_svnworkcopypath);
				e_svnworkcopypath.setAttribute("value", svnWorkCopyPath);
				Element e_svninfo = new Element("svnInfo");
				rootElement.addContent(e_svninfo);
				for (Iterator<Entry<String, FileSVNInfo>> iterator = map.entrySet().iterator(); iterator
						.hasNext();) {
					Entry<String, FileSVNInfo> next = iterator.next();
					createFileSVNInfoNode(e_svninfo, next.getValue());
				}
				doc.setRootElement(rootElement);
				XMLOutputter out = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				out.setFormat(format);
				out.output(doc, new FileOutputStream(file));
			}catch(Exception ex){
				ex.printStackTrace();
				GMLogger.error("生成work copy的文件版本信息文件出错！");
			}
		}
	}
	
	/**
	 * 初始化文件svn信息
	 * @param svnWorkCopyPath
	 */
	public static boolean readFileSVNInfo(String svnWorkCopyPath, String svnId) {
		SAXBuilder sb = new SAXBuilder();
		File file = new File(svnWorkCopyPath + "/" + VERSION_FILE_NAME);
		if (!file.exists()) {
			return false;
		}
		try {
			Document doc = sb.build(file);
			Element rootElement = doc.getRootElement();
			List<Element> list = rootElement.getChildren();
			String svnworkcopypath = "";
			ConcurrentHashMap<String, FileSVNInfo> map = new ConcurrentHashMap<String, FileSVNInfo>();
			for (Element element : list) {
				if (element.getName().equals("svnworkcopypath")) {
					svnworkcopypath = element.getAttributeValue("value");
				}
				if (element.getName().equals("svnInfo")) {
					List<Element> svnInfos = element.getChildren();

					for (Element element2 : svnInfos) {
						FileSVNInfo svnInfo = decodeFileSvnInfo(element2);
						map.put(svnInfo.getPath(), svnInfo);
					}

				}
			}
			if (!svnworkcopypath.equals("") && map.size() > 0) {
				FILE_SVN_CACHE.put(svnId, map);
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	private static FileSVNInfo decodeFileSvnInfo(Element element){
		List<Element> children = element.getChildren();
		String name = "";
		String path = "";
		long svnVersion = 0;
		boolean blnGenerate = false;
		long lastUpdateTime = 0;
		for (Element value : children) {
			if (value.getName().equals("name")) {
				name = value.getAttributeValue("value");
				continue;
			}
			if (value.getName().equals("path")) {
				path = value.getAttributeValue("value");
				continue;
			}
			if (value.getName().equals("svnVersion")) {
				if(value.getAttributeValue("value") == null || value.getAttributeValue("value").toString().equals("")){
					svnVersion = 0;
				}else{
					svnVersion = Long.parseLong(value.getAttributeValue("value"));
				}
				continue;
			}
			if (value.getName().equals("blnGenerate")) {
				if(value.getAttributeValue("value") == null || value.getAttributeValue("value").toString().equals("")){
					blnGenerate = false;
				}else{
					blnGenerate = Boolean.parseBoolean(value.getAttributeValue("value"));
				}
				continue;
			}
			if (value.getName().equals("lastUpdateTime")) {
				if(value.getAttributeValue("value") == null || value.getAttributeValue("value").toString().equals("")){
					lastUpdateTime = 0;
				}else{
					lastUpdateTime = Long.parseLong(value.getAttributeValue("value"));
				}
				continue;
			}
		}
		FileSVNInfo svnInfo = new FileSVNInfo(name, path, svnVersion, blnGenerate, lastUpdateTime);
		return svnInfo;
	}
	
	/**
	 * <filesvninfo>
	 * 	<name/>
	 * 	<path/>
	 * 	<svnVersion/>
	 *  <blnGenerate/>
	 * </filesvninfo>
	 * @param head
	 * @param svnInfo
	 */
	public static void createFileSVNInfoNode(Element head, FileSVNInfo svnInfo) {
		
		Element filesvninfo = new Element("filesvninfo");
		
		Element name = new Element("name");
		Element path = new Element("path");
		Element svnVersion = new Element("svnVersion");
		Element blnGenerate = new Element("blnGenerate");
		Element lastUpdateTime = new Element("lastUpdateTime");
		filesvninfo.addContent(name);
		filesvninfo.addContent(path);
		filesvninfo.addContent(svnVersion);
		filesvninfo.addContent(blnGenerate);
		name.setAttribute("value", svnInfo.getName());
		path.setAttribute("value", svnInfo.getPath());
		svnVersion.setAttribute("value", String.valueOf(svnInfo.getSvnVersion()));
		blnGenerate.setAttribute("value", String.valueOf(svnInfo.isBlnGenerate()));
		lastUpdateTime.setAttribute("value", String .valueOf(svnInfo.getLastUpdateTime()));
		head.addContent(filesvninfo);
	}
	
	public static List<FileSVNInfo> getNeedGenerateList(String svnId){
		List<FileSVNInfo> result = new ArrayList<FileSVNInfo>();
		ConcurrentHashMap<String, FileSVNInfo> needGenerateMap = UN_GENERATE_MAP
				.get(svnId);
		if (needGenerateMap == null) {
			ConcurrentHashMap<String, FileSVNInfo> concurrentHashMap = FILE_SVN_CACHE
					.get(svnId);
			if(concurrentHashMap == null){
				return null;
			}
			for (Iterator<Entry<String, FileSVNInfo>> iterator = concurrentHashMap
					.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, FileSVNInfo> next = iterator.next();
				FileSVNInfo svnInfo = next.getValue();
				if (!svnInfo.isBlnGenerate()) {
					result.add(svnInfo);
				}
			}
		} else {
			result.addAll(needGenerateMap.values());
		}
		return result;
	}
	
}
