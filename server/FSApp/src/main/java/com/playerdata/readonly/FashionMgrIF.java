package com.playerdata.readonly;

import java.util.List;

import com.playerdata.readonly.FashionMgrIF.ItemFilter;
import com.rwbase.dao.fashion.FashState;
import com.rwbase.dao.fashion.FashionItemIF;

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
	public ItemFilter getSwingOnItemPred();
	public interface ItemFilter{
		public boolean accept(FashionItemIF item);
	}
}
