package com.fy.version;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

public class VersionChannel {

	private List<Version> compVerOrderList = new ArrayList<Version>();
	
	/**
	 * 资源更新map
	 */
	private Map<Version, List<Version>> patchMap = new HashMap<Version, List<Version>>();
	/**
	 * 代码更新map
	 */
	private Map<Version, List<Version>> codePatchMap = new HashMap<Version, List<Version>>();
	
	public VersionChannel(List<Version> allChannelVerList){
		
		compVerOrderList = getCompVerOrderList(allChannelVerList);
		patchMap = sumPatchMap(allChannelVerList, compVerOrderList);
		codePatchMap = sumCodePatchMap(allChannelVerList, compVerOrderList);
		
		System.out.print("compVerOrderList:" + compVerOrderList.size()
				+ ";patchMap:" + patchMap.size());
		System.err.print("compVerOrderList:" + compVerOrderList.size()
				+ ";codePatchMap:" + codePatchMap.size());
	}
	
	//下一个升级全量包
	public Version getNextCompVer(Version clientVersion){
		System.out.print("compVerOrderList1:" + compVerOrderList.size()
				+ ";patchMap:" + patchMap.size());
		System.err.print("compVerOrderList1:" + compVerOrderList.size()
				+ ";codePatchMap:" + codePatchMap.size());
		Version maxVersion = compVerOrderList.get(compVerOrderList.size() - 1);
		maxVersion = maxVersion.isSameCompVer(clientVersion) ? null : maxVersion;
		
		for (Iterator<Version> iterator = compVerOrderList.iterator(); iterator.hasNext();) {
			Version version = (Version) iterator.next();
			if(!version.isMainVer()){
				continue;
			}
			maxVersion = version;
		}
		
		if (maxVersion != null) {
			maxVersion = maxVersion.isLatestCompVer(clientVersion) ? maxVersion : null;
		}
		return maxVersion;
	}
	
	public Version getMaxVersion(){
		return  compVerOrderList.get(compVerOrderList.size()-1);
	}
	
	//下一个资源补丁包
	public Version getNextPatch(Version clientVersion){
		Version target = null;
		for (Version verTmp : compVerOrderList) {
			if(verTmp.isSameCompVer(clientVersion)){
				target = verTmp;
				break;
			}
		}
		Version targetPatch = null;
		if(target!=null){
			List<Version> patchList = patchMap.get(target);
			for (Version patchTmp : patchList) {
				if(patchTmp.isBigPath(clientVersion)){
					targetPatch = patchTmp;
					break;
				}
			}
		}
		return targetPatch;
	}
	
	//剩下的资源补丁包
	public List<Version> getLeftPatch(Version clientVersion){
		Version target = null;
		for (Version verTmp : compVerOrderList) {
			if(verTmp.isSameCompVer(clientVersion)){
				target = verTmp;
				break;
			}
		}
		ArrayList<Version> result = new ArrayList<Version>();
		if(target!=null){
			List<Version> patchList = patchMap.get(target);
			boolean takeNext = false;
			for (Version patchTmp : patchList) {
				if(takeNext){
					result.add(patchTmp);
				}else if(patchTmp.isBigPath(clientVersion)){
					result.add(patchTmp);
					takeNext = true;
				}
			}
		}
		return result;
	}
	
	//下一个代码补丁包
	public Version getNextCodePatch(Version clientVersion){
		Version target = null;
		for (Version verTmp : compVerOrderList) {
			if(verTmp.isSameCompVer(clientVersion)){
				target = verTmp;
				break;
			}
		}
		Version targetPatch = null;
		boolean needRestart = false;
		if(target!=null){
			List<Version> patchList = codePatchMap.get(target);
			if(!patchList.isEmpty()){
				targetPatch = patchList.get(patchList.size() - 1);
				if(clientVersion.isSameCodePath(targetPatch) || clientVersion.isBigCodePath(targetPatch)){
					//客户端比服务端新，或者相同
					return null;
				}
				//查看比当前客户端版本新的，有没有需要强制重启的
				for (Version patchTmp : patchList) {
					if(patchTmp.isBigCodePath(clientVersion) && patchTmp.getPriority() == 1){
						needRestart = true;
					}
				}
			}
		}
		if (needRestart){
			Version tmpPatch = new Version();
			BeanUtils.copyProperties(targetPatch, tmpPatch);
			tmpPatch.setPriority("1");
			targetPatch = tmpPatch;
		}
		return targetPatch;
	}
	
	private Map<Version, List<Version>> sumPatchMap(List<Version> allVerList, List<Version> compVerList) {
		
		Map<Version, List<Version>> patchMapTmp= new HashMap<Version, List<Version>>();
		
		for (Version verTmp : compVerList) {
			List<Version> patchList = new ArrayList<Version>();
			patchList.add(verTmp);
			for (Version patchTmp : allVerList) {
				if(patchTmp.getThird() != 0){
					continue;
				}
				if(verTmp.targetIsVerPatch(patchTmp)){
					patchList.add(patchTmp);
				}
			}
			Collections.sort(patchList, new Comparator<Version>() {
				@Override
				public int compare(Version source, Version target) {
					return source.getPatch() - target.getPatch();
				}
			});
			
			patchMapTmp.put(verTmp, patchList);
		}
		return patchMapTmp;
	}
	
	private Map<Version, List<Version>> sumCodePatchMap(List<Version> allVerList, List<Version> compVerList){
		Map<Version, List<Version>> codePatchMapTmp = new HashMap<Version, List<Version>>();
		
		for (Version verTmp : compVerList) {
			List<Version> codPatchList = new ArrayList<Version>();
			codPatchList.add(verTmp);
			for (Version patchTmp : allVerList) {
				if(patchTmp.getPatch() != 0){
					continue;
				}
				if(verTmp.targetIsVerCodePatch(patchTmp)){
					codPatchList.add(patchTmp);
				}
			}
			Collections.sort(codPatchList, new Comparator<Version>() {
				@Override
				public int compare(Version source, Version target) {
					return source.getThird() - target.getThird();
				}
			});
			
			codePatchMapTmp.put(verTmp, codPatchList);
		}
		return codePatchMapTmp;
	}

	private List<Version>  getCompVerOrderList(List<Version> allVerList) {
		List<Version> compVerList = new ArrayList<Version>();
		for (Version verTmp : allVerList) {
			if(verTmp.getPatch() == 0 && verTmp.getThird() == 0){
				compVerList.add(verTmp);
			}
		}
		Collections.sort(compVerList, new Comparator<Version>() {

			@Override
			public int compare(Version source, Version target) {

				int result = source.getMain() - target.getMain();
				if(result ==0 ){
					result = source.getSub() - target.getSub();
				}
				if(result == 0){
					result = source.getThird() - target.getThird();
				}
				
				return result;
			}
		});
		return compVerList;
	}
}
