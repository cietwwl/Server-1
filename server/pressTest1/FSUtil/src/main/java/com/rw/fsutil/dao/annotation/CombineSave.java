package com.rw.fsutil.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 组合保存，被@combine标记的字段，
 * 且Column相同的字段会组合成一个map<fieldName,jsonValue> 然后转换成json保存.
 * 默认字段名extention，可以有多个。
 * @author allen
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CombineSave {

	String Column() default "extention";
	
}
