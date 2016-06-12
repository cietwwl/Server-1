package com.rwbase.common.attribute;

import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.common.BeanOperationHelper;

public class AttributeCalculator<T> {

	private final ReentrantLock lock = new ReentrantLock();
	private final String roleId;
	private final List<IAttributeComponent> list;
	private AttributeSet attribute;
	private IAttributeFormula<T> formula;
	private final String heroId;
	private volatile T resultObject;// 计算的总结果
	private volatile T resultBaseObject;// 只获取到固定值
	private volatile StringBuilder sb;// 打印属性

	public AttributeCalculator(String roleId, String heroId, List<IAttributeComponent> list, IAttributeFormula<T> formula) {
		this.roleId = roleId;
		this.list = list;
		this.heroId = heroId;
		this.formula = formula;
	}

	/**
	 * 更新属性
	 */
	public final T updateAttribute() {
		lock.lock();
		try {
			return calcAttributeNode(list);
		} finally {
			lock.unlock();
		}
	}

	public T getResult() {
		return this.resultObject;
	}

	public T getBaseResult() {
		return this.resultBaseObject;
	}

	private T calcAttributeNode(List<IAttributeComponent> list) {
		sb = new StringBuilder();
		AttributeSet current = null;

		for (int i = list.size(); --i >= 0;) {
			IAttributeComponent component = list.get(i);

			AttributeSet att = component.convertToAttribute(roleId, heroId);

			if (att == null) {
				continue;
			}

			att.initCheckSame();

			List<AttributeItem> readOnlyAttributes = att.getReadOnlyAttributes();
			if (component.getComponentTypeEnum() == AttributeComponentEnum.Hero_Base) {
				resultBaseObject = formula.convertOne(readOnlyAttributes, false);
			}

			if (current == null) {
				current = att;
			} else {
				current = current.add(att);
			}

			T add = formula.convertOne(readOnlyAttributes, false);
			T precent = formula.convertOne(readOnlyAttributes, true);
			sb.append(component.getComponentTypeEnum()).append(">>>[固定]-").append(BeanOperationHelper.getPositiveValueDiscription(add));
			sb.append("\n").append(">>>[万分]-").append(BeanOperationHelper.getPositiveValueDiscription(precent)).append("\n");
		}

		attribute = current;
		AttributeExtracterImpl extracter = new AttributeExtracterImpl();
		EnumMap<AttributeType, Integer> result = extracter.result;
		// 第一次计算
		List<AttributeItem> attributes = current.getReadOnlyAttributes();

		// 转换成Map
		for (int i = attributes.size(); --i >= 0;) {
			AttributeItem item = attributes.get(i);
			int add = item.getIncreaseValue();
			int percent = item.getIncPerTenthousand();
			if (percent != 0) {
				add = add + (add * percent / AttributeConst.DIVISION);
			}
			result.put(item.getType(), add);
		}

		// 二次计算
		if (current != null) {
			List<AttributeType> typeList = formula.getReCalculateAttributes();
			if (typeList != null && typeList.isEmpty()) {// 不是Null和空的情况下才来处理
				int size = typeList.size();
				for (int i = 0; i < size; i++) {
					AttributeType type = typeList.get(i);
					int value = formula.recalculate(extracter, type);
					Integer oldValue = result.get(type);
					if (oldValue != null) {
						value += oldValue;
					}
					result.put(type, value);
				}
			}
		}

		resultObject = formula.convert(result);
		return resultObject;
	}

	/**
	 * 获取属性描述
	 * 
	 * @return
	 */
	public String getAttrDesc() {
		return sb.toString();
	}

	public AttributeSet getAttribute() {
		lock.lock();
		try {
			if (attribute == null) {
				calcAttributeNode(list);
			}
			return attribute;
		} finally {
			lock.unlock();
		}
	}

	static class AttributeExtracterImpl implements IAttributeExtracter {

		private final EnumMap<AttributeType, Integer> result;

		public AttributeExtracterImpl() {
			this.result = new EnumMap<AttributeType, Integer>(AttributeType.class);
		}

		@Override
		public int getAttributeValue(AttributeType type) {
			Integer value = result.get(type);
			return value == null ? 0 : value;
		}
	}
}