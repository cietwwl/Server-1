package com.common.text;

public enum HyperTextAttachmentType {

	HERO(1),
	ITEM(2),
	TRUMP(3),
	;
	public final int sign;

	private HyperTextAttachmentType(int pSign) {
		this.sign = pSign;
	}
}
