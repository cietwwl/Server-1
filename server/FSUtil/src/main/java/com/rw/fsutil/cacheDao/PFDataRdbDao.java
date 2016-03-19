package com.rw.fsutil.cacheDao;


import java.util.List;


























import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.dao.JdbcDataRdbDao;
import com.rw.fsutil.dao.annotation.ClassHelper;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.log.SqlLog;


public class PFDataRdbDao<T> extends DataRdbDao<T> {

	public PFDataRdbDao() {
		super("dataSourcePF");
	}
	
}
