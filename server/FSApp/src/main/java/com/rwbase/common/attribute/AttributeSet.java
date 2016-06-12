package com.rwbase.common.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

/**
 * 属性集，包括1-N个属性项
 * 
 * @author Jamaz
 *
 */
public class AttributeSet {

	private final ArrayList<AttributeItem> attributes;
	private volatile List<AttributeItem> readOnlyAttributes;

	private AttributeSet(ArrayList<AttributeItem> attributes) {
		this.attributes = attributes;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	// /**
	// * 检查一下是否有重复元素，针对current从Null到第一次赋值
	// *
	// * @return
	// */
	// public AttributeSet initCheckSame() {
	// ArrayList<AttributeItem> newList = new ArrayList<AttributeItem>();
	// for (int i = attributes.size(); --i >= 0;) {
	// AttributeItem attributeItem = attributes.get(i);
	// if (attributeItem == null) {
	// continue;
	// }
	//
	// AttributeType type = attributeItem.getType();
	//
	// if (newList.isEmpty()) {
	// newList.add(attributeItem);
	// } else {
	// int addValue = attributeItem.getIncreaseValue();
	// int addPrecent = attributeItem.getIncPerTenthousand();
	// for (int j = newList.size(); --j >= 0;) {
	// AttributeItem item = newList.get(j);
	// if (item.getType() == type) {
	// addValue += item.getIncreaseValue();
	// addPrecent += item.getIncPerTenthousand();
	// }
	// }
	//
	// newList.add(new AttributeItem(type, addValue, addPrecent));
	// }
	// }
	//
	// return new AttributeSet(newList);
	// }

	public AttributeSet add(AttributeSet attributeNode) {
		if (attributeNode == null) {
			return this;
		}

		EnumMap<AttributeType, AttributeItem> attrMap = new EnumMap<AttributeType, AttributeItem>(AttributeType.class);
		for (int i = attributes.size(); --i >= 0;) {
			AttributeItem attributeItem = attributes.get(i);
			if (attributeItem == null) {
				continue;
			}

			AttributeType type = attributeItem.getType();
			AttributeItem oldItem = attrMap.get(type);
			if (oldItem == null) {
				attrMap.put(type, attributeItem);
			} else {
				attrMap.put(type, new AttributeItem(type, attributeItem.getIncreaseValue() + oldItem.getIncreaseValue(), attributeItem.getIncPerTenthousand() + oldItem.getIncPerTenthousand()));
			}
		}

		ArrayList<AttributeItem> addList = attributeNode.attributes;
		for (int i = addList.size(); --i >= 0;) {
			AttributeItem attributeItem = addList.get(i);
			if (attributeItem == null) {
				continue;
			}

			AttributeType type = attributeItem.getType();
			AttributeItem oldItem = attrMap.get(type);
			if (oldItem == null) {
				attrMap.put(type, attributeItem);
			} else {
				attrMap.put(type, new AttributeItem(type, attributeItem.getIncreaseValue() + oldItem.getIncreaseValue(), attributeItem.getIncPerTenthousand() + oldItem.getIncPerTenthousand()));
			}
		}
		return new AttributeSet(new ArrayList<AttributeItem>(attrMap.values()));

		// ArrayList<AttributeItem> newList = new ArrayList<AttributeItem>();
		// ArrayList<AttributeItem> addList = attributeNode.attributes;
		// for (int i = attributes.size(); --i >= 0;) {
		// AttributeItem oldItem = attributes.get(i);
		// AttributeType type = oldItem.getType();
		// AttributeItem newItem = null;
		// for (int j = addList.size(); --j >= 0;) {
		// AttributeItem addItem = addList.get(j);
		// if (type == addItem.getType()) {
		// newItem = new AttributeItem(type, oldItem.getIncreaseValue() + addItem.getIncreaseValue(), oldItem.getIncPerTenthousand() +
		// addItem.getIncPerTenthousand());
		// break;
		// }
		// }
		// newList.add(newItem == null ? oldItem : newItem);
		// }
		// for (int i = addList.size(); --i >= 0;) {
		// AttributeItem addItem = addList.get(i);
		// AttributeType type = addItem.getType();
		// boolean exist = false;
		// for (int j = attributes.size(); --j >= 0;) {
		// AttributeItem oldItem = attributes.get(j);
		// if (oldItem.getType() == type) {
		// exist = true;
		// break;
		// }
		// }
		// if (!exist) {
		// newList.add(addItem);
		// }
		// }
		//
		// return new AttributeSet(newList);
	}

	public List<AttributeItem> getReadOnlyAttributes() {
		if (readOnlyAttributes == null) {
			readOnlyAttributes = Collections.unmodifiableList(attributes);
		}
		return readOnlyAttributes;
	}

	public static class Builder {

		private final ArrayList<AttributeItem> list = new ArrayList<AttributeItem>();

		public Builder addAttribute(AttributeItem entity) {
			if (entity == null) {
				throw new IllegalArgumentException();
			}
			AttributeType type = entity.getType();
			for (int i = list.size(); --i >= 0;) {
				AttributeItem item = list.get(i);
				if (type == item.getType()) {
					item = new AttributeItem(type, item.getIncreaseValue() + entity.getIncreaseValue(), item.getIncPerTenthousand() + entity.getIncPerTenthousand());
					list.set(i, item);
					return this;
				}
			}
			list.add(entity);
			return this;
		}

		public Builder addAttribute(List<AttributeItem> entity) {
			for (AttributeItem s : entity) {
				if (s == null) {
					throw new ExceptionInInitializerError();
				}
			}
			list.addAll(entity);
			return this;
		}

		public AttributeSet build() {
			return new AttributeSet(list);
		}
	}

	public AttributeItem getAttributeItem(AttributeType type) {
		for (int i = attributes.size(); --i >= 0;) {
			AttributeItem item = attributes.get(i);
			if (item.getType() == type) {
				return item;
			}
		}
		return defaultAttributes[type.ordinal()];
	}

	private static AttributeItem defaultAttributes[];

	static {
		AttributeType[] array = AttributeType.values();
		int length = array.length;
		defaultAttributes = new AttributeItem[length];
		for (int i = 0; i < length; i++) {
			defaultAttributes[i] = new AttributeItem(array[i], 0, 0);
		}
	}
}