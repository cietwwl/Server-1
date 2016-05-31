package com.rw.service.friend.datamodel;

import java.util.HashMap;

public class WeightEntryBuilder {

	private final FactorExtractor extractor;
	private HashMap<Integer, PlusCalculator> calculatorMap;

	public WeightEntryBuilder(FactorExtractor extractor) {
		this.extractor = extractor;
		this.calculatorMap = new HashMap<Integer, PlusCalculator>();
	}

	public WeightEntryBuilder add(Integer key, PlusCalculator calculator) {
		this.calculatorMap.put(key, calculator);
		return this;
	}

	public WeightEntry build() {
		return new WeightEntry(extractor, calculatorMap);
	}
}
