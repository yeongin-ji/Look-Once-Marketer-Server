package LookOnceMarketer_server_ver1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LOM_DBManager {
	private Connection conn;
	private PreparedStatement pstmt;
	private Statement stmt;
	private ResultSet rs;

	private String dbHost, dbPort, dbName;
	private String id;
	private String pwd;

	// Database init
	public LOM_DBManager() {
		dbHost = "127.0.0.1";
		dbPort = "3306";
		dbName = "mydb";
		String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
		this.id = "LookOnceMarketer_DB";
		this.pwd = "abcd1234";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			System.err.println(e1);
		}

		dbInit(url);
		System.out.println("[DBManager]: DB Init Success.");
	}

	// Database connection
	public void dbInit(String url) {
		System.out.println("[DBManager]: DB Init..");
		try {
			this.conn = DriverManager.getConnection(url, this.id, this.pwd);
		} catch (SQLException e1) {
			System.err.println(e1);
		}
	}

	// insert values
	public void dbInsert(DetectedFruit df) {
		String query;

		query = "insert into measurement(id, marketability, result) values(?,?,?)";

		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, df.fruitNum);
			pstmt.setFloat(2, df.marketability);
			pstmt.setInt(3, df.result);
			pstmt.executeUpdate();
			pstmt.close();
			System.out.println("[DBManager]: insert success!");
		} catch (SQLException e1) {
			System.err.println(e1);
		}

	}

	public ArrayList<Fruit> dbFetch() {
		String query;
		ArrayList<Fruit> mkDataList = new ArrayList<Fruit>();
		
		query = "select * from measurement";

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				int frNum = rs.getInt("id");
				float mkabil = rs.getFloat("marketability");
				int res = rs.getInt("result");

				System.out.println("rs: " + frNum + ", " + mkabil + ", " + res);
				mkDataList.add(new Fruit(frNum, mkabil, res));
			}
			stmt.close();
			System.out.println("[DBManager]: Fetch success!");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("[DBManager]: Fetch error");
		}
		
		return mkDataList;
	}

}
