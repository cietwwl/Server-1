package com.rw.fsutil.dao.cache;

public class CacheKey {

	private final Class<?> clazz;
	private final String name;

	public CacheKey(Class<?> clazz, String name) {
		super();
		this.clazz = clazz;
		this.name = name;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CacheKey other = (CacheKey) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CacheKey [clazz=" + clazz.getSimpleName() + ", name=" + name + "]";
	}

}
