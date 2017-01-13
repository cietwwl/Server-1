package com.rounter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public interface IUCService extends IGetRoleService, IGetAreaService, IGetGiftService, ICheckGiftId{
	Logger logger = LoggerFactory.getLogger("mainlog");

}
