package com.rw.fsutil.common;

/**
 * 最终能用int来表达一种类型的抽象接口，该对象可以是一个枚举，也可以是一个普通对象
 * @author Jamaz
 *
 */
public interface TypeIdentification {

	/**
	 * 获取类型
	 * @return
	 */
	public int getTypeValue();

}
