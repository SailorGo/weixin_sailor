package com.hyx.db;

import hyxlog.HyxLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbManagerImp {
	private static final Logger logger = LoggerFactory
			.getLogger(DbManagerImp.class);

	/**
	 * 获取连接
	 * 
	 * @return
	 */
	public Connection getConnect() {
		Connection con = null;
		Context ctx = null;
		DataSource ds = null;
		try {
			ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/DBMaizuo");
		} catch (NamingException e) {
			HyxLog.info("没有找到连接池:java:/comp/env/jdbc/DBMaizuo");
			e.printStackTrace();
		}
		if (ds != null) {
			try {
				con = ds.getConnection();
			} catch (SQLException e) {
				HyxLog.info("获取连接池'java:/comp/env/jdbc/DBMaizuo'的连接对象失败");
				e.printStackTrace();
			} finally {
				if (null != ctx) {
					try {
						ctx.close();
						ctx = null;
					} catch (NamingException e) {
						e.printStackTrace();
					}

				}
			}
		}
		return con;
	}

	/**
	 * 获取连接
	 * 
	 * @return
	 */
	public Connection getConnect(String dataSource) {
		Connection con = null;
		Context ctx = null;
		DataSource ds = null;
		try {
			ctx = new InitialContext();
			ds = (DataSource) ctx.lookup(dataSource);
		} catch (NamingException e) {
			HyxLog.info("没有找到连接池:" + dataSource);
			e.printStackTrace();
		}
		if (ds != null) {
			try {
				con = ds.getConnection();
				// logger.debug("创建了" + (++count) + "个连接");
			} catch (SQLException e) {
				HyxLog.info("获取连接池'" + dataSource + "'的连接对象失败");
				e.printStackTrace();
			} finally {
				if (null != ctx) {
					try {
						ctx.close();
						ctx = null;
					} catch (NamingException e) {
						e.printStackTrace();
					}

				}
			}
		}
		return con;
	}

	/**
	 * 清理resultSet
	 * 
	 * @param rs
	 */
	public void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException e) {
			HyxLog.info("close rs error !");
			e.printStackTrace();
		}
	}

	/**
	 * 清理PreparedStatement
	 * 
	 * @param prestmt
	 */
	public void close(PreparedStatement prestmt) {
		try {
			if (null != prestmt) {
				prestmt.close();
				prestmt = null;
			}
		} catch (SQLException e) {
			HyxLog.info("close prestmt error !");
			e.printStackTrace();
		}
	}

	/**
	 * 关闭Connection
	 * 
	 * @param con
	 */
	public void close(Connection con) {
		try {
			if (null != con) {
				con.close();
				con = null;
			}
		} catch (SQLException e) {
			HyxLog.info("close con error !");
			e.printStackTrace();
		}

	}

	/**
	 * 关闭连接 释放内存
	 */
	public void close(Connection con, ResultSet rs) {
		this.close(rs);
		this.close(con);
	}

	/**
	 * 查询操作
	 * 
	 * @param sql
	 * @param data
	 *            参数都
	 * @return resultset
	 * @throws SQLException
	 */
	public ResultSet preExecQuery(Connection con, String sql, Object[] data)
			throws SQLException {
		String querySQL = getPreparedSQL(sql, data);
		PreparedStatement prestmt = (PreparedStatement) con
				.prepareStatement(sql);
		this.setParams(prestmt, data);
		ResultSet rs = prestmt.executeQuery();
		logger.debug("查询：{}", querySQL);
		return rs;
	}

	/**
	 * 插入数据返回主键
	 * 
	 * @param con
	 * @param sql
	 * @param data
	 * @return 主键
	 * @throws SQLException 
	 */
	public int preExecUpdateKey(Connection con, String sql, Object[] data) throws SQLException {
		String saveSQL = getPreparedSQL(sql, data);
		PreparedStatement prestmt = null;
		ResultSet rs = null;
		int _backKey = -1;
		// 1 设置提交方式为程序控制
		con.setAutoCommit(false);
		// 2 获得语句对象
		prestmt = con
				.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		// 3设置SQL语句的参数
		this.setParams(prestmt, data);
		// 4 执行语句
		prestmt.executeUpdate();
		// 5 程序提交
		con.commit();
		// 6 返回生成的主键
		rs = prestmt.getGeneratedKeys();
		if (rs.next()) {
			_backKey = rs.getInt(1);
		}
		close(rs);
		close(prestmt);
		logger.debug("保存-返回主键：{},{}",saveSQL,_backKey);
		return _backKey;
	}

	/**
	 * 更新操作
	 * 
	 * @param sql
	 * @param data
	 * @return 影响的行数
	 * @throws SQLException 
	 */
	public int preExecUpdate(Connection con, String sql, Object[] data) throws SQLException {
		String updateSQL = getPreparedSQL(sql, data);
		PreparedStatement prestmt = (PreparedStatement) con.prepareStatement(sql);
		this.setParams(prestmt, data);
		int _count = -1;
		_count = prestmt.executeUpdate();
		close(prestmt);
		logger.debug("更新-返回影响行数：{},{}", updateSQL,_count);
		return _count;
	}

	/**
	 * 取得总数
	 * 
	 * @param sql
	 * @return sql的第一个字段值
	 */
	public int getPreTotal(Connection con, String sql, Object[] data)
			throws SQLException {
		String querySQL = getPreparedSQL(sql, data);
		PreparedStatement prestmt = (PreparedStatement) con
				.prepareStatement(sql);
		this.setParams(prestmt, data);
		ResultSet rs = prestmt.executeQuery();
		int _count = 0;
		if (null != rs && rs.next()) {
			_count = rs.getInt(1);
		}
		logger.debug("查询-返回总数：{},{}", querySQL,_count);
		return _count;
	}

	/**
	 * 打印sql
	 * 
	 * @param sql
	 * @param params
	 * @return 去掉？号的sql
	 */
	public void printSQL(String sql, Object[] params) {
		int _paramNum = 0;
		String _backSql = "";
		if (null != params)
			_paramNum = params.length;
		if (1 > _paramNum) {// 1 如果没有参数，说明是不是动态SQL语句
			_backSql = sql;
		} else {// 2 如果有参数，则是动态SQL语句
			StringBuffer _returnSQL = new StringBuffer();
			String[] _subSQL = sql.split("\\?");
			for (int i = 0; i < _paramNum; i++) {
				if (null != params[i]) {
					if (params[i] instanceof java.util.Date) {
						_returnSQL
								.append(_subSQL[i])
								.append(" '")
								.append(this
										.util2sql((java.util.Date) params[i]))
								.append("' ");
					} else if (params[i] instanceof String) {
						_returnSQL.append(_subSQL[i]).append(" '")
								.append(params[i]).append("' ");
					} else if (params[i] instanceof Integer) {
						_returnSQL.append(_subSQL[i]).append(" ")
								.append(params[i]).append(" ");
					}
				}
			}
			if (_subSQL.length > params.length) {
				_returnSQL.append(_subSQL[_subSQL.length - 1]);
			}
			_backSql = _returnSQL.toString();
		}
		logger.debug(_backSql);
	}

	/**
	 * 设置参数
	 * 
	 * @param pstmt
	 * @param params
	 */
	private void setParams(PreparedStatement pstmt, Object[] params) {
		if (null != params) {
			for (int i = 0, j = 1; i < params.length; i++) {
				try {
					if (null != params[i]) {// 有值可写入
						if (params[i] instanceof java.util.Date) {
							pstmt.setDate(j,
									this.util2sql((java.util.Date) params[i]));
						} else {
							pstmt.setObject(j, params[i]);
						}
						j++;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private java.sql.Date util2sql(java.util.Date fechaUtil) {
		java.sql.Date _SQL = new java.sql.Date(fechaUtil.getTime());
		return _SQL;
	}

	/**
	 * 获得PreparedStatement向数据库提交的SQL语句
	 * 
	 * @param sql
	 * @param insertData
	 */
	public String getPreparedSQL(String sql, Object[] insertData) {
		// 1 如果没有参数，说明是不是动态SQL语句
		int paramNum = 0;
		if (null != insertData)
			paramNum = insertData.length;
		if (1 > paramNum)
			return sql;
		// 2 如果有参数，则是动态SQL语句
		StringBuffer returnSQL = new StringBuffer();
		String[] subSQL = sql.split("\\?");
		for (int i = 0; i < paramNum; i++) {
			if (insertData[i] instanceof Date) {
				returnSQL.append(subSQL[i]).append(" '")
						.append(this.util2sql((java.util.Date) insertData[i]))
						.append("' ");
			} else if (insertData[i] instanceof String) {
				returnSQL.append(subSQL[i]).append(" '").append(insertData[i])
						.append("' ");
			} else if (insertData[i] instanceof Integer) {
				returnSQL.append(subSQL[i]).append(" ").append(insertData[i])
						.append(" ");
			}
		}
		if (subSQL.length > insertData.length) {
			returnSQL.append(subSQL[subSQL.length - 1]);
		}
		return returnSQL.toString();
	}
}
