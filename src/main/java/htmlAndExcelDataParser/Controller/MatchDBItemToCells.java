package htmlAndExcelDataParser.Controller;

import htmlAndExcelDataParser.GetTableCellFromExcel;
import htmlAndExcelDataParser.GetTableCellFromHtml;
import htmlAndExcelDataParser.DBUtils.DBConnector;
import htmlAndExcelDataParser.DBUtils.MongoDB;
import htmlAndExcelDataParser.ReadDic.Dictionary;
import htmlAndExcelDataParser.ReadDic.DictionaryFactory;
import htmlAndExcelDataParser.ReadDic.Item;
import htmlAndExcelDataParser.dataStructure.Data;
import htmlAndExcelDataParser.dataStructure.FileDesDate;
import htmlAndExcelDataParser.dataStructure.NumericData;
import htmlAndExcelDataParser.dataStructure.StringData;
import htmlAndExcelDataParser.dataStructure.UnitData;
import htmlAndExcelDataParser.model.CellData;
import htmlAndExcelDataParser.model.Table;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MatchDBItemToCells {
	private FileDesDate fileDesDate;// 文件名中的日期
	private List<Table> tbList;// 文件包含的table列表
	private List<Item> dicItemList;// 数据字典中该文件所属的table字段名
	private java.sql.Date publishData;
	public static MongoDB db_QAconnector = new MongoDB();
	public MatchDBItemToCells() {
		// TODO Auto-generated constructor stub
		fileDesDate = null;
		tbList = null;
		dicItemList = null;
	}

	public void init(String dataFilePath, String DBDicTabName) throws Exception {
		File file = new File(dataFilePath);
		fileDesDate = new FileDesDate();
		String fileNameStr = file.getName();
		String fileNameRegex = "^*\\d{4}年\\d{1,2}月*";
		Pattern pat = Pattern.compile(fileNameRegex);
		Matcher mat = pat.matcher(fileNameStr);
		if (mat.find()) {
			fileDesDate.setDateString(mat.group());
			String[] fileDesDateArr = fileDesDate.getDateString().split("年|月");
			for (int i = 0; i < fileDesDateArr.length; i++) {
				fileDesDate.setYear(Integer.valueOf(fileDesDateArr[0]));
				fileDesDate.setMonth(Integer.valueOf(fileDesDateArr[1]));
			}
		}
		fileNameRegex = "^\\d{4}-\\d{2}-\\d{2}";
		pat = Pattern.compile(fileNameRegex);
		mat = pat.matcher(fileNameStr);
		if (mat.find()) {
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				java.util.Date date = bartDateFormat.parse(mat.group());
				publishData = new java.sql.Date(date.getTime());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
			//System.out.println("publishData:"+publishData);
		}

		if (fileNameStr.endsWith("htm") || fileNameStr.endsWith("html")) {
			tbList = GetTableCellFromHtml.readHtml(file);

		} else if (fileNameStr.endsWith("xls") || fileNameStr.endsWith("xlsx")) {
			List<Table> tableList = new ArrayList<Table>();
			tbList = GetTableCellFromExcel.readExcel(file);
		} else {
			throw new Exception("Not a qualified data file format!");
		}
		dicItemList = getItemListFromDBDicByTableName(DBDicTabName);
		//for (int j = 0; j < dicItemList.size(); j++) {
			//System.out.println(dicItemList.get(j).getSynonyms());
		//}
		//System.out.println("dic load complete!");
	}

	public MatchDBItemToCells(String dataFilePath, String DBDicTabName)
			throws Exception {
		// 初始化 fileDesDate,tbList,dicItemList
		init(dataFilePath, DBDicTabName);
	}

	private List<Item> getItemListFromDBDicByTableName(String TableName) {
		List<Item> DBItemList = null;
		try {
			//System.out.println("start getDic");
			Dictionary dic = DictionaryFactory.getDictionary();
			//System.out.println(dic == null);
			DBItemList = extracted(TableName, dic);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DBItemList;
	}

	private List<Item> extracted(String TableName, Dictionary dic)
	{
		return dic.getTableItemListByTableName(TableName);
	}
	void dealWithQiZhong(Table curtab)
	{
		HashMap<Point, CellData> cells = curtab.getCells();
		for (Entry<Point, CellData> cell : cells.entrySet()) {
			// 计算每个cell的范围交集
			if (cell.getValue().getDataContent() instanceof StringData) {
				StringData cellContent = (StringData) cell.getValue()
						.getDataContent();
				String contStr = cellContent.getContent();
				String[] contSpStr = contStr.split(":|：");
				if (contSpStr[0].equals("其中"))
				{
					contStr = contSpStr[1];
				}
				cellContent.setContent(contStr);
			}
		}
	}
	public void doit() {
		int ptcnt = 0;
		for (int i = 0; i < tbList.size(); i++) {
			Table curtab = tbList.get(i);
			//System.out.println(curtab.getCells().size());
			curtab.setFileDesDate(fileDesDate);
			curtab.fillInUnitCells();


			for (int j = 0; j < dicItemList.size(); j++) {
				Set<String> SynaNameSet = dicItemList.get(j).getSynonyms()
						.keySet();
				Set<Point> resPoints = new HashSet<Point>();
				for (String SynaName : SynaNameSet) {
					String[] nameList = SynaName.split(":|：");
//					if (nameList.length < 2)
//						break;
//					StringBuffer sb = new StringBuffer();
//					for(int u = 0; u < nameList.length; i++){
//					 sb. append(nameList[i]);
//					}

					//System.out.println(nameList.toString());
					Set<Point> points = curtab.locateCell(nameList);
					points = curtab.judgePoints(points);
					resPoints.addAll(points);
				}
				if (resPoints.size() == 0) {
					continue;
				}
				java.sql.Date refDate;
				SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy年MM月");
				try {
					java.util.Date date = bartDateFormat.parse(fileDesDate.getDateString());
					refDate = new java.sql.Date(date.getTime());
					for (Point point : resPoints) {
						ptcnt++;
						String insertTime = fileDesDate.toString();
						String sql = "INSERT INTO indexrelated"
								+ "(datayesCodeId,publishDate,referenceDate,fetchTime,"
								+ "updateTime,insertTime,amount,unit,isActive,producer)"
								+ "VALUES(?,?,?,?,?,?,?,?,?,?)";

						DBConnector myconn = new DBConnector();
						Object[] params = {
								dicItemList.get(j).getEncoding(),
								publishData,
								// new Timestamp(System.currentTimeMillis()),
								refDate,
								new Timestamp(System.currentTimeMillis()),
								new Timestamp(System.currentTimeMillis()),
								new Timestamp(System.currentTimeMillis()),
								((NumericData) curtab.getCells().get(point)
										.getDataContent()).getContent().intValue(),
								curtab.getUnit(point), false, 1 };
						myconn.insertDataIntoDB(sql,params);
						
//						System.out.println("tab "
//								+ i
//								+ ": insert into "
//								+ "xxxxx values("
//								+ SynaNameSet.toString()
//								+ ", "
//								+ curtab.getCells().get(point).getDataContent()
//										.toString() + ", " + curtab.getUnit(point)
//								+ ")");
					}
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		}
		//System.out.println(ptcnt);
	}

	public static void IncursiveDoParseInFileDirs(File dir) throws Exception
	{
		//File file = new File(DirName);// 读取文件地址
		File[] files = dir.listFiles();
		MatchDBItemToCells mytest;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {// 判断是否有子目录，如果有，就调用自己，没有就直接输出文件

				IncursiveDoParseInFileDirs(files[i]);
			} else {
				File f = files[i];
				System.out.println(f.getName()); // 文件名
//				++fileNo;
//				logger.log(Level.INFO, "No." + fileNo + ": " + f.getName()
//						+ " is Processing...");
				System.out.println(f.getPath()+"\n");	
				
				try{
					mytest = new MatchDBItemToCells(f.getPath(),
							dir.getName());
					
				}catch(Exception e)
				{
					System.out.println(e.toString());
					continue;
				}
				mytest.doit();
			}
		}
	}
	public DBCollection getColl(MongoDB db_connector)
	{
		DBCollection collReport = null;
		Properties prop = new Properties();
		DataInputStream prStm = new DataInputStream(
				MatchDBItemToCells.class
						.getResourceAsStream("/webParser.properties"));
		try{
			prop.load(prStm);
			String collectionName = prop.getProperty("wpClctName");
			DBObject options = new BasicDBObject();
			if(!db_connector.getDb().collectionExists(collectionName))
				collReport = db_connector.getDb().createCollection(collectionName, options);
			else collReport = db_connector.getDb().getCollection(collectionName);
		}catch(Exception e)
		{
			System.out.println(e.toString());
		}
		return collReport;
	}
	public void parseData()
	{
		db_QAconnector.initDBConnection();
		DBCollection collIndex = getColl(db_QAconnector);
		DBCursor cursor = null;
		BasicDBObject condition = new BasicDBObject(); 
		condition.put("isDeal", false);
		condition.put("downloadSuccess", true);
		cursor = collIndex.find(condition);
		cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
		int count = 0;
		while(cursor.hasNext())
		{
			if(count%100 == 0)
				System.out.println(count);
			DBObject fileInfoDB = cursor.next();
			count++;
			//logger.debug(fileInfoDB.get("full_path").toString());
			try {
				parseFromMongoDB(fileInfoDB.get("path").toString(), 
						fileInfoDB.get("tableName").toString());
				fileInfoDB.put("isDeal", true);
				collIndex.update(
						new BasicDBObject("path", fileInfoDB
								.get("path")), fileInfoDB);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//unParsedFiles.add(fileInfo);
			//saveToFile(fileInfo);
		}
	}
	public static void main(String[] args) throws Exception {

		MatchDBItemToCells myImp = new MatchDBItemToCells();
		myImp.parseData();
		

		
		//System.out.println("in main");
		//IncursiveDoParseInFileDirs(file);
	}

	private void parseFromMongoDB(String filePath, String tableName) throws Exception {
		// TODO Auto-generated method stub
		
//		++fileNo;
//		logger.log(Level.INFO, "No." + fileNo + ": " + f.getName()
//				+ " is Processing...");
		MatchDBItemToCells mytest;
		System.out.println(filePath + " " + tableName);	
		

		mytest = new MatchDBItemToCells(filePath, tableName);
		mytest.doit();

	}
}
