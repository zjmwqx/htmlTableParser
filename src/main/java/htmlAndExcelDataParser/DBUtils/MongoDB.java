/**
 * @Title MongoDB.java
 * @Author yue.you
 * @Description TODO(interactions with MongoDB)
 * @Date Created At: Aug 19, 2013 10:07:21 AM
 * @Version V1.0
 */
package htmlAndExcelDataParser.DBUtils;

import htmlAndExcelDataParser.Controller.MatchDBItemToCells;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.io.DataInputStream;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoOptions;
import com.mongodb.DB;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

public class MongoDB {

	private String host;// 服务器地址
	private int port;// 端口号
	private String database;// 数据库名
	private String username;// 用户名
	private String password;// 密码
	private MongoClient mc;
	private DB db;
	private static BasicDBObject whereQuery = new BasicDBObject();

	// connect to the DB
	public void connect(String host, int port, String database,
			String username, String password) throws Exception {
		// 连接数据库参数配置
		System.out.println(host + " " + port + " " + database + " " + username
				+ " " + password);
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;

		try {
			ServerAddress sa = new ServerAddress(host, port);
			this.mc = new MongoClient(sa);// 获取Instance
			if (mc != null)
				this.setDb(mc.getDB(database));
			else {
				throw new Exception(
						"Can not get database instance! Please ensure connected to mongoDB correctly.");
			}
			if (username != null && password != null) {
				boolean auth = getDb().authenticate(username,
						password.toCharArray());// 用户名密码认证
				if (auth)
					System.out.println("Connect to mongodb successfully!");
				else {
					setDb(null);
					throw new Exception(
							"Can not connect to mongoDB. Failed to authenticate!");
				}
			}
		} catch (Exception e) {
			
		}
	}

	// close db connection
	public void close() {
		if (mc != null)
			mc.close();
	}

	public MongoDB setHost(String host) {
		this.host = host;
		return this;
	}

	public MongoDB setPort(int port) {
		this.port = port;
		return this;
	}

	public MongoDB setDatabase(String database) {
		this.database = database;
		return this;
	}

	public MongoDB setUsername(String username) {
		this.username = username;
		return this;
	}

	public MongoDB setPassword(String password) {
		this.password = password;
		return this;
	}

	public boolean isclose() {
		if (mc == null) {
			return true;
		}
		return false;
	}

	public DB getDb() throws Exception {
		if (db == null)
			throw new Exception(
					"Can not get database instance! Please ensure connected to mongoDB correctly.");
		else
			return db;
	}

	public void setDb(DB db) {
		this.db = db;
	}

	public void initDBConnection() {
		try {
			//logger.info("initDB...");
			if (MatchDBItemToCells.db_QAconnector == null || MatchDBItemToCells.db_QAconnector.isclose()) {
				Properties prop = new Properties();
				DataInputStream prStm = new DataInputStream(
						MongoDB.class
								.getResourceAsStream("/webParser.properties"));
				prop.load(prStm);
				String url = prop.getProperty("MongoURL");
				String wpDBName = prop.getProperty("webParserDBName");
				String usr = prop.getProperty("DBuser");
				String password = prop.getProperty("DBpassword");
				if (usr.equals(""))
					usr = null;
				if (password.equals(""))
					password = null;
				connect(url, 27017, wpDBName, usr,
						password);
			}
		} catch (Exception e) {
		}
	}
}
