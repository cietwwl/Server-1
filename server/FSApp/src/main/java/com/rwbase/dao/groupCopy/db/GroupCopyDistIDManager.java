package com.rwbase.dao.groupCopy.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.ArrayType;
import org.codehaus.jackson.map.type.CollectionLikeType;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.springframework.util.StringUtils;

import com.log.GameLog;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

/**
 * 保存所有需要分发奖励的容器
 * @author Alex
 * 2016年7月4日 下午5:00:14
 */
public class GroupCopyDistIDManager {

	private static GroupCopyDistIDManager manager = new GroupCopyDistIDManager();
	private ObjectMapper MAPPER = new ObjectMapper();
	
	//所有需要分发的帮派id
	private List<String> GroupIDList = new ArrayList<String>();
	
	protected GroupCopyDistIDManager() {
	}

	public static GroupCopyDistIDManager getInstance(){
		return manager;
	}
	
	/**
	 * 帮派解散通知
	 * @param id
	 */
	public void groupDismissNotify(String id){
		GroupIDList.remove(id);
		UpdateGroupDistData();
	}
	
	public void addGroupID(String id){
		if(GroupIDList.contains(id))
			return;
		GroupIDList.add(id);
		UpdateGroupDistData();
	}
	
	
	public List<String> getGroupIDList(){
		return Collections.unmodifiableList(GroupIDList);
	}
	
	/**
	 * 初始化数据
	 */
	public void InitDistIDInfo(){
		String json = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.GROUP_COPY_DIST_REWARD_ID);
		if(StringUtils.isEmpty(json)){
			return;
		}
		
		CollectionType type = TypeFactory.defaultInstance().constructCollectionType(List.class, String.class);
		try {
			GroupIDList = MAPPER.readValue(json, type);
		} catch (JsonGenerationException e) {
			GameLog.error("分发奖励时效处理", "系统检查分发奖励时效", "转换json出现了JsonGenerationException异常", e);
		} catch (JsonMappingException e) {
			GameLog.error("分发奖励时效处理", "系统检查分发奖励时效", "转换json出现了JsonMappingException异常", e);
		} catch (IOException e) {
			GameLog.error("分发奖励时效处理", "系统检查分发奖励时效", "转换json出现了IOException异常", e);
		}
	}
	
	/**
	 * 保存
	 */
	private void UpdateGroupDistData(){
		try {
			String json = MAPPER.writeValueAsString(GroupIDList);
			GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.GROUP_COPY_DIST_REWARD_ID, json);
		
		} catch (JsonGenerationException e) {
			GameLog.error("分发奖励时效处理", "系统检查分发奖励时效", "转换json出现了JsonGenerationException异常", e);
		} catch (JsonMappingException e) {
			GameLog.error("分发奖励时效处理", "系统检查分发奖励时效", "转换json出现了JsonMappingException异常", e);
		} catch (IOException e) {
			GameLog.error("分发奖励时效处理", "系统检查分发奖励时效", "转换json出现了IOException异常", e);
		}
	}
}
