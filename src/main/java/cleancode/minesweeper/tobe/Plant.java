package cleancode.minesweeper.tobe;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Plant {
	enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }

	final String name;
	final LifeCycle lifeCycle;

	Plant(String name, LifeCycle lifeCycle) {
		this.name = name;
		this.lifeCycle = lifeCycle;
	}

	@Override public String toString() {
		return name;
	}

	public static void main(String[] args) {
		Plant[] garden = {
			new Plant("Cosmos", LifeCycle.ANNUAL), // 코스모스
			new Plant("Chrysanthemum", LifeCycle.PERENNIAL), // 국화
			new Plant("Evening Primrose", LifeCycle.BIENNIAL) // 달맞이꽃
		};

		System.out.println(Arrays.stream(garden)
			.collect(groupingBy(p -> p.lifeCycle, () ->
				new EnumMap<>(LifeCycle.class), toSet())));
	}
}
