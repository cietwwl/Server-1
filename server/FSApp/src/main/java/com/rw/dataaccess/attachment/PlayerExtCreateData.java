package com.rw.dataaccess.attachment;

import java.util.List;

import com.rw.fsutil.cacheDao.attachment.PlayerExtProperty;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStoreCache;

public class PlayerExtCreateData {

	public final short type;
	public final PlayerExtPropertyCreator<PlayerExtProperty> creator;
	public final PlayerExtPropertyStoreCache<PlayerExtProperty> cache;
	private List<NewAttachmentInsertData<PlayerExtProperty>> datas;

	public PlayerExtCreateData(short type, PlayerExtPropertyCreator<PlayerExtProperty> creator, PlayerExtPropertyStoreCache<PlayerExtProperty> cache) {
		this.creator = creator;
		this.cache = cache;
		this.type = type;
	}

	public void setDatas(List<NewAttachmentInsertData<PlayerExtProperty>> datas) {
		this.datas = datas;
	}

	public List<NewAttachmentInsertData<PlayerExtProperty>> getDatas() {
		return datas;
	}
}
