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
	 * 资源更新整合包的map
	 */
	private Map<Version, Version> totalPatchMap = new HashMap<Version, Version>();
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
		totalPatchMap = getTotalPatchMap(allChannelVerList, compVerOrderList);
		patchMap = sumPatchMap(allChannelVerList, compVerOrderList);
		codePatchMap = sumCodePatchMap(allChannelVerList, compVerOrderList);
		
		System.out.print("compVerOrderList:" + compVerOrderList.size()
				+ ";patchMap:" + patchMap.size());
		System.err.print("compVerOrderList:" + compVerOrderList.size()
				+ ";codePatchMap:" + codePatchMap.size());
	}
	
	/**
	 * 下一个升级全量包
	 * @param clientVersion
	 * @return
	 */
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
	
	/**
	 * 比较一个个的资源补丁包和整合资源补丁包的大小
	 * 
	 * 如果整合的小，替换result中的数据
	 * @param result
	 * @param target 一定只能是compVerOrderList中的对象
	 */
	private void compareWithTotalPatch(List<Version> result, Version target){
		if(!result.isEmpty()){
			Version totalPatch = totalPatchMap.get(target);
			if(null != totalPatch){
				Version latestPatch = result.get(result.size() - 1);
				if(latestPatch.getPatch() == totalPatch.getSub()){
					long sumSize = 0;
					for(Version patch : result){
						sumSize += patch.getSize();
					}
					if(sumSize > totalPatch.getSize()){
						result.clear();
						result.add(totalPatch);
					}
				}
			}
		}
	}
	
	/**
	 * 从当前的到最新的资源补丁包
	 * @param clientVersion
	 * @return
	 */
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
			//确定是下整合包还是单包集合
			compareWithTotalPatch(result, target);
		}
		return result;
	}
	
	/**
	 * 最新的代码补丁包（代码补丁包，只要最新的）
	 * @param clientVersion
	 * @return
	 */
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
	
	/**
	 * 综合各渠道的资源整合包
	 * @param allVerList 渠道集合包
	 * @param compVerList i.0.0.0的主包
	 * @return
	 */
	private Map<Version, Version> getTotalPatchMap(List<Version> allVerList, List<Version> compVerList) {
		Map<Version, Version> patchMapTmp= new HashMap<Version, Version>();
		for (Version verTmp : compVerList) {
			List<Version> patchList = new ArrayList<Version>();
			patchList.add(verTmp);
			for (Version patchTmp : allVerList) {
				if(verTmp.targetIsTotalPatch(patchTmp)){
					patchList.add(patchTmp);
				}
			}
			if(!patchList.isEmpty()){
				Collections.sort(patchList, new Comparator<Version>() {
					@Override
					public int compare(Version source, Version target) {
						return target.getSub() - source.getSub();
					}
				});
				patchMapTmp.put(verTmp, patchList.get(0));
			}
		}
		return patchMapTmp;
	}
	
	/**
	 * 各主包下的资源补丁list（已排序）
	 * @param allVerList
	 * @param compVerList
	 * @return
	 */
	private Map<Version, List<Version>> sumPatchMap(List<Version> allVerList, List<Version> compVerList) {
		
		Map<Version, List<Version>> patchMapTmp= new HashMap<Version, List<Version>>();
		
		for (Version verTmp : compVerList) {
			List<Version> patchList = new ArrayList<Version>();
			patchList.add(verTmp);
			for (Version patchTmp : allVerList) {
				if(patchTmp.getThird() != 0 || patchTmp.getSub() != 0){
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
	
	/**
	 * 各主包下的代码补丁list（已排序）
	 * @param allVerList
	 * @param compVerList
	 * @return
	 */
	private Map<Version, List<Version>> sumCodePatchMap(List<Version> allVerList, List<Version> compVerList){
		Map<Version, List<Version>> codePatchMapTmp = new HashMap<Version, List<Version>>();
		
		for (Version verTmp : compVerList) {
			List<Version> codPatchList = new ArrayList<Version>();
			codPatchList.add(verTmp);
			for (Version patchTmp : allVerList) {
				if(patchTmp.getPatch() != 0 || patchTmp.getSub() != 0){
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

	/**
	 * 主包的集合，用来当分类的key
	 * 
	 * 所有的请求来了之后，需要到这个list中找对应的key
	 * @param allVerList
	 * @return
	 */
	private List<Version> getCompVerOrderList(List<Version> allVerList) {
		List<Version> compVerList = new ArrayList<Version>();
		for (Version verTmp : allVerList) {
			if(verTmp.getPatch() == 0 && verTmp.getThird() == 0 && verTmp.getSub() == 0){
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
