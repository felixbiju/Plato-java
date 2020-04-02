package com.PLATO.Threads;

import java.sql.Connection;
import java.sql.DriverManager;

public class TEMDatabaseThread {

	public TEMDatabaseThread() {

	}

	public static String getDatabaseStatus(String dbUrl, String uname, String pass, String databaseDriver) {
		String dbStatus = "stopped";
		Connection conn1 = null;
		int databaseUP;
		try {

			Class.forName(databaseDriver);
			System.out.println("Connecting to database...");
			conn1 = DriverManager.getConnection(dbUrl, uname, pass);
			System.out.println("Connected successfully to database");
			dbStatus = "Running";
		} catch (Exception e) {
			System.out.println("Connection to database failed");
			e.printStackTrace();
			dbStatus = "stopped";
		}

		return dbStatus;
	}
}
