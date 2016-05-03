package com.rw.service.Privilege.datamodel;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.rwproto.PrivilegeProtos.PrivilegeValue;
import com.rwproto.PrivilegeProtos.PrivilegeValue.Builder;

public abstract class AbstractPropertyWriter<T extends Comparable<T>> implements PropertyWriter {
	@SuppressWarnings("unchecked")
	protected T extractVal(Object valObj){
		if (valObj == null) return null;
		T val = null;
		if (valObj instanceof Comparable<?>){
			val = (T) valObj;
		}else{
			String value = valObj.toString();
			if (StringUtils.isNotBlank(value)){
				try{
					val = parse(value);
				}catch(Exception ex){
					GameLog.error("特权", "AbstractPropertyWriter", "无法获取特权属性值");
				}
			}
		}
		return val;
	}
	protected abstract T parse(String val);
	
	@Override
	public boolean gt(Object left, Object right) {
		T leftVal = extractVal(left);
		T rightVal = extractVal(right);
		if (leftVal != null && rightVal != null){
			return leftVal.compareTo(rightVal) > 0;
		}
		return false;
	}

	@Override
	public Builder combine(Builder acc, PrivilegeValue added, String name) {
		if (acc == null || added == null || name == null
				|| !name.equals(acc.getName()) || !name.equals(added.getName())) {
			GameLog.error("特权", "合并特权属性", "无效特权属性名");
			return acc;
		}
		
		if (gt(added.getValue(),acc.getValue())){
			String chargeType = added.getChargeType();
			// 计算最大充值档次
			if (StringUtils.isNotBlank(chargeType)){
				acc.setChargeType(chargeType);
			}
			acc.setValue(added.getValue());
		}

		return acc;
	}

}
