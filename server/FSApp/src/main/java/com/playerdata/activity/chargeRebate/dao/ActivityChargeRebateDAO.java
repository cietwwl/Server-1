package com.playerdata.activity.chargeRebate.dao;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.common.CommonRowMapper;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;

public class ActivityChargeRebateDAO {
	private static ActivityChargeRebateDAO _instance = new ActivityChargeRebateDAO();
	
	public static ActivityChargeRebateDAO getInstance(){
		return _instance;
	}
	
	private JdbcTemplate _jdbcTemplate;
	private ClassInfo _classInfo;
	private String _querySql;
	private String _updateSql;
	private String _checkExistSql;
	
	protected ActivityChargeRebateDAO(){
		DruidDataSource dataSource = SpringContextUtil.getBean("dataSourcePF");
		_jdbcTemplate = new JdbcTemplate(dataSource);
		
		_classInfo = new ClassInfo(ActivityChargeRebateData.class);
		
		String tableName = _classInfo.getTableName();
		Field idField = _classInfo.getIdField();
		
		StringBuilder insertFields = new StringBuilder();
		StringBuilder insertHolds = new StringBuilder();
		StringBuilder updateFields = new StringBuilder();
		try {
			_classInfo.extractColumn(insertFields, insertHolds, updateFields);
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
		String idFieldName = idField.getName();
		_querySql = "select * from " + tableName + " where " + idFieldName + "= '%s'";
		
		String updateFieldsString = updateFields.toString();
		_updateSql = "update " + tableName + " set " + updateFieldsString + " where " + idFieldName + " = ?";
		
		_checkExistSql = "insert  into `charge_rebate_record`(`openAccount`,`zoneId`,`achieveTime`) values (?, ?, ?)";
		
	}
	
	public ActivityChargeRebateData queryActivityChargeRebateData(String openAccount){
		if(StringUtils.isEmpty(openAccount)){
			return null;
		}
//		ActivityChargeRebateData result = _jdbcTemplate.queryForObject(String.format(_querySql, openAccount), new CommonRowMapper<ActivityChargeRebateData>(_classInfo, openAccount));
		List<ActivityChargeRebateData> list = _jdbcTemplate.query(String.format(_querySql, openAccount), new CommonRowMapper<ActivityChargeRebateData>(_classInfo, openAccount));
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	
	public boolean checkAchieveChargeRebateReward(String openAccount, int zoneId){
		String currentTime = DateUtils.getDateTimeFormatString("yyyy-MM-dd HH:mm:ss");
		int update = _jdbcTemplate.update(_checkExistSql, openAccount, zoneId, currentTime);
		return update > 0;
	}
	
	public boolean updateActivityChargeRebateData(ActivityChargeRebateData data){
		int result = _jdbcTemplate.update(_updateSql, new ActivityChargePreparedStatementSetter(data, _classInfo));
		return result > 0;
	}
	
	private static class ActivityChargePreparedStatementSetter implements PreparedStatementSetter{

		private ActivityChargeRebateData _data;
		private ClassInfo _classInfo;
		
		ActivityChargePreparedStatementSetter(ActivityChargeRebateData data, ClassInfo classInfo) {
			this._data = data;
			this._classInfo = classInfo;
		}
		
		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
			try{
				List<Object> list = _classInfo.extractInsertAttributes(_data);
				for (int i = 0, size = list.size(); i < size; i++) {
					ps.setObject(i + 1, list.get(i));
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		
	}
}
