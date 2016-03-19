package com.rw.fsutil.dao.cache;

import java.util.Arrays;

public class CacheStackTraceEntity {

	public static final String lineSeparator = CacheFactory.LINE_SEPARATOR;
	private final StackTraceElement[] trace;
	private final int hashCode;
	private final int fromIndex;

	public CacheStackTraceEntity(StackTraceElement[] trace, int fromIndex) {
		super();
		this.trace = trace;
		this.fromIndex = fromIndex;
		final int prime = 31;
		int result = 1;
		this.hashCode = prime * result + Arrays.hashCode(trace);
	}

	public StackTraceElement[] getTrace() {
		return trace;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CacheStackTraceEntity other = (CacheStackTraceEntity) obj;
		return Arrays.equals(((StackTraceElement[]) trace), ((StackTraceElement[]) other.trace));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		fill(sb);
		return sb.toString();
	}

	public void fill(StringBuilder sb) {
		for (int i = 0; i < trace.length; i++) {
			if (i < fromIndex) {
				continue;
			}
			sb.append("      ").append(trace[i].toString()).append(lineSeparator);
		}
	}

}
