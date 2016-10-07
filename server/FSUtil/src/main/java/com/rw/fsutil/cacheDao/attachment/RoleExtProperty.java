package com.rw.fsutil.cacheDao.attachment;

import javax.persistence.Id;

import com.rw.fsutil.cacheDao.mapItem.RowMapItem;

/**
 * <pre>
 * 玩家创建属性的条目
 * 1.以逻辑指定的类型来区分，比如任务、关卡、活动(不同活动类型)、日常
 * 每个类型关联到一个{@link PlayerExtPropertyStore}，拥有多个 {@link RoleExtProperty}记录
 * 2.每个{@link RoleExtProperty}有逻辑唯一标识的ID(通常是由配置指定)比如副本的关卡ID、任务ID、活动ID
 * 标识每个ID对应的{@link RoleExtProperty}只能创建一次(要注意这个ID不是数据库ID，数据ID由下层生成，逻辑无需关注)
 * 而服务器与客户端通讯，比如标识领取某个ID的任务、打某个关卡，也应该用这个ID进行标识和交互(比用数据库ID安全性更高，效率也更高)
 * 3.增加{@link Id}、{@link OwnerId}可以避免extention字段在数据库中存储冗余数据，分别对应sub_type、owner_id
 * 4.不适合逻辑ID重复的模块，比如当前的道具模块，同一个modelId可以有多个道具
 * </pre>
 * @author Jamaz
 *
 */
public interface RoleExtProperty extends RowMapItem<Integer> {

}
