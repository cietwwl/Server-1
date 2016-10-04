package com.rw.fsutil.dao.attachment;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;

public interface RoleExtPropertyManager {

	/**
	 * 加载指定多个类型的角色附加属性列表
	 * 
	 * @param ownerId
	 * @param typeList
	 * @return
	 */
	public List<QueryRoleExtPropertyData> loadRangeEntitys(String ownerId, List<Short> typeList);

	/**
	 * 加载指定类型的角色附加属性列表
	 * 
	 * @param ownerId
	 * @param type
	 * @return
	 */
	public List<QueryRoleExtPropertyData> loadEntitys(String ownerId, Short type);

	/**
	 * 更新指定主键的扩展属性
	 * 
	 * @param ownerId
	 * @param extention
	 * @param id
	 * @return
	 */
	public boolean updateAttachmentExtention(String ownerId, String extention, Long id);

	/**
	 * <pre>
	 * 批量插入{@link InsertRoleExtPropertyData}列表，全部成功或者全部失败
	 * 返回数据库生成的主键列表
	 * </pre>
	 * 
	 * @param ownerId
	 * @param list
	 * @return
	 */
	public long[] insert(String ownerId, List<? extends InsertRoleExtPropertyData> list) throws Exception;

	/**
	 * 插入一条{@link InsertRoleExtPropertyData}记录，返回数据库生成的主键
	 * 
	 * @param ownerId
	 * @param entry
	 * @return
	 * @throws DuplicatedKeyException
	 * @throws Exception
	 */
	public long insert(final String ownerId, final InsertRoleExtPropertyData entry) throws DuplicatedKeyException, Exception;

	/**
	 * 批量插入{@link InsertRoleExtPropertyData}和删除指定的主键
	 * @param ownerId
	 * @param list
	 * @param deleteList
	 * @return
	 */
	public long[] insertAndDelete(String ownerId, List<InsertRoleExtPropertyData> list, List<Long> deleteList)  throws DataNotExistException, Exception;

	/**
	 * 根据搜索名字获取表名
	 * 
	 * @param searchName
	 * @return
	 */
	public String getTableName(String searchName);

	/**
	 * 获取名字对应的更新sql语句
	 * 
	 * @return
	 */
	public Map<String, String> getTableSqlMapping();

	/**
	 * 删除某条指定的记录
	 * 
	 * @param searchId
	 * @param id
	 * @return
	 * @throws DataNotExistException
	 * @throws Exception
	 */
	public boolean delete(String searchId, Long id) throws DataNotExistException, Exception;

	/**
	 * <pre>
	 * 删除指定的一批记录，返回成功删除的记录id(可能有部分删除失败)
	 * </pre>
	 * 
	 * @param searchId
	 * @param idList
	 * @return
	 * @throws Exception
	 */
	public List<Long> delete(String searchId, List<Long> idList) throws Exception;
}
