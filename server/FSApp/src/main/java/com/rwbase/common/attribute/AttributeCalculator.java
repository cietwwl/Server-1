package com.rwbase.common.attribute;

import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class AttributeCalculator<T> {

	private final ReentrantLock lock = new ReentrantLock();
	private final String roleId;
	private final List<IAttributeComponent> list;
	private AttributeSet attribute;
	private IAttributeFormula<T> formula;
	private final String heroId;
	private volatile T resultObject;

	public AttributeCalculator(String roleId, String heroId, List<IAttributeComponent> list, IAttributeFormula<T> formula) {
		this.roleId = roleId;
		this.list = list;
		this.formula = formula;
		this.heroId = heroId;
		this.formula = formula;
	}

	/**
	 * 更新属性
	 */
	public final T updateAttribute() {
		lock.lock();
		try {
			return calAttributeNode(list);
		} finally {
			lock.unlock();
		}
	}

	public T getResult() {
		return this.resultObject;
	}

	private T calAttributeNode(List<IAttributeComponent> list) {
		AttributeSet current = null;
		for (int i = list.size(); --i >= 0;) {
			IAttributeComponent component = list.get(i);
			AttributeSet att = component.convertToAttribute(roleId, heroId);
			if (current == null) {
				current = att;
			} else {
				current = current.add(att);
			}
		}
		attribute = current;
		AttributeExtracterImpl extracter = new AttributeExtracterImpl();
		EnumMap<AttributeType, Integer> result = extracter.result;
		List<AttributeItem> attributes = current.getReadOnlyAttributes();
		for (int i = attributes.size(); --i >= 0;) {
			AttributeItem item = attributes.get(i);
			int add = item.getIncreaseValue();
			int percent = item.getIncPerTenthousand();
			if (percent != 0) {
				add = add + (add * percent / 10000);
			}
			result.put(item.getType(), add);
		}
		if (current != null) {
			List<AttributeType> typeList = formula.getReCalculateAttributes();
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
		resultObject = formula.convert(result);
		return resultObject;
	}

	public AttributeSet getAttribute() {
		lock.lock();
		try {
			if (attribute == null) {
				calAttributeNode(list);
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
