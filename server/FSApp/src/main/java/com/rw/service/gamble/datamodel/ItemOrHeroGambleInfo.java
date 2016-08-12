package com.rw.service.gamble.datamodel;

public class ItemOrHeroGambleInfo {
	private String Id;
	private boolean isHero;
	private boolean isGuarantee;

	public void mergeGuaranteeProperty(boolean isG) {
		isGuarantee = isGuarantee || isG;
	}

	public ItemOrHeroGambleInfo(String id, boolean isHero, boolean isGuarantee) {
		super();
		Id = id;
		this.isHero = isHero;
		this.isGuarantee = isGuarantee;
	}

	public String getId() {
		return Id;
	}

	public boolean isHero() {
		return isHero;
	}

	public boolean isGuarantee() {
		return isGuarantee;
	}

}
