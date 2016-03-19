package com.rwbase.dao.group.pojo.cfg.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.group.pojo.cfg.GroupFunctionCfg;
import com.rwbase.dao.group.pojo.cfg.GroupFunctionTemplate;
import com.rwproto.GroupCommonProto.GroupFunction;
import com.rwproto.GroupCommonProto.GroupPost;

/*
 * @author HC
 * @date 2016年1月27日 下午4:59:58
 * @Description 帮派功能配置对应的DAO
 */
public class GroupFunctionCfgDAO extends CfgCsvDao<GroupFunctionCfg> {

	// private static GroupFunctionCfgDAO dao = new GroupFunctionCfgDAO();

	public static GroupFunctionCfgDAO getDAO() {
		// return dao;
		return SpringContextUtil.getBean(GroupFunctionCfgDAO.class);
	}

	private HashMap<Integer, GroupFunctionTemplate> templateMap;

	private GroupFunctionCfgDAO() {
	}

	@Override
	public Map<String, GroupFunctionCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Group/FunctionCfg.csv", GroupFunctionCfg.class);

		HashMap<Integer, GroupFunctionTemplate> map = new HashMap<Integer, GroupFunctionTemplate>();
		for (Entry<String, GroupFunctionCfg> e : cfgCacheMap.entrySet()) {
			GroupFunctionTemplate tmp = new GroupFunctionTemplate(e.getValue());
			map.put(tmp.getFunctionType(), tmp);
		}

		templateMap = map;
		return cfgCacheMap;
	}

	/**
	 * 检查是否可以用当前的某个功能
	 * 
	 * @param function 对应功能的值 {@link GroupFunction}
	 * @param post 职位 {@link GroupPost}
	 * @param groupLevel 帮派的等级
	 * @return
	 */
	public boolean canUseFunction(int function, int post, int groupLevel) {
		// 整个列表都是空的，说明要么出错，要么就是没有功能限制
		if (templateMap == null || templateMap.isEmpty()) {
			return true;
		}

		// 功能没有对应的限制配置表
		GroupFunctionTemplate tmp = templateMap.get(function);
		if (tmp == null) {
			return true;
		}

		// 检查职位
		List<Integer> postList = tmp.getPostList();
		if (postList != null && !postList.contains(post)) {
			return false;
		}

		// 检查等级
		if (groupLevel >= tmp.getNeedGroupLevel()) {
			return true;
		}

		return false;
	}
}