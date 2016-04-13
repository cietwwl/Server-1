package com.fy.version;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
				+ ";patchMap:" + patchMap.size());
	}
	


	//下一个升级全量包
	public Version getNextCompVer(Version clientVersion){
		System.out.print("compVerOrderList1:" + compVerOrderList.size()
				+ ";patchMap:" + patchMap.size());
		System.err.print("compVerOrderList1:" + compVerOrderList.size()
				+ ";patchMap:" + patchMap.size());
		Version maxVersion = compVerOrderList.get(compVerOrderList.size() - 1);
		maxVersion = maxVersion.isSameCompVer(clientVersion) ? null : maxVersion;
		if (maxVersion != null) {
			maxVersion = maxVersion.isLatestCompVer(clientVersion) ? maxVersion : null;
		}
		return maxVersion;
	}
	
	public Version getMaxVersion(){
		return  compVerOrderList.get(compVerOrderList.size()-1);
	}
	
	//下一个补丁包
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
			boolean takeNext = false;
			for (Version patchTmp : patchList) {
				if(takeNext){
					targetPatch = patchTmp;
					break;
				}
				if(patchTmp.isSamePatch(clientVersion)){
					takeNext = true;
				}
			}
			
		}
		
		return targetPatch;
	}
	
	public Version getNextCodePatch(Version clientVersion){
		Version target = null;
		for (Version verTmp : compVerOrderList) {
			if(verTmp.isSameCompVer(clientVersion)){
				target = verTmp;
				break;
			}
		}
		Version targetPatch = null;
		if(target!=null){
			List<Version> patchList = codePatchMap.get(target);
			boolean takeNext = false;
			for (Version patchTmp : patchList) {
				if(takeNext){
					targetPatch = compareCodePatch(patchTmp, targetPatch);
				}
				if(patchTmp.isSameCodePath(clientVersion)){
					takeNext = true;
				}
			}
			
		}
		
		return targetPatch;
	}

	private Version compareCodePatch(Version patchTmp, Version bigPatch){
		if(bigPatch == null){
			return patchTmp;
		}
		if(patchTmp.getMain() >= bigPatch.getMain()){
			if(patchTmp.getSub() >= bigPatch.getSub()){
				if(patchTmp.getThird() >= bigPatch.getThird()){
					return patchTmp;
				}
			}
		}
		return bigPatch;
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
			if(verTmp.getPatch() == 0){
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
