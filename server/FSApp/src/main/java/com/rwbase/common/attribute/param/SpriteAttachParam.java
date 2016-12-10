package com.rwbase.common.attribute.param;

import java.util.List;

import com.rwbase.dao.spriteattach.SpriteAttachItem;

public class SpriteAttachParam {
	private final String userId;
	private final String heroId;
	private final List<SpriteAttachItem> items;

	private SpriteAttachParam(String userId, String heroId, List<SpriteAttachItem> items) {
		super();
		this.userId = userId;
		this.heroId = heroId;
		this.items = items;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroId() {
		return heroId;
	}

	public List<SpriteAttachItem> getItems() {
		return items;
	}

	public static class SpriteAttachBuilder {
		private String userId;
		private String heroId;
		private List<SpriteAttachItem> items;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setHeroId(String heroId) {
			this.heroId = heroId;
		}

		public void setItems(List<SpriteAttachItem> items) {
			this.items = items;
		}

		public SpriteAttachParam build() {
			return new SpriteAttachParam(userId, heroId, items);
		}
	}
}
