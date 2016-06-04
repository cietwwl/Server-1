package com.rw.service.friend.datamodel;

import java.util.Collections;
import java.util.Map;
import com.playerdata.Player;

public class WeightEntry {

	private final FactorExtractor extractor;
	private final Map<Integer, PlusCalculator> calculatorMap;

	public WeightEntry(FactorExtractor extractor, Map<Integer, PlusCalculator> calculator) {
		super();
		this.extractor = extractor;
		this.calculatorMap = Collections.unmodifiableMap(calculator);
	}

	public int getWeight(Player player, Player otherPlayer) {
		Integer key = extractor.extract(player);
		PlusCalculator calculator = calculatorMap.get(key);
		if (calculator == null) {
			return 0;
		}
		Integer param = extractor.extract(otherPlayer);
		return calculator.calcutePlus(param);
	}

}
