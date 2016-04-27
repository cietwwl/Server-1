package com.playerdata.readonly;

import java.util.List;

import com.rwbase.dao.fashion.FashionItemIF;
import com.rwbase.dao.fashion.FashionUsedIF;

/**
 * FashionMgr只读接口
 * @author 
 *
 */
public interface FashionMgrIF {

	/**
	 * 按时装id、时装类型、时装状态搜索指定的时装列表
	 * @param itemId
	 * @param type
	 * @param state
	 * @return
	 */
	public List<FashionItemIF> search(ItemFilter predicate);
	public interface ItemFilter{
		public boolean accept(FashionItemIF item);
	}
	public FashionUsedIF getFashionUsed();
}
