package com.ecm;

//import com.mysql.jdbc.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class GenerateMenu {

	public static void run() {
		Connection conn = null;
		Statement stmt = null;

		// Database credentials
		String JDBC_DRIVER = DefaultAction.get("db.driver");
		String DB_URL = DefaultAction.get("db.url");
		String USER = DefaultAction.get("db.user");
		String PASS = DefaultAction.get("db.password");
		String QUERY = DefaultAction.get("db.query");

		try {
			String[] sql = QUERY.split(";");
			for (int i = 0; i < sql.length; i++) {
				Class.forName(JDBC_DRIVER);
				conn = (Connection) DriverManager.getConnection(DB_URL, USER,
						PASS);
				stmt = conn.createStatement();
				stmt.executeUpdate(sql[i]);
			}
		} catch (Exception e) {
			System.err.println(e.getCause());
		}

	}
}
