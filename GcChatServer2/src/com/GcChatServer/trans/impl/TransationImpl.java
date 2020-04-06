package com.GcChatServer.trans.impl;

import java.sql.Connection;
import java.sql.SQLException;

import com.GcChatServer.Util.JDBCUtil;
import com.GcChatServer.trans.Transation;

public class TransationImpl implements Transation{
	@Override
	public void begin() {
		try {
			Connection conn = JDBCUtil.getConnection();
			conn.setAutoCommit(false);
			System.out.println("开启事务");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void commit() {
		try {
			Connection conn = JDBCUtil.getConnection();
			conn.commit();
			System.out.println("事务提交成功");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close();
		}
	}


	@Override
	public void rollback() {
		try {
			Connection conn = JDBCUtil.getConnection();
			conn.rollback();
			System.out.println("事务回滚");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close();
		}		
	}

}
