package com.rw.trace;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.type.JavaType;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.common.HPCUtil;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rw.dataaccess.hero.HeroExtPropertyType;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.common.FastPair;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.annotation.FieldEntry;
import com.rw.fsutil.dao.attachment.InsertRoleExtPropertyData;
import com.rw.fsutil.dao.attachment.RoleExtPropertyManager;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.skill.pojo.SkillItem;

/**
 * 英雄属性的数据迁移 包括神器、技能、英雄身上装备、英雄身上宝石
 * 
 * @author Jamaz
 *
 */
public class HeroPropertyMigration {

	private static HeroPropertyMigration instance = new HeroPropertyMigration();
	static RoleExtPropertyManager heroManager = DataAccessFactory.getHeroAttachmentManager();

	public static HeroPropertyMigration getInstance() {
		return instance;
	}

	protected static Object readJsonValue(String json, FieldEntry field) {
		JavaType type = field.collectionType;
		if (type != null) {
			return JsonUtil.readValue((String) json, type);
		} else {
			return JsonUtil.readValue((String) json, field.field.getType());
		}
	}

	public void execute() throws Exception {
		JdbcTemplate template = DataAccessFactory.getSimpleSupport().getMainTemplate();
		ArrayList<String> tableNameList = new ArrayList<String>();
		PlatformTransactionManager tm = new DataSourceTransactionManager(template.getDataSource());
		DefaultTransactionDefinition df = new DefaultTransactionDefinition();
		df.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		for (int i = 0; i < 16; i++) {// hero_extended_property00
			String tableName = "hero_extended_property" + ((i >= 10) ? i : "0" + i);
			try {
				template.execute("select count(1) from " + tableName);
			} catch (Exception e) {
				throw new ExceptionInInitializerError("请先同步表结构："+tableName);
			}
		}
		TransactionStatus ts = tm.getTransaction(df);
		try {
			execute(new ExplainFixExpEquip(), tableNameList);
			execute(new ExplainFixNormEquip(), tableNameList);
			for (int i = 0; i < 10; i++) {
				execute(new SkillExplain("0" + i), tableNameList);
			}
			execute(new EquipItemExplain(), tableNameList);
			execute(new InlayItemExplain(), tableNameList);
			tm.commit(ts);
		} catch (Exception t) {
			tm.rollback(ts);
			throw t;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("ddHHmmss");
		String backupPostfix = formatter.format(new Date());
		for (int i = tableNameList.size(); --i >= 0;) {
			String tableName = tableNameList.get(i);
			try {
				String createSql = (String) template.queryForMap("SHOW CREATE TABLE " + tableName).get("Create Table");
				template.execute("RENAME table " + tableName + " to " + (tableName + '_' + backupPostfix));
				// 本来不需要,避免240被同步时删除有数据的本地表
				template.execute(createSql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void execute(IExplainOldData explainData, ArrayList<String> tableNameList) throws Exception {
		JdbcTemplate template = DataAccessFactory.getSimpleSupport().getMainTemplate();
		String tableName = explainData.getTableName();
		// 1.检查表数量
		int count;
		try {
			count = template.queryForObject("select count(*) from " + tableName, Integer.class);
			if (count == 0) {
				return;
			}
		} catch (Exception e) {
			return;
		}
		// //2.备份
		// String createSql = (String) template.queryForMap("SHOW CREATE TABLE "
		// + tableName).get("Create Table");
		// createSql = createSql.replace("`" + tableName + "`",
		// " if not EXISTS " + newTableName);
		// template.execute(createSql);
		// int backupCount = template.update("insert into " + newTableName +
		// " select * from " + tableName);
		// if (count != backupCount) {
		// throw new
		// RuntimeException("备份记录与原表不一致:"+tableName+",count="+count+",backupCount="+backupCount);
		// }
		System.out.println("备份记录数:" + count + ",tableName=" + tableName);
		// 3.插入新表
		List<Map<String, Object>> list = template.queryForList("select * from " + explainData.getTableName());
		ClassInfo classInfo = new ClassInfo(explainData.getHeroExtClass());
		short type = explainData.getHeroExtType().getType();
		HashMap<String, List<InsertRoleExtPropertyData>> insertMap = new HashMap<String, List<InsertRoleExtPropertyData>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);

			FastPair<String, Integer> pair = explainData.explain(map);
			String owner_id = pair.firstValue;
			int sub_type = pair.secondValue;
			String sql = heroManager.getInsertSql(owner_id);
			List<InsertRoleExtPropertyData> insertList = insertMap.get(sql);
			if (insertList == null) {
				insertList = new ArrayList<InsertRoleExtPropertyData>(1000);
				insertMap.put(sql, insertList);
			}
			String ext = (String) map.get("extention");
			Map<String, String> fieldJsonMap = JsonUtil.readToMap(ext);
			LinkedHashMap<String, Object> newMap = new LinkedHashMap<String, Object>();
			for (Map.Entry<String, String> entry : fieldJsonMap.entrySet()) {
				String key = entry.getKey();
				FieldEntry field = classInfo.getFieldEntry(key);
				if (field == null) {
					continue;
				}
				Object object = readJsonValue(entry.getValue(), field);
				if (object == null) {
					throw new RuntimeException("can not serilize:" + key + "=" + entry.getValue());
				}
				newMap.put(key, object);
			}
			String newExt = JsonUtil.writeValue(newMap);
			insertList.add(new InsertRoleExtPropertyData(owner_id, type, sub_type, newExt));
		}
		int total = 0;
		for (Map.Entry<String, List<InsertRoleExtPropertyData>> entry : insertMap.entrySet()) {
			total += batchInsert(template, entry.getKey(), entry.getValue());
		}
		System.out.println("成功插入总数:" + total);
		tableNameList.add(tableName);
		// 4.删除旧表
		// System.out.println("删除记录数:" + deleteCount + "," + tableName);
	}

	static abstract class IExplainOldData {

		private final HeroExtPropertyType type;
		private final String tableName;
		private final Class<? extends RoleExtProperty> heroExtClass;

		public IExplainOldData(HeroExtPropertyType type, String tableName, Class<? extends RoleExtProperty> heroExtClass) {
			this.type = type;
			this.tableName = tableName;
			this.heroExtClass = heroExtClass;

		}

		public HeroExtPropertyType getHeroExtType() {
			return type;
		}

		public String getTableName() {
			return tableName;
		}

		public Class<? extends RoleExtProperty> getHeroExtClass() {
			return heroExtClass;
		}

		public FastPair<String, Integer> explain(Map<String, Object> map) {
			String[] idArray = HPCUtil.parseStringArray((String) map.get("id"), "_");
			return new FastPair<String, Integer>(idArray[0], Integer.parseInt(idArray[1]));
		}
	}

	static class ExplainFixExpEquip extends IExplainOldData {

		public ExplainFixExpEquip() {
			super(HeroExtPropertyType.FIX_EXP_EQUIP, "fix_exp_equip_item", FixExpEquipDataItem.class);
		}

	}

	static class ExplainFixNormEquip extends IExplainOldData {

		public ExplainFixNormEquip() {
			super(HeroExtPropertyType.FIX_NORM_EQUIP, "fix_norm_equip_item", FixNormEquipDataItem.class);
		}

	}

	static class SkillExplain extends IExplainOldData {

		public SkillExplain(String postfix) {
			super(HeroExtPropertyType.SKILL_ITEM, "skill_item" + postfix, SkillItem.class);
		}

	}

	static class EquipItemExplain extends IExplainOldData {

		public EquipItemExplain() {
			super(HeroExtPropertyType.EQUIP_ITEM, "equip_item", EquipItem.class);
		}

	}

	static class InlayItemExplain extends IExplainOldData {

		public InlayItemExplain() {
			super(HeroExtPropertyType.INLAY_ITEM, "inlay_item", InlayItem.class);
		}

	}

	private static int batchInsert(JdbcTemplate template, String insertSql, final List<InsertRoleExtPropertyData> insertList) {
		int[] result = template.batchUpdate(insertSql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement pstmt, int i) throws SQLException {
				InsertRoleExtPropertyData entityData = insertList.get(i);
				entityData.setValues(pstmt);
			}

			@Override
			public int getBatchSize() {
				return insertList.size();
			}
		});
		for (int i = 0; i < result.length; i++) {
			if (result[i] != 1) {
				throw new RuntimeException("插入失败:" + insertSql + "," + insertList.get(i) + ",result=" + result[i]);
			}
		}
		int size = insertList.size();
		if (size != result.length) {
			throw new RuntimeException("插入失败:" + insertSql + "," + size + ",result=" + result.length);
		}
		System.out.println("插入记录数:" + size + "," + insertSql.substring(insertSql.indexOf("into") + 5, insertSql.indexOf("(")));
		return size;
	}
}
