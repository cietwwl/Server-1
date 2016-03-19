package com.dx.gods.service.tools.controller;

import java.util.ArrayList;
import java.util.List;

import com.dx.gods.controller.admin.common.DXAdminController;
import com.dx.gods.service.tools.ProjectVersion;
import com.dx.gods.service.tools.SVNService;
import com.dx.gods.service.tools.SVNWorkCopy;

public class ShowSVNIndexController extends DXAdminController{
	private List<ProjectVersion> list;
	
	public String getSVNList(){
		list = new ArrayList<ProjectVersion>();
		list.addAll(SVNService.ProjectMap.values());
		return SUCCESS;
	}

	public List<ProjectVersion> getList() {
		return list;
	}

	public void setList(List<ProjectVersion> list) {
		this.list = list;
	}
	
}
