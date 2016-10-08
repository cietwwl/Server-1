package com.rw.dataaccess.hero;

import com.rw.dataaccess.attachment.RoleExtPropertyCreator;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;

public interface HeroExtPropertyCreator<T extends RoleExtProperty> extends RoleExtPropertyCreator<T, HeroCreateParam>{

}
