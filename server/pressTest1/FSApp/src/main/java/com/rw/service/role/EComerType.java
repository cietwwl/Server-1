package com.rw.service.role;

public enum EComerType {
	/**新手*/
	newcomer(1),
	/**普通*/
	oldcomer(2);
	
	private int order;
	EComerType(int order){
		this.order = order;
	}
	public int getOrder() {
		return order;
	}
}
