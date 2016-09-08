package com.rw.dataaccess.hero;

import com.rw.dataaccess.attachment.RoleExtPropertyCreator;
import com.rw.fsutil.cacheDao.attachment.PlayerExtProperty;

public interface HeroExtPropertyCreator<T extends PlayerExtProperty> extends RoleExtPropertyCreator<T, HeroCreateParam>{

}
