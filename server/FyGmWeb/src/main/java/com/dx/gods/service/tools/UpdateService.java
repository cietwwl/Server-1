package com.dx.gods.service.tools;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

/**
 * 编译更新入口
 * 
 * @author lida
 *
 */
public class UpdateService {

	private static UpdateService instance = new UpdateService();

	public static UpdateService getInstance() {
		if (instance == null) {
			instance = new UpdateService();
		}
		return instance;
	}
	private UpdateClassInfo updateClassInfo;
	
	@SuppressWarnings("unchecked")
	public void init(Element element) throws Exception {

		try {
			List<String> classPath = new ArrayList<String>();
			List<String> libPath = new ArrayList<String>();
			List<Element> children = element.getChildren();
			for (Element element2 : children) {
				if (element2.getName().equals("classes")) {
					List<Element> children2 = element2.getChildren();
					for (Element element3 : children2) {
						String path = element3.getAttributeValue("value");
						classPath.add(path);
					}
				}
				if (element2.getName().equals("lib")) {
					List<Element> children2 = element2.getChildren();
					for (Element element3 : children2) {
						String path = element3.getAttributeValue("value");
						libPath.add(path);
					}
				}
			}
			updateClassInfo = new UpdateClassInfo(classPath, libPath);
		} catch (Exception ex) {
			throw new Exception("初始化编译更新模块失败:" + ex.getMessage());
		}
	}

	

	public UpdateClassInfo getUpdateClassInfo() {
		return updateClassInfo;
	}

	public void setUpdateClassInfo(UpdateClassInfo updateClassInfo) {
		this.updateClassInfo = updateClassInfo;
	}

}
