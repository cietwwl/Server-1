package com.rw.handler.magicSecret;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class MagicSecretHolder{
	
	private static MagicSecretHolder instance = new MagicSecretHolder();
	
	public static MagicSecretHolder getInstance(){
		return instance;
	}
	
	private  Map<String, UserMagicSecretData> list = new HashMap<String, UserMagicSecretData>();
	
	private  String chapterId ;//章节id，通过当前进度来截取
	
	private SynDataListHolder<UserMagicSecretData> listHolder = new SynDataListHolder<UserMagicSecretData>(UserMagicSecretData.class);
	
	public Map<String, UserMagicSecretData> getList() {
		return list;
	}
	



	public String getChapterId() {
		return chapterId;
	}




	public  void setChapterId(String chapterId) {
		this.chapterId = chapterId;
	}




	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<UserMagicSecretData> itemList = listHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			UserMagicSecretData userMagicSecretData = itemList.get(i);
			list.put(userMagicSecretData.getUserId(), userMagicSecretData);
			if(userMagicSecretData.getCurrentDungeonID()!=null){//有当前拿当前，若干个协议才有，其余保存在客户端
				chapterId = userMagicSecretData.getCurrentDungeonID().substring(0, 1);
			}else if(userMagicSecretData.getMaxStageID()!=0){//没当前拿历史最高，一般都有
				if(chapterId != null){
					continue;
				}
				chapterId = (userMagicSecretData.getMaxStageID()+"").substring(0, 1);
			}
		}
		
	}
}
