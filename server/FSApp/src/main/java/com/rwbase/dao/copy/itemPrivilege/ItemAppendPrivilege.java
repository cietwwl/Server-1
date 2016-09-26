package com.rwbase.dao.copy.itemPrivilege;

import com.playerdata.readonly.ItemInfoIF;

class ItemAppendPrivilege implements ItemInfoIF
{
	private ItemInfoIF itemIF;
	private float value;
	private boolean isPersent;
	
	public ItemAppendPrivilege() {}
	
	public ItemInfoIF getItemInfoIF() {
		return itemIF;
	}
	
	public void setItemInfoIF(ItemInfoIF itemIF) {
		this.itemIF = itemIF;
	}
	
	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public boolean isPersent() {
		return isPersent;
	}

	public void setPersent(boolean isPersent) {
		this.isPersent = isPersent;
	}

	@Override
	public int getItemID() {
		return itemIF.getItemID();
	}

	@Override
	public int getItemNum() {
		return itemIF.getItemNum() + (int)getAppendNum();
	}

	/**
	 * 获取原始的数据结构
	 * @return
	 */
	protected ItemInfoIF getOriItemInfoIF(){
		ItemInfoIF tmp = itemIF;
		while(tmp instanceof ItemAppendPrivilege){
			tmp = ((ItemAppendPrivilege)tmp).itemIF;
		}
		return tmp;
	}
	
	/**
	 * 获取附加值
	 * @return
	 */
	protected float getAppendNum(){
		if(isPersent){
			return getOriItemInfoIF().getItemNum() * value;
		}else{
			return value;
		}
	}
}
