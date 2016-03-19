package com.playerdata;

import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.PersistentLoader;

public class PlayerLoader implements PersistentLoader<String, Player>{

	@Override
	public Player load(String key) throws DataNotExistException, Exception {
		return new Player(key, true);
	}

	@Override
	public boolean delete(String key) throws DataNotExistException, Exception {
		// player不支持删除操作
		return false;
	}

	@Override
	public boolean insert(String key, Player value) throws DuplicatedKeyException, Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateToDB(String key, Player value) {
		// TODO Auto-generated method stub
		return false;
	}

}
