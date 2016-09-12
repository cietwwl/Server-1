package com.rw.dataaccess.mapitem;

import java.util.List;

import com.rw.fsutil.cacheDao.mapItem.IMapItem;

public interface MapItemCreator<T extends IMapItem> {

	public List<T> create(String userId, MapItemValidateParam param);

	public boolean isOpen(MapItemValidateParam param);
}
