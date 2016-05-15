package com.rwbase.common.attribute;

import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.log.GameLog;

public class AttributeCalculator<T> {

	private final ReentrantLock lock = new ReentrantLock();
	private final String roleId;
	private final List<IAttributeComponent> list;
	private AttributeSet attribute;
	private IAttributeFormula<T> formula;
	private final String heroId;
	private volatile T resultObject;// 计算的总结果
	private volatile T resultBaseObject;// 只获取到固定值

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
		AttributeSet current = null;

		StringBuilder sb = new StringBuilder();
		sb.append(roleId).append("<<<<<<").append(heroId).append(">>>>>>");

		for (int i = list.size(); --i >= 0;) {
			IAttributeComponent component = list.get(i);
			AttributeSet att = component.convertToAttribute(roleId, heroId);

			sb.append(component.getComponentTypeEnum()).append(":").append(att).append(",");
			if (att == null) {
				continue;
			}

			if (current == null) {
				current = att;
			} else {
				current = current.add(att);
			}
		}

		if (current == null) {
			GameLog.error("计算属性", roleId, sb.toString());
		}

		attribute = current;
		AttributeExtracterImpl extracter = new AttributeExtracterImpl();
		EnumMap<AttributeType, Integer> result = extracter.result;
		// 第一次计算
		List<AttributeItem> attributes = current.getReadOnlyAttributes();

		resultBaseObject = formula.convertOne(attributes, false);

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