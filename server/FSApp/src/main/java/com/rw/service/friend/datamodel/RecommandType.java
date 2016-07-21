package com.rw.service.friend.datamodel;


public enum RecommandType {

	CARERR {
		@Override
		public FactorExtractor getExtractor() {
			return careerPlus;
		}

		@Override
		public PlusParser getParser() {
			return mappedParser;
		}
	},
	VIP {
		@Override
		public FactorExtractor getExtractor() {
			return vipPlus;
		}

		@Override
		public PlusParser getParser() {
			return mappedParser;
		}

	}


	;
	private static VipPlus vipPlus = new VipPlus();
	private static CareerPlus careerPlus = new CareerPlus();
	private static MappedParser mappedParser = new MappedParser();

	public abstract FactorExtractor getExtractor();

	public abstract PlusParser getParser();

	public static RecommandType getType(int type) {
		if (type == 1) {
			return CARERR;
		} else {
			return VIP;
		}
	}

}
