package htmlAndExcelDataParser.DBUtils;

import java.sql.*;
import java.text.SimpleDateFormat;



public class DBConnector {

	public Connection connect2Database() {
		Connection conn = null;
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://10.20.111.101:3306/news";
		String user = "news_app";
		String passwd = "lKTOAIyoewzvCyc";
		try {
			// 加载驱动程序
			Class.forName(driver);
			// 连续数据库
			conn = DriverManager.getConnection(url, user, passwd);
			//if (!conn.isClosed())
				//System.out.println("Succeeded connecting to the Database!");
		} catch (ClassNotFoundException e) {
			System.out.println("Sorry,can`t find the Driver!");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public void insertDataIntoDB(String sql, Object[] params) {
		Connection conn = connect2Database();
		try {
			SimpleDateFormat bartDateFormat =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			PreparedStatement ps = conn.prepareStatement(sql);
			if (null != params && 0 < params.length) {
					ps.setString(1, (String)params[0]);
					//ps.setString(2, bartDateFormat.format(new java.util.Date()));
					ps.setDate(2, (Date)params[1]);
					ps.setDate(3, (Date) params[2]);
					ps.setTimestamp(4, (Timestamp) params[3]);
					ps.setTimestamp(5, (Timestamp) params[4]);
					ps.setTimestamp(6, (Timestamp) params[5]);
					ps.setInt(7, (Integer) params[6]);
					ps.setString(8, (String) params[7]);
					ps.setBoolean(9, (Boolean) params[8]);
					ps.setInt(10, (Integer) params[9]);
			}
			//System.out.println(ps.toString());
			ps.executeUpdate();
			// ResultSet rs = st.executeQuery(sql);
			// while (rs.next()) {
			// System.out.println(rs.toString());
			// }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// 要执行的SQL语句
	}

	void deleteDataFromDB(String sql) {
		Statement st = null;
		Connection conn = connect2Database();
		try {
			st = conn.createStatement();

			st.executeUpdate(sql);
			// ResultSet rs = st.executeQuery(sql);
			// while (rs.next()) {
			// System.out.println(rs.toString());
			// }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				st.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		DBConnector myconn = new DBConnector();
		myconn.connect2Database();
		String sql = "insert into passengerandfreighttransportamount"
				+ "(datayesCodeIdpublishDate,referenceDate,fetchTime,"
				+ "updateTime,insertTime,amount,unit,isActive,producer)"
				+ "values(3.12, 'zjm',  curDate(), curDate(), curDate(), curDate())";
//		myconn.insertDataIntoDB(sql);
//		// String sql = "delete from cpi_estimate where id<=3";
//		// myconn.deleteDataFromDB(sql);

	}

}
